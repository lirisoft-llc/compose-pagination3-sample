package com.example.composepaginationsampleapp

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composepaginationsampleapp.data.MessageData

class MessagePagingSource(private val repository: MessageRepositoryImpl) :
    PagingSource<Int, MessageData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageData> =
        try {
            val currentPage = params.key ?: STARTING_PAGE_INDEX
            val loadSize = params.loadSize
            val offset = currentPage * loadSize
            val upTo = offset + PAGE_SIZE
            val response = repository.getMessages(since = offset, upTo = upTo)
            val nextPage =
                if (response.size == loadSize) {
                    currentPage + (loadSize / PAGE_SIZE)
                } else {
                    null
                }

            val msg = "nextPage = $nextPage, loadSize = $loadSize, offset: $offset, upTo: $upTo response size: ${response.size}"
            Log.d(TAG, "scroll.issue currentPage=$currentPage, $msg")
            LoadResult.Page(
                itemsBefore = currentPage * loadSize,
                data = response,
                prevKey = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            Log.e(TAG, "scroll.issue error: ${e.message}")
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, MessageData>): Int? {
        val refreshKey = state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
        Log.d(TAG, "scroll.issue getRefreshKey= anchorPage $${state.anchorPosition}, refreshKey: $refreshKey")
        return refreshKey
    }

    override val jumpingSupported: Boolean
        get() = true

    companion object {
        const val PAGE_SIZE = 20
        private const val STARTING_PAGE_INDEX = 0
        private val TAG = MessagePagingSource::class.java.simpleName
    }
}