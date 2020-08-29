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
import java.util.Date;
import java.util.List;

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

    private User user;

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
        if (recordsTable.getSelectionModel().getSelectedItem() == null) { // Deal with an error in selecting an item
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
        addRecordController.setEditing(recordsTable.getSelectionModel().getSelectedItem()); // Set the mode to editing

        Stage stage = (Stage) addRecordButton.getScene().getWindow();
        stage.setScene(new Scene(addRecordGUI));

    }

    public void deleteRecordButtonClicked(MouseEvent mouseEvent) {
        Response response = getUser().deleteRecord(recordsTable.getSelectionModel().getSelectedItem()); // Attempt to delete the record and get the record
        // Deal with the response
        if (response == Response.DELETERECSUCCESS) {
            // For completeness so all the cases are explicitly dealt with
        } else if (response == Response.DELETERECFAILURE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to delete record");
            alert.setHeaderText(null);
            alert.setContentText("No record selected!");
            alert.showAndWait();
        } else if (response == Response.DELETERECSUCCESSGOALINCOMPLETE) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Goal no longer complete!");
            alert.setHeaderText(null);
            alert.setContentText("Deleting this record caused you to no longer fulfill the daily goal! You have lost " + getUser().getCurrentDailyGoal().getPoints() + " points!");
            alert.showAndWait();
        }
        update();
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
        addGoalController.setEditing(getUser().getCurrentDailyGoal()); // Set editing mode

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
        writeToFile(); // Save data to a file

        if (getUser().getCurrentDailyGoal() != null) { // Update the daily goal stats
            dailyProgressBar.setVisible(true);
            dailyGoalVolumeLabel.setVisible(true);
            dailyGoalLabel.setVisible(true);

            dailyProgressBar.setProgress(getUser().getDailyGoalProgress());
            if (getUser().getCurrentDailyGoal().isCompleted()) {
                dailyProgressBar.setStyle("-fx-accent: green;"); // Make the bar green if its complete
            } else {
                dailyProgressBar.setStyle("");
            }
            dailyGoalVolumeLabel.setText(getUser().getCurrentDailyGoal().getTargetVolume() + "ml");
        } else { // If there is no daily goal, hide all the related visual items
            dailyProgressBar.setVisible(false);
            dailyGoalVolumeLabel.setVisible(false);
            dailyGoalLabel.setVisible(false);
        }

        pointsValueLabel.setText(String.valueOf(getUser().getPoints()));
        volumeTodayLabel.setText(getUser().getVolumeOn(new Date()) + "ml");

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
        if (!directory.exists()) directory.mkdir(); // Ensure the directory exists

        try {
            objectMapper.writeValue(new File(System.getProperty("user.home") + "\\Hydrohomie\\user.json"), user); // Write the data to a JSON file
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
