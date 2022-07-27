package com.gang.rvcardgallery.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gang.gallery.CardScaleHelper
import com.gang.imageloader.java.BlurBitmapUtils
import com.gang.rvcardgallery.R
import com.gang.util.ViewSwitchUtils

class MainActivity : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null
    private var mBlurView: ImageView? = null
    private val mList: MutableList<Int> = ArrayList()
    private var mCardScaleHelper: CardScaleHelper? = null
    private var mBlurRunnable: Runnable? = null
    private var mLastPos = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decorView: View = window.decorView
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        for (i in 0..9) {
            mList.add(R.drawable.pic4)
            mList.add(R.drawable.pic5)
            mList.add(R.drawable.pic6)
        }
        mRecyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView?
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView?.layoutManager = linearLayoutManager
        mRecyclerView?.setAdapter(CardAdapter(mList))
        // mRecyclerView绑定scale效果
        mCardScaleHelper = CardScaleHelper()
        mCardScaleHelper?.currentItemPos = 2
        mRecyclerView?.apply {
            mCardScaleHelper?.attachToRecyclerView(this)
        }
        initBlurBackground()
    }

    private fun initBlurBackground() {
        mBlurView = findViewById<ImageView>(R.id.blurView)
        mRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyBackgroundChange()
                }
            }
        })
        notifyBackgroundChange()
    }

    private fun notifyBackgroundChange() {
        mCardScaleHelper?.apply {
            if (mLastPos == currentItemPos) return
            mLastPos = currentItemPos
            val resId = mList[currentItemPos]
            mBlurView?.removeCallbacks(mBlurRunnable)
            mBlurRunnable = Runnable {
                val bitmap: Bitmap = BitmapFactory.decodeResource(getResources(), resId)
                mBlurView?.apply {
                    ViewSwitchUtils.startSwitchBackgroundAnim(this,
                        BlurBitmapUtils.getBlurBitmap(
                            mBlurView?.context, bitmap, 15))
                }
            }
            mBlurView?.postDelayed(mBlurRunnable, 500)
        }
    }
}