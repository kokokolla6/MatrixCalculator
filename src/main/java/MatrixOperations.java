import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.stream.IntStream;

public class MatrixOperations {
    private static final Logger logger = LogManager.getLogger(MatrixOperations.class);

    public static Matrix sum(Matrix a, Matrix b) throws MatrixException {
        logger.debug("Начало операции сложения: A({}x{}) + B({}x{})", a.getRows(), a.getCols(), b.getRows(), b.getCols());
        if (a.getRows() != b.getRows() || a.getCols() != b.getCols()) {
            logger.error("Ошибка сложения: Матрицы должны быть одинакового размера. A: {}x{}, B: {}x{}", a.getRows(), a.getCols(), b.getRows(), b.getCols());
            throw new MatrixException("Для сложения матрицы должны быть одного размера.");
        }
        int rows = a.getRows();
        int cols = a.getCols();
        double[][] resultData = new double[rows][cols];

        IntStream.range(0, rows).forEach(i ->
                IntStream.range(0, cols).forEach(j ->
                        resultData[i][j] = a.get(i, j) + b.get(i, j)
                )
        );
        logger.info("Операция сложения успешно завершена. Размерность результата: {}x{}", rows, cols);
        return new Matrix(resultData);
    }

    public static Matrix subtract(Matrix a, Matrix b) throws MatrixException {
        logger.debug("Начало операции вычитания: A({}x{}) - B({}x{})", a.getRows(), a.getCols(), b.getRows(), b.getCols());
        if (a.getRows() != b.getRows() || a.getCols() != b.getCols()) {
            logger.error("Ошибка вычитания: Матрицы должны быть одинакового размера. A: {}x{}, B: {}x{}", a.getRows(), a.getCols(), b.getRows(), b.getCols());
            throw new MatrixException("Для вычитания матрицы должны быть одного размера.");
        }
        int rows = a.getRows();
        int cols = a.getCols();
        double[][] resultData = new double[rows][cols];

        IntStream.range(0, rows).forEach(i ->
                IntStream.range(0, cols).forEach(j ->
                        resultData[i][j] = a.get(i, j) - b.get(i, j)
                )
        );
        logger.info("Операция вычитания успешно завершена. Размерность результата: {}x{}", rows, cols);
        return new Matrix(resultData);
    }

    public static Matrix multiply(Matrix a, Matrix b) throws MatrixException {
        logger.debug("Начало операции умножения: A({}x{}) * B({}x{})", a.getRows(), a.getCols(), b.getRows(), b.getCols());

        if (a.getCols() != b.getRows()) {
            logger.error("Ошибка умножения: Количество столбцов A ({}) не равно количеству строк B ({}).", a.getCols(), b.getRows());
            throw new MatrixException("Для умножения количество столбцов A должно совпадать с количеством строк B.");
        }

        int rowsA = a.getRows();
        int colsA = a.getCols();
        int colsB = b.getCols();
        double[][] resultData = new double[rowsA][colsB];

        // Основной алгоритм умножения
        IntStream.range(0, rowsA).forEach(i -> {
            IntStream.range(0, colsB).forEach(j -> {
                double sum = IntStream.range(0, colsA)
                        .mapToDouble(k -> a.get(i, k) * b.get(k, j))
                        .sum();

                resultData[i][j] = sum;
            });
        });

        logger.info("Операция умножения успешно завершена. Размерность результата: {}x{}", rowsA, colsB);
        return new Matrix(resultData);
    }

    public static double determinant(Matrix matrix) throws MatrixException {
        logger.debug("Начало вычисления определителя для матрицы {}x{}", matrix.getRows(), matrix.getCols());

        if (matrix.getRows() != matrix.getCols()) {
            logger.error("Ошибка определителя: Матрица не квадратная ({}x{}).", matrix.getRows(), matrix.getCols());
            throw new MatrixException("Определитель можно вычислить только для квадратной матрицы.");
        }
        double result = DeterminantCalculator.calculate(matrix);

        logger.info("Вычисление определителя завершено. Результат: {}", result);
        return result;
    }
}