import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class AddRecordController implements Initializable {


    @FXML private Label volumeErrorLabel;
    @FXML private Label dateErrorLabel;
    @FXML private TextField volumeField;
    @FXML private TextField dateField;
    @FXML private ChoiceBox<LiquidType> typeField;

    @FXML private Button addButton;
    @FXML private Button resetButton;
    @FXML private Button exitButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        volumeField.setText("400");
        typeField.getItems().setAll(LiquidType.values());
        typeField.setValue(LiquidType.WATER);
        dateField.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
    }

    public void addButtonClicked(MouseEvent keyEvent) throws IOException {
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
        mainController.getUser().addRecord(volumeField.getText(), typeField.getValue(), dateField.getText());
        mainController.updateTable();

        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.setScene(new Scene(mainGUI, 580, 320));
    }

    public void resetButtonClicked(MouseEvent mouseEvent) {
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
}
