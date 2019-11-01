package com.shaheen.webviewtest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shaheen.webviewtest.databaseRef.PagesRef;
import com.shaheen.webviewtest.databaseRef.UserLikedPagesRef;
import com.shaheen.webviewtest.databaseRef.UsersRef;
import com.shaheen.webviewtest.model.FbPage;
import com.shaheen.webviewtest.model.UserProfile;
import com.shaheen.webviewtest.utils.Consts;
import com.shaheen.webviewtest.utils.PrefManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;


public class BottomSheetAdd extends BottomSheetDialogFragment implements View.OnClickListener {

    Button BTNsubmit;
    TextView TXT_verify;
    ArrayList<String> returnValue = new ArrayList<>();
    EditText ET_FbID, ET_points;
    View view;
    PrefManager prefManager;
    private ProgressDialog dialog = null;
    private boolean isVerified = false;
    private boolean isPointSet= false;
    private int selected_point = 0;
    FirebaseUser user;
    final CharSequence items[] = new CharSequence[]{"25 Pts", "30 Pts", "40 Pts", "60 Pts"};

    public static BottomSheetAdd newInstance() {
        return new BottomSheetAdd();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottomsheet, container,
                false);
        BTNsubmit = view.findViewById(R.id.submit);
        TXT_verify = view.findViewById(R.id.btn_verify);
        ET_FbID = view.findViewById(R.id.fb_id);
        ET_points = view.findViewById(R.id.spnr_points);
        returnValue = new ArrayList<>();
        prefManager = PrefManager.getInstance(getActivity());

        BTNsubmit.setOnClickListener(this);
        TXT_verify.setOnClickListener(this);
        ET_points.setOnClickListener(this);

        dialog = new ProgressDialog(getActivity());

        user = FirebaseAuth.getInstance().getCurrentUser();

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("val", "requestCode ->  " + requestCode+"  resultCode "+resultCode);
       /* switch (requestCode) {
            case (100): {
                if (resultCode == HowItWorksActivity.RESULT_OK) {
                    returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

                    mShowImage(returnValue.get(0));

                }
            }
            break;
        }*/
    }

    @SuppressLint({"JavascriptInterface", "ClickableViewAccessibility"})
    @Override
    public void onClick(View v) {

        //    dialog.show();

        if (v == ET_points) {

            final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

            adb.setSingleChoiceItems(items, selected_point, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface d, int n) {
                    isPointSet=true;
                    selected_point = n;
                    ET_points.setText(items[n]);
                    d.cancel();

                }

            });
            adb.setTitle("Points per like");

            adb.show();


        }


        if (v == BTNsubmit) {

            if (isVerified) {

                if (isPointSet) {

                    mAddPageToFirebase();
                }
                else {
                    Toast.makeText(getActivity(), "Please select Points per like", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(), "Please verify to continue", Toast.LENGTH_SHORT).show();
            }


        }


        if (v == TXT_verify) {

            if (TextUtils.isEmpty(ET_FbID.getText().toString())) {
                Toast.makeText(getActivity(), "Please enter a valid FB id", Toast.LENGTH_SHORT).show();
            } else {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Is this your page ?");

                final WebView webview = new WebView(getActivity());
                webview.getSettings().setJavaScriptEnabled(true);
                webview.addJavascriptInterface(new MyJavaScriptInterface(getActivity()), "HtmlViewer");
                alert.setView(webview);
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        isVerified = false;
                        dialog.dismiss();
                    }
                });
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        isVerified = true;
                    }
                });


                webview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });


                webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, final String url) {
                        webview.evaluateJavascript(
                                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                                new ValueCallback<String>() {
                                    @SuppressLint("ClickableViewAccessibility")
                                    @Override
                                    public void onReceiveValue(String html) {

                                        if (html.contains("Page Not Found") || html.contains("Content not found")) {
                                            hideProgressDialog();
                                            Toast.makeText(getActivity(), "Page Not Found", Toast.LENGTH_SHORT).show();
                                        } else {
                                            hideProgressDialog();
                                            alert.show();
                                        }

                                    }
                                });
                    }
                });

                webview.loadUrl("https://mbasic.facebook.com/" + ET_FbID.getText().toString().trim());
                showProgressDialog();
            }
        }
    }

    private void mAddPageToFirebase() {


        showProgressDialog();

        final FbPage fbPage = new FbPage();
        fbPage.setPageID(ET_FbID.getText().toString().trim());
        fbPage.setUserID(user.getUid());
        switch (selected_point) {
            case 0:
                fbPage.setPoints(10);
                break;
            case 1:
                fbPage.setPoints(15);
                break;
            case 2:
                fbPage.setPoints(20);
                break;
            case 3:
                fbPage.setPoints(25);
                break;
        }

        UsersRef.getUserByUserId(getActivity(), user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        int currentPoints = userProfile.getTotalPoints();
                        fbPage.setUserTotalPoints(currentPoints);
                        PagesRef.getInstance(getActivity()).child(fbPage.getPageID()).setValue(fbPage);
                        hideProgressDialog();
                        Toast.makeText(getActivity(), "Your page added successfully. Now earn more points to get more Likes!", Toast.LENGTH_LONG).show();
                        dismiss();
                        UserLikedPagesRef.getInstance(getActivity(),user.getUid()).child(fbPage.getPageID()).setValue(fbPage.getUserID()); //adding to users liked page so that user's page won't be visible to himself

                        //update userref- listedpageid
                        UsersRef.getUserByUserId(getActivity(),user.getUid()).child(Consts.F_LISTED_PAGE).setValue(fbPage.getPageID());
                        prefManager.setIsPageListed(true);
                        prefManager.setListedPageId(fbPage.getPageID());
                        prefManager.setPointPerLike(fbPage.getPoints());


                        MainListFragment.getUserLikedPages();


                    } else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    PagesRef.getInstance(getActivity()).child(fbPage.getPageID()).setValue(fbPage);

                }
            });

        }


       /* if (v == profile_image) {

            Options options = Options.init()
                    .setRequestCode(100)
                    .setCount(1);
            Pix.start(this, options);
        }
        if (v == BTNsubmit) {

            if (valid()) {


                TiktokIDsRef.getInstance(getActivity()).child(ET_FbID.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            Toast.makeText(getActivity(), "Your account is already added", Toast.LENGTH_SHORT).show();


                        }
                        else {

                            showProgressDialog();
                            mAddProfileToFirebase();



                         }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        }*/


    /*private void mAddProfileToFirebase() {


        mUploadProfilePic();


    }*/

  /*  private void mUploadProfilePic() {

        final StorageReference ref = storageReference.child("images").child("featured").child(UUID.randomUUID().toString());


        uploadTask = ref.putFile(Uri.fromFile(mReduceImageSize(new File(returnValue.get(0)))));

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadURL = downloadUri.toString();

                    mAddProfileToFirebaseDB(downloadURL);


                } else {
                    Toast.makeText(getActivity(), "Operation failed. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
*/
  /*  private void mAddProfileToFirebaseDB(String downloadURL) {

        Profile profile = new Profile();
        profile.setImg_url(downloadURL);
        profile.setTiktok_id(ET_FbID.getText().toString());
        profile.setProfileURL(ET_tiktokURL.getText().toString());

        FeaturedRef.getInstance(getActivity()).getRef().push().setValue(profile);
        TiktokIDsRef.getInstance(getActivity()).child(profile.getTiktok_id()).setValue(profile.getProfileURL());

        Toast.makeText(getActivity(), "Profile added successfully", Toast.LENGTH_SHORT).show();

        if (MyAccountActivity.fa!=null) {

            Intent i = new Intent("android.intent.action.SmsReceiver").putExtra("incomingSms", "ghg");
            i.putExtra("incomingPhoneNumber", "jgh");
            getActivity().sendBroadcast(i);
        }


        ET_tiktokURL.setText("");
        ET_FbID.setText("");
        myBitmap = null;

        hideProgressDialog();
        dismiss();
    }

   */

  /*private boolean valid() {

        if (returnValue.size() == 0) {

            Toast.makeText(getActivity(), "Please add a profile pic to continue", Toast.LENGTH_SHORT).show();

            return false;

        } else if (TextUtils.isEmpty(ET_FbID.getText().toString())) {

            Toast.makeText(getActivity(), "Please enter a valid TikTok Id to continue", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!ET_tiktokURL.getText().toString().toLowerCase().contains("tiktok")) {

            Toast.makeText(getActivity(), "Please enter a valid profile URL to continue", Toast.LENGTH_SHORT).show();
            return false;
        } else {

            return true;
        }
    }*/


    public File mReduceImageSize(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }


    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }


    }

    void showProgressDialog() {
        if (dialog != null) {
            dialog.setTitle("Loading");
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


}