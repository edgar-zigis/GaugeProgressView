package com.example.gaugeprogressview

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var handler = Handler(Looper.getMainLooper())
    private var runnable = object : Runnable {
        override fun run() {
            if (progressView.progress < 100) {
                progressView.setProgress(newProgress = progressView.progress + 10)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handler.postDelayed(runnable, 0)
    }
}