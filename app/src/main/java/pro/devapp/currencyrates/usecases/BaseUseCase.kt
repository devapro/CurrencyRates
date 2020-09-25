package pro.devapp.currencyrates.usecases

interface BaseUseCase<T, in Params> {
    suspend fun run(params: Params): T
}