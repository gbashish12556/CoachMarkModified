package com.android.app.coachmark

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.android.library.coachmark.components.*
import com.android.library.coachmark.components.listener.SequenceListener
import com.android.library.coachmark.configuration.*
import com.android.library.coachmark.utility.CoachGravity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startCoachMark()

//        start_coach_mark?.setOnClickListener {
//        }

    }

    private fun startCoachMark(){
        val coachMarkSequence = CoachMarkSequence(this)
        val coachMarkConfig = CoachMarkConfig(this)
        coachMarkConfig.showCoachMarkToolTip(boolean = false)
        val coachMarkBuilder = CoachMarkOverlay.Builder(this)
            .setOverlayTargetView(button_1)
            .setInfoViewBuilder(
                CoachMarkInfo.Builder(this)
                    .setInfoViewAllignment(Gravity.CENTER_HORIZONTAL)
                    .setDrawable(getDrawable(R.drawable.overlay_zero))
            ).setSkipButtonBuilder(
                CoachMarkSkipButton.Builder(this)
                    .setButtonClickListener(object : CoachMarkSkipButton.ButtonClickListener {
                        override fun onSkipButtonClick(view: View) {
                            (window.decorView as ViewGroup).removeView(view)
                            coachMarkSequence.clearList()
                        }
                    })
            )
        coachMarkSequence.addItem(coachMarkBuilder, true)

        coachMarkConfig.setInfoViewAllignment(Gravity.LEFT)
        coachMarkSequence.setSequenceConfig(coachMarkConfig)
        coachMarkSequence.addItem(button_2, getDrawable(R.drawable.overlay_one))

        coachMarkConfig.setInfoTextMargin(0,200,0,0);
        coachMarkConfig.setInfoGravity(CoachGravity.TOP)
        coachMarkConfig.setInfoViewAllignment(Gravity.RIGHT)
        coachMarkSequence.setSequenceConfig(coachMarkConfig)
        coachMarkSequence.addItem(button_3, getDrawable(R.drawable.overlay_two))

        coachMarkSequence.setSequenceListener(object : SequenceListener {
            override fun onNextItem(coachMark: CoachMarkOverlay, coachMarkSeq: CoachMarkSequence) {
                super.onNextItem(coachMark, coachMarkSequence)
            }
        })

        coachMarkSequence.start(window?.decorView as ViewGroup)
    }
}
