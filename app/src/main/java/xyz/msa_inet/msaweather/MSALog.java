package xyz.msa_inet.msaweather;

//import android.Manifest;
//import android.content.pm.PackageManager;
import android.os.Environment;
//import android.preference.PreferenceManager;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.util.Log;
//import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by moiseev on 01.08.2017.
 */

public class MSALog {

    private final static String LOG_FILE_NAME = "msaweather.log";
    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private boolean permissionGranted;
    private static FileOutputStream fos = null;
//    private static Boolean isLogEnable = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("enable_log", false);

    private final static String LOG_TAG = "MSA Weathe LOG";

    private static File getExternalPath() {
        File f;
        f = (new File(Environment.getExternalStorageDirectory(), LOG_FILE_NAME));
        Log.i(LOG_TAG+" LogFileName",f.getPath());
        return f;
    }

    public static void wrLog(String msgtolog, Boolean isLog){

        if (isLog) {
            Calendar calendar = Calendar.getInstance();
            String dLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").format(calendar.getTime()) + " - ";

            try {
                fos.write((dLog + msgtolog + "\n").getBytes());
            } catch (IOException ex) {
                Log.e(LOG_TAG, "Не могу записать в лог-файл! " + ex.getMessage());
            }
        }

    }

    public static void OpenLogFile (Boolean isLog){

//        if(!permissionGranted){
//            checkPermissions();
//            return;
//        }

        if (isLog) {
            try {
                fos = new FileOutputStream(getExternalPath());
            } catch (IOException ex) {
                Log.e(LOG_TAG, "Не могу создать лог-файл! " + ex.getMessage());
            }
        }
    }

    public static void CloseLogFile (Boolean isLog) {
        if (isLog) {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException ex) {
                Log.e(LOG_TAG, "Не могу закрыть лог-файл! " + ex.getMessage());
            }
        }
    }
/*
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

    private boolean checkPermissions(){

        if(!isExternalStorageReadable() || !isExternalStorageWriteable()){
//            Toast.makeText(super.getApplicationContext(), "Внешнее хранилище не доступно", Toast.LENGTH_LONG).show();
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
//                    Toast.makeText(getAppkicationContext(), "Разрешения получены", Toast.LENGTH_LONG).show();
                }
                else{
//                    Toast.makeText(MainActivity.this, "Необходимо дать разрешения", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
*/
}
