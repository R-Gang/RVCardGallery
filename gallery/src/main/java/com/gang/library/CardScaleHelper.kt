package com.gang.library

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gang.library.common.utils.LogUtil
import com.gang.library.common.utils.dip2px

/**
 * Created by haoruigang on 2021-12-20.
 */
class CardScaleHelper {
    private var mRecyclerView: RecyclerView? = null
    private var mContext: Context? = null
    var mScale = 0.9f // 两边视图scale
    var mPagePadding = 15 // 卡片的padding, 卡片间的距离等于2倍的mPagePadding
    var mShowLeftCardWidth = 15 // 左边卡片显示大小
    private var mCardWidth // 卡片宽度
            = 0
    private var mOnePageWidth // 滑动一页的距离
            = 0
    private var mCardGalleryWidth = 0
    var currentItemPos = 0
    private var mCurrentItemOffset = 0
    private val mLinearSnapHelper = CardLinearSnapHelper()
    fun attachToRecyclerView(mRecyclerView: RecyclerView) {
        this.mRecyclerView = mRecyclerView
        mContext = mRecyclerView.context
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mLinearSnapHelper.mNoNeedToScroll =
                        mCurrentItemOffset == 0 || mCurrentItemOffset == getDestItemOffset(
                            mRecyclerView.getAdapter()?.itemCount!! - 1)
                } else {
                    mLinearSnapHelper.mNoNeedToScroll = false
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // dx>0则表示右滑, dx<0表示左滑, dy<0表示上滑, dy>0表示下滑
                if (dx != 0) { //去掉奇怪的内存疯涨问题
                    mCurrentItemOffset += dx
                    computeCurrentItemPos()
                    LogUtil.i(mContext!!,
                        String.format("dx=%s, dy=%s, mScrolledX=%s", dx, dy, mCurrentItemOffset))
                    onScrolledChangedCallback()
                }
            }
        })
        initWidth()
        mLinearSnapHelper.attachToRecyclerView(mRecyclerView)
    }

    /**
     * 初始化卡片宽度
     */
    private fun initWidth() {
        mRecyclerView?.post(object : Runnable {
            override fun run() {
                mCardGalleryWidth = mRecyclerView?.width!!
                mCardWidth = mCardGalleryWidth - dip2px(2 * (mPagePadding + mShowLeftCardWidth))
                mOnePageWidth = mCardWidth
                mRecyclerView?.smoothScrollToPosition(currentItemPos)
                onScrolledChangedCallback()
            }
        })
    }

    private fun getDestItemOffset(destPos: Int): Int {
        return mOnePageWidth * destPos
    }

    /**
     * 计算mCurrentItemOffset
     */
    private fun computeCurrentItemPos() {
        if (mOnePageWidth <= 0) return
        var pageChanged = false
        // 滑动超过一页说明已翻页
        if (Math.abs(mCurrentItemOffset - currentItemPos * mOnePageWidth) >= mOnePageWidth) {
            pageChanged = true
        }
        if (pageChanged) {
            val tempPos = currentItemPos
            currentItemPos = mCurrentItemOffset / mOnePageWidth
            LogUtil.d(mContext!!,
                String.format("=======onCurrentItemPos Changed======= tempPos=%s, mCurrentItemPos=%s",
                    tempPos,
                    currentItemPos))
        }
    }

    /**
     * RecyclerView位移事件监听, view大小随位移事件变化
     */
    private fun onScrolledChangedCallback() {
        val offset = mCurrentItemOffset - currentItemPos * mOnePageWidth
        val percent = Math.max(Math.abs(offset) * 1.0 / mOnePageWidth, 0.0001)
            .toFloat()
        LogUtil.d(mContext!!, String.format("offset=%s, percent=%s", offset, percent))
        var leftView: View? = null
        var rightView: View? = null
        if (currentItemPos > 0) {
            leftView = mRecyclerView?.layoutManager?.findViewByPosition(currentItemPos - 1)
        }
        val currentView: View? = mRecyclerView?.layoutManager?.findViewByPosition(currentItemPos)
        if (currentItemPos < mRecyclerView?.adapter?.itemCount!! - 1) {
            rightView = mRecyclerView?.layoutManager?.findViewByPosition(currentItemPos + 1)
        }
        if (leftView != null) {
            // y = (1 - mScale)x + mScale
            leftView.scaleY = (1 - mScale) * percent + mScale
        }
        if (currentView != null) {
            // y = (mScale - 1)x + 1
            currentView.scaleY = (mScale - 1) * percent + 1
        }
        if (rightView != null) {
            // y = (1 - mScale)x + mScale
            rightView.scaleY = (1 - mScale) * percent + mScale
        }
    }

    fun setScale(scale: Float) {
        mScale = scale
    }

    fun setPagePadding(pagePadding: Int) {
        mPagePadding = pagePadding
    }

    fun setShowLeftCardWidth(showLeftCardWidth: Int) {
        mShowLeftCardWidth = showLeftCardWidth
    }
}