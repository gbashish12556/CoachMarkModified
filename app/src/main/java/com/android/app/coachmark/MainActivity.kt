package com.android.app.coachmark

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.android.library.coachmark.components.*
import com.android.library.coachmark.components.listener.SequenceListener
import com.android.library.coachmark.configuration.*
import com.android.library.coachmark.utility.Gravity
import com.android.library.coachmark.utility.TypeFace
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
                    .setDrawable(getDrawable(R.drawable.dummy))
                    .setMargin(30, -5, 30, 30)
            ).setSkipButtonBuilder(
                CoachMarkSkipButton.Builder(this)
                    .setButtonClickListener(object : CoachMarkSkipButton.ButtonClickListener {
                        override fun onSkipButtonClick(view: View) {
                            (window.decorView as ViewGroup).removeView(view)
                            coachMarkSequence.clearList()
                        }
                    })
            )
        coachMarkSequence.setSequenceConfig(coachMarkConfig)
        coachMarkSequence.addItem(coachMarkBuilder, true)

        coachMarkSequence.setSequenceConfig(coachMarkConfig)
        coachMarkSequence.addItem(button_2, getDrawable(R.drawable.dummy))

        coachMarkSequence.addItem(button_3, getDrawable(R.drawable.dummy))

        coachMarkSequence.setSequenceListener(object : SequenceListener {
            override fun onNextItem(coachMark: CoachMarkOverlay, coachMarkSeq: CoachMarkSequence) {
                super.onNextItem(coachMark, coachMarkSequence)
            }
        })

        coachMarkSequence.start(window?.decorView as ViewGroup)
    }
}
