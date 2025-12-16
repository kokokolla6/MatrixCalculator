import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Matrix {
    private static final Logger logger = LogManager.getLogger(Matrix.class);
    private final double[][] data;
    private final int rows;
    private final int cols;

    public Matrix(double[][] data) {
        logger.debug("Инициализация нового объекта Matrix.");

        if (data == null || data.length == 0 || data[0].length == 0) {
            logger.error("Ошибка инициализации: Матрица не может быть пустой.");
            throw new MatrixException("Матрица не может быть пустой.");
        }

        this.rows = data.length;
        this.cols = data[0].length;
        this.data = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            if (data[i].length != cols) {
                logger.error("Ошибка инициализации: Входной массив не является прямоугольным. Строка {} имеет {} столбцов, ожидалось {}.",
                        i, data[i].length, cols);
                throw new MatrixException("Входной массив не является прямоугольным.");
            }
            System.arraycopy(data[i], 0, this.data[i], 0, cols);
        }
        logger.info("Матрица успешно инициализирована. Размерность: {}x{}", this.rows, this.cols);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public double get(int i, int j) { return data[i][j]; }

    public double[][] getData() {
        logger.trace("Создание глубокой копии данных матрицы ({}x{})", rows, cols);
        double[][] copy = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(this.data[i], 0, copy[i], 0, cols);
        }
        return copy;
    }

    public String toString() {
        logger.trace("Генерация строкового представления матрицы {}x{}", rows, cols);
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, rows).forEach(i -> {
            IntStream.range(0, cols).forEach(j ->
                    sb.append(String.format("%.2f", data[i][j])).append("\t")
            );
            sb.append("\n");
        });
        return sb.toString();
    }
}