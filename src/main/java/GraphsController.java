import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphsController {

    @FXML private Button viewButton;
    @FXML private CheckBox recordsCheckbox;
    @FXML private CheckBox goalsCheckbox;
    @FXML private Button exitButton;

    public void viewButtonClicked(MouseEvent mouseEvent) {
    }

    public void exitButtonCLicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();

        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.setScene(new Scene(mainGUI, 580, 320));
    }
}
