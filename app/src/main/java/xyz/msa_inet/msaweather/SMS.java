package xyz.msa_inet.msaweather;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by moiseev on 19.07.2017.
 */

public class SMS {
    private static final String sms_gate_base_url = "http://sms.ru/sms/send?api_id=";
    private static final String sms_gate_id = "108A516B-2BC4-DAC3-3F91-AFF209C8D1F8";
    private static final String to_phone = "79263090367";
    private static final String sms_gate_url = "http://sms.ru/sms/send?api_id=108A516B-2BC4-DAC3-3F91-AFF209C8D1F8&to="+to_phone+"&json=1&test=1&text=";

    public static final String FIELD_REQ_STATUS = "status";
    public static final String FIELD_REQ_STATUS_CODE = "status_code";
    public static final String FIELD_SMS_ID = "sms_id";
    public static final String FIELD_SMS_STATUS = "status";
    public static final String FIELD_SMS_STATUS_CODE = "status_code";
    public static final String FIELD_SMS_STATUS_TEXT = "status_text";


    public static void SendSms (Context context, String smsTOsend, final OnCompleteListener listener){

        String smsUTF = null;
        try {
            smsUTF = URLEncoder.encode(smsTOsend,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String sms_url = sms_gate_url + smsUTF;

        new DownloadDataTask() {
            @Override
            protected void onPostExecute(String result) {
                if (!result.isEmpty()) {
                    try {

                        //WeatherJsonConverter.toForecast(result);

                        Log.d("MSA Weather JsonConverterSMS","DONE");
                        if (listener != null) listener.onCompleteSendSms();
                    } catch (Exception e) {
                        Log.e("MSA Weather Exception","SendSms Error");
                        System.out.println("Exception "+ e.getMessage());
                        if (listener != null) listener.onError();
                    }
                } else if (listener != null) listener.onError();
            }
        }.execute(sms_url,"GET");
    }

}
