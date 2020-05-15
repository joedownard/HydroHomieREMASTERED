import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddRecordController {

    boolean editing;
    Record recordEditing;

    @FXML private Label volumeErrorLabel;
    @FXML private Label dateErrorLabel;
    @FXML private TextField volumeField;
    @FXML private TextField dateField;
    @FXML private ChoiceBox<LiquidType> typeField;

    @FXML private Button addButton;
    @FXML private Button resetButton;
    @FXML private Button exitButton;

    @FXML
    public void initialize() {
        editing = false;
        recordEditing = null;

        volumeField.setText("400");
        typeField.getItems().setAll(LiquidType.values());
        typeField.setValue(LiquidType.WATER);
        dateField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
    }

    public void addButtonClicked(MouseEvent keyEvent) throws IOException {
        Response response = Response.ADDRECFAILURE;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();


        try {
            Integer.parseInt(volumeField.getText());
            new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateField.getText());
        } catch (IllegalArgumentException e) {
            volumeErrorLabel.setText("Invalid volume!");
            volumeErrorLabel.setTextFill(Color.color(1, 0 , 0));
            return;
        } catch (ParseException e) {
            dateErrorLabel.setText("Invalid date!");
            dateErrorLabel.setTextFill(Color.color(1, 0 , 0));
            return;
        }

        MainController mainController =  loader.getController();
        if (editing) {
            response = mainController.getUser().editRecord(recordEditing, volumeField.getText(), typeField.getValue(), dateField.getText());
        } else {
            response = mainController.getUser().addRecord(volumeField.getText(), typeField.getValue(), dateField.getText());
        }
        if (response == Response.ADDRECSUCCESSGOALCOMPLETE) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Goal complete!");
            alert.setHeaderText(null);
            alert.setContentText("You have reached your daily goal! You have been awarded " + mainController.getUser().getCurrentDailyGoal().getPoints() + " points!");
            alert.showAndWait();
        } else if (response == Response.ADDRECFAILURE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to create record!");
            alert.setHeaderText(null);
            alert.setContentText("Failed to create record.");
            alert.showAndWait();
        } else if (response == Response.EDITRECFAILURE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to edit record!");
            alert.setHeaderText(null);
            alert.setContentText("Failed to edit record.");
            alert.showAndWait();
        } else if (response == Response.EDITRECSUCCESSGOALCOMPLETE) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Goal complete!");
            alert.setHeaderText(null);
            alert.setContentText("You have reached your daily goal! You have been awarded " + mainController.getUser().getCurrentDailyGoal().getPoints() + " points!");
            alert.showAndWait();
        } else if (response == Response.EDITRECSUCCESSGOALINCOMPLETE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Goal no longer complete!");
            alert.setHeaderText(null);
            alert.setContentText("Editing this record caused you to not fulfill the daily goal! You have lost "+ mainController.getUser().getCurrentDailyGoal().getPoints() + " points!");
            alert.showAndWait();
        }

        mainController.update();

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(mainGUI, 580, 320));
    }

    public void resetButtonClicked(MouseEvent mouseEvent) {
        if (editing) {
            volumeField.setText(String.valueOf(recordEditing.getVolume()));
            typeField.setValue(recordEditing.getLiquidType());
            dateField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(recordEditing.getDate()));
        } else {
            volumeField.setText("400");
            typeField.setValue(LiquidType.WATER);
            dateField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        }
    }

    public void exitButtonClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(mainGUI, 580, 320));
    }

    public void setEditing(Record toEdit) {
        editing = true;
        recordEditing = toEdit;

        addButton.setText("Done");

        volumeField.setText(String.valueOf(recordEditing.getVolume()));
        typeField.setValue(recordEditing.getLiquidType());
        dateField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(recordEditing.getDate()));
    }
}
