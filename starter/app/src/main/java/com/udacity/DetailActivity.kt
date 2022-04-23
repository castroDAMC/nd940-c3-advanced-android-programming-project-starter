package com.udacity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        (findViewById<TextView>(R.id.txt_name_body)).text =
            intent.getStringExtra("DOWNLOAD_VIA").toString()
        (findViewById<TextView>(R.id.txt_status_body)).text =
            intent.getStringExtra("DOWNLOAD_STATUS").toString()

        val motion = findViewById<MotionLayout>(R.id.motion_layout)

        (findViewById<Button>(R.id.btn_back_home)).setOnClickListener {
            motion.transitionToEnd()
        }

        motion.setTransitionListener(object : TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                when (!isTaskRoot) {
                    true -> onBackPressed()
                    false -> {
                        val intent = Intent(this@DetailActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        })

    }


}
