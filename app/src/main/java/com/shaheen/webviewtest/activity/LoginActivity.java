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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.webviewtest.R;
import com.shaheen.webviewtest.databaseRef.PagesRef;
import com.shaheen.webviewtest.databaseRef.UsersRef;
import com.shaheen.webviewtest.model.FbPage;
import com.shaheen.webviewtest.model.UserProfile;
import com.shaheen.webviewtest.utils.PrefManager;
import com.shaheen.webviewtest.utils.Utils;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;


    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    private FirebaseAuth mAuth;
    private PrefManager prefManager;
    AlertDialog progressDialog;
    AdView mAdView_banner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(LoginActivity.this)) {
                    login();
                }
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    private void init() {

        mAuth = FirebaseAuth.getInstance();

      //  FirebaseUser currentUser = mAuth.getCurrentUser();
        prefManager = PrefManager.getInstance(this);


      /*  if (currentUser != null && !prefManager.getIsFirsttime()) {
            mMoveToHomePage();
        }*/


        //change ad unit id in layout
        mAdView_banner = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView_banner.loadAd(adRequest);



        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);


    }

    private void mMoveToHomePage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        //_loginButton.setEnabled(false);

        progressDialog = new SpotsDialog(LoginActivity.this,"Authenticating");
        progressDialog.setCancelable(false);
   //     progressDialog.setMessage("Authenticating");
        progressDialog.show();

        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mCheckIfUserAlreadyListedPage(user.getUid());


                        } else {
                            // If sign in fails, display a message to the user.
                           // Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Invalid username or password",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            //  updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void mCheckIfUserAlreadyListedPage(final String uid) {


        UsersRef.getUserByUserId(LoginActivity.this, uid).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                    if (userProfile.getListedPage() != null) {
                        prefManager.setListedPageId(userProfile.getListedPage());
                        prefManager.setIsPageListed(true);

                        mGetPageDetails(userProfile.getListedPage());


                    } else {
                        prefManager.setIsPageListed(false);
                        progressDialog.dismiss();
                        mShowHowToLoginDialog();
                    }

                } else {
                    progressDialog.dismiss();
                    prefManager.setIsPageListed(false);


                    mShowHowToLoginDialog();


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressDialog.dismiss();
                mShowHowToLoginDialog();
            }
        });


    }

    private void mGetPageDetails(String listedPage) {

        PagesRef.getPageByPageId(LoginActivity.this,listedPage).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                FbPage fbPage = dataSnapshot.getValue(FbPage.class);

                prefManager.setPointPerLike(fbPage.getPoints());
                progressDialog.dismiss();
                mShowHowToLoginDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the HowItWorksActivity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }


    public void onLoginFailed() {
      //  Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        //_loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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

        return valid;
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