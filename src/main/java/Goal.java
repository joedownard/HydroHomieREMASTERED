import java.util.Date;

public class Goal {

    private int targetVolume;
    private Date creationDate;

    public Goal (int targetVolume, Date creationDate) {
        this.targetVolume = targetVolume;
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getTargetVolume() {
        return targetVolume;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setTargetVolume(int targetVolume) {
        this.targetVolume = targetVolume;
    }
}
