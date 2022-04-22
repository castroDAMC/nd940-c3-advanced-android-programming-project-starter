package com.udacity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        (findViewById<TextView>(R.id.txt_name_body)).text = intent.getStringExtra("DOWNLOAD_VIA").toString()
        (findViewById<TextView>(R.id.txt_status_body)).text = intent.getStringExtra("DOWNLOAD_STATUS").toString()



        (findViewById<Button>(R.id.btn_back_home)).setOnClickListener {
            when (!isTaskRoot) {
                true -> super.onBackPressed()
                false -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }
    }

}
