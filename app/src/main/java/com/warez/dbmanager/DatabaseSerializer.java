package com.warez.dbmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Filter;

/**
 * Created by sid on 4/4/18.
 */

public class DatabaseSerializer {

    private Context context;
    private SQLiteDatabase db;
    private JSONObject constJson;
    private static final String TAG = "DbSerializer";

    public DatabaseSerializer(Context context, SQLiteDatabase db, Boolean enableLog) throws IOException, JSONException {
        this.context = context;
        this.db = db;
    }


    private ContentValues ContentValuesSerializezr(JSONObject obj, String [] constantsSelected) throws IllegalAccessException, JSONException {
        ContentValues cv = new ContentValues();
        for(int i=0; i<constantsSelected.length; i++) {
            try {
                String key = constantsSelected[i];
                String val = obj.getString(key);
                cv.put(key, val);
            } catch (JSONException e) {
                String key = constantsSelected[i];
                Log.d("KEY_NOT_FOUND:", key);
            }
        }
        return cv;
    }

    private JSONArray cur2Json(Cursor cursor) throws JSONException {

        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {

                    rowObject.put(cursor.getColumnName(i),
                            cursor.getString(i));
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        return resultSet;
    }

    private String whereClausegen(JSONObject Filter) throws JSONException {
        String whereClause = "";
        JSONObject seprated = new Helper(context).sperateKeyValue(Filter);
        JSONArray keys = seprated.getJSONArray("keys");
        JSONArray values = seprated.getJSONArray("values");
        for (int i=0; i<keys.length(); i++){
            String Type = values.get(i).getClass().getName();
            String key = keys.get(i).toString();
            String value = values.get(i).toString();
            if (Type.equals("java.lang.String")) {
                whereClause += "" + key + "='" + value + "' AND ";
            }
            else {
                whereClause += "" + key + "=" + value + " AND ";
            };

        }
        whereClause = whereClause.substring(0, whereClause.length() - 4);
        return whereClause;
    }

    JSONArray getData(String tableName, JSONObject Filter) throws IOException, JSONException {
        String rawQuery;
        rawQuery = "SELECT * FROM " + tableName;
        String whereClause = " WHERE 1 ";
        if (Filter != null) if (Filter.length() > 0) whereClause = " WHERE " + whereClausegen(Filter);
        rawQuery += whereClause;
        rawQuery += ";";
        Cursor selectQuery = db.rawQuery(rawQuery, null);
        return cur2Json(selectQuery);
    }

    boolean updateData(JSONObject data, String tableName, JSONObject FilterKeys) throws JSONException, IllegalAccessException {


        String whereClause = null;
        boolean isSuccess = false;

        if (FilterKeys != null) whereClause = whereClausegen(FilterKeys);

        String [] constantsSelected = new Helper(context).getSharedData(tableName).split(",");
        ContentValues cv = ContentValuesSerializezr(data, constantsSelected);
        isSuccess  = db.update(tableName, cv, whereClause, null)!=0;

        return isSuccess;
    }

    boolean deleteData(String tableName, JSONObject Filter) throws JSONException {
        boolean isSuccess = false;
        String whereClause = null;
        if (Filter != null) if (Filter.length() > 0) whereClause = whereClausegen(Filter);
        isSuccess  = db.delete(tableName, whereClause, null)!=0;
        return isSuccess;
    }


    boolean insertData(JSONArray dataArr, String tableName) throws IllegalAccessException, JSONException {
        boolean isSuccess = false;
        int arr_len = dataArr.length();
        for (int i=0; i< arr_len; i++) {
            JSONObject data = dataArr.getJSONObject(i);
            String [] constantsSelected = new Helper(context).getSharedData(tableName).split(",");
            ContentValues cv = ContentValuesSerializezr(data, constantsSelected);
            isSuccess  = db.insertWithOnConflict(tableName, "N/A", cv, SQLiteDatabase.CONFLICT_IGNORE)!=0;
        }

        return isSuccess;
    }
}
