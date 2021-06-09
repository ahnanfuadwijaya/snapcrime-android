package id.riverflows.lib_task_api.detection.tflite

import android.graphics.Bitmap
import android.graphics.RectF

interface DetectorKt {
    fun recognizeImage(bitmap: Bitmap): List<Recognition>
    fun enableStatLogging(logStats: Boolean)
    fun getStatString(): String
    fun close()
    fun setNumThreads(numThreads: Int)
    fun setUseNNAPI(isChecked: Boolean)

    data class Recognition(
        val id: String,
        val title: String,
        var confidence: Float,
        var location: RectF?
    )
}