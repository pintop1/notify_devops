package charmingdev.d.notify.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by CHARMING on 7/13/2018.
 */

public class UserInfo {
    private static final String TAG = UserInfo.class.getSimpleName();
    private static final String PREF_NAME = "login_data";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PHOTO = "photo";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public UserInfo(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences(PREF_NAME, ctx.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setKeyFirstname(String firstname){
        editor.putString(KEY_FIRSTNAME, firstname);
        editor.apply();
    }

    public void setKeyLastname(String lastname){
        editor.putString(KEY_LASTNAME, lastname);
        editor.apply();
    }

    public void setKeyPhone(String phone){
        editor.putString(KEY_PHONE,phone);
        editor.apply();
    }

    public void setKeyPhoto(String photo){
        editor.putString(KEY_PHOTO,photo);
        editor.apply();
    }

    public String getKeyFirstname(){
        return prefs.getString(KEY_FIRSTNAME, "");
    }

    public String getKeyLastname(){
        return prefs.getString(KEY_LASTNAME, "");
    }

    public String getKeyPhone(){
        return prefs.getString(KEY_PHONE, "");
    }

    public String getKeyPhoto(){
        return prefs.getString(KEY_PHOTO, "");
    }
}
