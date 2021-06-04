package id.riverflows.lib_task_api.detection.tflite

import android.graphics.Bitmap
import android.graphics.RectF

interface Detector {
    fun recognizeImage(bitmap: Bitmap?): List<Recognition?>?
    fun enableStatLogging(debug: Boolean)
    fun getStatString(): String

    fun close()
    fun setNumThreads(numThreads: Int)
    fun setUseNNAPI(isChecked: Boolean)

    /** An immutable result returned by a Detector describing what was recognized.  */
    class Recognition(
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of
         * the object.
         */
        private val id: String,
        /** Display name for the recognition.  */
        private val title: String,
        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         */
        private var confidence: Float?,
        /** Optional location within the source image for the location of the recognized object.  */
        private var location: RectF?
    ) {

        fun getId(): String {
            return id
        }

        fun getTitle(): String {
            return title
        }

        fun getConfidence(): Float? {
            return confidence
        }

        fun getLocation() = location

        fun setLocation(location: RectF) {
            this.location = location
        }

        override fun toString(): String {
            var resultString = ""
            resultString += "[$id] "
            resultString += "$title "
            confidence?.let { resultString += String.format("(%.1f%%) ", it * 100.0f) }
            if (location != null) {
                resultString += location.toString() + " "
            }
            return resultString.trim { it <= ' ' }
        }
    }
}