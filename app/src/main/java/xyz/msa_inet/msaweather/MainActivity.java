package xyz.msa_inet.msaweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnCompleteListener{
    /**
     * is download data on progress
     */
    static Boolean onProcess = false;
    public static String[] weatherTXT = new String[2];
//    weatherTXT[0] = "" ; weatherTXT[1]="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void getWeather (View v){
        Weather.GetWeather(this,this);
    }

    @Override
    public void onCompleteGetWeather() {
        onProcess = false;
        String weatherMSG = "";
        TextView weatherText = (TextView)findViewById(R.id.weatherText);
        if (Utils.WeatherInfo.length > 0 ) {
            for (int i = 0; i < Utils.WeatherInfo.length; ++i) {
                if (i < 1) weatherTXT[i] = "Сегодня"; else weatherTXT[i] = "Завтра";
                //weatherTXT += Utils.WeatherInfo[i].date;
                weatherTXT[i] += " " + Utils.WeatherInfo[i].weather;
                weatherTXT[i] += "\n" + "Ут "  + Utils.WeatherInfo[i].mon_temp + "°";
                weatherTXT[i] += "\n" + "Дн "  + Utils.WeatherInfo[i].day_temp + "°";
                weatherTXT[i] += "\n" + "Веч " + Utils.WeatherInfo[i].evn_temp + "°";
                weatherTXT[i] += "\n" + "Вет " + Utils.WeatherInfo[i].wind_speed + "м/с " + Utils.WeatherInfo[i].wind;
                weatherTXT[i] += "\n" + "Дав " + Utils.WeatherInfo[i].pressure;
                weatherTXT[i] += "\n" + "Вл "  + Utils.WeatherInfo[i].humidity + "%";
                if (i < 1) {
                    weatherTXT[i] += "\n";
                    weatherTXT[i] += "\n";
                }
                weatherMSG += weatherTXT[i];
            }
        }
        weatherText.setText(weatherMSG);
    }

    public void sendSms (View v){

        for (int i = 0; i < 2; ++i) {
            try {
                Log.d("MSA Weather Lenth of SMS is",String.valueOf(weatherTXT[i].length()));
                SMS.SendSms(this,weatherTXT[i],this);
            } catch (Exception e){
                Log.e("MSA Weather Send SMS from main","EROOR");
                System.out.println("Exception "+ e.getMessage());
            }
        }
    }

    @Override
    public void onCompleteSendSms() {
        onProcess = false;
        String smsResText = "";
        Log.d("MSA Weather onCompleteSendSms","Complete");
        TextView smsResult = (TextView)findViewById(R.id.sendSMSres);

        if (Utils.SmsResultTxt.req_status.equals("OK")) {
            if (Utils.SmsResultTxt.sms_status.equals("OK")) {
                smsResText += "СМС успешно отправлено." + "\n";
                smsResText += "ID сообщения - " + Utils.SmsResultTxt.sms_id + "\n";
            } else {
                smsResText += "СМС НЕ отправлено!" + "\n";
                smsResText += "Код ошибки: " + Utils.SmsResultTxt.sms_status_code +"\n";
                smsResText += "Текст ошибки: " + Utils.SmsResultTxt.sms_status_text +"\n";
            }
        } else {
            smsResText += "Запрос на отправку СМС НЕ выполнился!" + "\n";
            smsResText += "Код ошибки: " + Utils.SmsResultTxt.req_status_code +"\n";
            smsResText += "Текст ошибки: " + Utils.SmsResultTxt.req_status_text +"\n";
        }

        smsResult.setText(smsResText);
    }

    @Override
    public void onError() {
        onProcess = false;
        Log.e("MSA Weather onError","Error");
    }

}








/*
    JSONObject data = null;
    JSONObject smsres = null;

    String weatherURL = owm_weather_url;
    String sms_URL = sms_gate_url;
    String smsMSG ="";
    TextView smsTXTView;
    TextView smsSendRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsTXTView=(TextView)findViewById(R.id.smsText);
        smsSendRes=(TextView)findViewById(R.id.sendSMSres);
        getJSON("Kaliningrad,ru");
    }


    public void getJSON(final String city) {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                     URL url = new URL(weatherURL);
                            //URL("http://api.openweathermap.org/data/2.5/weather?q="+city+"&APPID=ea574594b9d36ab688642d5fbeab847e");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";

                    while((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONObject(json.toString());

                    if(data.getInt("cod") != 200) {
                        Log.w("URL = ",weatherURL);
                        Log.w("MSA Weather Cancelled received",data.toString());
//                        System.out.println("Cancelled");
//                        connection.disconnect();
                        return null;
                    }


                } catch (Exception e) {

//                    System.out.println("Exception "+ e.getMessage());
                    Log.e("URL = ",weatherURL);
                    Log.e("MSA Weather Exception ",e.getMessage().toString());
//                    connection.disconnect();
                    return null;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if(data!=null){
                    Log.d("URL = ",weatherURL);
                    Log.d("MSA Weather received",data.toString());

                    try {
//                        smsMSG = data.getString("name").toUpperCase(Locale.US) +
//                                ", " +
//                                data.getJSONObject("sys").getString("country") +
//                                " ";

                        JSONObject details = data.getJSONArray("weather").getJSONObject(0);
                        JSONObject main = data.getJSONObject("main");
                        JSONObject wind = data.getJSONObject("wind");
                        smsMSG +=
                                details.getString("description").toUpperCase(Locale.US) +
                                        "\n" + String.format("+%.0f", main.getDouble("temp")) + " ℃" +
                                        "\n" + "Влаж. " + main.getString("humidity") +
                                        "\n" + "Дав. " + String.format("%.0f",main.getDouble("pressure")*0.7500637554192) + " мм р.с." +
                                        "\n" + "Ветер " + get_wind_direction(wind.getInt("deg")) + " "+ wind.getString("speed") + " м/с";

                        Log.d("MSA Weather. Длинна сообщения ",Integer.toString(smsMSG.length()));

//                        DateFormat df = DateFormat.getDateTimeInstance();
//                        String updatedOn = df.format(new Date(data.getLong("dt")*1000));

//                        smsMSG += "Last update: " + updatedOn;

//                        setWeatherIcon(details.getInt("id"),
//                                json.getJSONObject("sys").getLong("sunrise") * 1000,
//                                json.getJSONObject("sys").getLong("sunset") * 1000);

                    }catch(Exception e){
                        Log.e("MSA Weather", "One or more fields not found in the JSON data");
//                        Log.e("MSA Weather",e.getMessage().toString());
//                        smsSendRes.setText(e.getMessage().toString());
                    }

                    smsTXTView.setText(smsMSG);

                    sms_URL = sms_gate_url + smsMSG;

                    try {
                         URL url_sms = new URL(sms_URL);

                        HttpURLConnection connection_sms = (HttpURLConnection) url_sms.openConnection();

                        BufferedReader reader_sms =
                                new BufferedReader(new InputStreamReader(connection_sms.getInputStream()));

                        StringBuffer json_sms = new StringBuffer(1024);
                        String tmp_sms = "";

                        while((tmp_sms = reader_sms.readLine()) != null)
                            json_sms.append(tmp_sms).append("\n");

                        reader_sms.close();

                        smsres = new JSONObject(json_sms.toString());

                    } catch (Throwable t) {
                        Log.e("MSA Weather Exception send SMS","Exception");
                        Log.e("sms_gate_URL = ",sms_gate_url);
                        Log.e("sms_MSG = ",smsMSG);
                        Log.e("sms_URL = ",sms_URL);
                        t.printStackTrace();
//                        Log.e("MSA Weather Exception send SMS ",e.getMessage().toString());
//                        smsSendRes.setText(e.getMessage().toString());
                    }

                    try {
                        if(smsres.getInt("status_code") != 100) {
                            Log.e("MSA Weather ERROR send SMS","status_code != 100");
                            Log.e("sms_gate_URL = ",sms_gate_url);
                            Log.e("sms_MSG = ",smsMSG);
                            Log.e("sms_URL = ",sms_URL);
//                            Log.e("MSA Weather ERROR send SMS",smsres.getString("status_text").toString());
//                            Log.e("MSA Weather ERROR send SMS resived",smsres.toString());
//                            smsSendRes.setText(smsres.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();

    }
}
*/