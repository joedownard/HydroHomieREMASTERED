import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    public float getDailyGoalProgress () {
        if (getCurrentDailyGoal() == null) return 0f;

        float progress = 0f;

        progress = (float)getVolumeToday() / (float)getCurrentDailyGoal().getTargetVolume();

        return progress;
    }

    public int getVolumeToday () {
        int totalVolume = 0;
        Date todayDate = Utils.stripDate(new Date());

        for (Record rec : records) {
            if (Utils.stripDate(rec.getDate()).equals(todayDate)) {
                totalVolume += rec.getVolume();
            }
        }
        return totalVolume;
    }

    public ArrayList<Double> getLowerUpperBound () {
        double min = new Date().getTime();
        double max = 0d;
        for (Record rec : records) {
            if (rec.getDate().getTime() > max) max = rec.getDate().getTime();
            if (rec.getDate().getTime() < min) min = rec.getDate().getTime();
        }
        for (Goal goal : dailyGoals) {
            if (goal.getCreationDate().getTime() > max) max = goal.getCreationDate().getTime();
            if (goal.getCreationDate().getTime() < min) min = goal.getCreationDate().getTime();
        }
        return new ArrayList<>(Arrays.asList(min-21600000, max+21600000));
    }

    public boolean addGoal (String volume, String points) {
        if (getCurrentDailyGoal() != null) return false;

        try {
            int newVolume = Integer.parseInt(volume);
            int newPoints = Integer.parseInt(points);

            dailyGoals.add(new Goal(newVolume, newPoints));
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public boolean addRecord (String volume, LiquidType type, String date) {
        try {
            int newVolume = Integer.parseInt(volume);
            Date newDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date);

            records.add(new Record(newDate, type, newVolume));
        } catch (IllegalArgumentException | ParseException e) {
            return false;
        }
        return true;
    }

    public boolean editRecord (Record recordToEdit, String volume, LiquidType type, String date) {
        try {
            int newVolume = Integer.parseInt(volume);
            Date newDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date);

            for (Record rec : records) {
                if (rec.matches(recordToEdit)) {
                    rec.setDate(newDate);
                    rec.setLiquidType(type);
                    rec.setVolume(newVolume);
                    return true;
                }
            }
        } catch (IllegalArgumentException | ParseException e) {
            return false;
        }
        return false;
    }


    public boolean editGoal(Goal goalEditing, String volume, String points) {
        if (getCurrentDailyGoal() == null) return false;

        try {
            int newVolume = Integer.parseInt(volume);
            int newPoints = Integer.parseInt(points);

            for (Goal goal : dailyGoals) {
                if (goal.matches(goalEditing)) {
                    goal.setTargetVolume(newVolume);
                    goal.setPoints(newPoints);
                    return true;
                }
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
