import sun.nio.ch.Util;

import java.util.Date;

public class Goal {

    private int targetVolume;
    int points;
    private Date creationDate;

    public Goal (){

    }

    public Goal (int targetVolume, int points) {
        this.targetVolume = targetVolume;
        this.points = points;
        this.creationDate = Utils.stripDate(new Date());
    }

    public boolean matches (Goal goal) {
        return this.getTargetVolume() == goal.getTargetVolume() && this.getPoints() == goal.getPoints() && this.getCreationDate().getTime() == goal.getCreationDate().getTime();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getTargetVolume() {
        return targetVolume;
    }

    public int getPoints() {
        return points;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setTargetVolume(int targetVolume) {
        this.targetVolume = targetVolume;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
