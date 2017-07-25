package xyz.msa_inet.msaweather;

/**
 * Created by moiseev on 19.07.2017.
 */
import java.io.Serializable;

public class ForecastInfo implements Serializable {
    public String date;
    public String weather;
    public String mon_temp;
    public String day_temp;
    public String evn_temp;
    public String pressure;
    public String humidity;
    public String wind;
    public String wind_speed;
    public String weather_icon;
}
