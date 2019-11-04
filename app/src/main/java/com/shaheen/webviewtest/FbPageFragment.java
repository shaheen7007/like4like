package com.shaheen.webviewtest;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import dmax.dialog.SpotsDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shaheen.webviewtest.activity.HomeActivity;
import com.shaheen.webviewtest.databaseRef.UserLikedPagesRef;
import com.shaheen.webviewtest.model.FbPage;
import com.shaheen.webviewtest.utils.Consts;

public class FbPageFragment extends Fragment {

    String gf_substring1;
    String gf_substring2;

    String fanid_substring1;
    String gfid;
    String fanid;
    boolean flag = false;
    int count = 0;
    AlertDialog progressBar;
    Button BTN_close;
    FbPage fbPage;
    FirebaseUser user;
    WebView webview;
    MainListFragment mainListFragment;
    FragmentTransaction ft;


    private void getExtra() {

        fbPage = getArguments().getParcelable("page");

    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }


    }

    public void showHTML(String html) {
        new AlertDialog.Builder(getActivity()).setTitle("HTML").setMessage(html)
                .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
    }


    @SuppressLint("JavascriptInterface")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);


        progressBar = new SpotsDialog(getActivity(),"Please wait");
       // progressBar.setMessage("Please wait..");
        progressBar.setCancelable(false);


        init(view);
        getExtra();


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

                                BTN_close.setVisibility(View.VISIBLE);

                                if (html.contains("unfan")) {
                                    webview.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            return true;
                                        }
                                    });
                                    Toast.makeText(getActivity(), "liked", Toast.LENGTH_SHORT).show();

                                    UserLikedPagesRef.getInstance(getActivity(),user.getUid()).child(fbPage.getPageID()).setValue(fbPage.getUserID());

                                    HomeActivity.updatePoint(fbPage, Consts.THIS_USER);
                                    HomeActivity.updatePoint(fbPage,Consts.THAT_USER);

                                    progressBar.dismiss();


                                    mGoToMainListFragment();

                                } else {
                                    webview.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            return false;
                                        }
                                    });

                                }


                                try {
                                    if (!flag) {
                                        flag = true;
                                        BTN_close.setVisibility(View.GONE);



                            /*            int maxLogSize = 1000;
                                    for (int i = 0; i <= html.length() / maxLogSize; i++) {
                                        int start = i * maxLogSize;
                                        int end = (i + 1) * maxLogSize;
                                        end = end > html.length() ? html.length() : end;
                                        Log.i("htmlllllll", html.substring(start, end));
                                    }*/




                                        String cookies = CookieManager.getInstance().getCookie(url);

                                        if (cookies.length() > 100) {
                                            Log.e("loginstatus", "true");
                                        } else {
                                            Log.e("loginstatus", "false");
                                            BTN_close.setVisibility(View.VISIBLE);
                                         //   Toast.makeText(getActivity(), "Login to continue", Toast.LENGTH_SHORT).show();


                                            //mShowHowToLoginDialog();

                                        }


                                        //finding gfid
                                        gf_substring1 = html.substring(html.indexOf("origin=page_profile"), html.indexOf("origin=page_profile") + 100);
                                        gf_substring2 = gf_substring1.substring(gf_substring1.indexOf("gfid="), gf_substring1.indexOf("&amp;ref"));
                                        gfid = gf_substring2.substring(gf_substring2.indexOf("=") + 1);


                                        //finding fan id
                                        // gf_substring1 = html.substring(html.indexOf("origin=page_profile"), html.indexOf("origin=page_profile") + 100);
                                        fanid_substring1 = html.substring(html.indexOf("fan&amp;id="), html.indexOf("&amp;origin=page_profile"));
                                        fanid = fanid_substring1.substring(fanid_substring1.indexOf("=") + 1);


                                        Log.e("gfidd", gfid);
                                        Log.e("fanid", fanid);


                                        webview.loadUrl("https://mbasic.facebook.com/a/profile.php?fan&id=" + fanid + "&origin=page_profile&pageSuggestionsOnLiking=1&gfid=" + gfid + "&refid=17");
                                        onPageFinished(webview, url);
                                        progressBar.show();

                                    } else {

                                        if (count == 3) {
                                            if (html.contains("unfan")) {
                                                Log.e("likestatus", "true");
                                            } else {
                                                Log.e("likestatus", "false");
                                            }
                                        }
                                        progressBar.dismiss();
                                    }
                                } catch (Exception e) {
                                    /*int maxLogSize = 1000;
                                    for (int i = 0; i <= html.length() / maxLogSize; i++) {
                                        int start = i * maxLogSize;
                                        int end = (i + 1) * maxLogSize;
                                        end = end > html.length() ? html.length() : end;
                                        Log.v("htmlllllll", html.substring(start, end));
                                    }*/
                                    Log.e("likestatus", "false");
                                    progressBar.dismiss();
                                }
                            }
                        });
            }
        });

        webview.loadUrl("https://mbasic.facebook.com/" + fbPage.getPageID());
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
        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, mainListFragment, "mainlist");
        ft.setTransition(
                FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @SuppressLint("JavascriptInterface")
    private void init(View view) {
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