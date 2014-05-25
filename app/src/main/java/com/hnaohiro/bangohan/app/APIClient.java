package com.hnaohiro.bangohan.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

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

    private Activity activity;
    private String server_url;

    public APIClient(Activity activity) {
        this.activity = activity;
        server_url = activity.getResources().getString(R.string.server_url);
    }

    public void getUsers(APIActionListener listener) {
        APIAsyncTask task = new APIAsyncTask(activity, listener);
        task.execute("GET", server_url + "/users.json");
    }

    public void getUser(int id, APIActionListener listener) {
        APIAsyncTask task = new APIAsyncTask(activity, listener);
        task.execute("GET", server_url + "/users/" + id + ".json");
    }

    public void updateUser(int id, Map<String, String> params, APIActionListener listener) {
        APIAsyncTask task = new APIAsyncTask(activity, listener);
        task.execute("PUT", server_url + "/users/" + id + ".json", params, "Processing...");
    }

    public String register(int userId, String token) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", Integer.toString(userId));
        params.put("platform_application_arn", activity.getString(R.string.arn));
        params.put("token", token);

        try {
            return request("POST", server_url + "/devices/register.json", params);
        } catch (Exception e) {
            return null;
        }
    }

    public String request(String method, String url, Map<String, String> params) throws IOException, MethodNotSupportedException {
        String result = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpRequestBase httpRequest = createHttpRequest(method, url, params);

        HttpResponse httpResponse = httpClient.execute(httpRequest);
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(outputStream);
            result = outputStream.toString();
        }

        return result;
    }

    private HttpRequestBase createHttpRequest(String method, String url, Map<String, String> params) throws MethodNotSupportedException, UnsupportedEncodingException {
        HttpRequestBase httpRequest = null;

        if (method.equalsIgnoreCase("GET")) {
            httpRequest = new HttpGet(url);
        } else if (method.equalsIgnoreCase("DELETE")) {
            httpRequest = new HttpDelete(url);
        } else if (method.equalsIgnoreCase("OPTION")) {
            httpRequest = new HttpOptions(url);
        } else if (method.equalsIgnoreCase("HEAD")) {
            httpRequest = new HttpHead(url);
        } else if (method.equalsIgnoreCase("TRACE")) {
            httpRequest = new HttpTrace(url);
        } else if (method.equalsIgnoreCase("POST")) {
            httpRequest = new HttpPost(url);
        } else if (method.equalsIgnoreCase("PUT")) {
            httpRequest = new HttpPut(url);
        } else {
            throw new MethodNotSupportedException("[" + method + "] is not supported!");
        }

        if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")
                && params != null) {
            httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            HttpEntityEnclosingRequestBase httpEntityEnclosingRequest = (HttpEntityEnclosingRequestBase) httpRequest;
            HttpEntity entity = new UrlEncodedFormEntity(mapToNameValueList(params), HTTP.UTF_8);
            httpEntityEnclosingRequest.setEntity(entity);
        }

        return httpRequest;
    }

    private List<NameValuePair> mapToNameValueList(Map<String, String> params) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            list.add(new BasicNameValuePair(key, params.get(key)));
        }
        return list;
    }

    public class APIAsyncTask extends AsyncTask<Void, Void, Result> {

        private ProgressDialog dialog;
        private APIActionListener listener;

        private String method;
        private String url;
        private Map<String, String> params;
        private String progressMessage = "Loading...";

        public APIAsyncTask(Activity activity) {
            this(activity, null);
        }

        public APIAsyncTask(Activity activity, APIActionListener listener) {
            this.dialog = new ProgressDialog(activity);
            this.listener = listener;
        }

        public void execute(String method, String url) {
            execute(method, url, null);
        }

        public void execute(String method, String url, Map<String, String> params) {
            execute(method, url, params, null);
        }

        public void execute(String method, String url, Map<String, String> params, String progressMessage) {
            this.method = method;
            this.url = url;
            this.params = params;
            this.progressMessage = progressMessage;
            super.execute();
        }

        @Override
        protected void onPreExecute() {
            if (progressMessage != null) {
                dialog.setMessage(progressMessage);
                dialog.show();
            }
        }

        @Override
        protected Result doInBackground(Void... voids) {
            Result result = new Result();

            try {
                result.content = request(method, url, params);
                result.succeed = true;
            } catch (Exception e) {
                result.errorMessage = e.getMessage();
                result.succeed = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            if (progressMessage != null) {
                closeDialog();
            }

            if (listener != null) {
                if (result.succeed) {
                    listener.onSuccess(result.content);
                } else {
                    listener.onError(result.errorMessage);
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            closeDialog();
        }

        private void closeDialog() {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private class Result {
        private boolean succeed;
        private String content;
        private String errorMessage;
    }

    public interface APIActionListener {
        public void onSuccess(String content);
        public void onError(String message);
    }
}
