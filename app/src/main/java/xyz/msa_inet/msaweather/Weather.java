package xyz.msa_inet.msaweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by moiseev on 19.07.2017.
 */

public class Weather {
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
                        WeatherJsonConverter.toForecast(result);
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
}
