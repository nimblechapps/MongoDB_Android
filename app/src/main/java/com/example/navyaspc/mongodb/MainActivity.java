package com.example.navyaspc.mongodb;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText editText_fname;
    EditText editText_lname;
    EditText editText_phonenumber;
    ArrayList<MyContact> returnValues = new ArrayList<MyContact>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_fname = (EditText) findViewById(R.id.firstName);
        editText_lname = (EditText) findViewById(R.id.lastName);
        editText_phonenumber = (EditText) findViewById(R.id.phoneNumber);
    }

    public void reset(View v){
        editText_fname.setText("");
        editText_lname.setText("");
        editText_phonenumber.setText("");

        Toast.makeText(this, "Reset done", Toast.LENGTH_SHORT).show();
    }

    public void save(View v) {

        MyContact contact = new MyContact();

        contact.setFirst_name(editText_fname.getText().toString());
        contact.setLast_name(editText_lname.getText().toString());
        contact.setPhone_number(editText_phonenumber.getText().toString());

        MongoLabSaveContact tsk = new MongoLabSaveContact();
        tsk.execute(contact);

        Toast.makeText(this, "Saved to MongoDB!!", Toast.LENGTH_SHORT).show();

        editText_fname.setText("");
        editText_lname.setText("");
        editText_phonenumber.setText("");
    }

    final class MongoLabSaveContact extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            MyContact contact = (MyContact) params[0];
            Log.d("contact", ""+contact);

            try {


                SupportData sd = new SupportData();
                URL url = new URL(sd.buildContactsSaveURL());

                Log.d("url", ""+url);

                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestMethod("PUT");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type",
                        "application/json");
                connection.setRequestProperty("Accept", "application/json");

                OutputStreamWriter osw = new OutputStreamWriter(
                        connection.getOutputStream());

                osw.write(sd.createContact(contact));
                osw.flush();
                osw.close();

                Log.d("Response code", ""+(connection.getResponseCode()));
                if(connection.getResponseCode() <205)
                {

                    return true;
                }
                else
                {
                    return false;

                }

            } catch (Exception e) {
                e.getMessage();
                Log.d("Got error", e.getMessage());
                return false;

            }

        }

    }

    public void fetch(View v){
        GetContactsAsyncTask task = new GetContactsAsyncTask();
        try {
            returnValues = task.execute().get();
            MyContact FetchedData = (MyContact) returnValues.toArray()[0];

            editText_fname.setText(FetchedData.getFirst_name());
            editText_lname.setText(FetchedData.getLast_name());
            editText_phonenumber.setText(FetchedData.getPhone_nubmer());

            Toast.makeText(this, "Fetched from MongoDB!!", Toast.LENGTH_SHORT).show();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
