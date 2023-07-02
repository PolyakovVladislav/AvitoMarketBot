package messaging

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.TelegramFile
import domain.TelegramMessenger
import domain.exceptions.NotebookDetailsParsingException
import domain.models.Notebook

class TelegramMessengerImpl : TelegramMessenger {

    private val bot by lazy(LazyThreadSafetyMode.NONE) {
        bot {
            token = "5621917835:AAGjgm6ieYpC-0llZMeXRtpRUGDO0VSgdjk"
        }
    }

    override fun sendMessage(notebook: Notebook, groupId: Long) {
        bot.sendMessage(chatId = ChatId.fromId(groupId), text = notebook.url)
    }

    override fun sendException(e: Exception, groupId: Long) {
        when (e) {
            is NotebookDetailsParsingException -> {
                val caption = "Error occurred parsing ${e.notebook}\nReason: ${e.reason}"
                val documentBody = e.pageHtml
                val documentName = "html_source.txt"
                sendDocument(groupId, documentBody, documentName, caption)
            }

            else -> {
                val stacktrace = e.stackTrace.filterIndexed { index, _ ->
                    index <= 50
                }.joinToString("   \n")
                val documentBody = "$e: + \n" +
                        "Stacktrace: $stacktrace"
                val documentName = "unknown_exception.txt"
                sendDocument(groupId, documentBody, documentName, e.message ?: "Message is null")
            }
        }
    }

    private fun sendDocument(groupId: Long, documentBody: String, documentName: String, caption: String) {
        bot.sendDocument(
            ChatId.fromId(groupId),
            TelegramFile.ByByteArray(documentBody.toByteArray(), documentName),
            caption
        )
    }
}
