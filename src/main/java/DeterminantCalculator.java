import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class DeterminantCalculator {
    private static final Logger logger = LogManager.getLogger(DeterminantCalculator.class);
    public static double calculate(Matrix matrix) {
        double[][] data = matrix.getData();
        if (data == null || data.length == 0 || data.length != data[0].length) {
            logger.error("Попытка вычисления определителя для неквадратной или пустой матрицы. Размер: {}x{}", data.length, (data.length > 0 ? data[0].length : 0));
            throw new MatrixException("Матрица должна быть квадратной и не пустой.");
        }
        logger.info("Начало вычисления определителя для матрицы {}x{}", data.length, data.length);
        return calculateDeterminantGauss(data);
    }

    private static double calculateDeterminantGauss(double[][] matrix) {
        int n = matrix.length;
        double[][] A = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, A[i], 0, n);
        }

        int rowSwaps = 0;
        final double wee = 1e-9;

        for (int k = 0; k < n; k++) {
            int pivotRow = k;
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(A[i][k]) > Math.abs(A[pivotRow][k])) {
                    pivotRow = i;
                }
            }
            if (pivotRow != k) {
                double[] temp = A[k];
                A[k] = A[pivotRow];
                A[pivotRow] = temp;
                rowSwaps++;
                logger.trace("Перестановка строк {} и {}. Общее число перестановок: {}", k, pivotRow, rowSwaps);
            }

            if (Math.abs(A[k][k]) < wee) {
                logger.debug("Опорный элемент слишком близок к нулю. Определитель равен 0.0.");
                return 0.0;
            }

            for (int i = k + 1; i < n; i++) {
                double factor = A[i][k] / A[k][k];
                for (int j = k; j < n; j++) {
                    A[i][j] -= factor * A[k][j];
                }
            }
        }
        double determinant = 1.0;
        for (int i = 0; i < n; i++) {
            determinant *= A[i][i];
        }
        if (rowSwaps % 2 != 0) {
            determinant = -determinant;
        }
        logger.info("Вычисление определителя завершено. Результат: {}", determinant);
        return determinant;
    }
}