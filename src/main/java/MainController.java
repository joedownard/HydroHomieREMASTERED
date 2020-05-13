import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TableView<Record> recordsTable;

    @FXML private TableColumn<Record, String> date;
    @FXML private TableColumn<Record, String> type;
    @FXML private TableColumn<Record, String> volume;

    @FXML private Button addGoalButton;
    @FXML private Button editGoalButton;
    @FXML private Button graphsButton;
    @FXML private Button factButton;
    @FXML private Button editRecordButton;
    @FXML private Button addRecordButton;
    @FXML private Button deleteRecordButton;

    @FXML private Label errorLabel;
    @FXML private Label pointsLabel;
    @FXML private Label pointsValueLabel;
    @FXML private Label dailyGoalLabel;

    @FXML private ProgressBar dailyProgressBar;

    User user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        readFromFile();
        date.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        type.setCellValueFactory(new PropertyValueFactory<>("liquidType"));
        volume.setCellValueFactory(new PropertyValueFactory<>("volume"));

        if (user != null) {
            if (user.getRecords() != null) {
                recordsTable.getItems().setAll(user.getRecords());
            }
        };
    }

    public void addRecordButtonClicked(MouseEvent mouseEvent) throws IOException {
        Parent addGUI = FXMLLoader.load(getClass().getResource("addRecord.fxml"));
        Stage stage = (Stage) addRecordButton.getScene().getWindow();
        stage.setScene(new Scene(addGUI));
    }

    public void editRecordButtonClicked(MouseEvent mouseEvent) {
    }

    public void deleteRecordButtonClicked(MouseEvent mouseEvent) {
    }

    public void addGoalButtonClicked(MouseEvent mouseEvent) {
    }

    public void editGoalButtonClicked(MouseEvent mouseEvent) {
    }

    public void graphsButtonClicked(MouseEvent mouseEvent) {
    }

    public void factButtonClicked(MouseEvent mouseEvent) {
    }

    public User getUser() {
        return user;
    }

    public void updateTable () {
        writeToFile();
        if (!user.getRecords().isEmpty()) {
            recordsTable.getItems().setAll(user.getRecords());
        } else {
            recordsTable.getItems().clear();
        }
        recordsTable.refresh();
    }

    private void writeToFile () {
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        File directory = new File(System.getProperty("user.home") + "\\Hydrohomie");
        if (!directory.exists()) directory.mkdir();

        try {
            objectMapper.writeValue(new File(System.getProperty("user.home") + "\\Hydrohomie\\user.json"), user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFromFile () {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            user = objectMapper.readValue(new File(System.getProperty("user.home") + "\\Hydrohomie\\user.json"), User.class);
        } catch (IOException e) {
            user = new User();
        }
    }
}
