package pro.devapp.currencyrates

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import pro.devapp.currencyrates.data.*
import pro.devapp.currencyrates.ui.rates.RatesViewModel
import pro.devapp.currencyrates.usecases.CreateCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase
import pro.devapp.currencyrates.usecases.LoadRatesListUseCase
import pro.devapp.currencyrates.utils.getOrAwaitValue

class RatesViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var createCurrencyByCodeUseCase: CreateCurrencyByCodeUseCase

    @Mock
    lateinit var loadRatesListUseCase: LoadRatesListUseCase

    @Mock
    lateinit var getRatesListUseCase: GetRatesListUseCase

    @Mock
    lateinit var application: Application

    private lateinit var viewModel: RatesViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        Mockito
            .`when`(loadRatesListUseCase.run(getLoadRatesListUseCaseParams()))
            .thenReturn(Single.just(getCurrencyRatesList()))

        Mockito
            .`when`(createCurrencyByCodeUseCase.run(getCreateCurrencyParams()))
            .thenReturn(getDefaultCurrencyEntity())

        viewModel = RatesViewModel(
            application,
            loadRatesListUseCase,
            getRatesListUseCase,
            createCurrencyByCodeUseCase
        )
    }

    @Test
    fun `should call CreateCurrencyByCodeUseCase after first start`() {
        val expectedCurrency = getDefaultCurrencyEntity()
        val expectedParams = getCreateCurrencyParams()
        Mockito
            .`when`(createCurrencyByCodeUseCase.run(expectedParams))
            .thenReturn(expectedCurrency)

        viewModel.startRefreshList()

        Mockito
            .verify(createCurrencyByCodeUseCase, Mockito.times(1))
            .run(expectedParams)
    }

    @Test
    fun `should load currency rates list`() {
        val expectedParams = getLoadRatesListUseCaseParams()
        val expectedCurrencyList = getCurrencyRatesList()
        Mockito
            .`when`(loadRatesListUseCase.run(expectedParams))
            .thenReturn(Single.just(expectedCurrencyList))

        viewModel.startRefreshList()

        Mockito
            .verify(loadRatesListUseCase, Mockito.times(1))
            .run(expectedParams)

        Assert.assertEquals(expectedCurrencyList, viewModel.currencyList.getOrAwaitValue())
    }

    @Test
    fun `should get currency list before api call after value update`() {
        val expectedLoadUseCaseParams = getLoadRatesListUseCaseParams(2.00)
        val expectedParams = getListUseCaseParams()
        val expectedCurrencyList = getCurrencyRatesList()

        Mockito
            .`when`(loadRatesListUseCase.run(expectedLoadUseCaseParams))
            .thenReturn(Single.just(expectedCurrencyList))

        Mockito
            .`when`(getRatesListUseCase.run(expectedParams))
            .thenReturn(Single.just(expectedCurrencyList))

        viewModel.startRefreshList()
        viewModel.setValue("2")

        val orderVerifier = Mockito.inOrder(getRatesListUseCase, loadRatesListUseCase)
        orderVerifier
            .verify(getRatesListUseCase, Mockito.times(1))
            .run(expectedParams)
        orderVerifier
            .verify(loadRatesListUseCase, Mockito.times(1))
            .run(expectedLoadUseCaseParams)

        Assert.assertEquals(expectedCurrencyList, viewModel.currencyList.getOrAwaitValue())
    }

    @Test
    fun `should stop load list`() {
        val expectedParams = getLoadRatesListUseCaseParams()
        val expectedCurrencyList = getCurrencyRatesList()
        Mockito
            .`when`(loadRatesListUseCase.run(expectedParams))
            .thenReturn(Single.just(expectedCurrencyList))

        val orderVerifier = Mockito.inOrder(loadRatesListUseCase)

        viewModel.startRefreshList()
        orderVerifier
            .verify(loadRatesListUseCase, Mockito.times(1))
            .run(expectedParams)

        viewModel.stopRefreshList()
        orderVerifier
            .verify(loadRatesListUseCase, Mockito.times(0))
            .run(expectedParams)
    }

    @Test
    fun `should set error message when api get error`() {
        val expectedParams = getLoadRatesListUseCaseParams()
        val expectedErrorMessage = "test"
        val expectedException = Throwable(expectedErrorMessage)
        Mockito
            .`when`(loadRatesListUseCase.run(expectedParams))
            .thenReturn(Single.error(expectedException))

        viewModel.startRefreshList()

        Assert.assertEquals(expectedErrorMessage, viewModel.errorMessage.getOrAwaitValue())
    }
}