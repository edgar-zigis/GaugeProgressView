package com.zigis.gaugeprogressview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.zigis.gaugeprogressview.custom.Coordinate2F
import com.zigis.gaugeprogressview.custom.ProgressAnimation
import kotlin.math.*

open class GaugeProgressView : View {

    var progress = 0
        private set

    var startAngle = -90f

    var outerArcThickness = dp(7.5f)
    var innerArcThickness = dp(10f)
    var offsetBetweenArcs = dp(15f)

    var outerArcColor = Color.parseColor("#FF2400")
    var innerArcColor = Color.parseColor("#AFB6BB")

    var innerArcDashThickness = dp(2f)
        set(value) {
            innerArcPathDashEffect = DashPathEffect(floatArrayOf(value, innerArcDashDistance), 0f)
            field = value
        }
    var innerArcDashDistance = dp(8f)
        set(value) {
            innerArcPathDashEffect = DashPathEffect(floatArrayOf(innerArcDashThickness, value), 0f)
            field = value
        }

    var valueTypefaceSize = dp(50f)
    var valueTextColor = Color.parseColor("#1A1A1A")
    var valueTypeface = Typeface.create("sans-serif", Typeface.BOLD)
    var isValueHidden = false

    private val outerArcPath = Paint(Paint.ANTI_ALIAS_FLAG)
    private val innerArcPath = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.style = Paint.Style.STROKE
    }
    private var innerArcPathDashEffect = DashPathEffect(
        floatArrayOf(innerArcDashThickness, innerArcDashDistance),
        0f
    )

    private var value = ""
    private val valueRect = Rect()
    private var valuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    //  Constructors

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    //  Initialization

    private fun init(context: Context, attrs: AttributeSet?) {
        if (isInEditMode) return

        val styledAttributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GaugeProgressView,
            0,
            0
        )

        outerArcThickness = styledAttributes.getDimension(R.styleable.GaugeProgressView_gpv_outerArcThickness, outerArcThickness)
        innerArcThickness = styledAttributes.getDimension(R.styleable.GaugeProgressView_gpv_innerArcThickness, innerArcThickness)
        innerArcDashThickness = styledAttributes.getDimension(R.styleable.GaugeProgressView_gpv_innerArcDashThickness, innerArcDashThickness)
        innerArcDashDistance = styledAttributes.getDimension(R.styleable.GaugeProgressView_gpv_innerArcDashDistance, innerArcDashDistance)
        offsetBetweenArcs = styledAttributes.getDimension(R.styleable.GaugeProgressView_gpv_offsetBetweenArcs, offsetBetweenArcs)
        startAngle = styledAttributes.getFloat(R.styleable.GaugeProgressView_gpv_startAngle, startAngle)
        progress = styledAttributes.getInteger(R.styleable.GaugeProgressView_gpv_progress, progress)
        outerArcColor = styledAttributes.getColor(R.styleable.GaugeProgressView_gpv_outerArcColor, outerArcColor)
        innerArcColor = styledAttributes.getColor(R.styleable.GaugeProgressView_gpv_innerArcColor, innerArcColor)

        val customFontRes = styledAttributes.getResourceId(R.styleable.GaugeProgressView_gpv_valueFont, 0)
        if (customFontRes > 0) {
            valueTypeface = ResourcesCompat.getFont(context, customFontRes) ?: valueTypeface
        }
        valueTypefaceSize = styledAttributes.getDimension(R.styleable.GaugeProgressView_gpv_valueTextSize, valueTypefaceSize)
        valueTextColor = styledAttributes.getColor(R.styleable.GaugeProgressView_gpv_valueTextColor, valueTextColor)
        isValueHidden = styledAttributes.getBoolean(R.styleable.GaugeProgressView_gpv_isValueHidden, isValueHidden)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val outerRadius = measuredWidth.toFloat() / 2
        val innerRadius = outerRadius - outerArcThickness

        drawOuterArc(
            canvas = canvas,
            cx = outerRadius,
            cy = outerRadius,
            rInn = innerRadius,
            rOut = outerRadius,
            sweepAngle = 359.99f / 100 * progress,
            paint = outerArcPath.also {
                it.color = outerArcColor
            }
        )

        drawInnerArc(
            canvas = canvas,
            cx = outerRadius - offsetBetweenArcs,
            cy = outerRadius - offsetBetweenArcs,
            rInn = innerRadius - offsetBetweenArcs,
            offset = offsetBetweenArcs,
            sweepAngle = 359.99f / 100 * progress,
            paint = innerArcPath.also {
                it.color = innerArcColor
                it.strokeWidth = innerArcThickness
                it.pathEffect = innerArcPathDashEffect
            }
        )

        if (!isValueHidden) {
            drawValueText(canvas)
        }
    }

    //  Arc drawing methods

    private fun drawInnerArc(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rInn: Float,
        offset: Float,
        sweepAngle: Float,
        paint: Paint
    ) {
        canvas.save()
        canvas.translate(offset, offset)

        val outerRect = RectF(cx - rInn, cy - rInn, cx + rInn, cy + rInn)

        canvas.drawArc(outerRect, startAngle, sweepAngle, false, paint)
        canvas.restore()
    }

    private fun drawOuterArc(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rInn: Float,
        rOut: Float,
        sweepAngle: Float,
        paint: Paint
    ) {
        val outerRect = RectF(cx - rOut, cy - rOut, cx + rOut, cy + rOut)
        val innerRect = RectF(cx - rInn, cy - rInn, cx + rInn, cy + rInn)
        val arcPath = Path()

        val start = Math.toRadians(startAngle.toDouble())
        val startX = (cx + rInn * cos(start)).toFloat()
        val startY = (cy + rInn * sin(start)).toFloat()

        arcPath.moveTo(startX, startY)
        arcPath.lineTo(
            (cx + rOut * cos(start)).toFloat(),
            (cy + rOut * sin(start)).toFloat()
        )
        arcPath.arcTo(outerRect, startAngle, sweepAngle)

        val end = Math.toRadians((startAngle + sweepAngle).toDouble())
        val endX = (cx + rInn * cos(end)).toFloat()
        val endY = (cy + rInn * sin(end)).toFloat()

        arcPath.lineTo(endX, endY)
        arcPath.arcTo(innerRect, startAngle + sweepAngle, -sweepAngle)
        arcPath.close()

        if (sweepAngle > 0 && sweepAngle < 359f) {
            roundCorner(
                arcPath,
                Coordinate2F((cx + rOut * cos(start)).toFloat(), (cy + rOut * sin(start)).toFloat()),
                Coordinate2F(startX, startY)
            )
            roundCorner(
                arcPath,
                Coordinate2F(endX, endY),
                Coordinate2F((cx + rOut * cos(end)).toFloat(), (cy + rOut * sin(end)).toFloat())
            )
        }

        canvas.drawPath(arcPath, paint)
    }

    private fun roundCorner(path: Path, startCoordinate: Coordinate2F, endCoordinate: Coordinate2F) {
        val radius = outerArcThickness / 2
        val sweepCoordinate = getMiddleCoordinate(startCoordinate, endCoordinate)

        path.moveTo(sweepCoordinate.x, sweepCoordinate.y)

        val endOval = RectF()
        endOval[sweepCoordinate.x - radius, sweepCoordinate.y - radius, sweepCoordinate.x + radius] = sweepCoordinate.y + radius

        val startAngleEnd = (180 / Math.PI * atan2(
            startCoordinate.y - endCoordinate.y,
            startCoordinate.x - endCoordinate.x
        )).toFloat()

        path.arcTo(endOval, startAngleEnd, -180f)
        path.close()
    }

    private fun getMiddleCoordinate(start: Coordinate2F, end: Coordinate2F): Coordinate2F {
        val measurePath = Path()
        measurePath.moveTo(start.x, start.y)
        measurePath.lineTo(end.x, end.y)

        val pathMeasure = PathMeasure(measurePath, false)
        val coordinates = floatArrayOf(0f, 0f)
        pathMeasure.getPosTan(pathMeasure.length * 0.5f, coordinates, null)

        return Coordinate2F(coordinates[0], coordinates[1])
    }

    //  Text drawing methods

    @Suppress("DEPRECATION")
    private fun drawValueText(canvas: Canvas) {
        valuePaint.color = valueTextColor
        valuePaint.typeface = valueTypeface
        valuePaint.textSize = valueTypefaceSize
        valuePaint.getTextBounds(value, 0, value.length, valueRect)

        val textLayout = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            val builder = StaticLayout.Builder.obtain(value, 0, value.length, valuePaint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
            builder.build()
        } else {
            StaticLayout(value, valuePaint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false)
        }

        canvas.save()

        canvas.translate(0f, measuredHeight / 2f - valueRect.height())
        textLayout.draw(canvas)

        canvas.restore()
    }

    //  Helper methods

    private fun dp(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    //  Public setters

    fun setProgress(newProgress: Int) {
        val targetProgress = max(0, min(newProgress, 100))
        val duration = (abs(progress - newProgress).toFloat() / 10) * 200

        value = targetProgress.toString()
        val animation = ProgressAnimation(progress, targetProgress) {
            progress = it
            requestLayout()
        }.also {
            it.duration = duration.toLong()
        }
        startAnimation(animation)
    }
}