package com.warez.dbmanager;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by warez on 1/6/18.
 */

public class DBManager {
    private Context context;
    private DBAdapter dbAdapter;
    private DatabaseSerializer dbSerilaizer;

    private void init(JSONObject tableConfig) throws IOException, JSONException {

        dbAdapter = new DBAdapter(context, tableConfig).open();
        dbSerilaizer = new DatabaseSerializer(context, dbAdapter.db, false);
    }

    public DBManager(Context context, JSONObject tableConfig) {
        this.context = context;
        try {
            init(tableConfig);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    public JSONArray fetch(String tableName, JSONObject Filter) {
        JSONArray result = new JSONArray();
        try {
            result = dbSerilaizer.getData(tableName, Filter);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void insert(String tableName, JSONArray data) {
        try {
            dbSerilaizer.insertData(data, tableName);
        } catch (IllegalAccessException | JSONException e) {
            e.printStackTrace();
        }
    }


    public void update(String tableName, JSONObject data, JSONObject Filter) {
        try {
            dbSerilaizer.updateData(data, tableName, Filter);
        } catch (IllegalAccessException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void delete(String tableName, JSONObject Filter) {
        try {
            dbSerilaizer.deleteData(tableName, Filter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
