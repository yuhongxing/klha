 package mylib.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import mylib.app.EventHandler.Events;

public class LocationService {
    public static double[] myGP = new double[2];
    public static double lat = 0;
    public static double lng = 0;

    public LocationService(int i) {
    }

    public void stopService() {
        AndroidApp app = AndroidApp.sInst;
        LocationManager locMan = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        if (null == locMan) {
            MyLog.LOGW("no location manager");
            return;
        }
        locMan.removeUpdates(locListener);
    }

    @SuppressLint("MissingPermission")
    public void start() throws Exception {
        AndroidApp app = AndroidApp.sInst;
        LocationManager locMan = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        if (null == locMan) {
            MyLog.LOGW("no location manager");
            return;
        }
        LocationProvider lp = null;

        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 15, 100.0f, locListener);
        locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000 * 15, 100.0f, locListener);
    }

    private final LocationListener locListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location arg0) {

            lat = arg0.getLatitude();
            lng = arg0.getLongitude();
            Log.d("Login", "LoSer lat-> " +lat+"\n "+ " lng-> " + lng);
            if (arg0.getLatitude() != myGP[1] && arg0.getLongitude() != myGP[0]) {
                myGP[1] = arg0.getLatitude();
                myGP[0] = arg0.getLongitude();
                EventHandler.notifyEvent(Events.onLocationChanged, myGP[0], myGP[1]);
            }

            AndroidApp.sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        start();
                    } catch (Exception e) {
                    }
                }
            }, 1000 * 60 * 5 );//1000 * 60 * 5
        }

        @Override
        public void onProviderDisabled(String provider) {
            MyLog.LOGD("provider disabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            MyLog.LOGD("provider enabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


//    private String locProvider;
//
//    public String getProvider() {
//        return locProvider;
//    }

//    private boolean locNet = true, locGPS = true;
//    private final LocationListener locListener = new LocationListener() {
//
//        @Override
//        public void onLocationChanged(Location arg0) {
//            if (LocationManager.GPS_PROVIDER.equals(locProvider)) {
//                lastGPSTime = System.currentTimeMillis(); // gps is live!
//            }
//            myGP = new double[2];
//            myGP[1] = arg0.getLatitude();
//            myGP[0] = arg0.getLongitude();
//            MyLog.LOGD("loc changed: " + myGP[0] + " -> " + myGP[1]);
//            EventHandler.notifyEvent(Events.onLocationChanged, myGP[0], myGP[1]);
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            if (provider.equals(LocationManager.GPS_PROVIDER)) {
//                locGPS = false;
//            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
//                locNet = false;
//            }
//            if (!locGPS && !locNet) {
//                EventHandler.notifyEvent(Events.onLocationError, "");
//            } else {
//                switchProvider();
//            }
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            if (provider.equals(LocationManager.GPS_PROVIDER)) {
//                locGPS = true;
//            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
//                locNet = true;
//            }
//
//            if (locGPS) {
//                boolean nowUsingNet = LocationManager.NETWORK_PROVIDER.equals(locProvider);
//                if (nowUsingNet) {
//                    switchProvider();
//                }
//            } else {
//                if (locNet) {
//                    switchProvider();
//                }
//            }
//
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//    };
//
//    private long lastGPSTime = 0;
//    private int locUpdateTime = 60;
//
//    public LocationService(int updateTime) {
//        this.locUpdateTime = updateTime;
//        if (updateTime < 10) {
//            updateTime = 10;
//        }
//        locProvider = LocationManager.GPS_PROVIDER;
//    }
//
//    private boolean enabled = false;
//
//    public void start() {
//        enabled = true;
//        resetLocProvider();
//        MyLog.LOGD("startLocation");
//    }
//
//    public void stop() {
//        enabled = false;
//        resetLocProvider();
//        MyLog.LOGD("stopLocation");
//    }
//
//    private void resetLocProvider() {
//        AndroidApp app = AndroidApp.sInst;
//        LocationManager locMan = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
//        if (null == locMan) {
//            MyLog.LOGW("no location manager");
//            return;
//        }
//        LocationProvider lp = null;
//        locMan.removeUpdates(locListener);
//        if (!enabled) {
//            MyLog.LOGW("loc service not enabled");
//            return;
//        }
//
//        lp = locMan.getProvider(locProvider);
//        if (lp != null) {
//            if (locProvider.equals(LocationManager.GPS_PROVIDER)) {
//                lastGPSTime = System.currentTimeMillis();
//                // AndroidApp.sHandler.postDelayed(locationSnifer, 1000 * 60);
//            }
//            MyLog.LOGD("start loc service: " + locProvider);
//            locMan.requestLocationUpdates(locProvider, locUpdateTime * 1000, 10.0f, locListener);
//        } else {
//            MyLog.LOGW("no location provider");
//            EventHandler.notifyEvent(Events.onLocationError, "");
//        }
//        // startOtherLocation();
//    }
//
//    private void switchProvider() {
//        boolean toogle = false;
//        if (locProvider.equals(LocationManager.GPS_PROVIDER)) {
//            locProvider = LocationManager.NETWORK_PROVIDER;
//            toogle = true;
//        } else if (locProvider.equals(LocationManager.NETWORK_PROVIDER)) {
//            locProvider = LocationManager.GPS_PROVIDER;
//            toogle = true;
//        }
//        if (toogle) {
//            resetLocProvider();
//        } else {
//            //  startOtherLocation();
//        }
//    }

}
