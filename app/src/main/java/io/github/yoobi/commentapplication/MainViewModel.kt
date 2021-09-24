package io.github.yoobi.commentapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private val nestedComment = mutableListOf(
        ShortComment(5, "15/05/2021 at 12:25", "Anonymous5", "NestedComment1", 2, parentId = 1),
        ShortComment(6,"15/05/2021 at 12:26", "Anonymous6", "NestedComment2", 2, parentId = 1)
    )

    private val _commentList = MutableLiveData(mutableListOf(
        ShortComment(1, "15/05/2021 at 12:20", "Anonymous1", "Comment1", replies = nestedComment),
        ShortComment(2, "15/05/2021 at 12:22", "Anonymous2", "Comment2"),
        ShortComment(3, "15/05/2021 at 12:23", "Anonymous3", "Comment3"),
        ShortComment(4, "15/05/2021 at 12:24", "Anonymous4", "Comment4"),
    ))
    val commentList: LiveData<MutableList<ShortComment>>
        get() = _commentList

    fun addComment(shortComment: ShortComment) {
        val newList = _commentList.value ?: mutableListOf()
        if(shortComment.parentId != null) {
            newList.find { it.id == shortComment.parentId }?.replies?.add(shortComment)
        } else {
            newList.add(shortComment)
        }
        _commentList.value = newList
    }
}