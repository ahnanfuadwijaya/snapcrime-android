package id.riverflows.camerasimulator.customview

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView

class AutoFitTextureView(context: Context,
                         attrs: AttributeSet?,
                         defStyle: Int): TextureView(context, attrs, defStyle) {
    private var ratioWidth = 0
    private var ratioHeight = 0

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context): this(context, null)

    fun setAspectRatio(width: Int, height: Int){
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        ratioWidth = width
        ratioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == ratioWidth || 0 == ratioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * ratioWidth / ratioHeight) {
                setMeasuredDimension(width, width * ratioHeight / ratioWidth)
            } else {
                setMeasuredDimension(height * ratioWidth / ratioHeight, height)
            }
        }
    }
}