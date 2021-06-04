package id.riverflows.camerasimulator.tracking

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Cap
import android.graphics.Paint.Join
import android.text.TextUtils
import android.util.Pair
import android.util.TypedValue
import id.riverflows.camerasimulator.env.BorderedText
import id.riverflows.camerasimulator.env.ImageUtils
import id.riverflows.camerasimulator.env.Logger
import id.riverflows.lib_task_api.detection.tflite.Detector.Recognition
import java.util.*

class MultiBoxTracker(context: Context) {
    val screenRect: MutableList<Pair<Float, RectF>> = LinkedList()
    private val availableColors: Queue<Int> = LinkedList()
    private val trackedObjects: MutableList<TrackedRecognition> = LinkedList()
    private val boxPaint = Paint()
    private val textSizePx: Float
    private val borderedText: BorderedText
    private var frameToCanvasMatrix: Matrix? = null
    private var frameWidth = 0
    private var frameHeight = 0
    private var sensorOrientation = 0
    private val logger = Logger()

    init {
        for(color in COLORS){
            availableColors.add(color)
        }
        boxPaint.color = Color.RED
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = 10.0f
        boxPaint.strokeCap = Cap.ROUND
        boxPaint.strokeJoin = Join.ROUND
        boxPaint.strokeMiter = 100f
        textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, context.resources.displayMetrics
        )
        borderedText = BorderedText(textSizePx)
    }

    @Synchronized
    fun setFrameConfiguration(width: Int, height: Int, sensorOrientation: Int) {
        frameWidth = width
        frameHeight = height
        this.sensorOrientation = sensorOrientation
    }

    @Synchronized
    fun drawDebug(canvas: Canvas) {
        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 60.0f

        val boxPaint = Paint()
        boxPaint.color = Color.RED
        boxPaint.alpha = 200
        boxPaint.style = Paint.Style.STROKE

        for (detection: Pair<Float, RectF> in screenRect) {
            val rect = detection.second
            canvas.drawRect(rect, boxPaint)
            canvas.drawText("" + detection.first, rect.left, rect.top, textPaint)
            borderedText.drawText(canvas, rect.centerX(), rect.centerY(), "" + detection.first)
        }
    }

    @Synchronized
    fun draw(canvas: Canvas){
        val rotated = sensorOrientation % 180 == 90
        val multiplier = Math.min(
            canvas.height / (if (rotated) frameWidth else frameHeight).toFloat(),
            canvas.width / (if (rotated) frameHeight else frameWidth).toFloat()
        )
        frameToCanvasMatrix = ImageUtils.getTransformationMatrix(
            frameWidth,
            frameHeight,
            (multiplier * if (rotated) frameHeight else frameWidth).toInt(),
            (multiplier * if (rotated) frameWidth else frameHeight).toInt(),
            sensorOrientation,
            false
        )
        for (recognition in trackedObjects) {
            val trackedPos = RectF(recognition.location)
            getFrameToCanvasMatrix()!!.mapRect(trackedPos)
            boxPaint.color = recognition.color
            val cornerSize = Math.min(trackedPos.width(), trackedPos.height()) / 8.0f
            canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint)
            val labelString = if (!TextUtils.isEmpty(recognition.title)) String.format(
                "%s %.2f", recognition.title,
                100 * recognition.detectionConfidence
            ) else String.format("%.2f", 100 * recognition.detectionConfidence)
            //            borderedText.drawText(canvas, trackedPos.left + cornerSize, trackedPos.top,
            // labelString);
            borderedText.drawText(
                canvas, trackedPos.left + cornerSize, trackedPos.top, "$labelString%", boxPaint
            )
        }
    }

    @Synchronized
    fun trackResults(results: List<Recognition>, timestamp: Long) {
        logger.i("Processing %d results from %d", results.size, timestamp)
        processResults(results)
    }

    private fun getFrameToCanvasMatrix(): Matrix? {
        return frameToCanvasMatrix
    }

    private fun processResults(results: List<Recognition>) {
        val rectToTrack: MutableList<Pair<Float, Recognition>> = LinkedList<Pair<Float, Recognition>>()

        screenRect.clear()
        val rgbFrameToScreen = Matrix(getFrameToCanvasMatrix())

        for (result in results) {
            val detectionFrameRect = RectF(result.getLocation())

            val detectionScreenRect = RectF()
            rgbFrameToScreen.mapRect(detectionScreenRect, detectionFrameRect)

            logger.v(
                "Result! Frame: " + result.getLocation() + " mapped to screen:" + detectionScreenRect)

            screenRect.add(Pair<Float, RectF>(result.getConfidence(), detectionScreenRect))

            if (detectionFrameRect.width() < MIN_SIZE || detectionFrameRect.height() < MIN_SIZE) {
                logger.w("Degenerate rectangle! " + detectionFrameRect)
                continue
            }

            rectToTrack.add(Pair<Float, Recognition>(result.getConfidence(), result))
        }

        trackedObjects.clear();
        if (rectToTrack.isEmpty()) {
            logger.v("Nothing to track, aborting.")
            return
        }

        for (potential in rectToTrack) {
            val trackedRecognition = TrackedRecognition();
            trackedRecognition.detectionConfidence = potential.first
            trackedRecognition.location = RectF(potential.second.getLocation())
            trackedRecognition.title = potential.second.getTitle()
            trackedRecognition.color = COLORS[trackedObjects.size]
            trackedObjects.add(trackedRecognition)

            if (trackedObjects.size >= COLORS.size) {
                break
            }
        }
    }

    companion object{
        private const val TEXT_SIZE_DIP = 18f
        private const val MIN_SIZE = 16.0f
        private val COLORS: List<Int> = listOf(
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.WHITE,
            Color.parseColor("#55FF55"),
            Color.parseColor("#FFA500"),
            Color.parseColor("#FF8888"),
            Color.parseColor("#AAAAFF"),
            Color.parseColor("#FFFFAA"),
            Color.parseColor("#55AAAA"),
            Color.parseColor("#AA33AA"),
            Color.parseColor("#0D0068")
        )
    }
    class TrackedRecognition{
        var location: RectF? = null
        var detectionConfidence = 0f
        var color = 0
        var title: String? = null
    }
}