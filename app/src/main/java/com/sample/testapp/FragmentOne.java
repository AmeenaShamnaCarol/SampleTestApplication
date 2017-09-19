package com.sample.testapp;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by vignaraj.r on 7/12/2017.
 */

public class FragmentOne extends Fragment {
    String barCodeValue = "";
    WebView webView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        webView = (WebView) view.findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new CustomWebClient());
//        webView.loadUrl("http://google.com");
//        webView.loadDataWithBaseURL( "file:///android_asset/", "http://google.com", "text/html","utf-8", null );
        webView.loadUrl("file:///android_asset/sample.html");
         /*((MainActivity) getActivity()).storeValue(new MainActivity.KeyListener() {
            @Override
            public void setEvent(KeyEvent event) {
                String barcode = "";
                int c = event.getUnicodeChar();
                if ((c >= 48 && c <= 57) || c == 10) {
                    if (event.getAction() == 0) {
                        if (c >= 48 && c <= 57)
                            barcode += "" + (char) c;
                        else {
                            if (!barcode.equals("")) {
                                barcode = "";
                            }
                        }
                    }
                }
                Log.i("barcode_val", barcode);
                barCodeValue += barcode;
                Log.i("vale", barCodeValue);
                Log.i("length", String.valueOf(barCodeValue.length()));
            }
        });*/
        return view;
    }

    public static void myOnKeyDown(int key_code) {
        //do whatever you want here
        Log.i("keyCode#", String.valueOf(key_code));
    }

    public class CustomWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }
    }
}
