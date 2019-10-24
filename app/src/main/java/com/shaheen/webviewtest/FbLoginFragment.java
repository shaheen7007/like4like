package com.shaheen.webviewtest;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shaheen.webviewtest.activity.HomeActivity;
import com.shaheen.webviewtest.databaseRef.UserLikedPagesRef;
import com.shaheen.webviewtest.model.FbPage;
import com.shaheen.webviewtest.utils.Consts;
import com.shaheen.webviewtest.utils.PrefManager;

public class FbLoginFragment extends Fragment {

    String gf_substring1;
    String gf_substring2;

    String fanid_substring1;
    String gfid;
    String fanid;
    boolean flag = false;
    int count = 0;
    ProgressDialog progressBar;
    Button BTN_close;
    FbPage fbPage;
    FirebaseUser user;
    WebView webview;
    MainListFragment mainListFragment;
    FragmentTransaction ft;
    PrefManager prefManager;


    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }


    }


    @SuppressLint("JavascriptInterface")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fblogin, container, false);


        progressBar = new ProgressDialog(getActivity());
        progressBar.setMessage("Please wait..");
        progressBar.setCancelable(false);


        init(view);

        BTN_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGoToMainListFragment();
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
                                Log.e("countttt", "1");
                                count++;



                                if (html.contains("What's on your mind?")){
                                    BTN_close.setVisibility(View.VISIBLE);
                                    mGoToMainListFragment();
                                }


                                progressBar.dismiss();

                            }
                        });
            }
        });

        webview.loadUrl("https://mbasic.facebook.com");
      progressBar.show();


        return view;
    }

    private void mShowHowToLoginDialog() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("If you face trouble while logging in:");
        alert.setMessage("* Click on 'Login' button \n* Enter username and password\n* Click 'Login' button\n* Will ask you to enter login code. Click on 'having trouble?'\n* Click on 'send me a text message with my login code'\n* Click 'Continue' button\n* Enter login code received and click 'Submit Code'");

        alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        alert.show();


    }


    private void mGoToMainListFragment() {

        prefManager.setIsFirstTime(false);

        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, mainListFragment, "mainlist");
        ft.setTransition(
                FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @SuppressLint("JavascriptInterface")
    private void init(View view) {
        prefManager=PrefManager.getInstance(getActivity());
        HomeActivity.changeNavButton(Consts.BACK);
        user = FirebaseAuth.getInstance().getCurrentUser();
        BTN_close = view.findViewById(R.id.btn_close);
        webview = (WebView) view.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new MyJavaScriptInterface(getActivity()), "HtmlViewer");
        mainListFragment = new MainListFragment();

        BTN_close.setVisibility(View.GONE);

    }
}