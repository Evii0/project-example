package com.dev.goldilocksdatacollection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Timestamp;

import javax.net.ssl.HttpsURLConnection;

public class WebAppInterface {

    private Context mContext;
    private Window mWindow;
    private Mediator mediator;
    private Bluetooth handler;
    private WebView mWebView;
    private String deviceMACAdd = "";

    private Scanner mScanner;

    WebAppInterface(Context c, Window w, Mediator mediator, WebView mWebView) {
        mContext = c;
        mWindow = w;
        this.mediator = mediator;
        this.mWebView = mWebView;
        System.out.println(mWebView.toString());
    }

    @JavascriptInterface
    public int showToast() {
        return 12;
    }

    @JavascriptInterface
    public int saveEvent(String event) throws IOException, JSONException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        JSONObject obj = new JSONObject();
        obj.put("id", android.os.Build.MODEL + "-" + deviceMACAdd);
        obj.put("time", timestamp.toString());
        obj.put("tags", event);
        obj.put("type", "event");

        Log.e("DEBUG", obj.toString());
        postData(obj, "forward/");
        return 0;   // failed
    }

    @SuppressLint("MissingPermission")
    @JavascriptInterface
    public void connectDevice(int index){
        Log.e("DEBUG", Integer.toString(index));
        Log.e("DEBUG", mediator.getDevice(index).getName());
        mScanner.close();
        deviceMACAdd = mediator.getDevice(index).getAddress();
        handler = new Bluetooth(mContext, mediator.getDevice(index));
    }

    @JavascriptInterface
    public void setActivity(String activity){
        handler.activity = activity;
    }

    @JavascriptInterface
    public String checkConnections(){
        String response = "";
        if(handler.deviceConnectedToSuit)
            response += "1";
        else
            response += "0";

        if(handler.hasConnected)
            response += "1";
        else
            response += "0";

        return response;
    }

    @JavascriptInterface
    public void backButton(){
        Log.e("DEBUG", "Terminating connection...");
        handler.endConnection();
        handler = null;
        mScanner = new Scanner(mContext, mWebView, mediator);
    }

    public void setScanner(Scanner s){
        mScanner = s;
    }

    private void postData(JSONObject data, String destination) throws IOException {
        URL url = new URL("https://goldilocks-tdc.herokuapp.com/" + destination);
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

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            Log.e("DEBUG", response.toString());
        }
    }
}

