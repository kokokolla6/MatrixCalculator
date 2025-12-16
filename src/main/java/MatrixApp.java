import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MatrixApp extends Application {
    private static final Logger logger = LogManager.getLogger(MatrixApp.class);
    private Matrix matrixA;
    private Matrix matrixB;
    private TextField fileAPath;
    private TextArea matrixADisplay;
    private TextField fileBPath;
    private TextArea matrixBDisplay;
    private TextArea resultArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("--- Приложение Matrix Calculator запущено. ---");
        primaryStage.setTitle("Matrix Calculator");
        VBox panelA = createMatrixPanel("A", this::loadMatrixA);
        VBox panelB = createMatrixPanel("B", this::loadMatrixB);
        VBox resultPanel = createResultPanel();
        HBox matrixContainer = new HBox(15, panelA, panelB);
        matrixContainer.setPadding(new Insets(15, 15, 0, 15));
        VBox root = new VBox(10, matrixContainer, resultPanel);
        root.setPadding(new Insets(0, 15, 15, 15));
        primaryStage.setScene(new Scene(root, 950, 700));
        primaryStage.show();
    }

    private VBox createMatrixPanel(String name, Runnable loadAction) {
        TextField pathField = new TextField();
        pathField.setPromptText("Путь к файлу " + name + "...");
        Button selectButton = new Button("Выбрать файл с матрицей");
        selectButton.setOnAction(e -> selectFile(pathField));
        Button loadButton = new Button("Загрузить матрицу");
        loadButton.setOnAction(e -> loadAction.run());
        TextArea displayArea = new TextArea("Здесь вы увидите свою загруженную матрицу (или нет)");
        displayArea.setEditable(false);
        displayArea.setPrefHeight(250);
        displayArea.setPrefWidth(500);

        if ("A".equals(name)) {
            this.fileAPath = pathField;
            this.matrixADisplay = displayArea;
        } else {
            this.fileBPath = pathField;
            this.matrixBDisplay = displayArea;
        }

        VBox layout = new VBox(5,
                new Label("Матрица " + name),
                pathField,
                new HBox(10, selectButton, loadButton),
                displayArea
        );
        return layout;
    }

    private VBox createResultPanel() {
        GridPane operationPane = new GridPane();
        operationPane.setHgap(10);
        operationPane.setVgap(10);
        operationPane.setPadding(new Insets(10, 0, 10, 0));

        Button addButton = new Button("Сложение матриц");
        addButton.setOnAction(e -> performOperation(() -> MatrixOperations.sum(matrixA, matrixB), "Сложение"));
        Button subtractButton = new Button("Вычитание матриц");
        subtractButton.setOnAction(e -> performOperation(() -> MatrixOperations.subtract(matrixA, matrixB), "Вычитание"));
        Button multiplyButton = new Button("Умножение матриц");
        multiplyButton.setOnAction(e -> performOperation(() -> MatrixOperations.multiply(matrixA, matrixB), "Умножение"));
        Button detAButton = new Button("Определитель матрицы 'A'");
        detAButton.setOnAction(e -> calculateDeterminant(matrixA, "A"));
        Button detBButton = new Button("Определитель матрицы 'B'");
        detBButton.setOnAction(e -> calculateDeterminant(matrixB, "B"));

        operationPane.add(addButton, 0, 0);
        operationPane.add(subtractButton, 1, 0);
        operationPane.add(multiplyButton, 2, 0);
        operationPane.add(detAButton, 0, 1);
        operationPane.add(detBButton, 1, 1);

        resultArea = new TextArea("  Результаты операций   ");
        resultArea.setEditable(false);
        resultArea.setPrefHeight(250);

        VBox layout = new VBox(5,
                new Separator(),
                new Label("  Операции  "),
                operationPane,
                new Separator(),
                new Label("  Результат вывода   "),
                resultArea
        );
        return layout;
    }

    private void selectFile(TextField pathField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбрать файл матрицы");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            pathField.setText(file.getAbsolutePath());
            logger.debug("Пользователь выбрал файл: {}", file.getAbsolutePath());
        }
    }

    private void loadMatrixA() {
        logger.info("Пользователь инициировал загрузку матрицы A.");
        matrixA = loadAndDisplayMatrix(fileAPath, matrixADisplay, "A");
    }

    private void loadMatrixB() {
        logger.info("Пользователь инициировал загрузку матрицы B.");
        matrixB = loadAndDisplayMatrix(fileBPath, matrixBDisplay, "B");
    }

    private Matrix loadAndDisplayMatrix(TextField pathField, TextArea displayArea, String name) {
        String path = pathField.getText();
        if (path.isEmpty()) {
            logger.warn("Невозможно загрузить матрицу {} - Путь к файлу не указан.", name);
            ErrorDialog.show(
                    "Ошибка загрузки матрицы " + name,
                    "Путь к файлу не указан."
            );
            return null;
        }

        try {
            logger.debug("Попытка загрузки матрицы {} из пути: {}", name, path);
            Matrix matrix = MatrixIO.readMatrixFromFile(path);
            displayArea.setText(String.format("✅ %s загружена (%dx%d):\n\n%s",
                    name, matrix.getRows(), matrix.getCols(), matrix.toString()));
            logger.info("Матрица {} успешно загружена. Размерность: {}x{}",
                    name, matrix.getRows(), matrix.getCols());
            return matrix;
        } catch (MatrixException e) {
            logger.error("Ошибка загрузки матрицы {}: {}", name, e.getMessage());
            ErrorDialog.show(
                    "Ошибка загрузки матрицы " + name,
                    e.getMessage()
            );
            return null;
        }
    }

    private void performOperation(MatrixSupplier operation, String opName) {
        logger.info("Пользователь инициировал операцию: {}", opName);

        if (matrixA == null || matrixB == null) {
            logger.warn("Невозможно выполнить операцию {}. Одна или обе матрицы не загружены.", opName);
            ErrorDialog.show(
                    "Ошибка операции " + opName,
                    "Сначала загрузите обе матрицы (A и B)."
            );
            return;
        }

        try {
            logger.debug("Выполнение операции {} для A({}x{}) и B({}x{})",
                    opName, matrixA.getRows(), matrixA.getCols(), matrixB.getRows(), matrixB.getCols());
            Matrix result = operation.get();
            String output = String.format("     Результат операции %s   \n\n", opName) +
                    result.toString();
            resultArea.setText(output);
            logger.info("Операция {} успешно завершена и результат отображен.", opName);
        } catch (MatrixException e) {
            logger.error("Ошибка при выполнении операции {}: {}", opName, e.getMessage());
            ErrorDialog.show(
                    "Ошибка операции " + opName,
                    e.getMessage()
            );
        }
    }

    private void calculateDeterminant(Matrix matrix, String name) {
        logger.info("Пользователь инициировал расчет определителя для Матрицы {}.", name);

        if (matrix == null) {
            logger.warn("Невозможно вычислить определитель - Матрица {} не загружена.", name);
            ErrorDialog.show(
                    "Ошибка вычисления определителя матрицы " + name,
                    "Матрица " + name + " не загружена."
            );
            return;
        }
        try {
            logger.debug("Запуск расчета определителя для Матрицы {} ({}x{})",
                    name, matrix.getRows(), matrix.getCols());
            double det = MatrixOperations.determinant(matrix);
            String output = String.format("  Определитель матрицы   %s \n\n" +
                            "Матрица:\n%s\n" +
                            "Det(%s) = %.4f\n",
                    name, matrix.toString(), name, det);
            resultArea.setText(output);
            logger.info("Определитель Матрицы {} успешно вычислен: Det={:.4f}", name, det);
        } catch (MatrixException e) {
            logger.error("Ошибка при вычислении определителя Матрицы {}: {}", name, e.getMessage());
            ErrorDialog.show(
                    "Ошибка вычисления определителя матрицы " + name,
                    e.getMessage()
            );
        }
    }
    @FunctionalInterface
    private interface MatrixSupplier {
        Matrix get();
    }
}