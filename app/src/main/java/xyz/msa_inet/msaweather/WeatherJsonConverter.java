package xyz.msa_inet.msaweather;

/**
 * Created by moiseev on 19.07.2017.
 */

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherJsonConverter {
    public static final String FIELD_LIST = "list";
    public static final String FIELD_TEMP = "temp";
    public static final String FIELD_WIND = "deg";
    public static final String FIELD_SPEED = "speed";
    public static final String FIELD_MORN = "morn";
    public static final String FIELD_DAY = "day";
    public static final String FIELD_EVE = "eve";
    public static final String FIELD_DT = "dt";
    public static final String FIELD_WEATHER = "weather";
    public static final String FIELD_PRESSURE = "pressure";
    public static final String FIELD_HUMIDITY = "humidity";
    public static final String DATE_FORMAT = "EE dd/MM";
    public static final String FIELD_DESCRIPTION = "description";

    public static void toForecast(String data) throws Exception {

        JSONArray a = new JSONObject(data).getJSONArray(FIELD_LIST);

        for (int i = 0; i < a.length(); ++i) {
            ForecastInfo info = new ForecastInfo();

            info.date = Utils.calcDate(DATE_FORMAT, a.getJSONObject(i).getString(FIELD_DT));
            info.weather  = a.getJSONObject(i).getJSONArray(FIELD_WEATHER).getJSONObject(0).getString(FIELD_DESCRIPTION);
            info.mon_temp = String.format("%+.0f",a.getJSONObject(i).getJSONObject(FIELD_TEMP).getDouble(FIELD_MORN));
            info.day_temp = String.format("%+.0f",a.getJSONObject(i).getJSONObject(FIELD_TEMP).getDouble(FIELD_DAY));
            info.evn_temp = String.format("%+.0f",a.getJSONObject(i).getJSONObject(FIELD_TEMP).getDouble(FIELD_EVE));
            info.pressure = Utils.calcPressure(a.getJSONObject(i).getString(FIELD_PRESSURE));
            info.humidity = String.format("%.0f",a.getJSONObject(i).getDouble(FIELD_HUMIDITY));
            info.wind =  Utils.get_wind_dir(a.getJSONObject(i).getInt(FIELD_WIND));
            info.wind_speed =  String.format("%.0f",a.getJSONObject(i).getDouble(FIELD_SPEED));

            Utils.WeatherInfo[i] = info;
        }
    }

}
