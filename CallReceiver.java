package com.example.andrewjohnson.scambusterz;

import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;


public class CallReceiver extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        if(state == TelephonyManager.CALL_STATE_IDLE) {
            if (phoneNumber.length() == 10) {
                System.out.println("Phone: " + phoneNumber);

                new apiCall(phoneNumber);
            }
            else {
                System.out.println("Phone: " + phoneNumber);

            }

//            System.out.println("In onCallStateChange\n");
            System.out.println(phoneNumber +"\n");
            super.onCallStateChanged(state, phoneNumber);
//
//            Intent i = new Intent(this, MainActivity.class);

        }
    }

}
