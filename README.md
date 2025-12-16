Инструкция по запуску приложения
1) Скомпилируйте проект через консоль, введя команду gradlew clean build, находясь в директории программы.
2) Пропишите команду для интеграции модулей JavaFX: java --module-path <Здесь укажите путь к библиотекам javaFX> --add-modules javafx.controls,javafx.fxml -jar build/libs/MatrixCalculator.jar
3) Откройте проект, прописав команду: java -jar MatrixCalculator.jar.

Profit!
