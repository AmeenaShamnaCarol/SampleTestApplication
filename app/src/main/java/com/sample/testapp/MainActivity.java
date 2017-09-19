package com.sample.testapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import co.poynt.os.model.Intents;
import co.poynt.os.services.v1.IPoyntOrderService;
import co.poynt.os.services.v1.IPoyntReceiptPrintingService;
import co.poynt.os.services.v1.IPoyntSecondScreenService;
import co.poynt.os.services.v1.IPoyntTokenService;
import co.poynt.os.services.v1.IPoyntTransactionService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int ids = 0;
    private ArrayList<KeyListener> onEventListener = new ArrayList<>();
    public static String Value = "";
    public static IPoyntOrderService orderService;
    public static IPoyntReceiptPrintingService receiptPrintingService;
    public static IPoyntTransactionService transactionService;
    public static IPoyntSecondScreenService poyntSecondScreenService;
    public static IPoyntTokenService tokenService;

    public static boolean isOrderServiceConnected = false;
    public static boolean isPrinterServiceConnected = false;
    public static boolean isTransactionServiceConnected = false;
    public static boolean isSecondScreenServiceConnected = false;
    public static boolean isTokenServiceConnected = false;


    public interface KeyListener {
        void setEvent(KeyEvent event);
    }
    public static ServiceConnection orderServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("OrderService", "Connected");
            orderService = IPoyntOrderService.Stub.asInterface(service);
            isOrderServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            orderService = null;
            Log.i("OrderService", "Disconnected");
        }
    };
    private ServiceConnection transactionServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            transactionService = IPoyntTransactionService.Stub.asInterface(iBinder);
            isTransactionServiceConnected = true;
        }

        public void onServiceDisconnected(ComponentName componentName) {
            transactionService = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ids =1;
        displayFragment(R.id.new_orders);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displayFragment(item.getItemId());
        return true;
    }

    private void displayFragment(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.new_orders:
//                Mint.transactionStart("Entry_Fragment");
                ids = 1;
                fragment = new FragmentOne();
                break;
            case R.id.order_history:
//                Mint.transactionStart("Order_Fragment");
                fragment = new FragmentTwo();
                break;
          /*  case R.id.manage_order:
                fragment = new ManageOrderFragment();
                break;
            case R.id.delivery_float:
                fragment = new DeliveryFloatFragment();
                break;*/
            case R.id.settings:
                fragment = new FragmentThree();
                break;
            case R.id.help:
                fragment = new FragmentFour();
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_navigation, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if(ids==1) {
                FragmentOne.myOnKeyDown(keyCode);
            }
            //and so on...
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("event_name", String.valueOf(event.getAction()));
        char pressedKey = (char) event.getUnicodeChar();
        Log.i("unicode_chars", String.valueOf(pressedKey));
        System.out.println("Meta_data "+event.getMetaState());
        Value += pressedKey;
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            Toast.makeText(getApplicationContext(), "barcode--->>>" + event.getCharacters(), Toast.LENGTH_LONG)
                    .show();
        }

//        for (KeyListener keyListener : onEventListener) {
//            keyListener.setEvent(event);
//        }
//        final StringBuilder stringBuilder = new StringBuilder();
//        boolean res = false;
//        //accept only 0..9 and ENTER
//        int c = event.getUnicodeChar();
//        String barcode = "";
//        if ((c >= 48 && c <= 57) || c == 10) {
//            if (event.getAction() == 0) {
//                if (c >= 48 && c <= 57)
//                    barcode += "" + (char) c;
//                else {
//                    if (!barcode.equals("")) {
//                        barcode = "";
//                    }
//                }
//            }
//            Log.i("barcode_val", barcode);
//            stringBuilder.append(barcode);
//            Value += barcode;
//            res = true;
//        }
//        Log.i("vale", Value);
        return super.dispatchKeyEvent(event);
    }

    public void storeValue(KeyListener event) {
        onEventListener.add(event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("DRAW_OnResume", "True");
        bindService(new Intent(IPoyntOrderService.class.getName()), orderServiceConnection, BIND_AUTO_CREATE);
        bindService(Intents.getComponentIntent(Intents.COMPONENT_POYNT_ORDER_SERVICE),
                orderServiceConnection, BIND_AUTO_CREATE);
        bindService(Intents.getComponentIntent(Intents.COMPONENT_POYNT_TRANSACTION_SERVICE),
                transactionServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("DRAW_onPause", "True");
        unbindService(orderServiceConnection);
        unbindService(transactionServiceConnection);
    }
}
