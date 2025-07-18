// app/src/main/java/com/example/ai_life/data/ml/InterpreterUtil.kt
package com.example.ai_life.data.ml

import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.DataType
import java.io.File

object InterpreterUtil {

    /**
     * Crea un Interpreter de TensorFlow Lite leyendo el archivo .tflite dado.
     */
    fun createInterpreter(modelFile: File): Interpreter {
        val options = Interpreter.Options().apply {
            setNumThreads(2)
        }
        return Interpreter(modelFile, options)
    }

    /**
     * Ejecuta una inferencia nativa TFLite.
     * @param interpreter intérprete ya creado
     * @param inputArray floatArrayOf(age, temperature, heart_rate, spo2)
     * @return índice de la clase con mayor probabilidad
     */
    fun runInference(
        interpreter: Interpreter,
        inputArray: FloatArray
    ): Int {
        // preparamos los buffers de entrada y salida
        val inputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 4), DataType.FLOAT32)
        inputBuffer.loadArray(inputArray)

        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 7), DataType.FLOAT32)

        // ejecutamos inferencia
        interpreter.run(inputBuffer.buffer, outputBuffer.buffer.rewind())

        // NO cerramos el interpreter aquí, lo reutilizaremos

        // leemos el array de scores y devolvemos el argmax
        val scores = outputBuffer.floatArray
        return scores.indices.maxByOrNull { scores[it] } ?: 0
    }
}
