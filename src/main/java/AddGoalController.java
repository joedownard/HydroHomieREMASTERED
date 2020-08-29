import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class AddGoalController {

    boolean editing;
    Goal goalEditing;

    public TextField volumeField;
    public TextField pointsField;
    public Button addButton;
    public Button resetButton;
    public Button exitButton;
    public Label volumeErrorLabel;
    public Label pointsErrorLabel;

    @FXML
    public void initialize() {
        editing = false;
        goalEditing = null;

        volumeField.setText("2000"); // Provide some default values
        pointsField.setText("10");
    }

    public void addButtonClicked(MouseEvent keyEvent) throws IOException {
        Response response = Response.ADDGOALFAILURE;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();

        try {
            Integer.parseInt(volumeField.getText()); // Check the content of the field can be converted to an integer
        } catch (IllegalArgumentException e) {
            volumeErrorLabel.setText("Invalid volume!"); // Inform the user of the error
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
        if (editing) {
            response = mainController.getUser().editGoal(goalEditing, volumeField.getText(), pointsField.getText()); // Attempt to edit the goal in the "backend" and get the response
        } else {
            response = mainController.getUser().addGoal(volumeField.getText(), pointsField.getText());
        }

        // Process the response from the "backend" and inform the user correspondingly
        if (response == Response.ADDGOALFAILURE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to create goal!");
            alert.setHeaderText(null);
            alert.setContentText("Failed to create goal. Goal already exists!");
            alert.showAndWait();
        } else if (response == Response.EDITGOALFAILURE){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to edit goal!");
            alert.setHeaderText(null);
            alert.setContentText("Failed to edit goal.");
            alert.showAndWait();
        } else if (response == Response.EDITGOALSUCCESSNOWCOMPLETE) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Goal complete!");
            alert.setHeaderText(null);
            alert.setContentText("You have reached your daily goal! You have been awarded " + mainController.getUser().getCurrentDailyGoal().getPoints() + " points!");
            alert.showAndWait();
        } else if (response == Response.EDITGOALSUCCESSNOWINCOMPLETE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Goal no longer complete!");
            alert.setHeaderText(null);
            alert.setContentText("Editing this goal caused you to no longer fulfill the daily goal! You have lost "+ mainController.getUser().getCurrentDailyGoal().getPoints() + " points!");
            alert.showAndWait();
        }
        mainController.update();

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(mainGUI, 580, 320));
    }

    public void resetButtonClicked(MouseEvent mouseEvent) {
        if (editing) { // Restore original values if editing
            volumeField.setText(String.valueOf(goalEditing.getTargetVolume()));
            pointsField.setText(String.valueOf(goalEditing.getPoints()));
        } else { // Restore default values if not
            volumeField.setText("2000");
            pointsField.setText("10");
        }
    }

    public void exitButtonClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(mainGUI, 580, 320)); // Return to main window without changing anything
    }

    public void setEditing(Goal toEdit) {
        // Function to be called when switching to this scene to set editing mode
        editing = true;
        goalEditing = toEdit;

        addButton.setText("Done");

        volumeField.setText(String.valueOf(goalEditing.getTargetVolume()));
        pointsField.setText(String.valueOf(goalEditing.getPoints()));
    }
}
