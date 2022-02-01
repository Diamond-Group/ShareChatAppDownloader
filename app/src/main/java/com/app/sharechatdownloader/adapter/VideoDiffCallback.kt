package com.app.sharechatdownloader.adapter

import androidx.recyclerview.widget.DiffUtil
import com.app.sharechatdownloader.model.Video

class VideoDiffCallback(
    private val oldVideoList: List<Video>,
    private val newVideoList: List<Video>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldVideoList.size
    }

    override fun getNewListSize(): Int {
        return newVideoList.size
    }


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldVideoList[oldItemPosition].id == newVideoList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldVideoList[oldItemPosition].id != newVideoList[newItemPosition].id -> false

            oldVideoList[oldItemPosition].title != newVideoList[newItemPosition].title -> false
            else -> true
        }
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}