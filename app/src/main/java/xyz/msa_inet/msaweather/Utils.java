package xyz.msa_inet.msaweather;

import android.content.Context;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by moiseev on 19.07.2017.
 */

public class Utils {

    public static ForecastInfo[] WeatherInfo = new ForecastInfo[2];
//    public static ArrayList<ForecastInfo> WeatherInfo;

    public static String calcDate(String format, String data) {
        if (data == null || data.isEmpty()) return "";
        long dv = Long.valueOf(data) * 1000;
        Date df = new Date(dv);
        return new SimpleDateFormat(format).format(df);
    }

    public static String get_wind_dir(int deg) {
        String[] l = {"С ","СВ"," В","ЮВ","Ю ","ЮЗ"," З","СЗ"};
        int step;
        int min;
        int max;
        String res = null;

        for (int i=0; i<8; i++) {
            step = 45;
            min = i*step - 45/2;
            max = i*step + 45/2;
            if (i == 0 & deg > 360-45/2)
                deg = deg - 360;
            if (deg >= min & deg <= max)
                res = l[i];
        }
        return res;
    }

    public static String calcPressure(String data) {
        if (data == null || data.isEmpty()) return "";
        double mbar = Double.valueOf(data);
        int mmrs = (int) Math.round((mbar / 1.3332));
        return String.valueOf(mmrs) + " мм р.с.";
    }
}
