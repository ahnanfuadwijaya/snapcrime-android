package id.riverflows.camerasimulator.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Typeface
import android.media.ImageReader.OnImageAvailableListener
import android.os.Bundle
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

class SimulatorMainActivity : CameraActivity(), OnImageAvailableListener {
    private enum class DetectorMode {
        TF_OD_API
    }
    private lateinit var trackingOverlay: OverlayView
    private var sensorOrientation: Int = 0
    private lateinit var detector: Detector
    private var lastProcessingTimeMs: Long = 0
    private var rgbFrameBitmap: Bitmap? = null
    private var croppedBitmap: Bitmap? = null
    private var cropCopyBitmap: Bitmap? = null
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
        TODO("Not yet implemented")
    }

    override fun getLayoutId(): Int {
        TODO("Not yet implemented")
    }

    override fun getDesiredPreviewFrameSize(): Size {
        TODO("Not yet implemented")
    }

    override fun onPreviewSizeChosen(size: Size, rotation: Int) {
        val textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            TEXT_SIZE_DIP,
            resources.displayMetrics
        )
        borderedText = BorderedText(textSizePx)
        borderedText.setTypeface(Typeface.MONOSPACE)

        tracker = MultiBoxTracker(this)

        var cropSize: Int =
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
        TODO("Not yet implemented")
    }

    override fun setUseNNAPI(isChecked: Boolean) {
        TODO("Not yet implemented")
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