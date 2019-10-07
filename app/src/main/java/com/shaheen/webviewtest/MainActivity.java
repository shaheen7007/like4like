package com.shaheen.webviewtest;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.*;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

    String gf_substring1;
    String gf_substring2;

    String fanid_substring1;
    String fanid_substring2;
    String gfid;
    String fanid;
    boolean flag = false;
    int count = 0;
    ProgressDialog progressBar;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Please wait..");
        progressBar.setCancelable(false);

        final WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, final String url) {
                webview.evaluateJavascript(
                        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                Log.e("countttt", "1");
                                count++;


                                if (html.contains("unfan")){
                                    webview.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            return true;
                                        }
                                    });
                                    Toast.makeText(MainActivity.this, "liked", Toast.LENGTH_SHORT).show();
                                }
                                else {
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


                                        String cookies = CookieManager.getInstance().getCookie(url);

                                        if (cookies.length() > 100) {
                                            Log.e("loginstatus", "true");
                                        } else {
                                            Log.e("loginstatus", "false");
                                        }


                                        //finding gfid
                                        gf_substring1 = html.substring(html.indexOf("origin=page_profile"), html.indexOf("origin=page_profile") + 100);
                                        gf_substring2 = gf_substring1.substring(gf_substring1.indexOf("gfid="), gf_substring1.indexOf("&amp;ref"));
                                        gfid = gf_substring2.substring(gf_substring2.indexOf("=") + 1);
//finding gfid

                                        //finding fan id
                                        // gf_substring1 = html.substring(html.indexOf("origin=page_profile"), html.indexOf("origin=page_profile") + 100);
                                        fanid_substring1 = html.substring(html.indexOf("fan&amp;id="), html.indexOf("&amp;origin=page_profile"));
                                        fanid = fanid_substring1.substring(fanid_substring1.indexOf("=") + 1);


                                        Log.e("gfidd", gfid);
                                        Log.e("fanid", fanid);


                                        webview.loadUrl("https://mbasic.facebook.com/a/profile.php?fan&id=" + fanid + "&origin=page_profile&pageSuggestionsOnLiking=1&gfid=" + gfid + "&refid=17");
                                        onPageFinished(webview, url);

                                    } else {

                                        if (count == 3) {
                                            if (html.contains("unfan")) {
                                                Log.e("likestatus", "true");
                                                progressBar.dismiss();
                                            } else {
                                                Log.e("likestatus", "false");
                                                progressBar.dismiss();
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    int maxLogSize = 1000;
                                    for (int i = 0; i <= html.length() / maxLogSize; i++) {
                                        int start = i * maxLogSize;
                                        int end = (i + 1) * maxLogSize;
                                        end = end > html.length() ? html.length() : end;
                                        Log.v("htmlllllll", html.substring(start, end));
                                    }
                                    Log.e("likestatus", "false");
                                    progressBar.dismiss();
                                }
                            }
                        });
            }
        });


        webview.loadUrl("https://mbasic.facebook.com/btechtrls");
        progressBar.show();

    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }


    }

    public void showHTML(String html) {
        new AlertDialog.Builder(MainActivity.this).setTitle("HTML").setMessage(html)
                .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
    }
}