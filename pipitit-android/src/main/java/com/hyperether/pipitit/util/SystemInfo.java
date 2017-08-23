package com.hyperether.pipitit.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hyperether.pipitit.cache.PipititLogger;
import com.hyperether.pipitit.config.PipititConfig;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Is used to get all information about user and device settings
 *
 * @author Nebojsa Brankovic
 * @author Slobodan Prijic
 * @version 1.0 - 4/28/2017
 */
public class SystemInfo {

    private static final String TAG = SystemInfo.class.getSimpleName();

    private static SystemInfo INSTANCE;

    private SystemInfo() {
    }

    public static synchronized SystemInfo getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SystemInfo();
        return INSTANCE;
    }

    public String getAndroidId(Context context) {
        if (PipititConfig.isFcmRegistrationEnabled(context)) {
            FirebaseApp.initializeApp(context);
            return FirebaseInstanceId.getInstance().getId();
        } else
            return makeUniqueId(context);
    }

    private String makeUniqueId(Context context) {
        String id = null;
        String wifiMac = "";
        String devIDShort = "131" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits

        String androidId =
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            WifiManager wm = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            wifiMac = wm.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            PipititLogger.e(TAG, "makeUniqueId -  wifi ", e);
        }

        PipititLogger.d(TAG, "devIDShort - " + devIDShort);
        PipititLogger.d(TAG, "androidId - " + androidId);
        PipititLogger.d(TAG, "wifiMac - " + wifiMac);
        id = devIDShort + androidId + wifiMac;
        try {
            // compute md5
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                PipititLogger.e(TAG, "makeUniqueId -  digest  ", e);
            }
            m.update(id.getBytes(), 0, id.length());
            // get md5 bytes
            byte p_md5Data[] = m.digest();
            // create a hex string
            String m_szUniqueID = new String();
            for (int i = 0; i < p_md5Data.length; i++) {
                int b = (0xFF & p_md5Data[i]);
                // if it is a single digit, make sure it have 0 in front (proper padding)
                if (b <= 0xF) m_szUniqueID += "0";
                // add number to string
                m_szUniqueID += Integer.toHexString(b);
            }
            // hex string to uppercase
            m_szUniqueID = m_szUniqueID.toUpperCase();
            return m_szUniqueID;
        } catch (Exception e) {
            PipititLogger.e(TAG, "makeUniqueId -  end ", e);
            id = id.toUpperCase();
            PipititLogger.d(TAG, "Unique ID - " + id);
            return id;
        }

    }

    /**
     * Get application version
     *
     * @param context
     *
     * @return app version
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public Locale getLocale(Context context) {
        Configuration conf = context.getApplicationContext().getResources()
                .getConfiguration();
        return getLocale(conf);
    }

    private Locale getLocale(Configuration conf) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            locale = conf.getLocales().get(0);
        else
            //noinspection deprecation
            locale = conf.locale;
        return locale;
    }

    private boolean checkPermission(Context context, String permission) {
        boolean enable = Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                ContextCompat.checkSelfPermission(context, permission) ==
                        PackageManager.PERMISSION_GRANTED;
        PipititLogger.d(TAG, "checkPermission for " + permission + " , pemission is  = " + enable);
        return enable;
    }

    public boolean notificationEnabled(Context context) {
        boolean enable = NotificationManagerCompat.from(context).areNotificationsEnabled();
        PipititLogger.d(TAG, "notification enabled from manager  = " + enable);
        return enable;
    }

    public boolean locationEnabled(Context context) {
        if (checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION))
            return true;
        return false;
    }

    public String getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            String version = pInfo.versionName + "|" + pInfo.versionCode;
            PipititLogger.d(TAG, "VersionName|VersionCode = " + version);
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public String getDeviceModel() {
        PipititLogger.d(TAG, "Device Model is " + Build.MANUFACTURER + "|" + Build.MODEL);
        return Build.MANUFACTURER + "|" + Build.MODEL;
    }

    public String getOsVersion() {
        PipititLogger.d(TAG, "Device OS version is " + Build.VERSION.SDK_INT + "");
        return Build.VERSION.SDK_INT + "";
    }

    public String getDeviceResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int realWidth = 0;
        int realHeight = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;
        } else {
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                realWidth = display.getWidth();
                realHeight = display.getHeight();
            }

        }
        String resolution = realHeight + " x " + realWidth;
        PipititLogger.d(TAG, "Device resolution is " + resolution);
        return resolution;
    }

    public String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        PipititLogger.d(TAG,
                "Time zone is " + tz.getDisplayName(false, TimeZone.SHORT) + " id = " + tz.getID());
        return tz.getDisplayName(false, TimeZone.SHORT);
    }

    /**
     * Get current network country
     *
     * @return if sim present and ready return country where network is other return user country
     * which enter in registration process
     */
    public String getNetworkCountry(Context context) {
        try {
            TelephonyManager tm = getTelephonyManager(context);
            if (tm != null) {
                String country = tm.getNetworkCountryIso();
                if (country != null && !country.isEmpty())
                    return country.toUpperCase();
                else {
                    Locale locale = getLocale(context);
                    if (locale != null) {
                        country = locale.getCountry();
                        if (country != null && !country.isEmpty())
                            return country.toUpperCase();
                    }
                }
            } else {
                Locale locale = getLocale(context);
                if (locale != null) {
                    String country = locale.getCountry();
                    if (country != null && !country.isEmpty())
                        return country.toUpperCase();
                }
            }
        } catch (Exception e) {
            PipititLogger.e(TAG, "getNetworkCountry", e);
        }
        return null;
    }

    public String getNetworkCarrier(Context context) {
        TelephonyManager tm = getTelephonyManager(context);
        if (tm != null) {
            tm.getDeviceId();
            String carrier = tm.getNetworkOperatorName();
            if (carrier != null && !carrier.isEmpty())
                return carrier;
            NetworkInfo networkInfo = getNetworkInfo(context);
            if (networkInfo != null) {
                return networkInfo.getExtraInfo();
            }
        } else {
            NetworkInfo networkInfo = getNetworkInfo(context);
            if (networkInfo != null) {
                return networkInfo.getExtraInfo();
            }
        }

        return "";
    }


    /**
     * @param context application context
     *
     * @return TelephonyManager
     */
    private TelephonyManager getTelephonyManager(Context context) {
        if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            return (TelephonyManager) context.getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
        } else {
            PipititLogger.d(TAG, Manifest.permission.READ_PHONE_STATE + " is not granted!");
        }
        PipititLogger.d(TAG, "TelephonyManager is null");
        return null;
    }

    /**
     * Get network type
     */
    public static String getNetworkType(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        int type = -1;
        if (info != null) {
            type = info.getType();
        }

        if (type == android.net.ConnectivityManager.TYPE_WIFI) {
            return info.getTypeName();
        } else if (type == android.net.ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            return info.getSubtypeName();
        } else {
            return "";
        }
    }

    /**
     * Get the network info
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }
}
