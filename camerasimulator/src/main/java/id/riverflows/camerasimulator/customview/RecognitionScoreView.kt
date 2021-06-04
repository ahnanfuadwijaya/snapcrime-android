package id.riverflows.camerasimulator.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import id.riverflows.lib_task_api.detection.tflite.Detector

class RecognitionScoreView(context: Context, attrs: AttributeSet): View(context, attrs), ResultView {
    private val textSizePx: Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP.toFloat(), resources.displayMetrics
    )
    private val fgPaint = Paint()
    private val bgPaint = Paint()
    private var results: List<Detector.Recognition>? = null

    override fun setResult(results: List<Detector.Recognition>) {
        this.results = results
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val x = 10
        var y = (fgPaint.textSize * 1.5f).toInt()

        canvas.drawPaint(bgPaint)

        if (results != null) {
            for (recog in results!!) {
                canvas.drawText(
                    recog.getTitle() + ": " + recog.getConfidence(),
                    x.toFloat(),
                    y.toFloat(),
                    fgPaint
                )
                y += (fgPaint.textSize * 1.5f).toInt()
            }
        }
    }

    companion object{
        const val TEXT_SIZE_DIP = 14
    }
}