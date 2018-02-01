package com.bee.drive.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bee.drive.data.FriendDB;
import com.bee.drive.data.GroupDB;
import com.bee.drive.data.StaticConfig;
import com.bee.drive.service.ServiceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import com.bee.drive.R;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;


/**
 * Created by Djimgou Patrick
 * Created on 09-oct-17.
 */

public class PhoneAuthActivity extends AppCompatActivity implements
        View.OnClickListener {

    EditText mPhoneNumberField, mVerificationField;
    Button mStartButton, mVerifyButton, mResendButton;
    private String phoneNumber;

    private EditText PhoneNumberRegistration ,
            email_user , UserNameRegistration  , RegistrationCodeSMS;
    private Spinner DriverType;
    private Button  RegistrationButton;
    private FloatingActionButton CloseRegisterCarte;
    private FrameLayout RgistrationPanel;
    private Button Registration_Phone;


    private CountryCodePicker ccp;
    public static FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;

    private static final String TAG = "PhoneAuthActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mVerificationField = (EditText) findViewById(R.id.field_verification_code);

        mStartButton = (Button) findViewById(R.id.button_start_verification);
        mVerifyButton = (Button) findViewById(R.id.button_verify_phone);
        mResendButton = (Button) findViewById(R.id.button_resend);
        Registration_Phone = (Button) findViewById(R.id.button_register_with_phone);
        Registration_Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RgistrationPanel.setVisibility(View.VISIBLE);

            }
        });

        // Registration ...
        CloseRegisterCarte = (FloatingActionButton) findViewById(R.id.fab_close);
        CloseRegisterCarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RgistrationPanel.setVisibility(View.GONE);

            }
        });



        RgistrationPanel = (FrameLayout) findViewById(R.id.regis_card);
        RgistrationPanel.setVisibility(View.GONE);
        PhoneNumberRegistration = (EditText) findViewById(R.id.field_phone_number_registration);
        UserNameRegistration = (EditText) findViewById(R.id.username);
        email_user = (EditText) findViewById(R.id.email_user);
        RegistrationCodeSMS = (EditText) findViewById(R.id.erification_code);
        DriverType = (Spinner)findViewById(R.id.spinner);
        RegistrationButton = (Button) findViewById(R.id.bt_go);
        RegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call the methode to process Phone Registration .... ...
                RegistrationPhone_Prozess();
                String UserName = UserNameRegistration.getText().toString();
                String Phone = PhoneNumberRegistration.getText().toString();
                String email = email_user.getText().toString();
                String DriverT = DriverType.getSelectedItem().toString().trim();



            }
        });




        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                Toast.makeText(getApplicationContext(), "Updated " + selectedCountry.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mPhoneNumberField.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(PhoneAuthActivity.this, SplaschScreen.class));
                            finish();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                mVerificationField.setError("Invalid code.");
                            }
                        }
                    }
                });
    }


    public void RegistrationPhone_Prozess (){


    }



    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }



    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private boolean validatePhoneNumber() {
        String ContryCode  = ccp.getSelectedCountryCode().toString();
        this.phoneNumber = mPhoneNumberField.getText().toString();

        Toast.makeText(getApplicationContext(), "Phone number ist : " + ContryCode+this.phoneNumber, Toast.LENGTH_LONG).show();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }
        this.phoneNumber = ContryCode+phoneNumber;
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            new AlertDialog.Builder(PhoneAuthActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("connected user ID ist "+currentUser.getUid().toString())
                    .setMessage("Are you sure you want to continue as User "+currentUser.getEmail().toString()+ "  ?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try{
                                FirebaseAuth.getInstance().signOut();
                                FriendDB.getInstance(getApplicationContext()).dropDB();
                                GroupDB.getInstance(getApplicationContext()).dropDB();
                                ServiceUtils.stopServiceFriendChat(getApplicationContext(), true);
                                // EmailLoginActivity.this.finish();
                                // finish();
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }

                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // User is signed in

                            startActivity(new Intent(PhoneAuthActivity.this, SplaschScreen.class));
                            finish();

                        }

                    })
                    .show();

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PhoneAuthActivity.this, MainActivity_App.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(phoneNumber);
                break;
            case R.id.button_verify_phone:
                if (!validatePhoneNumber()) {
                    return;
                }
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                if (!validatePhoneNumber()) {
                    return;
                }
                resendVerificationCode(phoneNumber, mResendToken);
                break;
        }

    }

}
