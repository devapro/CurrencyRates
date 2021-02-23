package pro.devapp.currencyrates

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import pro.devapp.core.entities.EntityCurrency
import pro.devapp.currencyrates.usecases.CreateCurrencyByCodeUseCase
import pro.devapp.storage.repositories.CurrencyDetailsRepository

class CreateCurrencyByCodeUseCaseTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var currencyDetailsRepository: CurrencyDetailsRepository

    lateinit var createCurrencyByCodeUseCase: CreateCurrencyByCodeUseCase

    private val currencyCode = "USD"
    private val currencyFlag = 1
    private val currencyName = "name"

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

        Mockito
            .`when`(currencyDetailsRepository.getFlag(currencyCode))
            .thenReturn(currencyFlag)

        Mockito
            .`when`(currencyDetailsRepository.getName(currencyCode))
            .thenReturn(currencyName)

        createCurrencyByCodeUseCase = CreateCurrencyByCodeUseCase(currencyDetailsRepository)
    }

    @Test
    fun `should get info about currency`() {
        val expectedValue = 2.00
        val expectedInfo = EntityCurrency(currencyCode, currencyName, currencyFlag, expectedValue)
        val expectedParams = CreateCurrencyByCodeUseCase.Params(currencyCode, expectedValue)
        val actualCurrencyInfo = createCurrencyByCodeUseCase.run(expectedParams)
        Assert.assertEquals(expectedInfo, actualCurrencyInfo)
    }
}