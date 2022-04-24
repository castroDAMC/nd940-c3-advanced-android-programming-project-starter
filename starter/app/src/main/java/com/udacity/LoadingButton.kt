package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val defaultDuration: Long = 5000

    private var widthSize = 0
    private var heightSize = 0

    private var buttonBackgroundColor = resources.getColor(android.R.color.holo_purple)
    private var buttonTextColor = resources.getColor(android.R.color.white)
    private var buttonCircleColor = resources.getColor(R.color.colorPrimaryDark)

    private val valueAnimator: ValueAnimator =
        ValueAnimator.ofInt(0, 360).setDuration(defaultDuration)

    private var buttonTextStr = "Download"
    private var progress = 0

    private val txtStyleSize = 50.0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = txtStyleSize
        typeface = Typeface.create("", Typeface.BOLD)
    }

    fun timeToFinishDownloadAnimation(): Long {
        val stepTime = valueAnimator.duration.toFloat() / (360).toFloat()
        val timeToFinish = stepTime * (360.0 - progress.toFloat())
        return timeToFinish.toLong()
    }

    fun timeToEachStepInDownloadAnimation(): Long {
        return (valueAnimator.duration.toFloat() / (360).toFloat()).toLong()
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Idle -> {
                initAnimationParameters()
                buttonTextStr = resources.getString(R.string.btn_idle_status_text)
            }
            ButtonState.Loading -> {
                buttonTextStr = resources.getString(R.string.button_loading)
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                buttonTextStr = "Finishing Download"
                valueAnimator.repeatCount = 0
            }
            else -> {}
        }

        invalidate()
    }

    private fun initAnimationParameters() {
        progress = 0
        valueAnimator.apply {
            setIntValues(progress, 360)
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            duration = defaultDuration
            cancel()

        }
    }

    init {
        buttonState = ButtonState.Idle

        initAnimationParameters()
        // setup animation
        valueAnimator.apply {
            addUpdateListener {
                progress = it.animatedValue as Int
                invalidate()
                if (buttonState == ButtonState.Completed && (progress == 360)) {
                    buttonState = ButtonState.Idle
                    progress = 0
                }
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // BackGround
        paint.color = buttonBackgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        // text
        paint.color = buttonTextColor
        val textPosition = ((heightSize) / 2.0f) - ((paint.descent() + paint.ascent()) / 2.0f)
        canvas?.drawText(buttonTextStr, widthSize / 2.0f, textPosition, paint)

        // Download Progress
        paint.color = buttonCircleColor

        val rect = RectF()
        rect.right = widthSize - 10.0f
        rect.left = widthSize - 10.0f - paint.textSize*2
        rect.top = ((heightSize)/2.0f) - (txtStyleSize)
        rect.bottom = ((heightSize)/2.0f) + (txtStyleSize)

        canvas?.drawArc(rect,0f, progress.toFloat(), true, paint
        )

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}