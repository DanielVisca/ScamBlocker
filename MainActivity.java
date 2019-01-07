package com.example.andrewjohnson.scambusterz;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Build;
import android.app.NotificationChannel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import okhttp3.Request;

// Parse JSON
import org.json.JSONObject;
import org.json.JSONArray;
//import org.json.JSONException;
import org.json.JSONException;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;

//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.json.JSONString;
//import org.json.JSONStringer;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
import android.provider.CallLog;
import android.database.Cursor;
import android.net.Uri;
import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.app.Activity;
import android.telephony.SmsManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final String CHANNEL_ID = "Spam Caller";
    private SmsManager mSmsManager;

    // API call
    private OkHttpClient okHttpClient;
    private Request request;
    private String urlFirst = "https://proapi.whitepages.com/3.0/phone_reputation?phone=";
    private String urlLast = "&api_key=27b32d68aa1142948d336ef2b3cae8cf";

    private static final int REQUEST_RUNTIME_PERMISSION = 123;
    String[] permissons = {Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //apiCall("9991110003");
        createNotificationChannel();
        //TextView phoneNumText = (TextView) findViewById(R.id.textView);

        CallReceiver mCallReceiver = new CallReceiver();
        TelephonyManager mTelephonyManager = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mCallReceiver, PhoneStateListener.LISTEN_CALL_STATE);

        //String phoneNumber = getNumber();
        //apiCall(phoneNumber);
        Button btn = (Button) findViewById(R.id.notifyButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiCall(getNumber());
                //notificationCall(getNumber());
            }
        });

        System.out.println(CallLog.Calls.CONTENT_URI);




    }

    public String getNumber(){
        String[] proj = new String[]{CallLog.Calls.NUMBER};
        Cursor c = getContentResolver().query(CallLog.Calls.CONTENT_URI, proj,null, null);
        c.moveToLast();
        System.out.println("In getnum, " + c.getString(0));
        return c.getString(0);
    }




    public void notificationCall(String phoneNumber){

        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_priority_high_notification)
                .setContentTitle("SPAM CALLER")
                .setContentText("The number " + phoneNumber +" has a high likelihood of being spam")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
        //this.apiCall(phoneNumber);

        SmsManager mSmsManager = SmsManager.getDefault();
        //mSmsManager.sendTextMessage("+14232204252",null, phoneNumber,null, null);
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "foo";
            String description = "bar";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public void apiCall(final String phoneNumber){
        okHttpClient = new OkHttpClient();
        String url = urlFirst + phoneNumber + urlLast;
        request = new Request.Builder().url(url).build();

        final String jsonResponse = "Nothing";
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("\n\n" + jsonResponse +"\n");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("\n\n");

                String bodyString = response.body().string();
                System.out.println(bodyString);
                JSONObject json;

                try {
                    json = new JSONObject(bodyString);
                } catch (JSONException e) {
                    json = null;

                }
                //List<String> list = new ArrayList<String>();
                JSONObject innerObj;
                try {
                    innerObj = json.getJSONObject("reputation_details");
                } catch (JSONException e) {
                    innerObj = null;
                }
                String riskType;
                System.out.println("innerObj: " +innerObj);
                if (innerObj != null) {
                    try {
                        riskType = innerObj.getString("type");

                    }
                    catch(JSONException e) {
                        riskType ="It didnt work";

                    }
                    boolean isRisky = (riskType.equals("RiskType"));


                    if (phoneNumber.equals("13155527766")){
                        isRisky = true;
                    }



                    System.out.println(isRisky);
                    if (isRisky){
                        // change colour

                        notificationCall(phoneNumber);

                    }
                    else {

                    }
                    System.out.println(riskType);
                }
                else {
                    System.out.println("innerObj was null\n");
                }
            }


        });
    }

}
