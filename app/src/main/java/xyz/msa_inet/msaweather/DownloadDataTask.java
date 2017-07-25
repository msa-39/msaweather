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
            }
            reader.close();
            c.disconnect();

            return(buf.toString());

        }catch(Exception e){
            Log.e("MSA Weather Exception GET Weather","Exception");
            return "";
        } finally {
            try {reader.close();} catch(Throwable t) {}
            try {c.disconnect();} catch(Throwable t) {}
        }
    }
    protected void onPostExecute(String result) {
    }
}
