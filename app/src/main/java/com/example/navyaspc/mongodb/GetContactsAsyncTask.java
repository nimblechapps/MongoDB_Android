package com.example.navyaspc.mongodb;

import android.os.AsyncTask;
import android.util.Log;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Navya's PC on 9/10/2017.
 */

public class GetContactsAsyncTask extends AsyncTask<MyContact, Void, ArrayList<MyContact>> {
    static String server_output = null;
    static String temp_output = null;

    @Override
    protected ArrayList<MyContact> doInBackground(MyContact... arg0) {

        ArrayList<MyContact> mycontacts = new ArrayList<MyContact>();
        try
        {
            SupportData sd = new SupportData();
            URL url = new URL(sd.buildContactsFetchURL());
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");


            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            while ((temp_output = br.readLine()) != null) {
                server_output = temp_output;
            }

            // create a basic db list
            String mongoarray = "{ DB_output: "+server_output+"}";
            Object o = com.mongodb.util.JSON.parse(mongoarray);


            DBObject dbObj = (DBObject) o;
            BasicDBList contacts = (BasicDBList) dbObj.get("DB_output");
            for (Object obj : contacts) {
                DBObject userObj = (DBObject) obj;

                MyContact temp = new MyContact();
                temp.setFirst_name(userObj.get("first_name").toString());
                temp.setLast_name(userObj.get("last_name").toString());
                temp.setPhone_number(userObj.get("phone").toString());
                mycontacts.add(temp);

            }

        }catch (Exception e) {
            e.getMessage();
        }

        return mycontacts;
    }
}
