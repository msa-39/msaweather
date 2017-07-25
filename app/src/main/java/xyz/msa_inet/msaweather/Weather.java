package xyz.msa_inet.msaweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by moiseev on 19.07.2017.
 */

public class Weather {

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
    public static final String DATE_FORMAT = "dd/MM";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_ICON = "icon";

//    private static final String owm_base_url = "http://api.openweathermap.org/data/2.5/";
//    private static final String owm_app_id = "117d5fa1db04067b40a31da2c1b139ae";
//    private static final String owm_forecast_url = "http://api.openweathermap.org/data/2.5/forecast?q=Kaliningrad,ru&units=metric&lang=ru&APPID=117d5fa1db04067b40a31da2c1b139ae";
//    private static final String owm_daily_forecast_url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Kaliningrad,ru&units=metric&lang=ru&APPID=117d5fa1db04067b40a31da2c1b139ae&cnt=2";
//    private static final String owm_weather_url = "http://api.openweathermap.org/data/2.5/weather?q=Kaliningrad,ru&units=metric&lang=ru&APPID=117d5fa1db04067b40a31da2c1b139ae";

//    private static final String undergroundweather_base_url = "http://api.wunderground.com/api/";
//    private static final String undergroundweather_app_id = "c73f181aa753933a";
//    private static final String undergroundweather_url = "http://api.wunderground.com/api/c73f181aa753933a/conditions/q/RU/Kaliningrad.json";

    public static void GetWeather (Context context, final OnCompleteListener listener){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String owm_city = settings.getString("owm_city","Kaliningrad,ru");
        String owm_base_url = settings.getString("owm_base_url","http://api.openweathermap.org/data/2.5/");
        String owm_app_id = settings.getString("owm_api_key","117d5fa1db04067b40a31da2c1b139ae");
        String owm_api_daily = settings.getString("owm_api_daily","/forecast/daily?units=metric&cnt=2&lang=ru");

        String owm_url = owm_base_url+owm_api_daily+"&q="+owm_city+"&APPID="+owm_app_id;

        new DownloadDataTask() {
            @Override
            protected void onPostExecute(String result) {
                if (!result.isEmpty()) {
                    try {
                        toForecast(result);
                        Log.d("MSA Weather JsonConverterWeather","DONE");
                        if (listener != null) listener.onCompleteGetWeather();
                    } catch (Exception e) {
                        Log.e("MSA Weather Exception","GetWeather Error");
                        System.out.println("Exception "+ e.getMessage());
                        if (listener != null) listener.onError();
                    }
                } else if (listener != null) listener.onError();
            }
        }.execute(owm_url,"GET");
    }

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
            info.weather_icon = a.getJSONObject(i).getJSONArray(FIELD_WEATHER).getJSONObject(0).getString(FIELD_ICON);

            Utils.WeatherInfo[i] = info;
        }
    }
}
