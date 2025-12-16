import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ErrorDialog {
    public static void show(String errorType, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Ошибка в приложении");
        alert.setHeaderText(errorType);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
