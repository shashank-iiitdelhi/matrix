package com.example.matrix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.matrix.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        binding.btnNext.setOnClickListener {
            val rows1 = binding.etRows1.text.toString().toIntOrNull()
            val cols1 = binding.etCols1.text.toString().toIntOrNull()
            val rows2 = binding.etRows2.text.toString().toIntOrNull()
            val cols2 = binding.etCols2.text.toString().toIntOrNull()

            if (validateDimensions(rows1, cols1, rows2, cols2)) {
                navigateToMatrixInput(rows1!!, cols1!!, rows2!!, cols2!!)
            }
        }
    }

    private fun validateDimensions(
        rows1: Int?, cols1: Int?,
        rows2: Int?, cols2: Int?
    ): Boolean {
        return when {
            rows1 == null || cols1 == null || rows2 == null || cols2 == null -> {
                showToast("Please enter valid dimensions for both matrices")
                false
            }
            rows1 <= 0 || cols1 <= 0 || rows2 <= 0 || cols2 <= 0 -> {
                showToast("Dimensions must be positive numbers")
                false
            }
            else -> true
        }
    }

    private fun navigateToMatrixInput(rows1: Int, cols1: Int, rows2: Int, cols2: Int) {
        val intent = Intent(this, MatrixInputActivity::class.java).apply {
            putExtra("MATRIX1_ROWS", rows1)
            putExtra("MATRIX1_COLS", cols1)
            putExtra("MATRIX2_ROWS", rows2)
            putExtra("MATRIX2_COLS", cols2)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Native methods for future matrix operations
    external fun addMatrices(
        matrixA: Array<DoubleArray>,
        matrixB: Array<DoubleArray>
    ): Array<DoubleArray>

    external fun subtractMatrices(
        matrixA: Array<DoubleArray>,
        matrixB: Array<DoubleArray>
    ): Array<DoubleArray>

    external fun multiplyMatrices(
        matrixA: Array<DoubleArray>,
        matrixB: Array<DoubleArray>
    ): Array<DoubleArray>

    companion object {
        init {
            System.loadLibrary("matrix")
        }
    }
}