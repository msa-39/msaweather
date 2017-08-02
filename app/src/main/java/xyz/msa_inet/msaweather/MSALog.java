package xyz.msa_inet.msaweather;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by moiseev on 01.08.2017.
 */

public class MSALog {

    private final static String LOG_FILE_NAME = "msaweather.log";
    private static FileOutputStream fos = null;

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
}
