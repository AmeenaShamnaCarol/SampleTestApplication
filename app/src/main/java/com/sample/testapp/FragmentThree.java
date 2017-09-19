package com.sample.testapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import co.poynt.api.model.Card;
import co.poynt.api.model.CardType;
import co.poynt.api.model.FulfillmentStatus;
import co.poynt.api.model.FundingSourceAccountType;
import co.poynt.api.model.Order;
import co.poynt.api.model.OrderAmounts;
import co.poynt.api.model.OrderItem;
import co.poynt.api.model.OrderItemStatus;
import co.poynt.api.model.OrderStatus;
import co.poynt.api.model.OrderStatuses;
import co.poynt.api.model.Transaction;
import co.poynt.api.model.TransactionStatusSummary;
import co.poynt.api.model.UnitOfMeasure;
import co.poynt.os.contentproviders.orders.transactionreferences.TransactionreferencesColumns;
import co.poynt.os.model.Intents;
import co.poynt.os.model.Payment;
import co.poynt.os.model.PaymentStatus;
import co.poynt.os.model.PoyntError;
import co.poynt.os.services.v1.IPoyntOrderServiceListener;
import co.poynt.os.services.v1.IPoyntTransactionServiceListener;

/**
 * Created by vignaraj.r on 7/12/2017.
 */

public class FragmentThree extends Fragment {
    Button btn_order, btn_cash;
    Order order;
    String lastReferenceId;
    private static final int COLLECT_PAYMENT_REQUEST = 13132;
    public static ProgressDialog progressDialog;
    private static String TAG = "F3";
    /**
     * Transaction listener
     **/
    private IPoyntTransactionServiceListener mTransactionServiceListener = new IPoyntTransactionServiceListener.Stub() {
        public void onResponse(Transaction _transaction, String s, PoyntError poyntError) throws RemoteException {
            Gson gson = new Gson();
            Type transactionType = new TypeToken<Transaction>() {
            }.getType();
            String transactionJson = gson.toJson(_transaction, transactionType);
            Log.d("Transc_Response", transactionJson);
        }

        //@Override
        public void onLaunchActivity(Intent intent, String s) throws RemoteException {
            //do nothing
        }

        public void onLoginRequired() throws RemoteException {
            Log.d("F3", "onLoginRequired called");
        }

    };
    /**
     * Store Order Listener
     **/
    private IPoyntOrderServiceListener storeOrderListener = new IPoyntOrderServiceListener.Stub() {
        @Override
        public void orderResponse(final Order order, final String s, final PoyntError poyntError) throws RemoteException {
//            closeProgress();
            if (poyntError == null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgress();
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgress();
                    }
                });

            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_three, container, false);
        btn_order = (Button) view.findViewById(R.id.btn_order);
        btn_cash = (Button) view.findViewById(R.id.btn_cash);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        btn_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order != null) {
                    Payment payment = new Payment();
                    payment.setMultiTender(false);
                    payment.setCashOnly(true);
                    payment.setCurrency("AED");

                    payment.setOrderId(order.getId().toString());
                    payment.setOrder(order);
                    payment.setAmount(order.getAmounts().getNetTotal());
                    Intent collectionPayment = new Intent(Intents.ACTION_COLLECT_PAYMENT);
                    collectionPayment.putExtra(Intents.INTENT_EXTRAS_PAYMENT, payment);
                    startActivityForResult(collectionPayment, COLLECT_PAYMENT_REQUEST);
                } else {
                    Toast.makeText(getActivity(), "Please make the order first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeOrder();
                Toast.makeText(getActivity(), "Order create successfuly", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    /**
     * close the Progress Dialog
     **/
    public void closeProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }

    public Order makeOrder() {
        order = new Order();
        List<OrderItem> itemList = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setName("Cabbage");
        item1.setUnitPrice(8500L);
        item1.setSku("CBU");
        item1.setUnitOfMeasure(UnitOfMeasure.KILOGRAM);
        item1.setQuantity(1.5f);
        item1.set_id(001L);
        item1.setProductId("CB_011");
        item1.setStatus(OrderItemStatus.ORDERED);

        OrderItem item2 = new OrderItem();
        item2.setName("Green Onion");
        item2.setUnitPrice(7500L);
        item2.setSku("GONI");
        item2.setUnitOfMeasure(UnitOfMeasure.KILOGRAM);
        item2.setQuantity(1.25f);
        item2.set_id(002L);
        item2.setProductId("GO_012");
        item2.setStatus(OrderItemStatus.ORDERED);

        itemList.add(item1);
        itemList.add(item2);

        order.setItems(itemList);
        order.setId(UUID.randomUUID());
        OrderStatuses orderStatuses = new OrderStatuses();
        orderStatuses.setStatus(OrderStatus.OPENED);
        orderStatuses.setFulfillmentStatus(FulfillmentStatus.FULFILLED);
        orderStatuses.setTransactionStatusSummary(TransactionStatusSummary.NONE);
        order.setStatuses(orderStatuses);

        BigDecimal subTotal = new BigDecimal(0);
        for (OrderItem item : itemList) {
            BigDecimal price = new BigDecimal(item.getUnitPrice());
            price.setScale(2, RoundingMode.HALF_UP);
            price = price.multiply(new BigDecimal(item.getQuantity()));
            subTotal = subTotal.add(price);
        }
        OrderAmounts amounts = new OrderAmounts();
        amounts.setCurrency("AED");
        amounts.setSubTotal(subTotal.longValue());
        amounts.setNetTotal(subTotal.longValue());
        order.setAmounts(amounts);
        return order;
    }

    /**
     * Save and Update Order Async Task
     **/
    private class SaveOrderTask extends AsyncTask<Order, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Saving Order...");
            progressDialog.show();
        }

        protected Void doInBackground(Order... params) {
            Order order = params[0];
            String requestId = UUID.randomUUID().toString();
            System.out.println("orderDtls   ++" + order);
            try {
                MainActivity.orderService.createOrder(order, requestId, storeOrderListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("FragmentThree", "Received onActivityResult (" + requestCode + ")");
        // Check which request we're responding to
        if (requestCode == COLLECT_PAYMENT_REQUEST) {
            logData("Received onActivityResult from Payment Action");
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Payment payment = data.getParcelableExtra(Intents.INTENT_EXTRAS_PAYMENT);

                    if (payment != null) {
                        //save order
                        String type = "";

                        Gson gson = new Gson();
                        Type paymentType = new TypeToken<Payment>() {
                        }.getType();
                        Log.d("payment_type", gson.toJson(payment, paymentType));
                        for (Transaction t : payment.getTransactions()) {
                            getTransaction(t.getId().toString());
                            Log.i("getTransaction_id", t.getId().toString());
                            //Log.d(TAG, "Card token: " + t.getProcessorResponse().getCardToken());
                            FundingSourceAccountType fsAccountType = t.getFundingSource().getAccountType();
                            if (t.getFundingSource().getCard() != null) {
                                Card c = t.getFundingSource().getCard();
                                String numberMasked = c.getNumberMasked();
                                String approvalCode = t.getApprovalCode();
                                CardType cardType = c.getType();
                                switch (cardType) {
                                    case AMERICAN_EXPRESS:
                                        // amex
                                        break;
                                    case VISA:
                                        // visa
                                        break;
                                    case MASTERCARD:
                                        // MC
                                        break;
                                    case DISCOVER:
                                        // discover
                                        break;
                                }
                            }
                        }
                        if (payment.getOrder() != null) {
                            for (int i = 0; i < payment.getOrder().getItems().size(); i++) {
                                payment.getOrder().getItems().get(i).setStatus(OrderItemStatus.FULFILLED);
                            }
                            new SaveOrderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, payment.getOrder());
                        }
                        Log.d("F3", "Received onPaymentAction from PaymentFragment w/ Status("
                                + payment.getStatus() + ")");
                        if (payment.getStatus().equals(PaymentStatus.COMPLETED)) {
                            logData("Payment Completed");
                        } else if (payment.getStatus().equals(PaymentStatus.AUTHORIZED)) {
                            logData("Payment Authorized");
                        } else if (payment.getStatus().equals(PaymentStatus.CANCELED)) {
                            logData("Payment Canceled");
                        } else if (payment.getStatus().equals(PaymentStatus.FAILED)) {
                            logData("Payment Failed");
                        } else if (payment.getStatus().equals(PaymentStatus.REFUNDED)) {
                            logData("Payment Refunded");
                        } else if (payment.getStatus().equals(PaymentStatus.VOIDED)) {
                            logData("Payment Voided");
                        } else {
                            logData("Payment Completed");
                        }
                    } else {
                        // This should not happen, but in case it does, handle it using Content Provider
                        getTransactionFromContentProvider();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                logData("Payment Canceled");
            }
        }

    }

    public void logData(final String data) {
        Log.i("Transaction Data#", data);
    }

    private void getTransactionFromContentProvider() {
        ContentResolver resolver = getActivity().getContentResolver();
        String[] projection = new String[]{TransactionreferencesColumns.TRANSACTIONID};
        Cursor cursor = resolver.query(TransactionreferencesColumns.CONTENT_URI,
                projection,
                TransactionreferencesColumns.REFERENCEID + " = ?",
                new String[]{lastReferenceId},
                null);
        List<String> transactions = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                transactions.add(cursor.getString(0));
            }
        }

        cursor.close();
        // handle transactions
        // full transaction can get retrieved using IPoyntTransactionService.getTransaction
        if (!transactions.isEmpty()) {
            logData("Found the following transactions for referenceId " + lastReferenceId + ": ");
            for (String txnId : transactions) {
                logData(txnId);
            }
        } else {
            logData("No Transactions found");
        }
    }

    public void getTransaction(String txnId) {
        try {
            MainActivity.transactionService.getTransaction(txnId, UUID.randomUUID().toString(),
                    mTransactionServiceListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
