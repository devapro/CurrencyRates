package pro.devapp.currencyrates.ui.rates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.currencyrates.usecases.GetCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase
import java.util.concurrent.TimeUnit

class RatesViewModel(
    application: Application,
    private val getRatesListUseCase: GetRatesListUseCase,
    private val getCurrencyByCodeUseCase: GetCurrencyByCodeUseCase
) : AndroidViewModel(application) {
    companion object {
        const val DEFAULT_CURRENCY_CODE = "EUR"
    }

    val currencyList = ReplaySubject.createWithSize<List<EntityCurrency>>(1)
    val errorMessage = ReplaySubject.create<String>()

    private var loadDataDisposable: Disposable? = null
    private var lastSelectedCurrency: EntityCurrency? = null
    private var currentValue = 1.00

    fun startRefreshList(selectedCurrencyCode: String) {
        val params = GetCurrencyByCodeUseCase.Params(selectedCurrencyCode, currentValue)
        val selectedCurrency = getCurrencyByCodeUseCase.run(params)
        lastSelectedCurrency = selectedCurrency
        loadData(selectedCurrency)
    }

    fun setSelectedCurrency(selectedCurrency: EntityCurrency) {
        if (lastSelectedCurrency?.code != selectedCurrency.code) {
            lastSelectedCurrency = selectedCurrency
            loadData(selectedCurrency)
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
                lastSelectedCurrency = lastSelectedCurrency?.run {
                    EntityCurrency(
                        code, name, flag, currentValue
                    )
                }

                currencyList.value?.map {
                    if (it.code != lastSelectedCurrency?.code) {
                        EntityCurrency(
                            it.code,
                            it.name,
                            it.flag,
                            it.rate * currentValue
                        )
                    } else {
                        it
                    }
                }?.let {
                    currencyList.onNext(it)
                }

                lastSelectedCurrency?.apply { loadData(this) }
            }
        }
    }

    /**
     * Start get data from server every 1s
     * If exception retry after 10s
     */
    private fun loadData(selectedCurrency: EntityCurrency) {
        loadDataDisposable?.dispose()
        val params = GetRatesListUseCase.Params(selectedCurrency, currentValue)
        loadDataDisposable = getRatesListUseCase
            .run(params)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                errorMessage.onNext(it.message ?: "Error")
            }
            .doOnSuccess {
                errorMessage.onNext("")
                currencyList.onNext(it)
            }
            .retryWhen {
                it.delay(10, TimeUnit.SECONDS)
            }
            .repeatWhen {
                it.delay(1, TimeUnit.SECONDS)
            }
            .subscribe()
    }
}