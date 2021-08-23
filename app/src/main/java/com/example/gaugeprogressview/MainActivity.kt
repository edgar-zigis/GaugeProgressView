package com.example.gaugeprogressview

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.gaugeprogressview.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var handler = Handler(Looper.getMainLooper())
    private var runnable = object : Runnable {
        override fun run() {
            if (binding.progressView.progress < 100) {
                binding.progressView.setProgress(
                    newProgress = binding.progressView.progress + Random.nextInt(3, 20)
                )
                handler.postDelayed(this, 2000)
            }
        }
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        handler.postDelayed(runnable, 0)
    }
}