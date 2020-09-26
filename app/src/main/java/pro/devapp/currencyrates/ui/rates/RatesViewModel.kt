package pro.devapp.currencyrates.ui.rates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.core.entities.ResultEntity
import pro.devapp.currencyrates.usecases.CreateCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase

class RatesViewModel(
    application: Application,
    private val getRatesListUseCase: GetRatesListUseCase,
    private val createCurrencyByCodeUseCase: CreateCurrencyByCodeUseCase
) : AndroidViewModel(application) {
    companion object {
        const val DEFAULT_CURRENCY_CODE = "EUR"
    }

    val currencyList = MutableLiveData<List<EntityCurrency>>()
    val errorMessage = MutableLiveData<String?>()

    private var loadDataJob: Job? = null
    private var lastSelectedCurrency: EntityCurrency? = null
    private var currentValue = 1.00

    fun startRefreshList() {
        lastSelectedCurrency?.apply { loadData(this) } ?: run {
            viewModelScope.launch {
                val params = CreateCurrencyByCodeUseCase.Params(DEFAULT_CURRENCY_CODE, currentValue)
                val selectedCurrency = createCurrencyByCodeUseCase.run(params)
                lastSelectedCurrency = selectedCurrency
                loadData(selectedCurrency)
            }
        }
    }

    fun setSelectedCurrency(selectedCurrency: EntityCurrency) {
        if (lastSelectedCurrency?.code != selectedCurrency.code) {
            lastSelectedCurrency = selectedCurrency
            loadData(selectedCurrency)
        }
    }

    fun stopRefreshList() {
        loadDataJob?.cancel()
    }

    fun setValue(value: String) {
        val newValue = value.toDoubleOrNull() ?: 0.00
        if (newValue != currentValue) {
            loadDataJob?.cancelChildren()

            currentValue = newValue

            currencyList.value?.map { itemCurrency ->
                if (itemCurrency.code != lastSelectedCurrency?.code) {
                    EntityCurrency(
                        itemCurrency.code,
                        itemCurrency.name,
                        itemCurrency.flag,
                        itemCurrency.rate * currentValue
                    )
                } else {
                    itemCurrency
                }
            }?.let { updatedList ->
                currencyList.postValue(updatedList)
            }

            if (currentValue > 0) {
                lastSelectedCurrency = lastSelectedCurrency?.run {
                    EntityCurrency(
                        code, name, flag, currentValue
                    )
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
                when (val result = getRatesListUseCase.run(params)) {
                    is ResultEntity.Success -> {
                        errorMessage.postValue(null)
                        currencyList.postValue(result.value)
                        delay(1000)
                    }
                    is ResultEntity.Error -> {
                        errorMessage.postValue(result.cause?.message)
                        delay(10000)
                    }
                }
            }
        }
    }
}