package id.riverflows.camerasimulator.ui

import android.graphics.*
import android.media.ImageReader.OnImageAvailableListener
import android.os.Bundle
import android.os.SystemClock
import android.util.Size
import android.util.TypedValue
import android.widget.Toast
import id.riverflows.camerasimulator.R
import id.riverflows.camerasimulator.customview.OverlayView
import id.riverflows.camerasimulator.env.BorderedText
import id.riverflows.camerasimulator.env.ImageUtils
import id.riverflows.camerasimulator.env.Logger
import id.riverflows.camerasimulator.tracking.MultiBoxTracker
import id.riverflows.lib_task_api.detection.tflite.Detector
import id.riverflows.lib_task_api.detection.tflite.TFLiteObjectDetectionAPIModel
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class SimulatorMainActivity : CameraActivity(), OnImageAvailableListener {
    private enum class DetectorMode {
        TF_OD_API
    }
    private lateinit var trackingOverlay: OverlayView
    private var sensorOrientation: Int = 0
    private lateinit var detector: Detector
    private var lastProcessingTimeMs: Long = 0
    private lateinit var rgbFrameBitmap: Bitmap
    private lateinit var croppedBitmap: Bitmap
    private lateinit var cropCopyBitmap: Bitmap
    private var computingDetection = false
    private var timestamp: Long = 0
    private lateinit var frameToCropTransform: Matrix
    private lateinit var cropToFrameTransform: Matrix
    private lateinit var tracker: MultiBoxTracker
    private lateinit var borderedText: BorderedText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulator_main)
    }

    override fun processImage() {
        ++timestamp
        val currTimestamp = timestamp
        trackingOverlay.postInvalidate()

        // No mutex needed as this method is not reentrant.

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage()
            return
        }
        computingDetection = true
        LOGGER.i("Preparing image $currTimestamp for detection in bg thread.")

        rgbFrameBitmap.setPixels(
            getRgbBytes(),
            0,
            previewWidth,
            0,
            0,
            previewWidth,
            previewHeight
        )

        readyForNextImage()

        val canvas = Canvas(croppedBitmap)
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null)
        // For examining the actual TF input.
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap)
        }

        runInBackground {
            LOGGER.i("Running detection on image $currTimestamp")
            val startTime = SystemClock.uptimeMillis()
            val results = detector.recognizeImage(croppedBitmap)
            lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime
            cropCopyBitmap = Bitmap.createBitmap(croppedBitmap)
            val cpyCanvas = Canvas(cropCopyBitmap)
            val paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.0f
            var minimumConfidence: Float = MINIMUM_CONFIDENCE_TF_OD_API
            when (MODE) {
                DetectorMode.TF_OD_API -> minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API
            }
            val mappedRecognitions: ArrayList<Detector.Recognition> =
                ArrayList()
            for (result in results) {
                val location = result.getLocation()
                if (location != null && result.getConfidence() >= minimumConfidence) {
                    cpyCanvas.drawRect(location, paint)
                    cropToFrameTransform.mapRect(location)
                    result.setLocation(location)
                    mappedRecognitions.add(result)
                }
            }
            tracker.trackResults(mappedRecognitions, currTimestamp)
            trackingOverlay.postInvalidate()
            computingDetection = false
            runOnUiThread {
                /*showFrameInfo(previewWidth.toString() + "x" + previewHeight)
                showCropInfo(
                    cropCopyBitmap.getWidth().toString() + "x" + cropCopyBitmap.getHeight()
                )
                showInference(lastProcessingTimeMs.toString() + "ms")*/
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_camera_connection_tracking

    override fun getDesiredPreviewFrameSize(): Size = DESIRED_PREVIEW_SIZE

    override fun onPreviewSizeChosen(size: Size, rotation: Int) {
        val textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            TEXT_SIZE_DIP,
            resources.displayMetrics
        )
        borderedText = BorderedText(textSizePx)
        borderedText.setTypeface(Typeface.MONOSPACE)

        tracker = MultiBoxTracker(this)

        val cropSize: Int =
            TF_OD_API_INPUT_SIZE

        try {
            detector = TFLiteObjectDetectionAPIModel.create(
                this,
                TF_OD_API_MODEL_FILE,
                TF_OD_API_LABELS_FILE,
                TF_OD_API_INPUT_SIZE,
                TF_OD_API_IS_QUANTIZED
            )
            TF_OD_API_INPUT_SIZE
        } catch (e: IOException) {
            e.printStackTrace()
            LOGGER.e(
                e,
                "Exception initializing Detector!"
            )
            val toast = Toast.makeText(
                applicationContext, "Detector could not be initialized", Toast.LENGTH_SHORT
            )
            toast.show()
            finish()
        }

        previewWidth = size.width
        previewHeight = size.height

        sensorOrientation = rotation - getScreenOrientation()
        LOGGER.i(
            "Camera orientation relative to screen canvas: %d",
            sensorOrientation
        )

        LOGGER.i(
            "Initializing at size %dx%d",
            previewWidth,
            previewHeight
        )
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888)

        frameToCropTransform = ImageUtils.getTransformationMatrix(
            previewWidth,
            previewHeight,
            cropSize,
            cropSize,
            sensorOrientation,
            MAINTAIN_ASPECT
        )

        cropToFrameTransform = Matrix()
        frameToCropTransform.invert(cropToFrameTransform)

        trackingOverlay = findViewById(R.id.tracking_overlay)
        trackingOverlay.addCallback(
            object : OverlayView.DrawCallback{
                override fun drawCallback(canvas: Canvas) {
                    tracker.draw(canvas)
                    if (isDebug()) {
                        tracker.drawDebug(canvas)
                    }
                }
            })

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation)
    }

    override fun setNumThreads(numThreads: Int) {
        runInBackground { detector.setNumThreads(numThreads) }
    }

    override fun setUseNNAPI(isChecked: Boolean) {
        runInBackground {
            try {
                detector.setUseNNAPI(isChecked)
            } catch (e: UnsupportedOperationException) {
                LOGGER.e(
                    e,
                    "Failed to set \"Use NNAPI\"."
                )
                runOnUiThread { Toast.makeText(this, e.message, Toast.LENGTH_LONG).show() }
            }
        }
    }

    companion object{
        private val LOGGER = Logger()

        // Configuration values for the prepackaged SSD model.
        private const val TF_OD_API_INPUT_SIZE = 300
        private const val TF_OD_API_IS_QUANTIZED = true
        private const val TF_OD_API_MODEL_FILE = "detect.tflite"
        private const val TF_OD_API_LABELS_FILE = "labelmap.txt"
        private val MODE = DetectorMode.TF_OD_API

        // Minimum detection confidence to track a detection.
        private const val MINIMUM_CONFIDENCE_TF_OD_API = 0.5f
        private const val MAINTAIN_ASPECT = false
        private val DESIRED_PREVIEW_SIZE = Size(640, 480)
        private const val SAVE_PREVIEW_BITMAP = false
        private const val TEXT_SIZE_DIP = 10f
    }
}