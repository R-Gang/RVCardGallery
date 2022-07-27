package com.gang.rvcardgallery.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.gang.gallery.CardAdapterHelper
import com.gang.library.common.utils.showToast
import com.gang.rvcardgallery.R
import java.util.*

/**
 * Created by haoruigang on 2021-12-20.
 */
internal class CardAdapter(mList: List<Int>) : RecyclerView.Adapter<CardAdapter.ViewHolder?>() {
    private var mList: List<Int> = ArrayList()
    private val mCardAdapterHelper: CardAdapterHelper = CardAdapterHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.view_card_item, parent, false)
        mCardAdapterHelper.onCreateViewHolder(parent, itemView)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount())
        holder.mImageView.setImageResource(mList[position])
        holder.mImageView.setOnClickListener {
            showToast("" + position)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    open inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView: ImageView

        init {
            mImageView = itemView.findViewById<View>(R.id.imageView) as ImageView
        }
    }

    init {
        this.mList = mList
    }
}