package pro.devapp.currencyrates

import android.app.Application
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.currencyrates.ui.rates.RatesViewModel
import pro.devapp.currencyrates.usecases.CreateCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase
import pro.devapp.storage.repositories.CurrencyRatesRepository

class RatesViewModelTest {

    @Mock
    lateinit var getCurrencyByCodeUseCase: CreateCurrencyByCodeUseCase

    @Mock
    lateinit var currencyRatesRepository: CurrencyRatesRepository

    @Mock
    lateinit var application: Application

    private val currency = EntityCurrency(RatesViewModel.DEFAULT_CURRENCY_CODE, "", null, 0.00)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun getCurrencyList() {
        Mockito
            .`when`(currencyRatesRepository.getCurrencyRates(RatesViewModel.DEFAULT_CURRENCY_CODE))
            .thenReturn(Single.just<List<EntityCurrency>>(listOf(currency)))
        val getRatesListUseCase = GetRatesListUseCase(currencyRatesRepository)
        val viewModel = RatesViewModel(application, getRatesListUseCase, getCurrencyByCodeUseCase)

        viewModel.currencyList.onNext(listOf(currency))

        viewModel.currencyList.test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue {
                it.isNotEmpty()
            }

        viewModel.stopRefreshList()
    }

    @Test
    fun getErrorMessage() {
        val testException = Exception("Some test exception")
        Mockito
            .`when`(currencyRatesRepository.getCurrencyRates(RatesViewModel.DEFAULT_CURRENCY_CODE))
            .thenReturn(Single.error(testException))

        val getRatesListUseCase = GetRatesListUseCase(currencyRatesRepository)

        val viewModel = RatesViewModel(application, getRatesListUseCase, getCurrencyByCodeUseCase)
        viewModel.setSelectedCurrency(currency)

        viewModel.errorMessage.test()
            .awaitCount(1)
            .assertValue {
                it.isNotEmpty()
            }
            .assertValue {
                it == testException.message
            }

        viewModel.stopRefreshList()
    }

    @Test
    fun startRefreshList() {
        val currency = EntityCurrency(RatesViewModel.DEFAULT_CURRENCY_CODE, "", null, 0.00)
        val currencyRatesRepository = Mockito.mock(CurrencyRatesRepository::class.java)
        Mockito
            .`when`(currencyRatesRepository.getCurrencyRates(RatesViewModel.DEFAULT_CURRENCY_CODE))
            .thenReturn(
                Single.just<List<EntityCurrency>>(
                    listOf(
                        EntityCurrency(
                            RatesViewModel.DEFAULT_CURRENCY_CODE,
                            "",
                            null,
                            0.00
                        )
                    )
                )
            )

        Mockito
            .`when`(
                getCurrencyByCodeUseCase.run(
                    CreateCurrencyByCodeUseCase.Params(
                        RatesViewModel.DEFAULT_CURRENCY_CODE,
                        1.00
                    )
                )
            )
            .thenReturn(currency)

        val getRatesListUseCase = GetRatesListUseCase(currencyRatesRepository)

        val viewModel = RatesViewModel(application, getRatesListUseCase, getCurrencyByCodeUseCase)
        viewModel.startRefreshList()
        viewModel.currencyList.test()
            .awaitCount(3)
            .assertNoErrors()
            .assertValueAt(0) {
                it.isNotEmpty()
            }

        viewModel.errorMessage.test()
            .awaitCount(1)
            .assertValueAt(0) {
                it.isEmpty()
            }

        viewModel.stopRefreshList()
    }

    @Test
    fun setSelectedCurrency() {
        Mockito
            .`when`(currencyRatesRepository.getCurrencyRates(RatesViewModel.DEFAULT_CURRENCY_CODE))
            .thenReturn(Single.just<List<EntityCurrency>>(listOf(currency)))

        val getRatesListUseCase = GetRatesListUseCase(currencyRatesRepository)

        val viewModel = RatesViewModel(application, getRatesListUseCase, getCurrencyByCodeUseCase)
        viewModel.setSelectedCurrency(currency)
        viewModel.currencyList.test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue {
                it.isNotEmpty()
            }

        viewModel.errorMessage.test()
            .awaitCount(1)
            .assertValue {
                it.isEmpty()
            }

        viewModel.stopRefreshList()
    }

    @Test
    fun stopRefreshList() {
        Mockito
            .`when`(currencyRatesRepository.getCurrencyRates(RatesViewModel.DEFAULT_CURRENCY_CODE))
            .thenReturn(Single.just<List<EntityCurrency>>(listOf(currency)))

        Mockito
            .`when`(
                getCurrencyByCodeUseCase.run(
                    CreateCurrencyByCodeUseCase.Params(
                        RatesViewModel.DEFAULT_CURRENCY_CODE,
                        1.00
                    )
                )
            )
            .thenReturn(currency)

        val getRatesListUseCase = GetRatesListUseCase(currencyRatesRepository)

        val viewModel = RatesViewModel(application, getRatesListUseCase, getCurrencyByCodeUseCase)
        viewModel.startRefreshList()
        viewModel.currencyList.test()
            .awaitCount(3)
            .assertNoErrors()
            .assertValueAt(0) {
                it.isNotEmpty()
            }

        viewModel.errorMessage.test()
            .awaitCount(1)
            .assertValueAt(0) {
                it.isEmpty()
            }

        viewModel.stopRefreshList()

        viewModel.currencyList.test()
            .awaitCount(1)

        viewModel.errorMessage.test()
            .awaitCount(1)
    }

    @Test
    fun setValue() {

        Mockito
            .`when`(currencyRatesRepository.getCurrencyRates(RatesViewModel.DEFAULT_CURRENCY_CODE))
            .thenReturn(Single.just<List<EntityCurrency>>(listOf(currency)))

        Mockito
            .`when`(
                getCurrencyByCodeUseCase.run(
                    CreateCurrencyByCodeUseCase.Params(
                        RatesViewModel.DEFAULT_CURRENCY_CODE,
                        1.00
                    )
                )
            )
            .thenReturn(currency)

        val getRatesListUseCase = GetRatesListUseCase(currencyRatesRepository)

        val viewModel = RatesViewModel(application, getRatesListUseCase, getCurrencyByCodeUseCase)
        viewModel.startRefreshList()
        viewModel.setValue("2.00")
        viewModel.currencyList.test()
            .awaitCount(3)
            .assertNoErrors()
            .assertValueAt(0) {
                it[0].rate == 2.00
            }

        viewModel.errorMessage.test()
            .awaitCount(1)
            .assertValueAt(0) {
                it.isEmpty()
            }

        viewModel.stopRefreshList()
    }
}