package forlife.yaperevod;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

//Класс по отправке текста на перевод в асинхронном режиме и получении ответного сообщения
public class TranslateTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        BufferedReader br = null;
        try {
            url = new URL("https://translate.yandex.net/api/v1.5/tr/translate?" +
                    "key=" + "trnsl.1.1.20170315T190158Z.bbcaf8c0fac5465a.e6cd5edd2f6a5e051157fd1ad697726b216794d6" +
                    "&text=" + "\"" + URLEncoder.encode(params[0], "UTF-8") + "\"" + // используем encode для правильной обработки спец.символов, в т.ч. пробелов.
                    "&lang="  + params[1]
            );


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Error";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


            String line;
            final StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return url.toString();
        }
    }
}
