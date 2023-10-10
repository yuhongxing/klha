
package mylib.utils;

import android.content.SharedPreferences;
import mylib.app.AndroidApp;

public class SPUtils {
    
    protected SPUtils() {
        String pkgName = AndroidApp.sInst.getPackageName();
        sp = AndroidApp.sInst.getSharedPreferences(pkgName, 4/*Context.MODE_MULTI_PROCESS*/);
    }

    private SharedPreferences sp = null;

    private static SPUtils config = null;

    public static SPUtils get() {
        if (config == null) {
            config = new SPUtils();
        }
        return config;
    }

    public void clear() {
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.commit();
    }

    public void remove(String key) {
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.commit();
    }

    public void setBool(String key, boolean val) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, val);
        edit.commit();
    }

    public boolean getBool(String key, boolean def) {
        return sp.getBoolean(key, def);
    }

    public void setInt(String key, int val) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, val);
        edit.commit();
    }

    public int getInt(String key, int def) {
        return sp.getInt(key, def);
    }

    public void setLong(String key, long val) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(key, val);
        edit.commit();
    }
    
    public long getLong(String key, long def) {
        return sp.getLong(key, def);
    }

    public void setString(String key, String val) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, val);
        edit.commit();
    }

    public String getString(String key) {
        return sp.getString(key, null);
    }

    public void setFloat(String key, float val) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putFloat(key, val);
        edit.commit();
    }

    public float getFloat(String key, float def) {
        return sp.getFloat(key, def);
    }
}
