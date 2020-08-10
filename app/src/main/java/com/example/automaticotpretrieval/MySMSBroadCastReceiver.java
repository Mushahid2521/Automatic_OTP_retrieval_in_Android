package com.example.automaticotpretrieval;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySMSBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // 7 digit number.... change the value below TODO
                    Pattern pattern = Pattern.compile("([\\d]{7})");

                    Matcher matcher = pattern.matcher(message);
                    if (matcher.find()) {
                        String OTP = matcher.group(1);
                        // Send the OTP to the Server for Match



                        if (OTP== "1234567") {
                            Toast.makeText(context, "OTP Matched", Toast.LENGTH_SHORT).show();
                            Intent successIntent = new Intent();
                            successIntent.setClassName("com.example.automaticotpretrieval", "com.example.automaticotpretrieval.SuccessActivity");
                            successIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(successIntent);
                        }

                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    break;
            }
        }
    }
}
