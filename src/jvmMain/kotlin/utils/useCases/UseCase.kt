package utils.useCases

abstract class UseCase<out Type : Any, in Params : UseCase.Params> {

    interface Params

    object None : Params
}
