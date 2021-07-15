package com.android.library.coachmark.configuration

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.android.library.coachmark.components.CoachMarkInfo
import com.android.library.coachmark.components.CoachMarkInfoToolTip
import com.android.library.coachmark.components.CoachMarkSkipButton
import com.android.library.coachmark.components.CoachMarkOverlay
import com.android.library.coachmark.components.listener.SequenceListener
import java.util.*

class CoachMarkSequence(private val mContext: Context) {

    private val mSequenceQueue: Queue<CoachMarkOverlay.Builder> = LinkedList()
    private var mSequenceConfig: CoachMarkConfig? = null
    private var position:Int = 0;
    private var mSequenceListener: SequenceListener = object : SequenceListener {
        override fun onNextItem(coachMark: CoachMarkOverlay, coachMarkSequence: CoachMarkSequence) {
            super.onNextItem(coachMark, coachMarkSequence)
        }
    }
    private var mSequenceItem: CoachMarkOverlay.Builder? = null
    var mCoachMark: CoachMarkOverlay? = null

    private val mCoachMarkSkipButtonClickListener: CoachMarkSkipButton.ButtonClickListener =
        object : CoachMarkSkipButton.ButtonClickListener {
            override fun onSkipButtonClick(view: View) {
                ((mContext as Activity).window.decorView as ViewGroup).removeView(view)
                mSequenceQueue.clear()
            }
        }
    private val mCoachMarkOverlayClickListener: CoachMarkOverlay.OverlayClickListener =
        object : CoachMarkOverlay.OverlayClickListener {
            override fun onOverlayClick(overlay: CoachMarkOverlay) {
                position++
                mCoachMark = overlay
                if (mSequenceQueue.size > 0) {
                    mSequenceItem = mSequenceQueue.poll()
                    if (mSequenceQueue.isEmpty()) {
                        overlay.mBuilder.setOverlayTransparentPadding(0, 0, 0, 0)
                    }
                    mSequenceItem?.apply {
                        overlay.mBuilder.setTabPosition(getTabPosition())
                        overlay.mBuilder.setInfoViewBuilder(getInfoViewBuilder()!!)
                        if (getOverlayTargetView() != null) {
                            overlay.mBuilder.setOverlayTargetView(getOverlayTargetView()!!)
                        } else {
                            overlay.mBuilder.setOverlayTargetView(null)
                            overlay.mBuilder.setOverlayTargetCoordinates(getOverlayTargetCoordinates())
                        }
                        overlay.removeAllViews()
                        overlay.addThreeDots(position%3)
                        overlay.resetView()
                        mSequenceListener?.let {
                            it.onNextItem(overlay, this@CoachMarkSequence)
                        }
                    }
                } else {
                    ((mContext as Activity).window.decorView as ViewGroup).removeView(overlay)
                }
            }
        }

    fun setSequenceConfig(config: CoachMarkConfig) {
        if (config.getOverlayBuilder().getOverlayClickListener() == null) {
            config.getOverlayBuilder().setOverlayClickListener(mCoachMarkOverlayClickListener)
        }
        if (config.getSkipButtonBuilder().getButtonClickListener() == null) {
            config.getSkipButtonBuilder().setButtonClickListener(mCoachMarkSkipButtonClickListener)
        }
        mSequenceConfig = config
    }

    // set the data to view the next descriptions.
    fun setNextView() {
        if (mCoachMark != null && mSequenceItem != null) {
            mCoachMark!!.mBuilder.setInfoViewBuilder(mSequenceItem?.getInfoViewBuilder())
            mCoachMark!!.mBuilder.setToolTipBuilder(mSequenceItem?.getToolTipBuilder())
            mCoachMark!!.invalidate()
        }
    }

    fun setSequenceListener(listener: SequenceListener) {
        mSequenceListener = listener
    }

    fun getRemainingListCount(): Int {
        return mSequenceQueue.size
    }

    fun addItem(targetView: View, drawable:Drawable) {
        addItem(targetView,  drawable, -1)
    }

    fun addItem(targetView: View, drawable:Drawable, position: Int) {
        CoachMarkOverlay.Builder(mContext).apply {
            setOverlayTargetView(targetView)
            setInfoViewBuilder(CoachMarkInfo.Builder(mContext))
            setTabPosition(position)
            setSkipButtonBuilder(CoachMarkSkipButton.Builder(mContext))
            if (mSequenceConfig != null) {
                getInfoViewBuilder()!!.setConfig(mSequenceConfig!!)
                getSkipButtonBuilder()!!.setConfig(mSequenceConfig!!)
                getInfoViewBuilder()!!.setDrawable(drawable)
                if (mSequenceConfig!!.getToolTipVisibility()) {
                    setToolTipBuilder(CoachMarkInfoToolTip.Builder(mContext))
                    if (mSequenceConfig!!.getCoachMarkToolTipBuilder() != null) {
                        getToolTipBuilder()!!.setConfig(mSequenceConfig!!)
                    }
                }
                if (getSkipButtonBuilder()!!.getButtonClickListener() == null && mSequenceConfig!!.getSkipButtonBuilder().getButtonClickListener() == null) {
                    getSkipButtonBuilder()!!.setButtonClickListener(mCoachMarkSkipButtonClickListener)
                }
            }
            mSequenceQueue.add(this)
        }
    }

    fun addItem(targetCoordinates: Rect, infoText: String) {
        CoachMarkOverlay.Builder(mContext).apply {
            setOverlayTargetView(null)
            setOverlayTargetCoordinates(targetCoordinates)
            setInfoViewBuilder(CoachMarkInfo.Builder(mContext))
            setSkipButtonBuilder(CoachMarkSkipButton.Builder(mContext))
            if (mSequenceConfig != null) {
                getInfoViewBuilder()!!.setConfig(mSequenceConfig!!)
                getSkipButtonBuilder()!!.setConfig(mSequenceConfig!!)
                getInfoViewBuilder()!!.setInfoText(infoText)
                if (mSequenceConfig!!.getToolTipVisibility()) {
                    setToolTipBuilder(CoachMarkInfoToolTip.Builder(mContext))
                    if (mSequenceConfig!!.getCoachMarkToolTipBuilder() != null) {
                        getToolTipBuilder()!!.setConfig(mSequenceConfig!!)
                    }
                }
                if (getSkipButtonBuilder()!!.getButtonClickListener() == null) {
                    getSkipButtonBuilder()!!.setButtonClickListener(mCoachMarkSkipButtonClickListener)
                }
            } else {
                getInfoViewBuilder()!!.setInfoText(infoText)
            }
            mSequenceQueue.add(this)
        }
    }

    fun addItem(coachMarkBuilder: CoachMarkOverlay.Builder, addOverlayClickListener: Boolean) {
        if (coachMarkBuilder.getOverlayClickListener() == null) {
            if (addOverlayClickListener) {
                coachMarkBuilder.setOverlayClickListener(mCoachMarkOverlayClickListener)
            }
            if (coachMarkBuilder.getSkipButtonBuilder() != null && coachMarkBuilder.getSkipButtonBuilder()?.getButtonClickListener() == null) {
                coachMarkBuilder.getSkipButtonBuilder()?.setButtonClickListener(mCoachMarkSkipButtonClickListener)
            }
        }
        mSequenceQueue.add(coachMarkBuilder)
    }

    fun clearList() {
        mSequenceQueue.clear()
    }

    fun start(rootView: ViewGroup? = null) {
        if (mSequenceQueue.size > 0) {
            val firstElement = mSequenceQueue.poll()
            if (rootView == null) {
                firstElement.build().show((mContext as Activity).window.decorView as ViewGroup)
            } else {
                firstElement.build().show(rootView)
            }
        }
    }
}