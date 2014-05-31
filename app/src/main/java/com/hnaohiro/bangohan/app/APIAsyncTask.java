package com.hnaohiro.bangohan.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Created by hnaohiro on 2014/05/31.
 */
public class APIAsyncTask extends AsyncTask<Void, Void, Object> {

    private ProgressDialog dialog;
    private APIAsyncTaskActionListener listener;

    public APIAsyncTask(APIAsyncTaskActionListener listener) {
        this.listener = listener;
    }

    public APIAsyncTask(Activity activity, String progressMessage, APIAsyncTaskActionListener listener) {
        this.dialog = new ProgressDialog(activity);
        this.dialog.setMessage(progressMessage);
        this.listener = listener;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        return listener.doTask();
    }

    @Override
    protected void onPreExecute() {
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        closeDialog();
        listener.onComplete(result);
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

interface APIAsyncTaskActionListener {
    public Object doTask();
    public void onComplete(Object result);
}
