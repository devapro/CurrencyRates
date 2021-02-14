package pro.devapp.currencyrates.ui.rates

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.currencyrates.ui.common.BaseViewModel
import pro.devapp.currencyrates.usecases.CreateCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase
import pro.devapp.currencyrates.usecases.LoadRatesListUseCase
import java.util.concurrent.TimeUnit

private const val DEFAULT_CURRENCY_CODE = "EUR"
private const val API_REQUEST_INTERVAL_SECOND = 1L
private const val API_RETRY_REQUEST_DELAY_SECOND = 10L

class RatesViewModel(
    application: Application,
    private val loadRatesListUseCase: LoadRatesListUseCase,
    private val getRatesListUseCase: GetRatesListUseCase,
    private val getCurrencyByCodeUseCase: CreateCurrencyByCodeUseCase
) : BaseViewModel(application) {

    val currencyList = MutableLiveData<List<EntityCurrency>>()
    val errorMessage = MutableLiveData<String?>()

    private var loadDataDisposable: Disposable? = null
    private var selectedCurrency: EntityCurrency? = null
    private var currentValue = 1.00

    fun startRefreshList() {
        selectedCurrency ?: run {
            setDefaultCurrency()
        }
        selectedCurrency?.let {
            restartRefreshing()
        }
    }

    private fun setDefaultCurrency() {
        val params = CreateCurrencyByCodeUseCase.Params(DEFAULT_CURRENCY_CODE, currentValue)
        val selectedCurrency = getCurrencyByCodeUseCase.run(params)
        this.selectedCurrency = selectedCurrency
    }

    fun setSelectedCurrency(newCurrency: EntityCurrency) {
        if (selectedCurrency?.code != newCurrency.code) {
            selectedCurrency = newCurrency
            restartRefreshing()
        }
    }

    fun stopRefreshList() {
        loadDataDisposable?.dispose()
    }

    fun setValue(value: String) {
        val newValue = value.toDoubleOrNull()
        newValue?.let {
            if (newValue != currentValue) {
                loadDataDisposable?.dispose()

                currentValue = newValue
                selectedCurrency = selectedCurrency?.run {
                    EntityCurrency(
                        code, name, flag, currentValue
                    )
                }
                updateListAndRestartRefreshing()
            }
        }
    }

    private fun updateListAndRestartRefreshing() {
        getData()
        restartRefreshing()
    }

    private fun getData() {
        loadDataDisposable?.dispose()
        loadDataDisposable = selectedCurrency?.let {
            val params = GetRatesListUseCase.Params(
                it,
                currentValue
            )
            getRatesListUseCase.run(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list ->
                    currencyList.postValue(list)
                }, {})
        }
    }

    private fun restartRefreshing() {
        loadDataDisposable?.dispose()
        loadDataDisposable = selectedCurrency?.let { currency ->
            val params = LoadRatesListUseCase.Params(currency, currentValue)
            val observable = loadRatesListUseCase.run(params)
            observable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    errorMessage.postValue(it.message ?: "Error")
                }
                .doOnSuccess {
                    errorMessage.postValue(null)
                    currencyList.postValue(it)
                }
                .retryWhen {
                    it.delay(API_RETRY_REQUEST_DELAY_SECOND, TimeUnit.SECONDS)
                }
                .repeatWhen {
                    it.delay(API_REQUEST_INTERVAL_SECOND, TimeUnit.SECONDS)
                }
                .subscribe()
        }
    }
}