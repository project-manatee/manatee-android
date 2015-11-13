package com.manateams.android.manateams.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.manateams.android.manateams.R;
import com.manateams.android.manateams.asynctask.CourseLoadTask;
import com.manateams.android.manateams.service.AlarmReceiver;
import com.quickhac.common.TEAMSGradeRetriever;
import com.quickhac.common.districts.TEAMSUserType;

import java.io.IOException;

public class Utils {

    /* Miscellaneous utility methods */

    private static boolean isConnectedToAISDGuest(final Context context) {
        final WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            Log.d("dibdib", "enabled");
            final WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                Log.d("dibdib", "enableddd");
                final NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    Log.d("dibdib", "enabwefled");
                    final String SSID = wifiInfo.getSSID();
                    Log.d("dibdib", SSID);
                    if(SSID.contains("AISD-GUEST")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void showConnectedtoAISDGuestDialog(final Activity activity, final Context context) {
        if (isConnectedToAISDGuest(context)) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(context).setTitle(R.string.dialog_aisd_wifi_title)
                    .setMessage(context.getString(R.string.dialog_aisd_wifi_message)).setCancelable(false)
                    .setPositiveButton(R.string.dialog_aisd_wifi_change_network, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.dialog_aisd_wifi_close, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            activity.finish();
                        }
                    });
            alert.show();
        }
    }

    /*
	 * Returns a color for a grade. Colors according to severity. Returned is an
	 * array of ints, with rgb values hexColor is supplied if you want the grade
	 * shade to have a different color - NOT YET FUNCTIONAL
	 */
    public static int[] getGradeColorNumber(double grade, String hexColor) {
        double hue = 0;

        int asianness = 4;
        // Make sure asianness isn't negative
        int asiannessLimited = Math.max(0, asianness);
        double h = 0, s = 0, v = 0, r = 0, g = 0, b = 0;
        if (grade < 0) {
            return new int[] { 225, 228, 225 };
        } else {

            h = Math.min(0.25 * Math.pow(grade / 100, asiannessLimited)
                            // The following line limits the amount hue is allowed to
                            // change in the gradient depending on how far the hue is
                            // from a multiple of 90.
                            + Math.abs(45 - (hue + 45) % 90) / 256,
                    // The following line puts a hard cap on the hue change.
                    0.13056);
            s = 1 - Math.pow(grade / 100, asiannessLimited * 2);
            v = 0.86944 + h;
        }

        // apply hue transformation
        h += hue / 360;
        h %= 1;
        if (h < 0)
            h += 1;

        // extra credit gets a special color
        if (grade > 100) {
            h = 0.5;
            s = Math.min((grade - 100) / 15, 1);
            v = 1;
        }

        // convert to rgb: http://goo.gl/J9ra3
        double i = Math.floor(h * 6);
        double f = h * 6 - i;
        double p = v * (1 - s);
        double q = v * (1 - f * s);
        double t = v * (1 - (1 - f) * s);
        switch (((int) i) % 6) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
        }

        return new int[] { (int) (r * 255), (int) (g * 255), (int) (b * 255) };
    }

    /* Returns true if the device is connected to the internet. */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isOnMobile(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo.isConnected();
    }
    public static boolean isOnWifi (Context context){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    /* Sets recurring alarms for running APIService. */
    public static void setAlarms(long interval, Context context) {
        Log.d("cookiecache", "registering alarms");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(alarmIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, interval, alarmIntent);
    }

    /* Returns true if alarms are already set. */
    public static boolean isAlarmsSet(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        boolean alarmsSet = (PendingIntent.getBroadcast(context, 0,
                intent,
                PendingIntent.FLAG_NO_CREATE) != null);
        return alarmsSet;
    }

    public static String getTEAMSCookies(DataManager dataManager,String username, String password, TEAMSUserType userType) {
        long lastUpdateTime = dataManager.getCookieLastUpdated();
        if (Math.abs(lastUpdateTime -System.currentTimeMillis()) > Constants.INTERVAL_EXPIRE_COOKIE){
            //Get cookies
            Log.d("cookiecache", "cache miss");
            try {
                final String cstonecookie = TEAMSGradeRetriever.getAustinisdCookie(username, password);
                final String teamscookie = TEAMSGradeRetriever.getTEAMSCookie(cstonecookie, userType);
                final String finalcookie = teamscookie + ';' + cstonecookie;
                TEAMSGradeRetriever.postTEAMSLogin(username, password,dataManager.getStudentId(), finalcookie, userType);
                dataManager.setCookie(finalcookie);
                return finalcookie;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d("cookiecache", "cache hit");
            return dataManager.getCookie();
        }

        return "";
    }
}
