package com.warez.dbmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by warez on 01/06/18.
 */

class Helper {

    private final Context context;
    private SharedPreferences sharedPreferences;

    Helper(Context context){
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences("db_manager_data",MODE_PRIVATE);
    }

    String getSharedData(String data_key){

        String dbData;
        dbData = sharedPreferences.getString(data_key,null);
        return dbData;
    }

    void putSharedData(String key, String value){
        SharedPreferences.Editor userEditor = sharedPreferences.edit();
        userEditor.putString(key,value);
        userEditor.apply();
    }

    void clearSharedData(String sharedKey){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(sharedKey);
        editor.apply();
    }


    JSONObject sperateKeyValue(JSONObject Objects) throws JSONException {
        JSONArray keys = new JSONArray();
        JSONArray values = new JSONArray();

        for (final Iterator<String> iter = Objects.keys(); iter.hasNext();) {
            final String key = iter.next();

            try {
                keys.put(key);
                values.put(Objects.get(key));
            } catch (final JSONException e) {
                // Something went wrong!
            }
        }

        return new JSONObject().put("keys", keys).put("values", values);

    }
}
