package id.riverflows.camerasimulator.customview

import id.riverflows.lib_task_api.detection.tflite.Detector

interface ResultView {
    fun setResult(results: List<Detector.Recognition>)
}