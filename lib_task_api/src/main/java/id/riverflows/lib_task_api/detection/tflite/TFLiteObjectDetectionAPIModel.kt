package id.riverflows.lib_task_api.detection.tflite

import android.content.Context
import android.graphics.Bitmap

class TFLiteObjectDetectionAPIModel: Detector {

    constructor(
        context: Context,
        modelFilename: String,
        labelFilename: String,
        inputSize: Int,
        isQuantized: Boolean
    )

    override fun recognizeImage(bitmap: Bitmap?): List<Detector.Recognition?>? {
        TODO("Not yet implemented")
    }

    override fun enableStatLogging(debug: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getStatString(): String {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun setNumThreads(numThreads: Int) {
        TODO("Not yet implemented")
    }

    override fun setUseNNAPI(isChecked: Boolean) {
        TODO("Not yet implemented")
    }

    companion object{

        fun create(
            context: Context,
            modelFilename: String,
            labelFilename: String,
            inputSize: Int,
            isQuantized: Boolean
        ): TFLiteObjectDetectionAPIModel{
            return TFLiteObjectDetectionAPIModel(context, modelFilename, labelFilename, inputSize, isQuantized)
        }
    }
}