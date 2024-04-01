package Game;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class InputPopup extends Stage{

    public InputPopup(Stage parentStage) {
        initOwner(parentStage);
        initModality(Modality.WINDOW_MODAL);
        initStyle(StageStyle.UTILITY);
        setTitle("Connect to Host");

        GridPane popupRoot = new GridPane();
        popupRoot.setPadding(new Insets(10));
        popupRoot.setHgap(5);
        popupRoot.setVgap(5);

        Label usernameLbl = new Label("Username:");
        Label ipLbl = new Label("Host-IP:");
        TextField usernametxtfield = new TextField();

        TextField iptxtfield = new TextField();
        Button confirmButton = new Button("Confirm");

        confirmButton.setOnAction(e -> {
            if(usernametxtfield.getText().isEmpty() || iptxtfield.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Either Username or Host-IP is empty");
                alert.showAndWait();

            }
            else{
                Gui.clientUsername = usernametxtfield.getText();
                Gui.serverIP = iptxtfield.getText();
                close();
            }



        });

        popupRoot.add(usernameLbl, 0, 0);
        popupRoot.add(usernametxtfield, 1, 0);
        popupRoot.add(ipLbl, 0, 1);
        popupRoot.add(iptxtfield, 1, 1);
        popupRoot.add(confirmButton, 1, 5, 2, 1);

        Scene scene = new Scene(popupRoot, 300, 150);
        setScene(scene);
    }

}
