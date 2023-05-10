package com.example.composepaginationsampleapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.example.composepaginationsampleapp.MessagePagingSource.Companion.PAGE_SIZE
import com.example.composepaginationsampleapp.data.MessageData

class MessageViewModel: ViewModel() {

    private val repository: MessageRepositoryImpl = MessageRepositoryImpl()

    var pagingSource: PagingSource<Int, MessageData>? = null

    fun sendNewMessage(onNewMessageSent: () -> Unit) {
        Log.d(TAG, "scroll.issue sendNewMessage")
        repository.sendNewMessage(onNewMessageSent)
    }

    val messages = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            pagingSource = MessagePagingSource(
                repository = repository
            )
            pagingSource!!
        }
    )
        .flow

    companion object {
        private val TAG = MessageViewModel::class.java.simpleName
    }

}