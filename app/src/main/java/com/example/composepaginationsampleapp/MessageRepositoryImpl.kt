package com.example.composepaginationsampleapp

import com.example.composepaginationsampleapp.data.MessageData
import kotlinx.coroutines.delay

class MessageRepositoryImpl {

    private val list = mutableListOf<MessageData>()

    init {
        repeat(101) {
            list.add(MessageData(id = it.toLong(), text = "Message $it"))
        }
        list
    }

    suspend fun getMessages(since: Int, upTo: Int): List<MessageData> {
        delay(1500)
        val newDataset = mutableListOf<MessageData>()
        if (list.lastIndex < upTo) {
            newDataset.addAll(list.subList(since, list.lastIndex))
        } else {
            newDataset.addAll(list.subList(since, upTo))
        }
        return newDataset
    }

    fun sendNewMessage(onNewMessageSent: () -> Unit) {
        list.add(0, MessageData(id = list.size.toLong(), text = "- New msg added ${list.size.toLong()}"))
        onNewMessageSent()
    }
}