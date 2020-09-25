package pro.devapp.currencyrates.ui.rates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.currencyrates.usecases.GetCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase

class RatesViewModel(
    application: Application,
    private val getRatesListUseCase: GetRatesListUseCase,
    private val getCurrencyByCodeUseCase: GetCurrencyByCodeUseCase
) : AndroidViewModel(application) {
    companion object {
        const val DEFAULT_CURRENCY_CODE = "EUR"
    }

    val currencyList = MutableLiveData<List<EntityCurrency>>()
    val errorMessage = MutableLiveData<String?>()

    private var loadDataJob: Job? = null
    private var lastSelectedCurrency: EntityCurrency? = null
    private var currentValue = 1.00

    fun startRefreshList(selectedCurrencyCode: String) {
        viewModelScope.launch {
            val params = GetCurrencyByCodeUseCase.Params(selectedCurrencyCode, currentValue)
            val selectedCurrency = getCurrencyByCodeUseCase.run(params)
            lastSelectedCurrency = selectedCurrency
            loadData(selectedCurrency)
        }
    }

    fun setSelectedCurrency(selectedCurrency: EntityCurrency) {
        if (lastSelectedCurrency?.code != selectedCurrency.code) {
            lastSelectedCurrency = selectedCurrency
            loadData(selectedCurrency)
        }
    }

    fun stopRefreshList() {
        loadDataJob?.cancelChildren()
    }

    fun setValue(value: String) {
        val newValue = value.toDoubleOrNull()
        newValue?.let {
            if (newValue != currentValue) {
                loadDataJob?.cancelChildren()

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
                    currencyList.postValue(it)
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
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch {
            while (isActive) {
                val params = GetRatesListUseCase.Params(selectedCurrency, currentValue)
                val result = getRatesListUseCase.run(params)
                result.getOrNull()?.let { currencyRates ->
                    errorMessage.postValue(null)
                    currencyList.postValue(currencyRates)
                    delay(1000)
                } ?: run {
                    errorMessage.postValue(result.exceptionOrNull()?.message)
                    delay(10000)
                }
            }
        }
    }
}