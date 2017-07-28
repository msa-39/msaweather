package xyz.msa_inet.msaweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by moiseev on 26.07.2017.
 */

public class MSAWeather_service extends Service {

    private int NOTIFY_ID = 666;
    private NotificationManager mNM;

    int mStartMode = START_STICKY;       // indicates how to behave if the service is killed
    IBinder mBinder = null;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used

    final String LOG_TAG = "MSA Weather Service LOG";
    Timer timer;
    TimerTask timerTask;
    String hour;// = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("HH", "9");
    String minutes;// = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("MM", "30");

    public static String[] weatherTXT = new String[2];
    public static String smsMessage = "";

    @Override
    public void onCreate() {
        // The service is being created
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
        PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext()).edit().putBoolean("IS_SERVICE_RUN", true).commit();

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()

        Log.i(LOG_TAG, "onStartCommand");
        hour = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("HH", "9");
        minutes = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("MM", "30");
        Log.d(LOG_TAG, "hour = " + hour + " minutes = " + minutes);

        timer = new Timer();
        timerTask = new msaTimerTask();
        timer.schedule(timerTask, 0, 60000);

//        return super.onStartCommand(intent, flags, startId);
        return mStartMode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Log.i(LOG_TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.i(LOG_TAG, "onUnbind");
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.d(LOG_TAG, "onRebind");
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        timer.cancel();
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");

        PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext()).edit().putBoolean("IS_SERVICE_RUN", false).commit();

//        stopForeground(NOTIFY_ID);
        mNM.cancel(NOTIFY_ID);
    }

    void getWeatherTask() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext());
        String owm_city = settings.getString("owm_city", "Kaliningrad,ru");
        String owm_base_url = settings.getString("owm_base_url", "http://api.openweathermap.org/data/2.5/");
        String owm_app_id = settings.getString("owm_api_key", "117d5fa1db04067b40a31da2c1b139ae");
        String owm_api_daily = settings.getString("owm_api_daily", "/forecast/daily?units=metric&cnt=2&lang=ru");

        String owm_url = owm_base_url + owm_api_daily + "&q=" + owm_city + "&APPID=" + owm_app_id;

        String result = Utils.MSAhttp(owm_url, "GET");

        if (!result.isEmpty()) {
            try {
                Weather.toForecast(result);
                Log.i("MSA Weather JsonConverterWeather from SERVICE", "DONE");

            } catch (Exception e) {
                Log.e("MSA Weather Exception from SERVICE", "GetWeather Error");
                System.out.println("Exception " + e.getMessage());
            }

            if (Utils.WeatherInfo.length > 0) {
                for (int i = 0; i < Utils.WeatherInfo.length; ++i) {
                    weatherTXT[i] = Utils.WeatherInfo[i].date;
                    weatherTXT[i] += " " + Utils.WeatherInfo[i].weather;
                    weatherTXT[i] += "\n" + "Ут " + Utils.WeatherInfo[i].mon_temp + "°";
                    weatherTXT[i] += "\n" + "Дн " + Utils.WeatherInfo[i].day_temp + "°";
                    weatherTXT[i] += "\n" + "Вч " + Utils.WeatherInfo[i].evn_temp + "°";
                    weatherTXT[i] += "\n" + "Вет " + Utils.WeatherInfo[i].wind_speed + "м/с " + Utils.WeatherInfo[i].wind;
                    weatherTXT[i] += "\n" + "Дав " + Utils.WeatherInfo[i].pressure;
                    if (i < 1) {
                        weatherTXT[i] += "\n";
                        weatherTXT[i] += "\n";
                    }
                    smsMessage += weatherTXT[i];
                }
                Log.d("MSA Weather weather_data from SERVICE", smsMessage);
            }
        }

/*
        new DownloadDataTask() {
            @Override
            protected void onPostExecute(String result) {
                if (!result.isEmpty()) {
                    try {
                        Weather.toForecast(result);
                        Log.i("MSA Weather JsonConverterWeather from SERVICE", "DONE");

                    } catch (Exception e) {
                        Log.e("MSA Weather Exception from SERVICE", "GetWeather Error");
                        System.out.println("Exception " + e.getMessage());
                    }

                    if (Utils.WeatherInfo.length > 0) {
                        for (int i = 0; i < Utils.WeatherInfo.length; ++i) {
                            weatherTXT[i] = Utils.WeatherInfo[i].date;
                            weatherTXT[i] += " " + Utils.WeatherInfo[i].weather;
                            weatherTXT[i] += "\n" + "Ут " + Utils.WeatherInfo[i].mon_temp + "°";
                            weatherTXT[i] += "\n" + "Дн " + Utils.WeatherInfo[i].day_temp + "°";
                            weatherTXT[i] += "\n" + "Вч " + Utils.WeatherInfo[i].evn_temp + "°";
                            weatherTXT[i] += "\n" + "Вет " + Utils.WeatherInfo[i].wind_speed + "м/с " + Utils.WeatherInfo[i].wind;
                            weatherTXT[i] += "\n" + "Дав " + Utils.WeatherInfo[i].pressure;
                            if (i < 1) {
                                weatherTXT[i] += "\n";
                                weatherTXT[i] += "\n";
                            }
                            smsMessage += weatherTXT[i];
                        }
                        Log.d("MSA Weather weather_data from SERVICE", smsMessage);
                    }
                }
            }
        }.execute(owm_url, "GET");
*/
    }

    void sendSmsTask(String[] cMessage) {
        String smsUTF = null;
        String smsTOsend = "";
        String is_test_sms = "&test=1";

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(super.getApplicationContext());

        String sms_gate_base_url = settings.getString("sms_gate_url", "http://sms.ru/sms/send");
        String sms_gate_id = settings.getString("sms_gate_id", "108A516B-2BC4-DAC3-3F91-AFF209C8D1F8");
        final String to_phone = settings.getString("tophone", "79263090367");
        if (settings.getBoolean("testsms", true)) is_test_sms = "&test=1";
        else is_test_sms = "";
        String sms_gate_url = sms_gate_base_url + "?api_id=" + sms_gate_id + "&to=" + to_phone + "&json=1" + is_test_sms + "&text=";

        for (int i = 0; i < cMessage.length; ++i) {

            if (cMessage[i].length() > 69) smsTOsend = cMessage[i].substring(0, 69);
            else smsTOsend = cMessage[i];
            try {
                smsUTF = URLEncoder.encode(smsTOsend, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String sms_url = sms_gate_url + smsUTF;

            String result = Utils.MSAhttp(sms_url, "GET");

                if (!result.isEmpty()) {
                    try {

                        SMS.toSmsInfo(result, to_phone);
                        Log.i("MSA Weather JsonConvertSMS from SERVICE", "DONE");

                    } catch (Exception e) {
                        Log.e("MSA Weather Exception from SERVICE", "SendSms Error");
                        System.out.println("Exception " + e.getMessage());

                    }
                    String smsResText = "";

                    if (Utils.SmsResultTxt.req_status.equals("OK")) {
                        if (Utils.SmsResultTxt.sms_status.equals("OK")) {
                            smsResText += "СМС успешно отправлено." + "\n";
                            smsResText += "ID сообщения - " + Utils.SmsResultTxt.sms_id + "\n";
                        } else {
                            smsResText += "СМС НЕ отправлено!" + "\n";
                            smsResText += "Код ошибки: " + Utils.SmsResultTxt.sms_status_code + "\n";
                            smsResText += "Текст ошибки: " + Utils.SmsResultTxt.sms_status_text + "\n";
                        }
                    } else {
                        smsResText += "Запрос на отправку СМС НЕ выполнился!" + "\n";
                        smsResText += "Код ошибки: " + Utils.SmsResultTxt.req_status_code + "\n";
                        smsResText += "Текст ошибки: " + Utils.SmsResultTxt.req_status_text + "\n";
                    }

                    Log.d("MSA Weather SendSMS_result", smsResText);

                } else
                    Log.e("MSA Weather from SERVICE", "Не получен ответ от сервиса отправки СМС.");

/*
            new DownloadDataTask() {
                @Override
                protected void onPostExecute(String result) {
                    if (!result.isEmpty()) {
                        try {

                            SMS.toSmsInfo(result, to_phone);
                            Log.i("MSA Weather JsonConvertSMS from SERVICE", "DONE");

                        } catch (Exception e) {
                            Log.e("MSA Weather Exception from SERVICE", "SendSms Error");
                            System.out.println("Exception " + e.getMessage());

                        }
                        String smsResText = "";

                        if (Utils.SmsResultTxt.req_status.equals("OK")) {
                            if (Utils.SmsResultTxt.sms_status.equals("OK")) {
                                smsResText += "СМС успешно отправлено." + "\n";
                                smsResText += "ID сообщения - " + Utils.SmsResultTxt.sms_id + "\n";
                            } else {
                                smsResText += "СМС НЕ отправлено!" + "\n";
                                smsResText += "Код ошибки: " + Utils.SmsResultTxt.sms_status_code + "\n";
                                smsResText += "Текст ошибки: " + Utils.SmsResultTxt.sms_status_text + "\n";
                            }
                        } else {
                            smsResText += "Запрос на отправку СМС НЕ выполнился!" + "\n";
                            smsResText += "Код ошибки: " + Utils.SmsResultTxt.req_status_code + "\n";
                            smsResText += "Текст ошибки: " + Utils.SmsResultTxt.req_status_text + "\n";
                        }

                        Log.d("MSA Weather SendSMS_result", smsResText);

                    } else
                        Log.e("MSA Weather from SERVICE", "Не получен ответ от сервиса отправки СМС.");
                }
            }.execute(sms_url, "GET");
*/
        }
    }

    class msaTimerTask extends TimerTask {
        @Override
        public void run() {
            // Берем время из системного календаря:
            Calendar calendar = Calendar.getInstance();
            String h = new SimpleDateFormat("k").format(calendar.getTime());
            String m = new SimpleDateFormat("mm").format(calendar.getTime());

            if (h.equals(hour) & m.equals(minutes)) getWeatherTask();
            if (h.equals(hour) & m.equals(String.format("%02d", Integer.parseInt(minutes) + 1)))
                sendSmsTask(weatherTXT);
        }
    }

    /**
     * Show a notification while this service is running.
     */

    private void showNotification() {

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MSAWeather_service.class), 0);

        Notification notification;
        Notification.Builder builder = new Notification.Builder(this);
        // Set the info for the views that show in the notification panel.
        builder
                .setSmallIcon(R.drawable.msaweather_icon)  // the status icon
                .setTicker(getText(R.string.ticker_text))  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.notification_title))  // the label of the entry
                .setContentText(getText(R.string.notification_message))  // the contents of the entry
                .setContentIntent(contentIntent);  // The intent to send when the entry is clicked

        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else
            notification = builder.build();
        // Send the notification.
//        mNM.notify(NOTIFY_ID, notification);
        startForeground(NOTIFY_ID,notification);
    }
}
/*
        Context context = super.getApplicationContext();

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

        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT; // ФЛАГ - Текущее уведомление

        notificationManager.notify(NOTIFY_ID, notification);
*/