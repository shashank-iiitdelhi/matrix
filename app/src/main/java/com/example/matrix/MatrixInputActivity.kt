package com.example.matrix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.matrix.databinding.ActivityMatrixInputBinding

class MatrixInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatrixInputBinding
    private var rows1 = 0
    private var cols1 = 0
    private var rows2 = 0
    private var cols2 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatrixInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDimensionsFromIntent()
        createMatrixInputs()
        setupCalculateButton()
    }

    private fun getDimensionsFromIntent() {
        rows1 = intent.getIntExtra("MATRIX1_ROWS", 2)
        cols1 = intent.getIntExtra("MATRIX1_COLS", 2)
        rows2 = intent.getIntExtra("MATRIX2_ROWS", 2)
        cols2 = intent.getIntExtra("MATRIX2_COLS", 2)
    }

    private fun createMatrixInputs() {
        createMatrixGrid(binding.containerMatrix1, rows1, cols1)
        createMatrixGrid(binding.containerMatrix2, rows2, cols2)
    }

    private fun createMatrixGrid(container: LinearLayout, rows: Int, cols: Int) {
        container.removeAllViews()
        for (i in 0 until rows) {
            val rowLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
            }

            for (j in 0 until cols) {
                val editText = EditText(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    ).apply {
                        marginEnd = 8.dpToPx()
                        marginStart = 8.dpToPx()
                    }
                    hint = "0.0"
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                            android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                }
                rowLayout.addView(editText)
            }
            container.addView(rowLayout)
        }
    }

    private fun setupCalculateButton() {
        binding.btnCalculate.setOnClickListener {
            try {
                val matrixA = getMatrixValues(binding.containerMatrix1, rows1, cols1)
                val matrixB = getMatrixValues(binding.containerMatrix2, rows2, cols2)

                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("MATRIX_A", flattenMatrix(matrixA))
                    putExtra("MATRIX_B", flattenMatrix(matrixB))
                    putExtra("ROWS1", rows1)
                    putExtra("COLS1", cols1)
                    putExtra("ROWS2", rows2)
                    putExtra("COLS2", cols2)
                }
                startActivity(intent)
            } catch (e: NumberFormatException) {
                showToast("Please enter valid numbers in all fields")
            }
        }
    }

    private fun getMatrixValues(container: LinearLayout, rows: Int, cols: Int): Array<DoubleArray> {
        val matrix = Array(rows) { DoubleArray(cols) }
        for (i in 0 until rows) {
            val row = container.getChildAt(i) as LinearLayout
            for (j in 0 until cols) {
                val editText = row.getChildAt(j) as EditText
                matrix[i][j] = editText.text.toString().toDouble()
            }
        }
        return matrix
    }

    private fun flattenMatrix(matrix: Array<DoubleArray>): DoubleArray {
        return matrix.flatMap { it.toList() }.toDoubleArray()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}