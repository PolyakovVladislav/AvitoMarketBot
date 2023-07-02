package utils.useCases

abstract class NoneParamsUseCase<out Type : Any> : UseCase<Type, UseCase.None>()
