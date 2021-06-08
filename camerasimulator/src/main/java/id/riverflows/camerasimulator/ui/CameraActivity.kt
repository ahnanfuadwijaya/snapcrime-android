package id.riverflows.camerasimulator.ui

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image
import android.media.ImageReader
import android.os.*
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import id.riverflows.camerasimulator.R
import id.riverflows.camerasimulator.env.ImageUtils
import id.riverflows.camerasimulator.env.Logger
import java.nio.ByteBuffer

abstract class CameraActivity: AppCompatActivity(), CompoundButton.OnCheckedChangeListener, ImageReader.OnImageAvailableListener, Camera.PreviewCallback, View.OnClickListener {
    protected var previewWidth = 0
    protected var previewHeight = 0
    private var yRowStride = 0

    private var debug = false
    private var useCamera2API = false
    private var isProcessingFrame = false

    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null
    private var postInferenceCallback: Runnable? = null
    private var imageConverter: Runnable? = null

    private val yuvBytes = Array(3){ ByteArray(0) }

    private var rgbBytes: IntArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        LOGGER.d("onCreate $this")
        super.onCreate(null)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_simulator_main)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (hasPermission()) {
            setFragment()
        } else {
            requestPermission()
        }
    }

    @Synchronized
    override fun onStart() {
        LOGGER.d("onStart $this")
        super.onStart()
    }

    @Synchronized
    override fun onResume() {
        super.onResume()
        handlerThread = HandlerThread("inference")
        handlerThread?.let {
            it.start()
            handler = Handler(it.looper)
        }
    }

    @Synchronized
    override fun onPause() {
        LOGGER.d("onPause $this")
        handlerThread?.let {
            it.quitSafely()
            try {
                it.join()
                handlerThread = null
                handler = null
            } catch (e: InterruptedException) {
                LOGGER.e(e, "Exception!")
            }
        }
        super.onPause()
    }

    @Synchronized
    override fun onStop() {
        LOGGER.d("onStop $this")
        super.onStop()
    }

    @Synchronized
    override fun onDestroy() {
        LOGGER.d("onDestroy $this")
        super.onDestroy()
    }

    @Synchronized
    protected fun runInBackground(r: Runnable){
        handler?.post(r)
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        if (isProcessingFrame) {
            LOGGER.w("Dropping frame!")
            return
        }

        try {
            if(rgbBytes == null){
                val previewSize = camera.parameters.previewSize
                previewHeight = previewSize.height
                previewWidth = previewSize.width
                rgbBytes = IntArray(previewWidth * previewHeight)
                onPreviewSizeChosen(Size(previewSize.width, previewSize.height), 90)
            }
        } catch (e: Exception) {
            LOGGER.e(e, "Exception!")
            return
        }

        isProcessingFrame = true
        yuvBytes[0] = data
        yRowStride = previewWidth

        rgbBytes?.let {
            imageConverter = Runnable {
                ImageUtils.convertYUV420SPToARGB8888(
                    data,
                    previewWidth,
                    previewHeight,
                    it
                )
            }
        }

        postInferenceCallback = Runnable {
            camera.addCallbackBuffer(data)
            isProcessingFrame = false
        }
        processImage()
    }

    override fun onImageAvailable(reader: ImageReader) {
        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return
        }
        if(rgbBytes == null){
            rgbBytes = IntArray(previewWidth * previewHeight)
        }
        try {
            val image = reader.acquireLatestImage() ?: return
            if (isProcessingFrame) {
                image.close()
                return
            }
            isProcessingFrame = true
            Trace.beginSection("imageAvailable")
            val planes = image.planes
            fillBytes(planes, yuvBytes)
            yRowStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride

            rgbBytes?.let {
                imageConverter = Runnable {
                    ImageUtils.convertYUV420ToARGB8888(
                        yuvBytes[0],
                        yuvBytes[1],
                        yuvBytes[2],
                        previewWidth,
                        previewHeight,
                        yRowStride,
                        uvRowStride,
                        uvPixelStride,
                        it
                    )
                }
            }
            postInferenceCallback = Runnable {
                image.close()
                isProcessingFrame = false
            }
            processImage()
        } catch (e: java.lang.Exception) {
            LOGGER.e(e, "Exception!")
            Trace.endSection()
            return
        }
        Trace.endSection()
    }

    protected fun fillBytes(planes: Array<Image.Plane>, yuvBytes: Array<ByteArray>){
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer: ByteBuffer = planes[i].buffer
            LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity())
            yuvBytes[i] = ByteArray(buffer.capacity())
            buffer.get(yuvBytes[i])
        }
    }

    protected fun readyForNextImage(){
        postInferenceCallback?.run()
    }



    protected fun getRgbBytes(): IntArray?{
        imageConverter?.run()
        return rgbBytes
    }

    fun isDebug(): Boolean = debug

    protected fun getLuminanceStride(): Int = yRowStride

    private fun hasPermission(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
                Toast.makeText(
                    this@CameraActivity,
                    "Camera permission is required for this demo",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            requestPermissions(arrayOf(PERMISSION_CAMERA), PERMISSIONS_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST) {
            if (allPermissionsGranted(grantResults)) {
                setFragment()
            } else {
                requestPermission()
            }
        }
    }

    protected fun setFragment(){
        val cameraId: String? = chooseCamera()
        val fragment: Fragment
        if (useCamera2API) {
            val camera2Fragment = CameraConnectionFragment.newInstance(
                object : CameraConnectionFragment.Companion.ConnectionCallback {
                    override fun onPreviewSizeChosen(size: Size, cameraRotation: Int) {
                        previewHeight = size.height
                        previewWidth = size.width
                        onPreviewSizeChosen(size, cameraRotation)
                    }
                },
                this,
                getLayoutId(),
                getDesiredPreviewFrameSize()
            )
            camera2Fragment.setCamera(cameraId)
            fragment = camera2Fragment
        } else {
            fragment =
                LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize())
        }
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private fun chooseCamera(): String?{
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }
                characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue


                // Fallback to camera1 API for internal cameras that don't have full support.
                // This should help with legacy situations where using the camera2 API causes
                // distorted or otherwise broken previews.
                useCamera2API = (facing == CameraCharacteristics.LENS_FACING_EXTERNAL
                        || isHardwareLevelSupported(
                    characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
                ))
                LOGGER.i("Camera API lv2?: %s", useCamera2API)
                return cameraId
            }
        } catch (e: CameraAccessException) {
            LOGGER.e(e, "Not allowed to access camera")
        }
        return null
    }

    private fun isHardwareLevelSupported(characteristics: CameraCharacteristics, requiredLevel: Int): Boolean{
        val deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return requiredLevel == deviceLevel
        }
        return requiredLevel <= deviceLevel as Int
    }

    protected fun getScreenOrientation(): Int{
        return when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_90 -> 90
            else -> 0
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        setUseNNAPI(isChecked)
        //if (isChecked) apiSwitchCompat.setText("NNAPI") else apiSwitchCompat.setText("TFLITE")
    }

    override fun onClick(v: View?) {
        /*if (v?.id == R.id.plus) {
            val threads: String = threadsTextView.getText().toString().trim { it <= ' ' }
            var numThreads = threads.toInt()
            if (numThreads >= 9) return
            numThreads++
            threadsTextView.setText(numThreads.toString())
            setNumThreads(numThreads)
        } else if (v?.id == R.id.minus) {
            val threads: String = threadsTextView.getText().toString().trim { it <= ' ' }
            var numThreads = threads.toInt()
            if (numThreads == 1) {
                return
            }
            numThreads--
            threadsTextView.setText(numThreads.toString())
            setNumThreads(numThreads)
        }*/
    }

    protected abstract fun processImage()
    protected abstract fun getLayoutId(): Int
    protected abstract fun getDesiredPreviewFrameSize(): Size
    protected abstract fun onPreviewSizeChosen(size: Size, rotation: Int)
    protected abstract fun setNumThreads(numThreads: Int)

    protected abstract fun setUseNNAPI(isChecked: Boolean)

    companion object{
        private val LOGGER = Logger()
        private const val PERMISSIONS_REQUEST = 1
        private const val PERMISSION_CAMERA = Manifest.permission.CAMERA

        private fun allPermissionsGranted(grantResults: IntArray): Boolean{
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }
}