package com.example.automaticotpretrieval;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.GoogleApiClient;



public class MainActivity extends AppCompatActivity{

    private static final int RESOLVE_HINT = 200;
    EditText phoneNumberView;
    String phoneNumber;
    Button sendOTPButton;
    SmsRetrieverClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Code to generate Hash
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(MainActivity.this);
        Log.v("TAG", appSignatureHelper.getAppSignatures().get(0));

        // App HASH
        // 1/Rzs9CM0et


        phoneNumberView = findViewById(R.id.numberfield);
        try {
            getPhoneNumber();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }

        sendOTPButton = findViewById(R.id.sendOTP);
        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = phoneNumberView.getText().toString();
                if (phoneNumber!="") {
                    sendNumberToServer(phoneNumber);
                }
                else {
                    // Show error message
                }
            }
        });


    }

    private void sendNumberToServer(String phoneNumber) {
        // Server side code to send OTP to server

        // If successful take to the OTP activity TODO
        Intent intent = new Intent(MainActivity.this, OTPActivity.class);
        startActivity(intent);


        // Else show internet issue TODO
    }

    private void getPhoneNumber() throws IntentSender.SendIntentException {
        requestHint();
    }


    // Construct a request for phone numbers and show the picker
    private void requestHint() throws IntentSender.SendIntentException {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .build();


        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                googleApiClient, hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                RESOLVE_HINT, null, 0, 0, 0);
    }

//    // Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {

                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                // credential.getId();  <-- will need to process phone number string

                if (credential!=null) {

                    // User has selected a number, in the (+88----) format
                    phoneNumber = credential.getId();
                    Log.v("Number", String.valueOf(phoneNumber));
                    phoneNumberView.setText(phoneNumber);
                    sendNumberToServer(phoneNumber);
                } else {
                    // User has not selected any number, take it manually

                }
            }
        }
    }







}
