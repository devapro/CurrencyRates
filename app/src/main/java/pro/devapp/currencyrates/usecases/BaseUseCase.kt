package pro.devapp.currencyrates.usecases

/**
 * Base class that describe UseCase signature
 */
interface BaseUseCase<T, in Params> {
    fun run(params: Params): T
}