package com.example.lenovo.getit;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternetBroadcast extends BroadcastReceiver {
    public boolean checkCon = true;
    private AlertDialog alertDialog;
    static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                try {
                    if (alertDialog != null && alertDialog.isShowing())
                        alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (alertDialog == null || !alertDialog.isShowing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("No internet connection");
                    builder.setMessage("check your connection.");
                    builder.setCancelable(false);
                    alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        }
    }
}
