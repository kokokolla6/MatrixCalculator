import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MatrixIO {
    private static final Logger logger = LogManager.getLogger(MatrixIO.class);
    public static Matrix readMatrixFromFile(String filePath) throws MatrixException {
        logger.info("Начало загрузки матрицы из файла: {}", filePath);
        List<String> lines = readLinesFromPath(filePath);
        List<double[]> rowData = parseAndValidateLines(lines);
        if (rowData.isEmpty()) {
            logger.error("В файле '{}' не найдено действительных строк матрицы.", filePath);
            throw new MatrixException("В файле не найдено действительных строк матрицы.");
        }
        double[][] data = rowData.toArray(new double[0][0]);
        logger.info("Матрица успешно загружена. Размерность: {}x{}", data.length, data[0].length);
        return new Matrix(data);
    }

    private static List<String> readLinesFromPath(String filePath) throws MatrixException {
        try {
            return Files.readAllLines(Paths.get(filePath)).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Ошибка I/O при чтении файла: {}", filePath, e);
            throw new MatrixException("Ошибка чтения файла: ");
        }
    }

    private static List<double[]> parseAndValidateLines(List<String> lines) throws MatrixException {
        if (lines.isEmpty()) {
            throw new MatrixException("Файл пуст или не содержит данных.");
        }
        List<double[]> rowData = new ArrayList<>();
        int expectedCols = -1;

        for (String line : lines) {
            double[] row = parseLineToDouble(line);

            if (expectedCols == -1) {
                expectedCols = row.length;
                logger.debug("Установлено ожидаемое количество столбцов: {}", expectedCols);
            } else if (row.length != expectedCols) {
                logger.error("Нарушена прямоугольность: строка {} содержит {} столбцов, ожидалось {}.", line, row.length, expectedCols);
                throw new MatrixException("Количество элементов в некоторых строках отличается от большинства");
            }
            if (row.length > 0) {
                rowData.add(row);
            }
        }
        return rowData;
    }

    private static double[] parseLineToDouble(String line) throws MatrixException {
        String[] elements = line.split("\\s+");
        double[] row = new double[elements.length];

        for (int i = 0; i < elements.length; i++) {
            try {
                row[i] = Double.parseDouble(elements[i].replace(',', '.'));
            } catch (NumberFormatException e) {
                logger.error("Неверный формат числа '{}' в строке парсинга.", elements[i], e);
                throw new MatrixException("Неверный формат числа в файле:");
            }
        }
        return row;
    }
}