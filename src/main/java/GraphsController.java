import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GraphsController {

    @FXML private Label favDrinkLabel;
    @FXML private Label goalsMetLabel;
    @FXML private Label percentageGoalsMetLabel;
    @FXML private Label avgDailyIntakeLabel;
    @FXML private LineChart<Number, Number> lineGraph;
    @FXML private Button viewButton;
    @FXML private CheckBox recordsCheckbox;
    @FXML private CheckBox goalsCheckbox;
    @FXML private Button exitButton;

    @FXML
    public void initialize () throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();
        MainController mainController = loader.getController();

        favDrinkLabel.setText(mainController.getUser().getFavouriteDrink().toString());
        goalsMetLabel.setText(String.valueOf(mainController.getUser().getDailyGoalsMet()));
        percentageGoalsMetLabel.setText(String.format("%.0f", mainController.getUser().getPercentageDailyGoalsMet()) + "%");
        avgDailyIntakeLabel.setText(mainController.getUser().getAverageDailyVolume() + "ml");
    }

    public void viewButtonClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();
        MainController mainController = loader.getController();

        ArrayList<Record> records = mainController.getUser().getRecords();
        ArrayList<Goal> dailyGoals = mainController.getUser().getDailyGoals();
        ArrayList<Double> lowerUpperBound = mainController.getUser().getLowerUpperBound();

        // Set some custom properties of the graph to render the graph properly
        ((NumberAxis) lineGraph.getXAxis()).setTickLabelFormatter(stringConverter);
        lineGraph.getXAxis().setAutoRanging(false);
        ((NumberAxis) lineGraph.getXAxis()).setTickUnit(3600000d);
        ((NumberAxis) lineGraph.getXAxis()).setLowerBound(lowerUpperBound.get(0));
        ((NumberAxis) lineGraph.getXAxis()).setUpperBound(lowerUpperBound.get(1));

        XYChart.Series<Number, Number> recordSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> goalSeries = new XYChart.Series<>();



        recordSeries.setName("Records");
        goalSeries.setName("Goals");

        // Ensure the graph data is empty before adding to it
        lineGraph.getData().clear();

        // Add the data to the data series
        records.forEach(rec -> recordSeries.getData().add(new XYChart.Data<Number, Number>(rec.getDate().getTime(), rec.getVolume())));
        dailyGoals.forEach(goal -> goalSeries.getData().add(new XYChart.Data<Number, Number>(goal.getCreationDate().getTime(), goal.getTargetVolume())));


        if (recordsCheckbox.isSelected() && goalsCheckbox.isSelected()) {
            lineGraph.getData().add(recordSeries);
            lineGraph.getData().add(goalSeries);
        } else if (recordsCheckbox.isSelected()) {
            lineGraph.getData().add(recordSeries);
        } else if (goalsCheckbox.isSelected()) {
            lineGraph.getData().add(goalSeries);
        }

    }

    public void exitButtonCLicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent mainGUI = loader.load();

        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.setScene(new Scene(mainGUI, 580, 320));
    }

    // Custom String Converter to properly deal with dates
    StringConverter<Number> stringConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number object) {
            Date date = new Date(object.longValue());
            return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
        }

        @Override
        public Number fromString(String string) {
            try {
                Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(string);
                return date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    };
}
