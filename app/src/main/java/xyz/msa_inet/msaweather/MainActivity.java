package xyz.msa_inet.msaweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

//    Timer timer;
//    TimerTask timerTask;
    Button startMSAService;
    Button stopMSAService;
//    Calendar calendar;
    String hour = "9";
    String minutes = "30";
    int NOTIFY_ID = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startMSAService = (Button)findViewById(R.id.startService);
        stopMSAService = (Button)findViewById(R.id.stopService);

        startMSAService.setClickable(true);
        startMSAService.setTextColor(Color.WHITE);
        stopMSAService.setClickable(false);
        stopMSAService.setTextColor(Color.GRAY);

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

        getweatherbtn.setText("Получить погоду " + settings.getString("owm_city", "Kaliningrad,ru"));
        sendbtn.setText("Отправить СМС на " + settings.getString("tophone", "79263090367"));
        startMSAService.setText("Запустить службу автоматической отправкм СМС на " + settings.getString("tophone", "79263090367"));
        stopMSAService.setText("Остановить службу автоматической отправки СМС на " + settings.getString("tophone", "79263090367"));
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

/*
    public void startTimerTask (View v){
        timer = new Timer();
        timerTask = new msaTimerTask();
        timer.schedule(timerTask,0,60000);
        startTimer.setClickable(false);
        startTimer.setTextColor(Color.GRAY);
        cancelTimer.setClickable(true);
        cancelTimer.setTextColor(Color.WHITE);
    }
*/
    public void startmsaservice (View v) {

        startMSAService.setClickable(false);
        startMSAService.setTextColor(Color.GRAY);
        stopMSAService.setClickable(true);
        stopMSAService.setTextColor(Color.WHITE);

        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.msaweather_icon)
                // большая картинка
                //.setLargeIcon(R.drawable.msaweather_icon)
                .setTicker(res.getString(R.string.ticker_text)) // текст в строке состояния
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(res.getString(R.string.notification_title)) // Заголовок уведомления
                .setContentText(res.getString(R.string.notification_message)); // Текст уведомления

         Notification notification = builder.getNotification(); // до API 16
//        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT; // ФЛАГ - Текущее уведомление

        notificationManager.notify(NOTIFY_ID, notification);

        startService(new Intent(this, MSAWeather_service.class).putExtra("hh",hour).putExtra("mm",minutes));
    }
/*
    public void cancelTimerTask (View v){
        timer.cancel();
        startTimer.setClickable(true);
        startTimer.setTextColor(Color.WHITE);
        cancelTimer.setClickable(false);
        cancelTimer.setTextColor(Color.GRAY);
    }
*/
    public void stopmsaservice (View v){

        startMSAService.setClickable(true);
        startMSAService.setTextColor(Color.WHITE);
        stopMSAService.setClickable(false);
        stopMSAService.setTextColor(Color.GRAY);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFY_ID);

        stopService(new Intent(this, MSAWeather_service.class));
    }
    public void getWeather (View v){

        Weather.GetWeather(this,this);
    }

    @Override
    public void onCompleteGetWeather() {
        onProcess = false;
        String weatherMSG = "";
        TextView weatherText = (TextView)findViewById(R.id.weatherText);

        String img_url ="";

        if (Utils.WeatherInfo.length > 0 ) {
            ImageView[] img = new ImageView[Utils.WeatherInfo.length];
            for (int i = 0; i < Utils.WeatherInfo.length; ++i) {
                if (i < 1) img[i] = (ImageView)findViewById(R.id.im1);
                    else img[i] = (ImageView)findViewById(R.id.im2);

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
                img_url = getText(R.string.weather_img_url)+Utils.WeatherInfo[i].weather_icon+".png";
                Log.i("MSA Weather IMG URL",img_url);
                Glide.with(this)
                        .load(img_url)
                        .into(img[i]);
            }
        }
        Log.d("MSA Weather weather_data",weatherMSG);
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

        Log.d("MSA Weather SendSMS_result",smsResText);
        smsResult.setText(smsResText);
    }

    @Override
    public void onError() {
        onProcess = false;
        Log.e("MSA Weather onError","Error");
    }

/*
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
*/
}
