package com.shaheen.webviewtest.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.databaseRef.TransactionsRef;
import com.shaheen.webviewtest.databaseRef.UsersRef;
import com.shaheen.webviewtest.model.Transaction;
import com.shaheen.webviewtest.model.UserProfile;
import com.shaheen.webviewtest.utils.Consts;
import com.shaheen.webviewtest.utils.PrefManager;
import com.shaheen.webviewtest.utils.Utils;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText,_confirmPasswordText;
    Button _signupButton;
    TextView _loginLink;
    AdView mAdView_banner;
    PrefManager prefManager;


    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(SignupActivity.this)) {
                    signup();
                }
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    private void init() {


        //change ad unit id in layout

        mAdView_banner = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView_banner.loadAd(adRequest);

        _nameText = findViewById(R.id.input_name);
        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _confirmPasswordText = findViewById(R.id.input_confirm_password);
        _signupButton = (findViewById(R.id.btn_signup));
        _loginLink = (findViewById(R.id.link_login));

        mAuth = FirebaseAuth.getInstance();

        prefManager=PrefManager.getInstance(SignupActivity.this);

    }

    public void signup() {
       // Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final AlertDialog progressDialog = new SpotsDialog(SignupActivity.this,"Creating Account");
        progressDialog.setCancelable(false);
       // progressDialog.setMessage("Creating Account");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        _signupButton.setEnabled(true);

                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mAddProfileToFirebaseDB(user,name);


                            Toast.makeText(SignupActivity.this, "Like4Like account created successfully", Toast.LENGTH_SHORT).show();

                            mShowHowToLoginDialog();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            //  updateUI(null);
                        }

                        // ...
                    }
                });
    }




    public void onSignupFailed() {
      //  Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

     //   _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if ( !(_confirmPasswordText.getText().toString().equals(_passwordText.getText().toString()))){
            _confirmPasswordText.setError("passwords don't match");
            valid = false;
        }

        return valid;
    }

    private void mMoveToHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();

    }

    private void mAddProfileToFirebaseDB(FirebaseUser user, String name) {

        UserProfile profile = new UserProfile();
        profile.setUserID(user.getUid());
        profile.setUserEmail(user.getEmail());
        profile.setTotalPoints(50);
        profile.setUserName(name);
        profile.setDeviceToken(prefManager.getDeviceToken());

        UsersRef.getInstance(SignupActivity.this).getRef().child(profile.getUserID()).setValue(profile);

        Transaction transaction=new Transaction();
        transaction.setBalance(50);
        transaction.setDate(Utils.DateMonthyear(System.currentTimeMillis()));
        transaction.setMsg("SignUp Bonus");
        transaction.setPlusOrMinus(Consts.PLUS);
        transaction.setPoints(50);
        transaction.setType(Consts.TRANSACTION_BONUS);
        TransactionsRef.getInstance(SignupActivity.this,user.getUid()).push().setValue(transaction);

        Toast.makeText(SignupActivity.this, "SignUp successful", Toast.LENGTH_SHORT).show();
    }


    private void mShowHowToLoginDialog() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Facebook login session required:");
        alert.setMessage("* Facebook login session is required for the working of this app.\n\n* So on the next page, you have to login into Facebook to continue. \n\n* Don't worry it's just like login in a browser. Your credentials are 100% safe.\n\n*After logging in, you will be redirected to LIKE4LIKE app\n\n* If you face any trouble while login, click on 'having trouble?' button");

        alert.setCancelable(false);
        alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mMoveToHomePage();
            }
        });

        alert.show();


    }


}