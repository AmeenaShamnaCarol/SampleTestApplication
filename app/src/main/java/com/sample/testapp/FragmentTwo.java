package com.sample.testapp;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * Created by vignaraj.r on 7/12/2017.
 */

public class FragmentTwo extends Fragment {
    Button btn_print;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        btn_print = (Button) view.findViewById(R.id.btn_print);
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.i("track", "connect");
                            SocketAddress sc = new InetSocketAddress("192.168.1.77", 9100);
                            Socket sock = new Socket();
                            sock.connect(sc, 10000);
                            Log.i("track", String.valueOf(sock.isConnected()));
                            PrintWriter oStream = new PrintWriter(sock.getOutputStream());

                            oStream.println("HI,test from Android Device");
                            oStream.println("\n\n\n");
                            oStream.close();
                            sock.close();
                            Log.i("track", "end");
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        return view;
    }
}