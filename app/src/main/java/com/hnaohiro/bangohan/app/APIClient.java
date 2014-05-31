package com.hnaohiro.bangohan.app;

import android.app.Activity;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by hnaohiro on 2014/05/17.
 */
public class APIClient {

    private final static String SERVER_URL = "http://bangohan.herokuapp.com";

    public static List<UserData> getUsers() throws IOException, JSONException {
        String url = SERVER_URL + "/users.json";
        String response = request(new HttpGet(url));
        return UserData.fromJSONObject(new JSONArray(response));
    }

    public static UserData getUser(int id) throws IOException, JSONException {
        String url = SERVER_URL + "/users/" + id + ".json";
        String response = request(new HttpGet(url));
        return UserData.fromJSONObject(new JSONObject(response));
    }

    public static boolean updateUser(int id, Map<String, String> params) throws IOException, JSONException {
        String url = SERVER_URL + "/users/" + id + ".json";
        String response = request(new HttpPut(url), params);

        JSONObject result = new JSONObject(response);
        if (result.has("id")) {
            if (result.getInt("id") == id) {
                return true;
            }
        }

        return false;
    }

    public static boolean register(int userId, String arn, String token) throws IOException, JSONException {
        String url = SERVER_URL + "/devices/register.json";

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(userId));
        params.put("platform_application_arn", arn);
        params.put("token", token);

        String response = request(new HttpPost(url), params);
        JSONObject result = new JSONObject(response);
        return result.getBoolean("result");
    }

    private static String request(HttpRequestBase httpRequest) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = httpClient.execute(httpRequest);
        return getResponse(httpResponse);
    }

    private static String request(HttpEntityEnclosingRequestBase httpEntityEnclosingRequest, Map<String, String> params) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        setParams(httpEntityEnclosingRequest, params);
        HttpResponse httpResponse = httpClient.execute(httpEntityEnclosingRequest);
        return getResponse(httpResponse);
    }

    private static String getResponse(HttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(outputStream);
            return outputStream.toString();
        } else {
            return null;
        }
    }

    private static void setParams(HttpEntityEnclosingRequestBase httpEntityEnclosingRequest, Map<String, String> params) throws UnsupportedEncodingException {
        httpEntityEnclosingRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        List<NameValuePair> nameValuePairParams = mapToNameValueList(params);
        HttpEntity entity  = new UrlEncodedFormEntity(nameValuePairParams, HTTP.UTF_8);
        httpEntityEnclosingRequest.setEntity(entity);
    }

    private static List<NameValuePair> mapToNameValueList(Map<String, String> params) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            list.add(new BasicNameValuePair(key, params.get(key)));
        }
        return list;
    }
}
