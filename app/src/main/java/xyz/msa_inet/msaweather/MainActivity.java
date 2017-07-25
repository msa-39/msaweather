package xyz.msa_inet.msaweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
// import android.support.v4.*;

public class MainActivity extends AppCompatActivity implements OnCompleteListener{
    /**
     * is download data on progress
     */
    static Boolean onProcess = false;

    public static String[] weatherTXT = new String[2];

    Timer timer;
    TimerTask timerTask;
    Button startTimer;
    Button cancelTimer;
//    Calendar calendar;
    String hour = "9";
    String minutes = "30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTimer = (Button)findViewById(R.id.startTimer);
        cancelTimer = (Button)findViewById(R.id.cancelTimer);
        startTimer.setClickable(true);
        startTimer.setTextColor(Color.WHITE);
        cancelTimer.setClickable(false);
        cancelTimer.setTextColor(Color.GRAY);

        EditText h_edit = (EditText)findViewById(R.id.hourEdit);
//        TextView h_text = (TextView)findViewById(R.id.hour);

        EditText m_edit = (EditText)findViewById(R.id.minutesEdit);
//        TextView m_text = (TextView)findViewById(R.id.minutes);

        h_edit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                hour = String.valueOf(s);
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                TextView h_text = (TextView)findViewById(R.id.hour);
                h_text.setText(s);
//                hour = String.valueOf(s);
            }
        });

        m_edit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                minutes = String.valueOf(s);
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                TextView m_text = (TextView)findViewById(R.id.minutes);
                m_text.setText(s);
//                minutes = String.valueOf(s);
            }
        });

    }

 /*   // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(MainActivity.this, t,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true)
                .show();
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    }
*/
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Button getweatherbtn = (Button) findViewById(R.id.buttonGetWeather);
        Button sendbtn = (Button) findViewById(R.id.buttonSendSms);
        Button startsendbtn = (Button) findViewById(R.id.startTimer);
        Button cancelsendbtn = (Button) findViewById(R.id.cancelTimer);

        getweatherbtn.setText("Получить погоду " + settings.getString("owm_city", "Kaliningrad,ru"));
        sendbtn.setText("Отправить СМС на " + settings.getString("tophone", "79263090367"));
        startsendbtn.setText("Запустить автоматическую отправку СМС на " + settings.getString("tophone", "79263090367"));
        cancelsendbtn.setText("Остановить автоматическую отправку СМС на " + settings.getString("tophone", "79263090367"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.pref :
                Intent intent;
                intent = new Intent(this, PrefActivity.class);
                try {
                    startActivity(intent);
                }
                catch (Exception e) {
                    Log.e("MSA Weather Select MENU","EROOR");
                    System.out.println("Exception "+ e.getMessage());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startTimerTask (View v){
        timer = new Timer();
        timerTask = new msaTimerTask();
        timer.schedule(timerTask,0,60000);
        startTimer.setClickable(false);
        startTimer.setTextColor(Color.GRAY);
        cancelTimer.setClickable(true);
        cancelTimer.setTextColor(Color.WHITE);
    }

    public void cancelTimerTask (View v){
        timer.cancel();
        startTimer.setClickable(true);
        startTimer.setTextColor(Color.WHITE);
        cancelTimer.setClickable(false);
        cancelTimer.setTextColor(Color.GRAY);
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
//                if (i < 1) weatherTXT[i] = "Сегодня"; else weatherTXT[i] = "Завтра";
                weatherTXT[i] = Utils.WeatherInfo[i].date;
                weatherTXT[i] += " " + Utils.WeatherInfo[i].weather;
                weatherTXT[i] += "\n" + "Ут "  + Utils.WeatherInfo[i].mon_temp + "°";
                weatherTXT[i] += "\n" + "Дн "  + Utils.WeatherInfo[i].day_temp + "°";
                weatherTXT[i] += "\n" + "Вч "  + Utils.WeatherInfo[i].evn_temp + "°";
                weatherTXT[i] += "\n" + "Вет " + Utils.WeatherInfo[i].wind_speed + "м/с " + Utils.WeatherInfo[i].wind;
                weatherTXT[i] += "\n" + "Дав " + Utils.WeatherInfo[i].pressure;
//                weatherTXT[i] += "\n" + "Вл "  + Utils.WeatherInfo[i].humidity + "%";
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

        for (int i = 0; i < weatherTXT.length; ++i) {
            try {
                SMS.SendSms(this,weatherTXT[i],this);
                Thread.sleep(1000);

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
//        Log.d("MSA Weather onCompleteSendSms","Complete");
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
        // Берем время из системного календаря:
        Calendar calendar = Calendar.getInstance();
        String h = new SimpleDateFormat("k").format(calendar.getTime());
        String m = new SimpleDateFormat("mm").format(calendar.getTime());

        if (h.equals(hour) & m.equals(minutes)) getWeather(null);
        if (h.equals(hour) & m.equals(String.format("%02d",Integer.parseInt(minutes)+1))) sendSms(null);
    }
}

}
