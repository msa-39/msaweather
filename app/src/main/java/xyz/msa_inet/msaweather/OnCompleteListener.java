package xyz.msa_inet.msaweather;

/**
 * Created by moiseev on 20.07.2017.
 */

public interface OnCompleteListener {
    void onCompleteGetWeather();
    void onCompleteSendSms();
    void onError();
}
