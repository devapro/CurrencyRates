package pro.devapp.currencyrates.usecases

interface BaseUseCase<T, in Params> {
    fun run(params: Params): T
}