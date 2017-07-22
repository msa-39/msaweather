package xyz.msa_inet.msaweather;

/**
 * Created by moiseev on 19.07.2017.
 */
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadDataTask extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... params) { // httpConnection("http://mysite.ru","POST")
        if (params.length < 1) return "";
        String uri = params[0];
        Log.d("MSA Weather URL = ",uri);

        String metod = params[1]; //GET or POST
        Log.d("MSA Weather Method = ",metod);

        BufferedReader reader = null;
        HttpURLConnection c;
        if (uri == null || uri.isEmpty()) return null;

        try {
            URL url = new URL(uri);
//            Log.d("1","1");

            c = (HttpURLConnection) url.openConnection();
//            Log.d("1","2");

            c.setRequestMethod(metod);
//            Log.d("1","3");

            c.setReadTimeout(10000);
//            Log.d("1","4");

            c.connect();
//            Log.d("1","5");

            reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
//            Log.d("1","6");

            StringBuilder buf = new StringBuilder();
//            Log.d("1","7");

            String line = null;
//            Log.d("1","8");

            while ((line = reader.readLine()) != null) {
                buf.append(line + "\n");
                Log.d("MSA Weather Buf Line",line);
            }
            reader.close();
//            Log.d("MSA Weather Reader close","OK");

            c.disconnect();
//            Log.d("MSA Weather Connection disconnect","OK");
            return(buf.toString());

        }catch(Exception e){
            Log.e("MSA Weather Exception GET Weather","Exception");
//            c.disconnect();
            return "";
        }
    }
    protected void onPostExecute(String result) {
    }
}
