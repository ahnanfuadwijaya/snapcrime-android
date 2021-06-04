package id.riverflows.camerasimulator.env

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Size
import id.riverflows.camerasimulator.env.Size.Companion.parseFromString
import java.io.Serializable

class Size: Comparable<Size>, Serializable {
    val height: Int
    val width: Int

    constructor(width: Int, height: Int){
        this.height = height
        this.width = width
    }

    constructor(bmp: Bitmap){
        this.height = bmp.height
        this.width = bmp.width
    }

    override fun compareTo(other: Size): Int = width * height - other.width * other.height

    fun aspectRatio(): Float {
        return width.toFloat() / height.toFloat()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is Size) {
            return false
        }

        val otherSize = other as Size
        return (width == otherSize.width && height == otherSize.height)
    }

    override fun hashCode(): Int {
        return width * 32713 + height
    }

    override fun toString(): String {
        return dimensionsAsString(width, height);
    }

    companion object{
        const val serialVersionUID = 7689808733290872361L

        fun getRotatedSize(size: Size, rotation: Int): Size {
            return if (rotation % 180 != 0) {
                // The phone is portrait, therefore the camera is sideways and frame should be rotated.
                Size(size.height, size.width)
            } else size
        }

        fun parseFromString(_sizeString: String): Size? {
            if (TextUtils.isEmpty(_sizeString)) {
                return null
            }

            val sizeString = _sizeString.trim()

            // The expected format is "<width>x<height>".
            val components = sizeString.split("x");
            return if (components.size == 2) {
                try {
                    val width = Integer.parseInt(components[0]);
                    val height = Integer.parseInt(components[1]);
                    Size(width, height);
                } catch (e: NumberFormatException) {
                    null
                }
            } else null
        }

        fun sizeStringToList(sizes: String): List<Size> {
            val sizeList = mutableListOf<Size>()
            val pairs = sizes.split(",")
            for (pair in pairs) {
                val size = parseFromString(pair)
                if (size != null) {
                    sizeList.add(size)
                }
            }
            return sizeList
        }

        fun sizeListToString(sizes: List<Size>): String {
            var sizesString = ""
            if (sizes.isNotEmpty()) {
                sizesString = sizes[0].toString()
                for (i in sizes.indices) {
                    sizesString += "," + sizes[i].toString()
                }
            }
            return sizesString
        }

        fun dimensionsAsString(width: Int, height: Int): String {
            return "$width x $height"
        }


    }
}