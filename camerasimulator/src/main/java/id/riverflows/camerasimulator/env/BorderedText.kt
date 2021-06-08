package id.riverflows.camerasimulator.env

import android.graphics.*
import java.util.*

class BorderedText(
    interiorColor: Int,
    exteriorColor: Int,
    private val textSize: Float
) {
    private val interiorPaint = Paint()
    private val exteriorPaint = Paint()

    constructor(textSize: Float): this(Color.BLACK, Color.WHITE, textSize)

    init {
        interiorPaint.textSize = textSize
        interiorPaint.color = interiorColor
        interiorPaint.style = Paint.Style.FILL
        interiorPaint.isAntiAlias = false
        interiorPaint.alpha = 255

        exteriorPaint.textSize = textSize
        exteriorPaint.color = exteriorColor
        exteriorPaint.style = Paint.Style.FILL_AND_STROKE
        exteriorPaint.strokeWidth = textSize / 8
        exteriorPaint.isAntiAlias = false
        exteriorPaint.alpha = 255
    }

    fun setTypeface(typeface: Typeface) {
        interiorPaint.typeface = typeface
        exteriorPaint.typeface = typeface
    }

    fun drawText(canvas: Canvas, posX: Float, posY: Float, text: String) {
        canvas.drawText(text, posX, posY, exteriorPaint)
        canvas.drawText(text, posX, posY, interiorPaint)
    }

    fun drawText(canvas: Canvas, posX: Float, posY: Float, text: String, bgPaint: Paint) {
        val width = exteriorPaint.measureText(text)
        val textSize = exteriorPaint.textSize
        val paint = Paint(bgPaint)
        paint.style = Paint.Style.FILL
        paint.alpha = 160
        canvas.drawRect(posX, (posY + textSize.toInt()), (posX + width.toInt()), posY, paint);
        canvas.drawText(text, posX, (posY + textSize), interiorPaint)
    }

    fun drawLines(canvas: Canvas, posX: Float, posY: Float, lines: Vector<String>) {
        for ((lineNum, line) in lines.withIndex()) {
            drawText(canvas, posX, posY - textSize * (lines.size - lineNum - 1), line)
        }
    }

    fun setInteriorColor(color: Int) {
        interiorPaint.color = color
    }

    fun setExteriorColor(color: Int) {
        exteriorPaint.color = color
    }

    fun setAlpha(alpha: Int) {
        interiorPaint.alpha = alpha
        exteriorPaint.alpha = alpha
    }

    fun getTextBounds(line: String, index: Int, count: Int,  lineBounds: Rect) {
        interiorPaint.getTextBounds(line, index, count, lineBounds)
    }

    fun setTextAlign(align: Paint.Align) {
        interiorPaint.textAlign = align
        exteriorPaint.textAlign = align
    }
}