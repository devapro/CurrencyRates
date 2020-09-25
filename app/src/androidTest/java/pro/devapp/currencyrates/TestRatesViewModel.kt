package pro.devapp.currencyrates

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import junit.framework.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.currencyrates.ui.MainActivity
import pro.devapp.currencyrates.ui.rates.RatesViewModel
import pro.devapp.currencyrates.usecases.GetCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase
import pro.devapp.storage.getCurrencyDetailsRepository
import pro.devapp.storage.getCurrencyRatesRepository
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TestRatesViewModel {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainActivityTestRule: ActivityTestRule<MainActivity> =
        ActivityTestRule(MainActivity::class.java, true, false)

    private val observerRatesList: Observer<List<EntityCurrency>> = mock()
    private val observerErrorMessages: Observer<String?> = mock()

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testRatesListLoading() {
        mainActivityTestRule.launchActivity(null)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = RatesViewModel(
            mainActivityTestRule.activity.application,
            GetRatesListUseCase(getCurrencyRatesRepository(appContext)),
            GetCurrencyByCodeUseCase(getCurrencyDetailsRepository(appContext))
        )

        viewModel.currencyList.observeForever(observerRatesList)
        viewModel.startRefreshList()
        Assert.assertTrue(
            "List currency isNotEmpty",
            viewModel.currencyList.getOrAwaitValue(5000, TimeUnit.MILLISECONDS).isNotEmpty()
        )
        viewModel.stopRefreshList()
    }

    @Test
    fun testRatesListLoadingError() {
        mainActivityTestRule.launchActivity(null)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = RatesViewModel(
            mainActivityTestRule.activity.application,
            GetRatesListUseCase(getCurrencyRatesRepository(appContext)),
            GetCurrencyByCodeUseCase(getCurrencyDetailsRepository(appContext))
        )

        viewModel.errorMessage.observeForever(observerErrorMessages)
        viewModel.setSelectedCurrency(
            EntityCurrency(
                "Invalid currency code",
                "test",
                null,
                1.00
            )
        )
        Assert.assertTrue(
            "Error message isNotEmpty",
            viewModel.errorMessage.getOrAwaitValue(5000, TimeUnit.MILLISECONDS)
                ?.isNotEmpty() == true
        )
        viewModel.stopRefreshList()
    }

    @Test
    fun testRatesListCalculationUpdate() {
        mainActivityTestRule.launchActivity(null)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = RatesViewModel(
            mainActivityTestRule.activity.application,
            GetRatesListUseCase(getCurrencyRatesRepository(appContext)),
            GetCurrencyByCodeUseCase(getCurrencyDetailsRepository(appContext))
        )

        viewModel.currencyList.observeForever(observerRatesList)
        viewModel.startRefreshList()
        Assert.assertTrue(
            "List currency isNotEmpty",
            viewModel.currencyList.getOrAwaitValue(5000, TimeUnit.MILLISECONDS).isNotEmpty()
        )
        viewModel.setValue("2")
        Assert.assertTrue(
            "List currency updated and isNotEmpty",
            viewModel.currencyList.getOrAwaitValue(5000, TimeUnit.MILLISECONDS).isNotEmpty()
        )
        viewModel.stopRefreshList()
    }

    @Test
    fun testRatesListCalculationWithIncorrectValueUpdate() {
        mainActivityTestRule.launchActivity(null)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = RatesViewModel(
            mainActivityTestRule.activity.application,
            GetRatesListUseCase(getCurrencyRatesRepository(appContext)),
            GetCurrencyByCodeUseCase(getCurrencyDetailsRepository(appContext))
        )

        viewModel.currencyList.observeForever(observerRatesList)
        viewModel.startRefreshList()
        Assert.assertTrue(
            "List currency isNotEmpty",
            viewModel.currencyList.getOrAwaitValue(5000, TimeUnit.MILLISECONDS).isNotEmpty()
        )
        viewModel.setValue("incorrect string")
        Assert.assertTrue(
            "List currency updated and isNotEmpty",
            viewModel.currencyList.getOrAwaitValue(5000, TimeUnit.MILLISECONDS).isNotEmpty()
        )
        viewModel.stopRefreshList()
    }


}