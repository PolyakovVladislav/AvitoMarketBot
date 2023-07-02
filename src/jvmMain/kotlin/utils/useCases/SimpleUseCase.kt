package utils.useCases

abstract class SimpleUseCase<out Type : Any,
    in Params : UseCase.Params> : UseCase<Type, Params>() {

    abstract fun run(params: Params): Type

    operator fun invoke(params: Params): Type {
        return run(params)
    }
}
