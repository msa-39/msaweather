package xyz.msa_inet.msaweather;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements OnCompleteListener{
    /**
     * is download data on progress
     */
    static Boolean onProcess = false;

    public static String[] weatherTXT = new String[2];

    Button startMSAServiceBTN;
    Button stopMSAServiceBTN;
    String hour = "9";
    String minutes = "30";

    TextView h_text; //= (TextView)findViewById(R.id.hour);
    TextView m_text; //= (TextView)findViewById(R.id.minutes);

    TextView bat_status;

    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private boolean permissionGranted;

   private void set_BTN(){

       Log.d("MSA Weather Service_RUN",String.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("IS_SERVICE_RUN", false)));

       h_text.setText(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("HH", "9"));
       m_text.setText(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("MM", "30"));

       if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("IS_SERVICE_RUN", false)) {

           startMSAServiceBTN.setClickable(false);
           startMSAServiceBTN.setTextColor(Color.GRAY);
           stopMSAServiceBTN.setClickable(true);
           stopMSAServiceBTN.setTextColor(Color.WHITE);
       } else {
           startMSAServiceBTN.setClickable(true);
           startMSAServiceBTN.setTextColor(Color.WHITE);
           stopMSAServiceBTN.setClickable(false);
           stopMSAServiceBTN.setTextColor(Color.GRAY);
       }
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

 //IntentFilter Служит неким фильтром данных, которые мы хотим получить.
//ACTION_BATTERY_CHANGED - отслеживает изменение батареи
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//Чтобы получить текущее состояние батареи в виде намерения, нужно вызвать registerReceiver, передав null в качестве приемника, как показано в коде ниже.
        Intent battery = registerReceiver(null, ifilter);
        int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = (level / (float)scale) * 100;

        bat_status = (TextView)findViewById(R.id.charge);
        bat_status.setText(String.valueOf(batteryPct) + '%');

        startMSAServiceBTN = (Button)findViewById(R.id.startService);
        stopMSAServiceBTN = (Button)findViewById(R.id.stopService);

        final String[] hours = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
        final String[] mins = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23",
                               "24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47",
                               "48","49","50","51","52","53","54","55","56","57","58","59"};
//        final String[] mins = {"00","05","10","15","20","25","30","35","40","45","50","55"};
        final String[] periodArray = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
//        final Integer[] periodArray = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24};
        h_text = (TextView)findViewById(R.id.hour);
        m_text = (TextView)findViewById(R.id.minutes);

        set_BTN();
//-------------- Часы ----------------------------------------------
        Spinner h_edit = (Spinner) findViewById(R.id.hourEdit);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapterH = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hours);
        // Определяем разметку для использования при выборе элемента
        adapterH.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        h_edit.setAdapter(adapterH);

        int spinnerPositionH =adapterH.getPosition(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("HH", "9"));
        h_edit.setSelection(spinnerPositionH);

        AdapterView.OnItemSelectedListener itemSelectedListenerH = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                String item = (String)parent.getItemAtPosition(position);
                hour = String.valueOf(item);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("HH", hour).commit();
                h_text.setText(hour);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        h_edit.setOnItemSelectedListener(itemSelectedListenerH);

//--------------------- Минуты------------------------------------------------
        Spinner m_edit = (Spinner)findViewById(R.id.minutesEdit);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapterM = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mins);
        // Определяем разметку для использования при выборе элемента
        adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        m_edit.setAdapter(adapterM);

        int spinnerPositionM = adapterM.getPosition(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("MM", "30"));
        m_edit.setSelection(spinnerPositionM);

        AdapterView.OnItemSelectedListener itemSelectedListenerM = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                String item = (String)parent.getItemAtPosition(position);
                minutes = String.valueOf(item);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("MM", minutes).commit();
                m_text.setText(minutes);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        m_edit.setOnItemSelectedListener(itemSelectedListenerM);

//--------------------- Период------------------------------------------------
        Spinner p_edit = (Spinner)findViewById(R.id.periodEdit);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapterP = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, periodArray);
        // Определяем разметку для использования при выборе элемента
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        p_edit.setAdapter(adapterP);

        int spinnerPositionP = adapterP.getPosition(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("PERIOD", "12"));
        p_edit.setSelection(spinnerPositionP);

        AdapterView.OnItemSelectedListener itemSelectedListenerP = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                String itemP = (String) parent.getItemAtPosition(position);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("PERIOD", String.valueOf(itemP)).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        p_edit.setOnItemSelectedListener(itemSelectedListenerP);
    }

 // проверяем, доступно ли внешнее хранилище для чтения и записи
 public boolean isExternalStorageWriteable(){
     String state = Environment.getExternalStorageState();
     return  Environment.MEDIA_MOUNTED.equals(state);
 }
    // проверяем, доступно ли внешнее хранилище хотя бы только для чтения
    public boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        return  (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    // Проверка разрешений
    private boolean checkPermissions(){

        if(!isExternalStorageReadable() || !isExternalStorageWriteable()){
            Toast.makeText(this, "Внешнее хранилище не доступно", Toast.LENGTH_LONG).show();
            return false;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case REQUEST_PERMISSION_WRITE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permissionGranted = true;
                    Toast.makeText(this, "Разрешения получены", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this, "Необходимо дать разрешения", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Button getweatherbtn = (Button) findViewById(R.id.buttonGetWeather);
        Button sendbtn = (Button) findViewById(R.id.buttonSendSms);

        getweatherbtn.setText("Получить погоду " + settings.getString("owm_city", "Kaliningrad,ru"));
        sendbtn.setText("Отправить СМС на " + settings.getString("tophone", "79263090367"));
        startMSAServiceBTN.setText("Запустить службу автоматической отправкм СМС на " + settings.getString("tophone", "79263090367"));
        stopMSAServiceBTN.setText("Остановить службу автоматической отправки СМС на " + settings.getString("tophone", "79263090367"));

        set_BTN();

        if(settings.getBoolean("enable_log", false) && !permissionGranted)
            checkPermissions();

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

    public void startmsaservice (View v) {

        startMSAServiceBTN.setClickable(false);
        startMSAServiceBTN.setTextColor(Color.GRAY);
        stopMSAServiceBTN.setClickable(true);
        stopMSAServiceBTN.setTextColor(Color.WHITE);

        startService(new Intent(this, MSAWeather_service.class).putExtra("hh",hour).putExtra("mm",minutes));
    }

    public void stopmsaservice (View v){

        startMSAServiceBTN.setClickable(true);
        startMSAServiceBTN.setTextColor(Color.WHITE);
        stopMSAServiceBTN.setClickable(false);
        stopMSAServiceBTN.setTextColor(Color.GRAY);

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
                if (i < 1) {
                    img[i] = (ImageView)findViewById(R.id.im1);
                    ((TextView)findViewById(R.id.dt1)).setText(Utils.WeatherInfo[i].date);
                }
                    else {
                    img[i] = (ImageView)findViewById(R.id.im2);
                    ((TextView)findViewById(R.id.dt2)).setText(Utils.WeatherInfo[i].date);
                }

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
                SMS.SendSms(this,weatherTXT[i],this, null, null);
                Thread.sleep(3000);

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

}
