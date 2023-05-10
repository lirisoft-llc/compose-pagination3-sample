package com.example.composepaginationsampleapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.composepaginationsampleapp.data.MessageData
import com.theozgurr.composesamplepaginationapp.ui.EmptyItem
import com.theozgurr.composesamplepaginationapp.ui.PaginationErrorItem
import com.theozgurr.composesamplepaginationapp.ui.PaginationLoadingItem
import com.theozgurr.composesamplepaginationapp.ui.PaginationRetryItem
import kotlinx.coroutines.launch

@Composable
fun MessageScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = MessageViewModel()
    val users = viewModel.messages.collectAsLazyPagingItems()
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column {
        Row {
            Button(onClick = {
                if (!scrollState.isScrollInProgress) {
                    viewModel.sendNewMessage(onNewMessageSent = {
                        viewModel.pagingSource!!.invalidate()
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    })
                }
            }) {
                Text(text = "Send new message")
            }

            Button(modifier = Modifier.padding(start = 25.dp), onClick = {
                if (!scrollState.isScrollInProgress) {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                }
            }) {
                Text(text = "Scroll to bottom")
            }
        }

        UsersScreen(
            modifier = modifier.padding(top = 50.dp),
            users = users,
            scrollState = scrollState
        )
    }
}

@Composable
fun UsersScreen(
    modifier: Modifier = Modifier,
    users: LazyPagingItems<MessageData>,
    scrollState: LazyListState
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        reverseLayout = true,
        state = scrollState,
        verticalArrangement = if (users.itemCount < 1)
            Arrangement.Center
        else
            Arrangement.Top
    ) {
        when (users.loadState.refresh) {
            LoadState.Loading -> {
                item {
                    PaginationLoadingItem(circularProgressSize = 64.dp)
                }
            }
            is LoadState.Error -> {
                item {
                    PaginationErrorItem {
                        users.refresh()
                    }
                }
            }
            is LoadState.NotLoading -> {
                if (users.itemCount < 1) {
                    item {
                        EmptyItem()
                    }
                }
            }
        }
        items(items = users, key = {
            it.id.toString()
        }) { user ->
            UserItem(user = user)
        }
        when (users.loadState.append) {
            LoadState.Loading -> {
                item {
                    PaginationLoadingItem()
                }
            }
            is LoadState.Error -> {
                item {
                    PaginationRetryItem {
                        users.retry()
                    }
                }
            }
            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
fun UserItem(
    modifier: Modifier = Modifier,
    user: MessageData?
) {
    Text(
        modifier = modifier.padding(all = 32.dp),
        text = user?.text ?: ""
    )
}
