package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.O)
class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val defaultDuration: Long = 5000
    private val downloadFinishedDuration: Long = 500

    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator: ValueAnimator =
        ValueAnimator.ofInt(0, 360).setDuration(defaultDuration)

    private lateinit var valueAnimatorWhenFinished: ValueAnimator

    //Values will be load from attrs.xml. They are exposed to be changed in any layout .xml file
    private lateinit var showIdleText: String
    private lateinit var showLoadingText: String
    private var buttonBackgroundColor : Int = 0
    private var buttonTextColor: Int = 0
    private var buttonCircleColor: Int = 0
    private var txtStyleSize: Float = 0.0f

    private lateinit var buttonTextStr : String
    private var progress = 0

    private lateinit var paint: Paint

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Idle -> {
                buttonTextStr = showIdleText
            }
            ButtonState.Loading -> {
                buttonTextStr = showLoadingText
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                setupAnimatorWhenFinished()
                valueAnimatorWhenFinished.start()
            }
            else -> {}
        }
    }

    init {
        getValuesFromStyleable(attrs)
        initPaint()
        setUpAnimatorWhenDownloading()
        buttonState = ButtonState.Idle
    }

    private fun setUpAnimatorWhenDownloading() {
        // setup animation
        valueAnimator.apply {
            setIntValues(progress, 360)
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            duration = defaultDuration
            cancel()
            addUpdateListener {
                animateBasedOnProgress(it)
            }
        }
    }

    private fun setupAnimatorWhenFinished() {

        valueAnimatorWhenFinished =
            ValueAnimator.ofInt(progress, 360).setDuration(downloadFinishedDuration)

        valueAnimatorWhenFinished.apply {
            setIntValues(progress, 360)
            repeatCount = 0
            repeatMode = ValueAnimator.RESTART

            addUpdateListener {
                animateBasedOnProgress(it)
            }
        }
    }

    private fun getValuesFromStyleable(attrs: AttributeSet?) {
        // Based on https://developer.android.com/training/custom-views/create-view
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0).apply {
            try {
                //Get Text
                showIdleText = getString(R.styleable.LoadingButton_customShowIdleText).toString()
                showLoadingText = getString(R.styleable.LoadingButton_customShowLoadingText).toString()

                //Get Color
                buttonBackgroundColor = getColor(R.styleable.LoadingButton_customBackgroundColor,resources.getColor(android.R.color.holo_purple, null))
                buttonTextColor = getColor(R.styleable.LoadingButton_customShowTextColor,resources.getColor(android.R.color.white, null))
                buttonCircleColor = getColor(R.styleable.LoadingButton_customShowCircleColor,resources.getColor(R.color.colorPrimary, null))

                //Get Dimension
                txtStyleSize = getDimension(R.styleable.LoadingButton_customShowTxtSize, 25.0f)

            } finally {
                recycle()
            }
        }
    }

    private fun initPaint() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = txtStyleSize
            typeface = Typeface.create("", Typeface.BOLD)
        }
    }

    private fun animateBasedOnProgress(valueAnimator: ValueAnimator){
        invalidate()
        progress = valueAnimator.animatedValue as Int
        when (progress) {
            in 0..119 -> {
                buttonTextStr = showLoadingText
            }
            in 120..239 -> {
                buttonTextStr = resources.getString(R.string.button_loading_120)
            }
            in 120..329 -> {
                buttonTextStr = resources.getString(R.string.button_loading_240)
            }
            in 330..359 -> {
                buttonTextStr = resources.getString(R.string.button_loading_350)
            }
            360 -> {
                if (buttonState == ButtonState.Completed) {
                    buttonState = ButtonState.Idle
                    progress = 0
                }
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // BackGround
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint.apply { color = buttonBackgroundColor })

        // text
        val textPosition = ((heightSize) / 2.0f) - ((paint.descent() + paint.ascent()) / 2.0f)
        canvas?.drawText(buttonTextStr, widthSize / 2.0f, textPosition, paint.apply { color = buttonTextColor })

        // Download Progress
        paint.color = buttonCircleColor

        val rect = RectF()
        rect.right = widthSize - 10.0f
        rect.left = widthSize - 10.0f - txtStyleSize*2
        rect.top = ((heightSize)/2.0f) - (txtStyleSize)
        rect.bottom = ((heightSize)/2.0f) + (txtStyleSize)

        canvas?.drawArc(rect,0f, progress.toFloat(), true, paint.apply { color = buttonCircleColor }
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