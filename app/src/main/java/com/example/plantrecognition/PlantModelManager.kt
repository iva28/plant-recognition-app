package com.example.plantrecognition

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.plantrecognition.ml.PlantModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.abs

class PlantModelManager {
    fun classifyImage(context: Context, image: Bitmap): String {
        val model = PlantModel.newInstance(context)
        val imageSize = 32
        val numChannels = 3
        val image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)


        val byteBuffer = convertBitmapToByteBuffer(image)

        val floatBuffer = FloatBuffer.allocate(imageSize * imageSize * numChannels)
        byteBuffer.rewind() // Reset the buffer position to the beginning

        for (i in 0 until floatBuffer.array().size) {
            floatBuffer.put(byteBuffer[i].toFloat())
        }
        floatBuffer.rewind() // Reset the buffer position to the beginning
        val tfBuffer = TensorBuffer.createFixedSize(
            intArrayOf(imageSize, imageSize, numChannels),
            DataType.FLOAT32
        )
        tfBuffer.loadArray(floatBuffer.array())
        val outputs = model.process(tfBuffer)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val confidence: FloatArray = outputFeature0.floatArray
        var maxPos = 0
        var maxConfidence = 0f
        for (i in confidence.indices) {
            Log.w("iva","${confidence[i]} ${abs(confidence[i])}")
            if (abs(confidence[i]) > maxConfidence) {
                maxConfidence = abs(confidence[i])
                maxPos = i
                Log.w("iva","${maxConfidence} ${maxPos}")

            }
        }
        val classes: Array<String> = arrayOf("daisy","dandelion","roses","sunflower","tulips")
        Log.w("iva", "${confidence.contentToString()}")
        model.close()
        return classes[maxPos]
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer =
            ByteBuffer.allocateDirect(32 * 32 * 3 * 4) // 32x32x3 float values (4 bytes each)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(32 * 32)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until 32) {
            for (j in 0 until 32) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16 and 0xFF) - 127.5f) / 127.5f) // Red channel
                byteBuffer.putFloat(((value shr 8 and 0xFF) - 127.5f) / 127.5f) // Green channel
                byteBuffer.putFloat(((value and 0xFF) - 127.5f) / 127.5f) // Blue channel
            }
        }
        return byteBuffer
    }
}
