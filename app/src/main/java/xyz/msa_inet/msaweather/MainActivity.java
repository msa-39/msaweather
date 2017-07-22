package xyz.msa_inet.msaweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnCompleteListener{
    /**
     * is download data on progress
     */
    static Boolean onProcess = false;

    public static String[] weatherTXT = new String[2];

    Timer timer;
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = new Timer();
        timerTask = new msaTimerTask();
        timer.schedule(timerTask,0,60000);
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

class msaTimerTask extends TimerTask {
    @Override
    public void run() {
        // Берем дату и время с системного календаря:
        Calendar calendar = Calendar.getInstance();

        String h = new SimpleDateFormat("k").format(calendar.getTime());
//        Log.i("MSA Weather HOUR",h);
        String m = new SimpleDateFormat("m").format(calendar.getTime());
//        Log.i("MSA Weather MINUTES",m);
        if (h.equals("12") & m.equals("0")) getWeather(null);
        if (h.equals("12") & m.equals("5")) sendSms(null);
    }
}
}
