package com.dev.goldilocksdatacollection;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Uploader {

    public Uploader(){}

    public void postData(JSONObject data, String destination) throws IOException {
        Log.e("DEBUG", "Uploading Data...");
        URL url = new URL("https://peter.goldilocks.schwarzsoftware.com.au/olddatacollect/");
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setInstanceFollowRedirects(true);
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = data.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        catch(Exception e){
            Log.e("DEBUG UPLOAD", e.getMessage());
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            Log.e("Server response", response.toString());
        }
        catch(Exception e){
            Log.e("DEBUG RESPONSE ", e.getMessage());
        }
    }
}
