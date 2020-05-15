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

public class AddRecordController {

    boolean editing = false;
    Record recordEditing = null;

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
        volumeField.setText("400");
        typeField.getItems().setAll(LiquidType.values());
        typeField.setValue(LiquidType.WATER);
        dateField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
    }

    public void addButtonClicked(MouseEvent keyEvent) throws IOException {
        boolean result = false;

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
            result = mainController.getUser().editRecord(recordEditing, volumeField.getText(), typeField.getValue(), dateField.getText());
        } else {
            result = mainController.getUser().addRecord(volumeField.getText(), typeField.getValue(), dateField.getText());
        }
        if (!result) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to create/edit recortd!");
            alert.setHeaderText(null);
            alert.setContentText("Failed to create/edit record.");
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
        volumeField.setText("400");
        typeField.getItems().setAll(LiquidType.values());
        typeField.setValue(LiquidType.WATER);
        dateField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
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
