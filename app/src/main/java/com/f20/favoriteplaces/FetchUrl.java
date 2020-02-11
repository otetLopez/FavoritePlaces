package com.f20.favoriteplaces;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchUrl {
    public String readUrl(String myUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;

        HttpURLConnection httpURLConnection = null;
        try {
            Log.i("FetchUrl", "readUrl: " + "URL");
            URL url = new URL(myUrl);
            Log.i("FetchUrl", "readUrl: " + "HttpURLConnection");
            httpURLConnection = (HttpURLConnection) url.openConnection();
            inputStream = httpURLConnection.getInputStream();
            Log.i("FetchUrl", "readUrl: " + "BufferedReader");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            Log.i("FetchUrl", "readUrl: " + "StringBuffer");
            StringBuffer sb = new StringBuffer();

            //Need to read line
            String line = "";
            Log.i("FetchUrl", "readUrl: " + "while");
            while((line = br.readLine()) != null) {
                sb.append(line);
                //Log.i("FetchUrl", "readUrl: " + line);
            }

            data = sb.toString();
            br.close();
            Log.i("FetchUrl", "readUrl: " + "read done");

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("FetchUrl", "readUrl: " + "MalformedURLException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("FetchUrl", "readUrl: " + "IOException");
        } finally {
            Log.i("FetchUrl", "readUrl: " + "finally");
            inputStream.close();
            Log.i("FetchUrl", "readUrl: " + "finally inputstream.close()");
            httpURLConnection.disconnect();
            Log.i("FetchUrl", "readUrl: " + "finally httpURLConnection.disconnect()");
        }
        return data;
    }


}
