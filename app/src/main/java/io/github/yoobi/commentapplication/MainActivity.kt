package io.github.yoobi.commentapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

data class ShortComment(
    val id: Int,
    val date: String,
    val author: String,
    val message: String,
    val level: Int = 1,
    val replies: MutableList<ShortComment> = mutableListOf(),
    val parentId: Int? = null
)

fun Calendar.toSimpleDate(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    return formatter.format(time)
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnShow = findViewById<Button>(R.id.btn_comment)
        val btnAddComment = findViewById<Button>(R.id.btn_add_comment)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_comment)

        val viewModel: MainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val commentAdapter = CommentAdapter(CommentAdapter.CommentClickListener { author, message, level, parentId ->
                val id = (viewModel.commentList.value?.size ?: 0) + 1
                viewModel.addComment(ShortComment(id,"15/05/2021 at 12:30", author, message, level, parentId = parentId))
            })

        btnShow.text = "Show Comments " + (viewModel.commentList.value?.size ?: 0)
        btnShow.setOnClickListener {
            recyclerView.isVisible = !recyclerView.isVisible
            btnAddComment.isVisible = !btnAddComment.isVisible
            btnShow.text = if(recyclerView.isVisible) "Hide Comments" else "Show Comments " + (viewModel.commentList.value?.size ?: 0)
        }

        recyclerView.adapter = commentAdapter

        viewModel.commentList.observe(this) {
            commentAdapter.submitList(it.toMutableList())
        }

        btnAddComment.setOnClickListener {
            val id = (viewModel.commentList.value?.size ?: 0) + 1
            viewModel.addComment(ShortComment(id, Calendar.getInstance(Locale.ROOT).toSimpleDate(), "test", "message$id"))
        }

    }
}
