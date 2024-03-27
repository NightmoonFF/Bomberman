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

public class InputPopup {

    private String username;
    private String ip;

    public InputPopup(Stage parentStage) {
        Stage popupStage = new Stage();
        popupStage.initOwner(parentStage);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);
        popupStage.setTitle("Enter Username");

        GridPane popupRoot = new GridPane();
        popupRoot.setPadding(new Insets(10));
        popupRoot.setHgap(5);
        popupRoot.setVgap(5);

        Label usernameLbl = new Label("Enter username:");
        Label ipLbl = new Label("Enter ip-address:");
        TextField usernametxtfield = new TextField();
        TextField iptxtfield = new TextField();
        Button confirmButton = new Button("Confirm");

        confirmButton.setOnAction(e -> {
            username = usernametxtfield.getText();
            ip = iptxtfield.getText();
            popupStage.close();
        });

        popupRoot.add(usernameLbl, 0, 0);
        popupRoot.add(usernametxtfield, 1, 0);
        popupRoot.add(ipLbl, 0, 1);
        popupRoot.add(iptxtfield, 1, 1);
        popupRoot.add(confirmButton, 1, 5, 2, 1);

        Scene scene = new Scene(popupRoot, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    public String getUsername() {
        return username;
    }

    public String getIp() {
        return ip;
    }
}
