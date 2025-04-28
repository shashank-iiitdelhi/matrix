// native-lib.cpp

#include <jni.h>
#include <vector>
#include <Eigen/Dense>

using namespace Eigen;

extern "C" {

// Read a flat Java array (row-major) into an Eigen::MatrixXd
static MatrixXd javaArrayToEigenRowMajor(JNIEnv* env, jdoubleArray array, jint rows, jint cols) {
    jdouble* elems = env->GetDoubleArrayElements(array, nullptr);
    MatrixXd M(rows, cols);
    for (int i = 0; i < rows; ++i) {
        for (int j = 0; j < cols; ++j) {
            // Java stores row-major: element (i,j) is at elems[i*cols + j]
            M(i, j) = elems[i * cols + j];
        }
    }
    // We only read from elems, so tell JNI we don't need to copy it back:
    env->ReleaseDoubleArrayElements(array, elems, JNI_ABORT);
    return M;
}

// Write an Eigen::MatrixXd back to a flat Java array in row-major order
static jdoubleArray eigenToJavaArrayRowMajor(JNIEnv* env, const MatrixXd& M) {
    int rows = M.rows();
    int cols = M.cols();
    int len  = rows * cols;

    // Allocate the Java array to return
    jdoubleArray out = env->NewDoubleArray(len);
    std::vector<jdouble> buffer(len);

    // Flatten in row-major
    for (int i = 0; i < rows; ++i) {
        for (int j = 0; j < cols; ++j) {
            buffer[i * cols + j] = M(i, j);
        }
    }

    // Copy into the Java array
    env->SetDoubleArrayRegion(out, 0, len, buffer.data());
    return out;
}

JNIEXPORT jdoubleArray JNICALL
Java_com_example_matrix_ResultActivity_addMatrices(
        JNIEnv* env,
        jobject /* this */,
        jint rows,
        jint cols,
        jdoubleArray matrixA,
        jdoubleArray matrixB) {

    MatrixXd A = javaArrayToEigenRowMajor(env, matrixA, rows, cols);
    MatrixXd B = javaArrayToEigenRowMajor(env, matrixB, rows, cols);
    MatrixXd C = A + B;
    return eigenToJavaArrayRowMajor(env, C);
}

JNIEXPORT jdoubleArray JNICALL
Java_com_example_matrix_ResultActivity_subtractMatrices(
        JNIEnv* env,
        jobject /* this */,
        jint rows,
        jint cols,
        jdoubleArray matrixA,
        jdoubleArray matrixB) {

    MatrixXd A = javaArrayToEigenRowMajor(env, matrixA, rows, cols);
    MatrixXd B = javaArrayToEigenRowMajor(env, matrixB, rows, cols);
    MatrixXd C = A - B;
    return eigenToJavaArrayRowMajor(env, C);
}

JNIEXPORT jdoubleArray JNICALL
Java_com_example_matrix_ResultActivity_multiplyMatrices(
        JNIEnv* env,
        jobject /* this */,
        jint rows1,
        jint cols1,
        jint rows2,
        jint cols2,
        jdoubleArray matrixA,
        jdoubleArray matrixB) {

    if (cols1 != rows2) {
        env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"),
                      "Matrix dimensions mismatch for multiplication");
        return nullptr;
    }

    MatrixXd A = javaArrayToEigenRowMajor(env, matrixA, rows1, cols1);
    MatrixXd B = javaArrayToEigenRowMajor(env, matrixB, rows2, cols2);
    MatrixXd C = A * B;
    return eigenToJavaArrayRowMajor(env, C);
}

} // extern "C"
