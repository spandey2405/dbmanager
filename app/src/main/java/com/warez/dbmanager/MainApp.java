//package com.warez.dbmanager;
//import android.app.Application;
//import com.facebook.stetho.Stetho;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStream;
//
//
///**
// * Created by sid on 26/5/18.
// */
//
//public class MainApp extends Application {
//    DBManager dbManager;
//    public JSONObject dummy;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Stetho.initializeWithDefaults(getApplicationContext());
//        try {
//            JSONObject config = readFromFile();
//            dbManager = new DBManager(this, config);
//            dummy = readFromFile2();
//            init();
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//    public void init() throws IOException, JSONException {
//        updateFunctionTest();
//        insertFunctionTest();
//        getFunctionTest();
//        deleteFuncTest();
//
//    }
//    public void updateFunctionTest() throws JSONException, IOException {
//        JSONArray obj = dummy.getJSONArray("update");
//        dummy = readFromFile2();
//        for (int i =0 ; i<obj.length(); i++ ) {
//            String name = obj.getJSONObject(i).getString("table_name");
//            JSONObject values = obj.getJSONObject(i).getJSONObject("values");
//            JSONObject filter = obj.getJSONObject(i).getJSONObject("filter");
//            dbManager.update(name, values, filter);
//        }
//    }
//
//    public void insertFunctionTest() throws JSONException {
//        JSONArray obj = dummy.getJSONArray("insert");
//
//        for (int i =0 ; i<obj.length(); i++ ) {
//            String name = obj.getJSONObject(i).getString("table_name");
//            JSONArray values = obj.getJSONObject(i).getJSONArray("values");
//            dbManager.insert(name, values);
//        }
//    }
//
//    public void getFunctionTest() throws JSONException {
//        JSONArray obj = dummy.getJSONArray("fetch");
//        for (int i =0 ; i<obj.length(); i++ ) {
//            String name = obj.getJSONObject(i).getString("table_name");
//            JSONObject filter = obj.getJSONObject(i).getJSONObject("filter");
//            System.out.println(dbManager.fetch(name, filter));
//        }
//
//    }
//
//    public void deleteFuncTest() throws JSONException {
//        JSONArray obj = dummy.getJSONArray("delete");
//        for (int i =0 ; i<obj.length(); i++ ) {
//            String name = obj.getJSONObject(i).getString("table_name");
//            JSONObject filter = obj.getJSONObject(i).getJSONObject("filter");
//            dbManager.delete(name, filter);
//        }
//    }
//
//
//    JSONObject readFromFile2() throws IOException, JSONException {
//        String json;
//        InputStream is = this.getApplicationContext().getAssets().open("dummy_variables.json");
//        int size = is.available();
//        byte[] buffer = new byte[size];
//        is.read(buffer);
//        is.close();
//        json = new String(buffer, "UTF-8");
//        return new JSONObject(json);
//    }
//
//    JSONObject readFromFile() throws IOException, JSONException {
//        String json;
//        InputStream is = this.getApplicationContext().getAssets().open("table_json.json");
//        int size = is.available();
//        byte[] buffer = new byte[size];
//        is.read(buffer);
//        is.close();
//        json = new String(buffer, "UTF-8");
//        return new JSONObject(json);
//    }
//
//}
