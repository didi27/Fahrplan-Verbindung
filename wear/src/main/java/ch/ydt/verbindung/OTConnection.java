package ch.ydt.verbindung;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by didi on 06.09.14.
 */
public class OTConnection {
    private Date departureTime;
    private Date arrivalTime;
    private String locationTo;
    private String locationFrom;
    private Date duration;
    private int platform;

    public String getLocationTo() {
        return locationTo;
    }

    public void setLocationTo(String locationTo) {
        this.locationTo = locationTo;
    }

    public String getLocationFrom() {
        return locationFrom;
    }

    public void setLocationFrom(String locationFrom) {
        this.locationFrom = locationFrom;
    }

    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration;
    }

    public Date getdepartureTime() {
        return departureTime;
    }

    public void setdepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getarrivalTime() {
        return arrivalTime;
    }

    public void setarrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public OTConnection(String locationFrom, String locationTo, Date departureTime, Date arrivalTime, Date duration, int platform){
    this.locationFrom = locationFrom;
    this.locationTo = locationTo;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.platform = platform;
    //dateFrom = new SimpleDateFormat("HH:mm").format(new Date(Integer.parseInt(timeStampFrom)*1000));
    //this.dateTo = new SimpleDateFormat("HH:mm").format(new Date(timeStampTo*1000));
    this.locationTo = locationTo;
    this.duration = duration;

}
}
