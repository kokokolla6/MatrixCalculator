import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MatrixOperationsTest {

    private static final double DELTA = 1e-9;

    private Matrix createMatrix(double[][] data) {
        return new Matrix(data);
    }

    private void assertMatricesEqual(Matrix expected, Matrix actual) {
        assertNotNull(actual, "Результирующая матрица не должна быть null");
        assertEquals(expected.getRows(), actual.getRows(), "Количество строк должно совпадать");
        assertEquals(expected.getCols(), actual.getCols(), "Количество столбцов должно совпадать");

        for (int i = 0; i < expected.getRows(); i++) {
            for (int j = 0; j < expected.getCols(); j++) {
                assertEquals(expected.get(i, j), actual.get(i, j), DELTA,
                        String.format("Элементы в позиции (%d, %d) должны совпадать", i, j));
            }
        }
    }
    @Test
    @DisplayName("Сложение: Успешное сложение двух матриц 2x2")
    void sum_SuccessfulAddition_2x2() {
        Matrix a = createMatrix(new double[][]{
                {1.0, 2.0},
                {3.0, 4.0}
        });
        Matrix b = createMatrix(new double[][]{
                {5.0, 6.0},
                {7.0, 8.0}
        });
        Matrix expected = createMatrix(new double[][]{
                {6.0, 8.0},
                {10.0, 12.0}
        });

        Matrix result = MatrixOperations.sum(a, b);
        assertMatricesEqual(expected, result);
    }

    @Test
    @DisplayName("Сложение: Исключение при разных размерах (2x2 + 2x3)")
    void sum_ThrowsException_DifferentSizes() {
        Matrix a = createMatrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
        Matrix b = createMatrix(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}});

        MatrixException exception = assertThrows(MatrixException.class, () ->
                MatrixOperations.sum(a, b)
        );

        assertEquals("Для сложения матрицы должны быть одного размера.", exception.getMessage());
    }
    @Test
    @DisplayName("Вычитание: Успешное вычитание двух матриц 3x1")
    void subtract_SuccessfulSubtraction_3x1() {
        Matrix a = createMatrix(new double[][]{{10.0}, {20.0}, {30.0}});
        Matrix b = createMatrix(new double[][]{{1.0}, {2.0}, {3.0}});
        Matrix expected = createMatrix(new double[][]{{9.0}, {18.0}, {27.0}});

        Matrix result = MatrixOperations.subtract(a, b);

        assertMatricesEqual(expected, result);
    }

    @Test
    @DisplayName("Вычитание: Исключение при разных размерах (1x3 - 3x1)")
    void subtract_ThrowsException_DifferentDimensions() {
        Matrix a = createMatrix(new double[][]{{1.0, 2.0, 3.0}});
        Matrix b = createMatrix(new double[][]{{1.0}, {2.0}, {3.0}});

        MatrixException exception = assertThrows(MatrixException.class, () ->
                MatrixOperations.subtract(a, b)
        );

        assertEquals("Для вычитания матрицы должны быть одного размера.", exception.getMessage());
    }
    @Test
    @DisplayName("Умножение: Успешное умножение (2x3 * 3x2 -> 2x2)")
    void multiply_SuccessfulMultiplication_2x3_by_3x2() {
        Matrix a = createMatrix(new double[][]{
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        });
        Matrix b = createMatrix(new double[][]{
                {7.0, 8.0},
                {9.0, 10.0},
                {11.0, 12.0}
        });
        Matrix expected = createMatrix(new double[][]{
                {58.0, 64.0},
                {139.0, 154.0}
        });
        Matrix result = MatrixOperations.multiply(a, b);
        assertMatricesEqual(expected, result);
    }

    @Test
    @DisplayName("Умножение: Исключение при несовместимых размерах (2x3 * 2x3)")
    void multiply_ThrowsException_IncompatibleSizes() {
        Matrix a = createMatrix(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}}); // 2x3
        Matrix b = createMatrix(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}}); // 2x3

        MatrixException exception = assertThrows(MatrixException.class, () ->
                MatrixOperations.multiply(a, b)
        );
        assertEquals("Для умножения количество столбцов A должно совпадать с количеством строк B.", exception.getMessage());
    }

    @Test
    @DisplayName("Определитель: Успешное вычисление для матрицы 2x2")
    void determinant_SuccessfulCalculation_2x2() {
        Matrix a = createMatrix(new double[][]{
                {4.0, 6.0},
                {3.0, 8.0}
        });
        double expectedDet = 14.0;

        double result = MatrixOperations.determinant(a);

        assertEquals(expectedDet, result, DELTA);
    }

    @Test
    @DisplayName("Определитель: Успешное вычисление для матрицы 3x3 (ненулевой)")
    void determinant_SuccessfulCalculation_3x3_NonZero() {
        Matrix a = createMatrix(new double[][]{
                {1.0, 2.0, 3.0},
                {0.0, 1.0, 4.0},
                {5.0, 6.0, 0.0}
        });
        double expectedDet = 1.0;

        double result = MatrixOperations.determinant(a);

        assertEquals(expectedDet, result, DELTA);
    }

    @Test
    @DisplayName("Определитель: Определитель равен нулю")
    void determinant_IsZero() {
        Matrix a = createMatrix(new double[][]{
                {1.0, 2.0},
                {2.0, 4.0}
        });
        double expectedDet = 0.0;
        double result = MatrixOperations.determinant(a);
        assertEquals(expectedDet, result, DELTA);
    }

    @Test
    @DisplayName("Определитель: Исключение для неквадратной матрицы (2x3)")
    void determinant_ThrowsException_NonSquareMatrix() {
        Matrix a = createMatrix(new double[][]{
                {1.0, 2.0, 3.0},
                {4.0, 5.0, 6.0}
        });

        MatrixException exception = assertThrows(MatrixException.class, () ->
                MatrixOperations.determinant(a)
        );

        assertEquals("Определитель можно вычислить только для квадратной матрицы.", exception.getMessage());
    }
}