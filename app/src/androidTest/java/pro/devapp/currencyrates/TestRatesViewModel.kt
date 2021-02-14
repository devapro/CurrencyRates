package pro.devapp.currencyrates

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.currencyrates.ui.MainActivity
import pro.devapp.currencyrates.ui.rates.RatesViewModel
import pro.devapp.currencyrates.usecases.CreateCurrencyByCodeUseCase
import pro.devapp.currencyrates.usecases.GetRatesListUseCase
import pro.devapp.storage.Storage.getCurrencyDetailsRepository
import pro.devapp.storage.Storage.getCurrencyRatesRepository

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
            CreateCurrencyByCodeUseCase(getCurrencyDetailsRepository(appContext))
        )


        viewModel.startRefreshList()
        viewModel.currencyList
            .test()
            .awaitCount(1)
            .assertValue { it.isNotEmpty() }
        viewModel.stopRefreshList()
    }

    @Test
    fun testRatesListLoadingError() {
        mainActivityTestRule.launchActivity(null)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = RatesViewModel(
            mainActivityTestRule.activity.application,
            GetRatesListUseCase(getCurrencyRatesRepository(appContext)),
            CreateCurrencyByCodeUseCase(getCurrencyDetailsRepository(appContext))
        )

        viewModel.startRefreshList()
        viewModel.setSelectedCurrency(
            EntityCurrency(
                "InvalidCode",
                "test",
                null,
                0.00
            )
        )
        viewModel.errorMessage.test().awaitCount(1).assertValue { it.isNotEmpty() }
        viewModel.stopRefreshList()
    }

    @Test
    fun testRatesListCalculationUpdate() {
        mainActivityTestRule.launchActivity(null)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = RatesViewModel(
            mainActivityTestRule.activity.application,
            GetRatesListUseCase(getCurrencyRatesRepository(appContext)),
            CreateCurrencyByCodeUseCase(getCurrencyDetailsRepository(appContext))
        )

        viewModel.startRefreshList()
        viewModel.currencyList
            .test()
            .awaitCount(1)
            .assertValue { it.isNotEmpty() }
        viewModel.setValue("2")
        viewModel.currencyList
            .test()
            .awaitCount(1)
            .assertValue { it.isNotEmpty() }
        viewModel.stopRefreshList()
    }

    @Test
    fun testRatesListCalculationWithIncorrectValueUpdate() {
        mainActivityTestRule.launchActivity(null)
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = RatesViewModel(
            mainActivityTestRule.activity.application,
            GetRatesListUseCase(getCurrencyRatesRepository(appContext)),
            CreateCurrencyByCodeUseCase(getCurrencyDetailsRepository(appContext))
        )

        viewModel.startRefreshList()
        viewModel.currencyList
            .test()
            .awaitCount(1)
            .assertValue { it.isNotEmpty() }
        viewModel.setValue("incorrect string")
        viewModel.currencyList
            .test()
            .awaitCount(1)
            .assertValue { it.isNotEmpty() }
        viewModel.stopRefreshList()
    }


}