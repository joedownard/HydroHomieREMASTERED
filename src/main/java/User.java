import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private ArrayList<Record> records = new ArrayList<>();
    private ArrayList<Goal> dailyGoals = new ArrayList<>();

    public User () {

    }

    public ArrayList<Goal> getDailyGoals() {
        return dailyGoals;
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    public Goal getCurrentDailyGoal () {
        Date todayDate = Utils.stripDate(new Date());
        for (Goal goal : dailyGoals) {
            if (goal.getCreationDate().equals(todayDate)) return goal;
        }
        return null;
    }

    public boolean addRecord (String volume, LiquidType type, String date) {
        boolean success = true;
        try {
            int newVolume = Integer.parseInt(volume);
            Date newDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date);

            records.add(new Record(newDate, type, newVolume));
        } catch (IllegalArgumentException | ParseException e) {
            return success = false;
        }
        return success;
    }


}
