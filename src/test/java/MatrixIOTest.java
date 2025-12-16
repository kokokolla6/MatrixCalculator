import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MatrixIOTest {
    @TempDir
    Path tempDir;

    private static final double DELTA = 1e-9;

    private Path writeToFile(String filename, List<String> content) throws IOException {
        Path file = tempDir.resolve(filename);
        Files.write(file, content);
        return file;
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
    @DisplayName("Успешное чтение: Чтение матрицы 2x2 с целыми числами")
    void readMatrixFromFile_SuccessfulRead_Integers() throws IOException {
        List<String> fileContent = Arrays.asList(
                "1 2",
                "3 4"
        );
        Path file = writeToFile("matrix_2x2.txt", fileContent);
        Matrix expected = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});

        Matrix result = MatrixIO.readMatrixFromFile(file.toString());

        assertMatricesEqual(expected, result);
    }

    @Test
    @DisplayName("Успешное чтение: Чтение матрицы 2x3 с плавающей точкой и разными разделителями")
    void readMatrixFromFile_SuccessfulRead_DoublesAndSpaces() throws IOException {
        List<String> fileContent = Arrays.asList(
                "1.5 2.0 3.14",
                "4.5   5.5   6.0"
        );
        Path file = writeToFile("matrix_2x3_doubles.txt", fileContent);
        Matrix expected = new Matrix(new double[][]{
                {1.5, 2.0, 3.14},
                {4.5, 5.5, 6.0}
        });

        Matrix result = MatrixIO.readMatrixFromFile(file.toString());
        assertMatricesEqual(expected, result);
    }

    @Test
    @DisplayName("Успешное чтение: Чтение с пустыми строками и запятыми в числах (замена на точку)")
    void readMatrixFromFile_SuccessfulRead_WithCommasAndEmptyLines() throws IOException {
        List<String> fileContent = Arrays.asList(
                " 1,1 2,2 ",
                "",
                " 3.3 4.4 "
        );
        Path file = writeToFile("matrix_commas.txt", fileContent);
        Matrix expected = new Matrix(new double[][]{
                {1.1, 2.2},
                {3.3, 4.4}
        });

        Matrix result = MatrixIO.readMatrixFromFile(file.toString());
        assertMatricesEqual(expected, result);
    }

    @Test
    @DisplayName("Ошибка I/O: Файл не найден")
    void readMatrixFromFile_ThrowsException_FileNotFound() {
        String nonExistentPath = tempDir.resolve("non_existent.txt").toString();

        MatrixException exception = assertThrows(MatrixException.class, () ->
                MatrixIO.readMatrixFromFile(nonExistentPath)
        );
        assertTrue(exception.getMessage().startsWith("Ошибка чтения файла:"));
    }

    @Test
    @DisplayName("Ошибка формата: Неверное количество столбцов (непрямоугольный файл)")
    void readMatrixFromFile_ThrowsException_NonRectangular() throws IOException {
        List<String> fileContent = Arrays.asList(
                "1 2 3",
                "4 5"
        );
        Path file = writeToFile("matrix_non_rectangular.txt", fileContent);
        MatrixException exception = assertThrows(MatrixException.class, () ->
                MatrixIO.readMatrixFromFile(file.toString())
        );

        assertEquals("Количество элементов в некоторых строках отличается от большинства", exception.getMessage());
    }

    @Test
    @DisplayName("Ошибка формата: Неверный формат числа (не числовой символ)")
    void readMatrixFromFile_ThrowsException_InvalidNumberFormat() throws IOException {
        List<String> fileContent = Arrays.asList(
                "1 2",
                "3 X"
        );
        Path file = writeToFile("matrix_invalid_number.txt", fileContent);
        MatrixException exception = assertThrows(MatrixException.class, () ->
                MatrixIO.readMatrixFromFile(file.toString())
        );
        assertTrue(exception.getMessage().startsWith("Неверный формат числа в файле:"));
    }

    @Test
    @DisplayName("Ошибка формата: Файл содержит только пустые строки")
    void readMatrixFromFile_ThrowsException_EmptyFile() throws IOException {
        List<String> fileContent = Arrays.asList(
                "",
                "   ",
                ""
        );
        Path file = writeToFile("matrix_empty_content.txt", fileContent);

        MatrixException exception = assertThrows(MatrixException.class, () ->
                MatrixIO.readMatrixFromFile(file.toString())
        );

        assertEquals("В файле не найдено действительных строк матрицы.", exception.getMessage());
    }

    @Test
    @DisplayName("Ошибка формата: Матрица-строка (1xN), корректно")
    void readMatrixFromFile_SingleRow_Correct() throws IOException {
        List<String> fileContent = List.of("1.0 2.0 3.0");
        Path file = writeToFile("matrix_single_row.txt", fileContent);
        Matrix expected = new Matrix(new double[][]{{1.0, 2.0, 3.0}});
        Matrix result = MatrixIO.readMatrixFromFile(file.toString());
        assertMatricesEqual(expected, result);
    }
}