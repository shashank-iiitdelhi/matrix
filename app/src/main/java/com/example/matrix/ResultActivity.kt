package com.example.matrix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.matrix.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var matrixA: DoubleArray
    private lateinit var matrixB: DoubleArray
    private var rows1 = 0
    private var cols1 = 0
    private var rows2 = 0
    private var cols2 = 0

    enum class Operation { ADD, SUBTRACT, MULTIPLY }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getMatricesFromIntent()
        setupOperationButtons()
    }

    private fun getMatricesFromIntent() {
        matrixA = intent.getDoubleArrayExtra("MATRIX_A") ?: doubleArrayOf()
        matrixB = intent.getDoubleArrayExtra("MATRIX_B") ?: doubleArrayOf()
        rows1 = intent.getIntExtra("ROWS1", 2)
        cols1 = intent.getIntExtra("COLS1", 2)
        rows2 = intent.getIntExtra("ROWS2", 2)
        cols2 = intent.getIntExtra("COLS2", 2)
    }

    private fun setupOperationButtons() {
        binding.btnAdd.setOnClickListener { performOperation(Operation.ADD) }
        binding.btnSubtract.setOnClickListener { performOperation(Operation.SUBTRACT) }
        binding.btnMultiply.setOnClickListener { performOperation(Operation.MULTIPLY) }
    }

    private fun performOperation(operation: Operation) {
        try {
            val result = when (operation) {
                Operation.ADD -> {
                    if (rows1 != rows2 || cols1 != cols2) {
                        throw IllegalArgumentException("Matrices must have same dimensions for addition")
                    }
                    addMatrices(rows1, cols1, matrixA, matrixB)
                }
                Operation.SUBTRACT -> {
                    if (rows1 != rows2 || cols1 != cols2) {
                        throw IllegalArgumentException("Matrices must have same dimensions for subtraction")
                    }
                    subtractMatrices(rows1, cols1, matrixA, matrixB)
                }
                Operation.MULTIPLY -> {
                    if (cols1 != rows2) {
                        throw IllegalArgumentException("Matrices dimensions mismatch for multiplication")
                    }
                    multiplyMatrices(rows1, cols1, rows2, cols2, matrixA, matrixB)
                }
            }
            displayResultMatrix(result, operation)
        } catch (e: Exception) {
            showToast(e.message ?: "Operation failed")
        }
    }

    private fun displayResultMatrix(matrix: DoubleArray, operation: Operation) {
        binding.containerResult.removeAllViews()

        // Determine result dimensions based on operation
        val (rows, cols) = when (operation) {
            Operation.ADD, Operation.SUBTRACT -> rows1 to cols1
            Operation.MULTIPLY -> rows1 to cols2
        }

        // Dynamically create TextViews for each element
        for (i in 0 until rows) {
            val rowLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
            }

            for (j in 0 until cols) {
                val textView = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    ).apply {
                        marginEnd = 8.dpToPx()
                        marginStart = 8.dpToPx()
                    }
                    text = "%.2f".format(matrix[i * cols + j])
                    textSize = 16f
                }
                rowLayout.addView(textView)
            }
            binding.containerResult.addView(rowLayout)
        }
    }

    private external fun addMatrices(rows: Int, cols: Int, matrixA: DoubleArray, matrixB: DoubleArray): DoubleArray
    private external fun subtractMatrices(rows: Int, cols: Int, matrixA: DoubleArray, matrixB: DoubleArray): DoubleArray
    private external fun multiplyMatrices(rows1: Int, cols1: Int, rows2: Int, cols2: Int, matrixA: DoubleArray, matrixB: DoubleArray): DoubleArray

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    companion object {
        init {
            System.loadLibrary("matrix")
        }
    }
}
