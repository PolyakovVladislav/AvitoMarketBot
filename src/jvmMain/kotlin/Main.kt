import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import bot.BotImplementation
import data.AvitoRepositoryImpl
import data.ConfigurationsRepositoryImpl
import data.source.local.ConfigurationsLocalDataSource
import data.source.local.NotebookHistoryLocalDataSource
import data.source.remote.NotebookRemoteDataSource
import domain.models.Instruction
import domain.models.instructions.ScanInstruction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import messaging.TelegramMessengerImpl
import ui.App

val configurationsDataSource = ConfigurationsLocalDataSource()
val notebookDataSource = NotebookRemoteDataSource()
val notebookHistoryDataSource = NotebookHistoryLocalDataSource()

var appScope = CloseableCoroutineScope(SupervisorJob() + Dispatchers.IO)
val avitoRepository = AvitoRepositoryImpl(
    notebookDataSource,
    notebookHistoryDataSource,
    configurationsDataSource
)
val configs = ConfigurationsRepositoryImpl(configurationsDataSource, notebookHistoryDataSource)
val telegramMessenger = TelegramMessengerImpl()

val bot: BotImplementation = BotImplementation(appScope, ::onInstructionTimeout)


@ExperimentalUnitApi
@ExperimentalComposeUiApi
fun main() = application {

    Window(
        onCloseRequest = {
            appScope.launch {
                bot.stop(true)
                avitoRepository.closeWebDriver()
            }
            exitApplication()
        },
        title = "AvitoBot",
        state = rememberWindowState(
            position = WindowPosition(alignment = Alignment.Center),
            size = DpSize(1280.dp, 800.dp)
        )
    ) {
        App(
            avitoRepository,
            configs, {
                bot.add(
                    ScanInstruction(
                        0,
                        0,
                        0,
                        0,
                        270_000,
                        ::onInstructionExecuted,
                        "Scan avito for fresh posts",
                        avitoRepository,
                        configs,
                        telegramMessenger,
                    )
                )
                bot.start()
            },
            { cancelExecution ->
                bot.stop(cancelExecution)
                bot.remove(0)
            }
        )
    }
}

private fun onInstructionTimeout(e: Exception) {
    appScope.launch {
        delay(15000)
        bot.start()
    }
}

private fun onInstructionExecuted(instruction: Instruction) {
    instruction.executionTime = System.currentTimeMillis() + configs.refreshTime
    bot.add(instruction)
}
