package com.zigis.gaugeprogressview.custom

import android.view.animation.Animation
import android.view.animation.Transformation

class ProgressAnimation(
    private val initialProgress: Int,
    private val newProgress: Int,
    private val onValueChanged: ((Int) -> Unit)? = null
) : Animation() {

    override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
        val progress = (newProgress - initialProgress) * interpolatedTime
        onValueChanged?.invoke(initialProgress + progress.toInt())
    }
}