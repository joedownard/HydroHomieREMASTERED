import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML private Label dailyGoalVolumeLabel;
    @FXML private Label volumeTodayLabel;
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

    @FXML
    public void initialize() {
        readFromFile();
        date.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
        type.setCellValueFactory(new PropertyValueFactory<>("liquidType"));
        volume.setCellValueFactory(new PropertyValueFactory<>("volume"));

        update();
    }

    public void addRecordButtonClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addRecord.fxml"));
        Parent addRecordGUI = loader.load();

        Stage stage = (Stage) addRecordButton.getScene().getWindow();
        stage.setScene(new Scene(addRecordGUI));
    }

    public void editRecordButtonClicked(MouseEvent mouseEvent) throws IOException {
        if (recordsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to edit record");
            alert.setHeaderText(null);
            alert.setContentText("No record selected!");
            alert.showAndWait();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addRecord.fxml"));
        Parent addRecordGUI = loader.load();

        AddRecordController addRecordController = loader.getController();
        addRecordController.setEditing(recordsTable.getSelectionModel().getSelectedItem());

        Stage stage = (Stage) addRecordButton.getScene().getWindow();
        stage.setScene(new Scene(addRecordGUI));

    }

    public void deleteRecordButtonClicked(MouseEvent mouseEvent) {
    }

    public void addGoalButtonClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addGoal.fxml"));
        Parent addGoalGUI = loader.load();

        Stage stage = (Stage) addGoalButton.getScene().getWindow();
        stage.setScene(new Scene(addGoalGUI));
    }

    public void editGoalButtonClicked(MouseEvent mouseEvent) throws IOException {
        if (getUser().getCurrentDailyGoal() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to edit record");
            alert.setHeaderText(null);
            alert.setContentText("There is no current daily goal to edit!");
            alert.showAndWait();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("addGoal.fxml"));
        Parent addGoalGUI = loader.load();

        AddGoalController addGoalController = loader.getController();
        addGoalController.setEditing(getUser().getCurrentDailyGoal());

        Stage stage = (Stage) addGoalButton.getScene().getWindow();
        stage.setScene(new Scene(addGoalGUI));
    }

    public void graphsButtonClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("graphs.fxml"));
        Parent graphsGUI = loader.load();

        Stage stage = (Stage) addRecordButton.getScene().getWindow();
        stage.setScene(new Scene(graphsGUI));
    }

    public void factButtonClicked(MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Water Fact");
        alert.setHeaderText(null);
        alert.setContentText("Water is a liquid!");
        alert.showAndWait();
    }

    public User getUser() {
        return user;
    }

    public void update () {
        writeToFile();

        if (getUser().getCurrentDailyGoal() != null) {
            dailyProgressBar.setProgress(getUser().getDailyGoalProgress());
            dailyGoalVolumeLabel.setText(getUser().getCurrentDailyGoal().getTargetVolume() + "ml");
        } else {
            dailyProgressBar.setVisible(false);
            dailyGoalVolumeLabel.setVisible(false);
            dailyGoalLabel.setVisible(false);
        }

        volumeTodayLabel.setText(getUser().getVolumeToday() + "ml");

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
