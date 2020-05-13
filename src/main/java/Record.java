import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Record {

    private Date date;
    private LiquidType liquidType;
    private int volume;

    public Record () {

    }

    public Record(Date newDate, LiquidType newType, int newVolume) {
        this.date = newDate;
        this.liquidType = newType;
        this.volume = newVolume;
    }

    public Date getDate () {
        return date;
    }

    public String getFormattedDate() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LiquidType getLiquidType() {
        return liquidType;
    }

    public void setLiquidType(LiquidType liquidType) {
        this.liquidType = liquidType;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
