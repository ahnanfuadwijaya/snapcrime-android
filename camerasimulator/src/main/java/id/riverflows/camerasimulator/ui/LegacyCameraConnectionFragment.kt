package id.riverflows.camerasimulator.ui

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Bundle
import android.os.HandlerThread
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import androidx.fragment.app.Fragment
import id.riverflows.camerasimulator.R
import id.riverflows.camerasimulator.customview.AutoFitTextureView
import id.riverflows.camerasimulator.env.ImageUtils
import id.riverflows.camerasimulator.env.Logger
import java.io.IOException

class LegacyCameraConnectionFragment(
    private val imageListener: Camera.PreviewCallback,
    private val layout: Int,
    private val desiredSize: Size
): Fragment() {
    private var camera: Camera? = null
    private lateinit var textureView: AutoFitTextureView
    private var availableSurfaceTexture: SurfaceTexture? = null
    /** An additional thread for running tasks that shouldn't block the UI.  */
    private var backgroundThread: HandlerThread? = null

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener{
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            availableSurfaceTexture = surface
            startCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textureView = view.findViewById<View>(R.id.texture) as AutoFitTextureView
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (textureView.isAvailable) {
            startCamera()
        } else {
            textureView.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onPause() {
        stopCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun startCamera(){
        val index: Int = getCameraId()
        camera = Camera.open(index)

        try {
            camera?.let {
                val parameters = it.parameters
                val focusModes = parameters.supportedFocusModes
                if (focusModes != null
                    && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                ) {
                    parameters.focusMode =
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                }
                val cameraSizes = parameters.supportedPreviewSizes
                val sizes = arrayOf<Size>()
                var i = 0
                for (size in cameraSizes) {
                    sizes[i++] = Size(size.width, size.height)
                }
                val previewSize = CameraConnectionFragment.chooseOptimalSize(
                    sizes, desiredSize.width, desiredSize.height
                )
                parameters.setPreviewSize(previewSize.width, previewSize.height)
                it.setDisplayOrientation(90)
                it.parameters = parameters
                it.setPreviewTexture(availableSurfaceTexture)
                it.setPreviewCallbackWithBuffer(imageListener)
                val s: Camera.Size = it.parameters.previewSize
                it.addCallbackBuffer(ByteArray(ImageUtils.getYUVByteSize(s.height, s.width)))

                textureView.setAspectRatio(s.height, s.width)

                it.startPreview()
            }
        } catch (exception: IOException) {
            camera?.release()
        }
    }

    private fun stopCamera(){
        camera?.let {
            it.stopPreview()
            it.setPreviewCallback(null)
            it.release()
            camera = null
        }
    }

    private fun getCameraId(): Int{
        val ci = CameraInfo()
        for (i in 0 until Camera.getNumberOfCameras()) {
            Camera.getCameraInfo(i, ci)
            if (ci.facing == CameraInfo.CAMERA_FACING_BACK) return i
        }
        return -1 // No camera found
    }

    private fun startBackgroundThread(){
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread!!.start()
    }

    private fun stopBackgroundThread(){
        backgroundThread!!.quitSafely()
        try {
            backgroundThread!!.join()
            backgroundThread = null
        } catch (e: InterruptedException) {
            LOGGER.e(e, "Exception!")
        }
    }

    companion object{
        private val LOGGER = Logger()
        private val ORIENTATIONS = SparseIntArray()

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }
}