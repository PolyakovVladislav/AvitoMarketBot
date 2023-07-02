package domain

import domain.models.Notebook

interface TelegramMessenger {

    fun sendMessage(notebook: Notebook, groupId: Long)

    fun sendException(e: Exception, groupId: Long)
}