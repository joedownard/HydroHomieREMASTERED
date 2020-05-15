import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class AddGoalController {


    public TextField volumeField;
    public TextField pointsField;
    public Button addButton;
    public Button clearButton;
    public Button exitButton;
    public Label volumeErrorLabel;
    public Label pointsErrorLabel;

    @FXML
    public void initialize() {
        volumeField.setText("2000");
        pointsField.setText("10");
    }

    public void addButtonClicked(MouseEvent keyEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();

        try {
            Integer.parseInt(volumeField.getText());
        } catch (IllegalArgumentException e) {
            volumeErrorLabel.setText("Invalid volume!");
            volumeErrorLabel.setTextFill(Color.color(1, 0 , 0));
            return;
        }

        try {
            Integer.parseInt(pointsField.getText());
        } catch (IllegalArgumentException e) {
            pointsErrorLabel.setText("Invalid points!");
            pointsErrorLabel.setTextFill(Color.color(1, 0 , 0));
            return;
        }

        MainController mainController =  loader.getController();
        boolean result = mainController.getUser().addGoal(volumeField.getText(), pointsField.getText());
        if (!result) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to create goal!");
            alert.setHeaderText(null);
            alert.setContentText("Failed to create goal. There is already a goal for today.");
            alert.showAndWait();
        }
        mainController.update();

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(mainGUI, 580, 320));
    }

    public void clearButtonClicked(MouseEvent mouseEvent) {
        volumeField.setText("2000");
        pointsField.setText("10");
    }

    public void exitButtonClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(mainGUI, 580, 320));
    }
}
