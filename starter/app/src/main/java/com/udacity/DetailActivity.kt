package com.udacity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import org.w3c.dom.Text

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val txt1: TextView = findViewById(R.id.textView)
        val txt2: TextView = findViewById(R.id.textView2)

        txt1.text = intent.getStringExtra("DOWNLOAD_STATUS").toString()
        txt2.text = intent.getStringExtra("DOWNLOAD_VIA").toString()

        Log.d("TEST_DOWNLOAD_STATUS", intent.getStringExtra("DOWNLOAD_STATUS").toString())
        Log.d("TEST_DOWNLOAD_VIA", intent.getStringExtra("DOWNLOAD_VIA").toString())
    }

}
