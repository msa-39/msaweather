package xyz.msa_inet.msaweather;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by moiseev on 19.07.2017.
 */

public class Utils {

    public static ForecastInfo[] WeatherInfo = new ForecastInfo[2];

    public static SmsInfo SmsResultTxt = new SmsInfo();

    public static String calcDate(String format, String data) {
        if (data == null || data.isEmpty()) return "";
        long dv = Long.valueOf(data) * 1000;
        Date df = new Date(dv);
        return new SimpleDateFormat(format).format(df);
    }

    public static String get_wind_dir(int deg) {
        String[] l0 = {"\u21d1", "\u21d7", "\u21db", "\u21d8", "\u21d3", "\u21d9", "\u21da", "\u21d6"};
        String[] l1 = {"С","СВ","В","ЮВ","Ю","ЮЗ","З","СЗ"};
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
                res = l0[i] + l1[i];
        }
        return res;
    }

    public static String calcPressure(String data) {
        if (data == null || data.isEmpty()) return "";
        double mbar = Double.valueOf(data);
        int mmrs = (int) Math.round((mbar / 1.3332));
        return String.valueOf(mmrs);// + " мм р.с.";
    }

    public static String MSAhttp (String url_str, String metod_str, Boolean isLogWr){

        if (url_str.length()<1) return "";

        String uri = url_str;
        Log.i("MSA Weather URL = ",uri);
        MSALog.wrLog("MSA Weather URL = "+uri,isLogWr);

        String metod = metod_str; //GET or POST
        Log.i("MSA Weather Method = ",metod);
        MSALog.wrLog("MSA Weather Method = "+metod, isLogWr);

        BufferedReader reader = null;
        HttpURLConnection c = null;
        if (uri == null || uri.isEmpty()) return null;

        try {
            URL url = new URL(uri);
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod(metod);
            c.setReadTimeout(10000);
            c.connect();
            reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder buf = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                buf.append(line + "\n");
                Log.d("MSA Weather Buf Line",line);
                MSALog.wrLog("MSA Weather Buf Line"+line, isLogWr);
            }
            reader.close();
            c.disconnect();

            return(buf.toString());

        }catch(Exception e){
            Log.e("MSA Weather Exception GET Weather","Exception");
            MSALog.wrLog("MSA Weather Exception GET Weather"+e.getMessage(),isLogWr);
            return "";
        } finally {
            try {reader.close();} catch(Throwable t) {}
            try {c.disconnect();} catch(Throwable t) {}
        }

    }
}
