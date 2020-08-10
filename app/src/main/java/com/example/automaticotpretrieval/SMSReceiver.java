package com.example.automaticotpretrieval;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SMSReceiver extends BroadcastReceiver {

    private OTPReceiveListener otpListener;

    /**
     * @param otpListener
     */
    public void setOTPListener(OTPReceiveListener otpListener) {
        this.otpListener = otpListener;
    }


    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:

                    //This is the full message
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                    /*<#> Your ExampleApp code is: 123ABC78
                    FA+9qCX9VSu*/

                    Log.v("Message ddddd", message);

                    //Extract the OTP code and send to the listener
                    Pattern pattern = Pattern.compile("([\\d]{7})");

                    Matcher matcher = pattern.matcher(message);

                    if (otpListener != null) {
                        if (matcher.find()) {
                            String OTP = matcher.group(1); // First group is the OTP
                            otpListener.onOTPReceived(OTP);
                        }
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    if (otpListener != null) {
                        otpListener.onOTPTimeOut();
                    }
                    break;

                case CommonStatusCodes.API_NOT_CONNECTED:

                    if (otpListener != null) {
                        otpListener.onOTPReceivedError("API NOT CONNECTED");
                    }

                    break;

                case CommonStatusCodes.NETWORK_ERROR:

                    if (otpListener != null) {
                        otpListener.onOTPReceivedError("NETWORK ERROR");
                    }

                    break;

                case CommonStatusCodes.ERROR:

                    if (otpListener != null) {
                        otpListener.onOTPReceivedError("SOME THING WENT WRONG");
                    }

                    break;

            }
        }
    }

    /**
     *
     */
    public interface OTPReceiveListener {

        void onOTPReceived(String otp);

        void onOTPTimeOut();

        void onOTPReceivedError(String error);
    }
}
