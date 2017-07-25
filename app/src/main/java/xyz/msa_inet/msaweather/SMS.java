package xyz.msa_inet.msaweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by moiseev on 19.07.2017.
 */

public class SMS {

/*
    private static final String sms_gate_base_url = settings.getString("sms_gate_url","http://sms.ru/sms/send");
    private static final String sms_gate_id = settings.getString("sms_gate_id","108A516B-2BC4-DAC3-3F91-AFF209C8D1F8");
    private static final String to_phone = settings.getString("tophone","79263090367");
    private static final String sms_gate_url = sms_gate_base_url+"?api_id="+sms_gate_id+"&to="+to_phone+"&json=1&test=1&text=";
*/
    public static final String FIELD_REQ_STATUS = "status";
    public static final String FIELD_REQ_STATUS_CODE = "status_code";
    public static final String FIELD_REQ_STATUS_TEXT = "status_text";
    public static final String FIELD_SMS = "sms";
    public static final String FIELD_SMS_ID = "sms_id";
    public static final String FIELD_SMS_STATUS = "status";
    public static final String FIELD_SMS_STATUS_CODE = "status_code";
    public static final String FIELD_SMS_STATUS_TEXT = "status_text";


    public static void SendSms (Context context, String smsMSGTOsend, final OnCompleteListener listener){

        String smsUTF = null;
        String smsTOsend = "";
        String is_test_sms = "&test=1";
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String sms_gate_base_url = settings.getString("sms_gate_url","http://sms.ru/sms/send");
        String sms_gate_id = settings.getString("sms_gate_id","108A516B-2BC4-DAC3-3F91-AFF209C8D1F8");
        final String to_phone = settings.getString("tophone","79263090367");
        if (settings.getBoolean("testsms",true)) is_test_sms = "&test=1"; else is_test_sms = "";
        String sms_gate_url = sms_gate_base_url+"?api_id="+sms_gate_id+"&to="+to_phone+"&json=1"+is_test_sms+"&text=";

        if (smsMSGTOsend.length() > 69) smsTOsend = smsMSGTOsend.substring(0,69);
           else smsTOsend = smsMSGTOsend;
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

                        JSONObject a = new JSONObject(result);
                        Utils.SmsResultTxt.req_status = a.getString(FIELD_REQ_STATUS);
                        Utils.SmsResultTxt.req_status_code = a.getString(FIELD_REQ_STATUS_CODE);
//                        Log.d("MSA Weather req_status_code",Utils.SmsResultTxt.req_status_code);

                        if (Utils.SmsResultTxt.req_status_code.equals("100")) { // Запрос выполнен успешно (нет ошибок в авторизации, проблем с отправителем, итд...)

                            Utils.SmsResultTxt.sms_status = a.getJSONObject(FIELD_SMS).getJSONObject(to_phone).getString(FIELD_SMS_STATUS);
                            Utils.SmsResultTxt.sms_status_code = a.getJSONObject(FIELD_SMS).getJSONObject(to_phone).getString(FIELD_SMS_STATUS_CODE);
//                            Log.d("MSA Weather sms_status_code",Utils.SmsResultTxt.sms_status_code);

                            if (!Utils.SmsResultTxt.sms_status_code.equals("100")) { // Ошибка отправки сообщения на конкретный номер
                                 Utils.SmsResultTxt.sms_status_text = a.getJSONObject(FIELD_SMS).getJSONObject(to_phone).getString(FIELD_SMS_STATUS_TEXT);
                            } else Utils.SmsResultTxt.sms_id = a.getJSONObject(FIELD_SMS).getJSONObject(to_phone).getString(FIELD_SMS_ID);

                        } else Utils.SmsResultTxt.req_status_text = a.getString(FIELD_REQ_STATUS_TEXT);
                        Log.d("MSA Weather JsonConvertSMS","DONE");

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
