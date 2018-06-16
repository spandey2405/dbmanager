package com.warez.dbmanager;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by warez on 1/6/18.
 */

class DBAdapter {

    private final Context context;
    private final Helper helper;
    private int VERSION;
    private String DATABASE_NAME;
    private String[] queriesArray;
    private String[] delQueriesArray;
    private DatabaseHelper DBHelper;
    public SQLiteDatabase db;



    private String DelQueryBuilder(JSONObject tableinfo) throws JSONException {
        String table_name           = tableinfo.getString("_name");
        return "DROP TABLE IF EXISTS " + table_name;
    }

    private String QueryBuilder(JSONObject table_info) throws JSONException {

        JSONObject _columns = new Helper(context).sperateKeyValue(table_info.getJSONObject("_columns"));
        JSONArray _keys = _columns.getJSONArray("keys");
        JSONArray _values = _columns.getJSONArray("values");

        String share_pref_keys = "";


        String table_name = table_info.getString("_name");
        String query = "CREATE TABLE IF NOT EXISTS "+table_name+" (";

        JSONArray foriegn_key;
        try {
            foriegn_key = table_info.getJSONArray("_foriegn_keys");

        } catch (Exception e){
            foriegn_key = new JSONArray();
        }


        for(int i = 0; i < _keys.length(); ++i) {

            // get current JSON objects
            String column_name = _keys.getString(i);
            JSONObject column_properties = _values.getJSONObject(i);
            String primary_key_text = "";
            try {
                boolean primary_key = column_properties.getBoolean("_primary_key");
                if (primary_key) primary_key_text = "PRIMARY KEY";
            } catch (Exception e) {}
            String type             = column_properties.getString("_type");
            query += column_name + " " + type + " " + primary_key_text + ",";

            share_pref_keys += column_name +",";
        }

        // add foreign key property to table
        for(int i=0; i<foriegn_key.length(); i++) {
            JSONObject selected = foriegn_key.getJSONObject(i);
            String column = selected.getString("_column");
            String table  = selected.getString("_ref_table");
            String ref    = selected.getString("_ref_key");
            query += "FOREIGN KEY("+column+") REFERENCES "+table+"("+ref+"),";
        }
        // remove last "," and add ");" to query
        query = query.substring(0, query.length() - 1) + ")";

        share_pref_keys = share_pref_keys.substring(0, share_pref_keys.length() - 1);
        helper.putSharedData(table_name, share_pref_keys);
        return query;
    }

    DBAdapter(Context context, JSONObject config) throws IOException, JSONException {
        this.context = context;
        this.helper = new Helper(this.context);
        if (config != null) {
            VERSION = config.getInt("_version");
            DATABASE_NAME = config.getString("_dbName");

            JSONArray tables = config.getJSONArray("_tables");
            int no_tables = tables.length();
            queriesArray = new String[no_tables];
            delQueriesArray = new String[no_tables];


            // creating queriesArray.
            for (int i = 0; i < no_tables; i++) {
                queriesArray[i] = QueryBuilder(tables.getJSONObject(i));
                delQueriesArray[i] = DelQueryBuilder(tables.getJSONObject(i));
            }

            DBHelper = new DatabaseHelper(this.context);
        }

    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private final Context context;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if(queriesArray!=null && queriesArray.length>0) {
                for (String Query : queriesArray) {
                    db.execSQL(Query);
                }
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(delQueriesArray!=null && delQueriesArray.length>0) {
                for (String Query : delQueriesArray) {
                    db.execSQL(Query);
                }
            }

            onCreate(db);
        }
        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if(db.isReadOnly()) {
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }
    }
    DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    void close() throws SQLException {
        db.close();
    }
}
