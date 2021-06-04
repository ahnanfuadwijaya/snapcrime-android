package id.riverflows.camerasimulator.env

import android.annotation.SuppressLint
import android.util.Log
import java.util.*

class Logger{
    private val tag: String
    private val messagePrefix: String
    private var minLogLevel = DEFAULT_MIN_LOG_LEVEL

    constructor(tag: String, messagePrefix: String?){
        this.tag = tag
        val prefix = messagePrefix ?: getCallerSimpleName()
        this.messagePrefix = if (prefix.isNotEmpty()) "$prefix: " else prefix
    }

    constructor(): this(DEFAULT_TAG, null)

    constructor(messagePrefix: String): this(DEFAULT_TAG, messagePrefix)

    constructor(minLogLevel: Int): this(DEFAULT_TAG, null){
        this.minLogLevel = minLogLevel
    }

    fun setMinLogLevel(minLogLevel: Int) {
        this.minLogLevel = minLogLevel;
    }

    fun isLoggable(logLevel: Int): Boolean {
        return logLevel >= minLogLevel || Log.isLoggable(tag, logLevel);
    }

    fun toMessage(format: String, vararg args: Any): String {
        return "$messagePrefix${(if(args.isNotEmpty()) String.format(format, args) else format)}"
    }

    @SuppressLint("LogTagMismatch")
    fun v(format: String, vararg args: Any){
        if (isLoggable(Log.VERBOSE)) {
            Log.v(tag, toMessage(format, *args))
        }
    }

    @SuppressLint("LogTagMismatch")
    fun v(t: Throwable, format: String, vararg args: Any){
        if (isLoggable(Log.VERBOSE)) {
            Log.v(tag, toMessage(format, *args), t)
        }
    }

    @SuppressLint("LogTagMismatch")
    fun d(format: String, vararg args: Any) {
        if (isLoggable(Log.DEBUG)) {
            Log.d(tag, toMessage(format, *args))
        }
    }

    @SuppressLint("LogTagMismatch")
    fun d(t: Throwable, format: String, vararg args: Any) {
        if (isLoggable(Log.DEBUG)) {
            Log.d(tag, toMessage(format, *args), t)
        }
    }

    @SuppressLint("LogTagMismatch")
    fun i(format: String, vararg args: Any) {
        if (isLoggable(Log.INFO)) {
            Log.i(tag, toMessage(format, *args))
        }
    }

    @SuppressLint("LogTagMismatch")
    fun i(t: Throwable?, format: String, vararg args: Any) {
        if (isLoggable(Log.INFO)) {
            Log.i(tag, toMessage(format, *args), t)
        }
    }

    @SuppressLint("LogTagMismatch")
    fun w(format: String, vararg args: Any) {
        if (isLoggable(Log.WARN)) {
            Log.w(tag, toMessage(format, *args))
        }
    }

    @SuppressLint("LogTagMismatch")
    fun w(t: Throwable?, format: String, vararg args: Any) {
        if (isLoggable(Log.WARN)) {
            Log.w(tag, toMessage(format, *args), t)
        }
    }

    @SuppressLint("LogTagMismatch")
    fun e(format: String, vararg args: Any) {
        if (isLoggable(Log.ERROR)) {
            Log.e(tag, toMessage(format, *args))
        }
    }

    @SuppressLint("LogTagMismatch")
    fun e(t: Throwable?, format: String, vararg args: Any) {
        if (isLoggable(Log.ERROR)) {
            Log.e(tag, toMessage(format, *args), t)
        }
    }


    companion object{
        private const val DEFAULT_TAG = "tensorflow"
        private const val DEFAULT_MIN_LOG_LEVEL = Log.DEBUG
        private val IGNORED_CLASS_NAMES: MutableSet<String?> = HashSet<String?>(3)
        init {
            IGNORED_CLASS_NAMES.add("dalvik.system.VMStack")
            IGNORED_CLASS_NAMES.add("java.lang.Thread")
            IGNORED_CLASS_NAMES.add(Logger::class.java.canonicalName)
        }

        fun getCallerSimpleName(): String{

            // Get the current callstack so we can pull the class of the caller off of it.
            val stackTrace = Thread.currentThread().stackTrace

            for (elem in stackTrace) {
                val className = elem.className
                if (!IGNORED_CLASS_NAMES.contains(className)) {
                    // We're only interested in the simple name of the class, not the complete package.
                    val classParts: List<String> = className.split("\\.")
                    return classParts[classParts.size - 1]
                }
            }

            return Logger::class.java.simpleName
        }
    }
}