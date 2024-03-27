package Game;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UsernamePopup {

    private String username;

    public UsernamePopup(Stage parentStage) {
        Stage popupStage = new Stage();
        popupStage.initOwner(parentStage);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle("Enter Username");

        GridPane popupRoot = new GridPane();
        popupRoot.setPadding(new Insets(10));
        popupRoot.setHgap(5);
        popupRoot.setVgap(5);

        Label label = new Label("Enter username:");
        TextField textField = new TextField();
        Button confirmButton = new Button("Confirm");

        confirmButton.setOnAction(e -> {
            username = textField.getText();
            popupStage.close();
        });

        popupRoot.add(label, 0, 0);
        popupRoot.add(textField, 1, 0);
        popupRoot.add(confirmButton, 0, 1, 2, 1);

        Scene scene = new Scene(popupRoot, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    public String getUsername() {
        return username;
    }
}
