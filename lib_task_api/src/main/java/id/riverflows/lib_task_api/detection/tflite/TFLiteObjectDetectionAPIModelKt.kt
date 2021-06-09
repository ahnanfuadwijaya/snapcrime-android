package id.riverflows.lib_task_api.detection.tflite

import android.content.Context
import android.graphics.Bitmap
import android.os.Trace
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions
import java.io.IOException
import java.nio.MappedByteBuffer
import java.util.*

class TFLiteObjectDetectionAPIModelKt @Throws(IOException::class)
constructor(
    context: Context,
    modelFilename: String
) : DetectorKt {
    private val modelBuffer: MappedByteBuffer
    private var objectDetector: ObjectDetector
    private val optionsBuilder: ObjectDetectorOptions.Builder

    init {
        try{
            modelBuffer = FileUtil.loadMappedFile(context, modelFilename)
            optionsBuilder = ObjectDetectorOptions.builder().setMaxResults(NUM_DETECTIONS)
            objectDetector = ObjectDetector.createFromBufferAndOptions(modelBuffer, optionsBuilder.build())
        }catch (ioe: IOException){
            throw ioe
        }
    }

    override fun recognizeImage(bitmap: Bitmap): List<DetectorKt.Recognition> {
        Trace.beginSection("recognizeImage")
        val results = objectDetector.detect(TensorImage.fromBitmap(bitmap))
        val recognitions = ArrayList<DetectorKt.Recognition>()
        for ((index, detection) in results.withIndex()) {
            recognitions.add(
                DetectorKt.Recognition(
                    "$index",
                    detection.categories[0].label,
                    detection.categories[0].score,
                    detection.boundingBox
                )
            )
        }
        Trace.endSection()
        return recognitions
    }

    override fun enableStatLogging(logStats: Boolean) {}

    override fun getStatString(): String = ""

    override fun close() {
        objectDetector.close()
    }

    override fun setNumThreads(numThreads: Int) {
        optionsBuilder.setNumThreads(numThreads)
        recreateDetector()
    }

    override fun setUseNNAPI(isChecked: Boolean) {
        throw UnsupportedOperationException(
            "Manipulating the hardware accelerators is not allowed in the Task"
                    + " library currently. Only CPU is allowed."
        )
    }

    private fun recreateDetector(){
        objectDetector.close()
        objectDetector =
            ObjectDetector.createFromBufferAndOptions(modelBuffer, optionsBuilder.build())
    }

    companion object{
        private const val TAG = "TFLiteObjectDetectionAPIModelWithTaskApi"
        private const val NUM_DETECTIONS = 10


        fun create(
            context: Context,
            modelFilename: String,
            labelFilename: String,
            inputSize: Int,
            isQuantized: Boolean
        ): TFLiteObjectDetectionAPIModelKt{
            return TFLiteObjectDetectionAPIModelKt(context, modelFilename)
        }
    }
}