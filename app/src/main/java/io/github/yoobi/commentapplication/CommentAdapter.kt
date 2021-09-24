package io.github.yoobi.commentapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.yoobi.commentapplication.databinding.ItemCommentBinding

class CommentAdapter(private val commentListener: CommentClickListener)
    : ListAdapter<ShortComment, CommentAdapter.CommentViewHolder>(ShortCommentDiffUtil) {

    private val listShowReplies = arrayListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {

        private fun initBinding(binding: ItemCommentBinding, shortComment: ShortComment) {
            binding.tvComment.text = shortComment.message
            binding.tvDate.text = "${shortComment.date} - ${shortComment.author}"
            if(shortComment.replies.isNotEmpty() && shortComment.level == 1)
                createNestedComment(binding, shortComment)
            binding.btnReply.setOnClickListener {
                val dialogCommentView = LayoutInflater.from(it.context).inflate(R.layout.dialog_comment, null, false)
                MaterialAlertDialogBuilder(it.context)
                    .setTitle("Post a comment")
                    .setView(dialogCommentView)
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Send") { _,_ ->
                        val author: String = dialogCommentView.findViewById<EditText>(R.id.ti_author).text.toString()
                        val message: String = dialogCommentView.findViewById<EditText>(R.id.ti_message).text.toString()
                        commentListener.onSendComment(
                            author,
                            message,
                            shortComment.level + 1,
                            shortComment.parentId ?: shortComment.id
                        )
                    }
                    .show()
            }
        }

        fun bind(shortComment: ShortComment) {
            initBinding(binding, shortComment)
        }

        private fun createNestedComment(binding: ItemCommentBinding, shortComment: ShortComment) {
            binding.btnMore.isVisible = true
            binding.btnMore.text = "Show ${shortComment.replies.size} replies"
            binding.btnMore.setOnClickListener {
                if(!listShowReplies.contains(shortComment.id)) {
                    listShowReplies.add(shortComment.id)
                    binding.btnMore.text = "Hide replies"
                    shortComment.replies.forEach { nestedComment ->
                        val newComment = ItemCommentBinding.inflate(LayoutInflater.from(binding.root.context), null, false)
                        initBinding(newComment, nestedComment)
                        binding.llReplies.addView(newComment.root)
                    }
                    binding.llReplies.isVisible = true
                } else {
                    binding.btnMore.text = "Show ${shortComment.replies.size} replies"
                    listShowReplies.remove(shortComment.id)
                    binding.llReplies.removeAllViews()
                    binding.llReplies.isVisible = false
                }
            }
        }
    }

    object ShortCommentDiffUtil: DiffUtil.ItemCallback<ShortComment>() {

        override fun areItemsTheSame(oldItem: ShortComment, newItem: ShortComment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShortComment, newItem: ShortComment): Boolean {
            return oldItem == newItem
        }
    }

    open class CommentClickListener(
        private val sendComment: (author: String, message: String, level: Int, parentId: Int?) -> Unit
    ) {
        fun onSendComment(author: String, message: String, level: Int, parentId: Int? = null) =
            sendComment(author, message, level, parentId)
    }
}