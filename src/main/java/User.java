import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private ArrayList<Record> records = new ArrayList<>();
    private ArrayList<Goal> dailyGoals = new ArrayList<>();
    private int points = 0;

    public User () {

    }

    public int getPoints() {
        return points;
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
            if (goal.getCreationDate().equals(todayDate)) return goal; // Find the relevant goal and return it
        }
        return null;
    }

    public float getDailyGoalProgress () {
        if (getCurrentDailyGoal() == null) return 0f;

        float progress = 0f;

        progress = (float)getVolumeOn(new Date()) / (float)getCurrentDailyGoal().getTargetVolume(); // Calculate progress on daily goal by collating total volume on the day and dividing by target volume

        return progress;
    }

    public int getVolumeOn (Date date) {
        int totalVolume = 0;
        Date strippedDate = Utils.stripDate(date);

        for (Record rec : records) {
            if (Utils.stripDate(rec.getDate()).equals(strippedDate)) {
                totalVolume += rec.getVolume(); // Add up all the volume of water drank on the day
            }
        }
        return totalVolume;
    }

    public LiquidType getFavouriteDrink () {
        // Go through all the records and find which drink has the highest usage frequency
        LiquidType favouriteDrink = LiquidType.WATER;
        ArrayList<LiquidType> types = new ArrayList<>();
        records.forEach(n -> types.add(n.getLiquidType()));
        int currMax = Collections.frequency(types, LiquidType.WATER);

        if (currMax < Collections.frequency(types, LiquidType.MILK)) {
            currMax = Collections.frequency(types, LiquidType.MILK);
            favouriteDrink = LiquidType.MILK;
        }
        if (currMax < Collections.frequency(types, LiquidType.SOUP)) {
            currMax = Collections.frequency(types, LiquidType.SOUP);
            favouriteDrink = LiquidType.SOUP;
        }
        if (currMax < Collections.frequency(types, LiquidType.SODA)) {
            favouriteDrink = LiquidType.SODA;
        }
        return favouriteDrink;
    }

    public int getDailyGoalsMet () {
        int counter = 0;
        for (Goal goal : dailyGoals) {
            if (goal.isCompleted()) counter++; // Count the number of goals completed
        }
        return counter;
    }

    public float getPercentageDailyGoalsMet () {
        int goalsMet = getDailyGoalsMet();
        int goalsSet = dailyGoals.size();
        return ((float)goalsMet / (float)goalsSet) * 100;
    }

    public int getAverageDailyVolume () {
        // Go through every day for which a record is recorded and find the total volume and then find the average daily volume
        Date earliestDate = new Date();
        for (Record rec : records) {
            if (rec.getDate().getTime() < earliestDate.getTime()) {
                earliestDate = rec.getDate();
            }
        }

        earliestDate = Utils.stripDate(earliestDate);
        Date today = Utils.stripDate(new Date());

        int totalVolume = 0;
        int daysCounter = 0;

        while (earliestDate.getTime() <= today.getTime()) {
            totalVolume += getVolumeOn(earliestDate);
            daysCounter++;
            earliestDate.setTime(earliestDate.getTime() + 86400000);
        }
        return totalVolume / daysCounter;
    }

    public ArrayList<Double> getLowerUpperBound () {
        // Go through all the records and find the earliest date where a record was recorded
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

    public Response addGoal (String volume, String points) {
        // Attempt to add a goal and return the response
        Response response = Response.ADDGOALSUCCESS;
        if (getCurrentDailyGoal() != null) {
            response = Response.ADDGOALFAILURE;
        } else {
            try {
                int newVolume = Integer.parseInt(volume);
                int newPoints = Integer.parseInt(points);

                dailyGoals.add(new Goal(newVolume, newPoints));
            } catch (IllegalArgumentException e) {
                response = Response.ADDGOALFAILURE;
            }
        }
        return response;
    }

    public Response addRecord (String volume, LiquidType type, String date) {
        // Attempt to add a record and return the response
        Response response = Response.ADDRECSUCCESS;
        try {
            int newVolume = Integer.parseInt(volume);
            Date newDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date);

            if (getCurrentDailyGoal() != null) {
                if (getVolumeOn(new Date()) + newVolume >= getCurrentDailyGoal().getTargetVolume() && !getCurrentDailyGoal().isCompleted()) {
                    getCurrentDailyGoal().setCompleted(true);
                    points += getCurrentDailyGoal().getPoints();
                    response = Response.ADDRECSUCCESSGOALCOMPLETE;
                }
            }

            records.add(new Record(newDate, type, newVolume));
        } catch (IllegalArgumentException | ParseException e) {
            response = Response.ADDRECFAILURE;
        }
        return response;
    }

    public Response editRecord (Record recordToEdit, String volume, LiquidType type, String date) {
        // Attempt to edit a record and return the response
        Response response = Response.EDITRECFAILURE;
        try {
            int newVolume = Integer.parseInt(volume);
            Date newDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(date);

            for (Record rec : records) {
                if (rec.matches(recordToEdit)) {
                    rec.setDate(newDate);
                    rec.setLiquidType(type);
                    rec.setVolume(newVolume);
                    response = Response.EDITRECSUCCESS;

                    if (getCurrentDailyGoal() != null) {
                        if (getVolumeOn(new Date()) < getCurrentDailyGoal().getTargetVolume() && getCurrentDailyGoal().isCompleted()) {
                            getCurrentDailyGoal().setCompleted(false);
                            response = Response.EDITRECSUCCESSGOALINCOMPLETE;
                            points -= getCurrentDailyGoal().getPoints();

                        } else if (getVolumeOn(new Date()) >= getCurrentDailyGoal().getTargetVolume() && !getCurrentDailyGoal().isCompleted()){
                            response = Response.EDITRECSUCCESSGOALCOMPLETE;
                            points += getCurrentDailyGoal().getPoints();
                        }
                    }
                    break;
                }
            }
        } catch (IllegalArgumentException | ParseException e) {
            response = Response.EDITRECFAILURE;
        }
        return response;
    }

    public Response deleteRecord (Record recordToDelete) {
        // Attempt to delete a record and return the response
        Response response = Response.DELETERECFAILURE;

        for (Record rec : records) {
            if (rec.matches(recordToDelete)) {
                records.remove(rec);
                response = Response.DELETERECSUCCESS;

                if (getCurrentDailyGoal() != null) {
                    if (getVolumeOn(new Date()) < getCurrentDailyGoal().getTargetVolume() && getCurrentDailyGoal().isCompleted()) {
                        getCurrentDailyGoal().setCompleted(false);
                        response = Response.DELETERECSUCCESSGOALINCOMPLETE;
                        points -= getCurrentDailyGoal().getPoints();
                    }
                }
                break;
            }
        }

        return response;
    }

    public Response editGoal(Goal goalEditing, String volume, String points) {
        // Attempt to edit a goal and return the response
        Response response = Response.EDITGOALFAILURE;

        try {
            int newVolume = Integer.parseInt(volume);
            int newPoints = Integer.parseInt(points);

            for (Goal goal : dailyGoals) {
                if (goal.matches(goalEditing)) {
                    goal.setTargetVolume(newVolume);
                    goal.setPoints(newPoints);
                    response = Response.EDITGOALSUCCESS;
                    if (getVolumeOn(new Date()) < getCurrentDailyGoal().getTargetVolume() && getCurrentDailyGoal().isCompleted()) {
                        getCurrentDailyGoal().setCompleted(false);
                        response = Response.EDITGOALSUCCESSNOWINCOMPLETE;
                        this.points -= getCurrentDailyGoal().getPoints();

                    } else if (getVolumeOn(new Date()) >= getCurrentDailyGoal().getTargetVolume() && !getCurrentDailyGoal().isCompleted()){
                        response = Response.EDITGOALSUCCESSNOWCOMPLETE;
                        this.points += getCurrentDailyGoal().getPoints();
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            response =  Response.EDITGOALFAILURE;
        }
        return response;
    }
}
