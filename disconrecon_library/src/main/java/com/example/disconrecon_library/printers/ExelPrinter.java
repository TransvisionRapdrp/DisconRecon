package com.example.disconrecon_library.printers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.TextPaint;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cie.btp.CieBluetoothPrinter;
import com.cie.btp.DebugLog;
import com.cie.btp.PrintColumnParam;
import com.cie.btp.PrinterWidth;
import com.example.disconrecon_library.model.ReconMemo;
import com.example.disconrecon_library.values.FunctionCall;

import java.util.List;
import java.util.Objects;

import static android.icu.lang.UCharacter.LineBreak.NEXT_LINE;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_CONN_DEVICE_NAME;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_CONN_STATE_CONNECTED;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_CONN_STATE_CONNECTING;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_CONN_STATE_LISTEN;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_CONN_STATE_NONE;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_MESSAGES;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_MSG;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_NAME;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_NOTIFICATION_ERROR_MSG;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_NOTIFICATION_MSG;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_NOT_CONNECTED;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_NOT_FOUND;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_SAVED;
import static com.cie.btp.BtpConsts.RECEIPT_PRINTER_STATUS;

import static com.example.disconrecon_library.fragment.ReconectionMemo.date;
import static com.example.disconrecon_library.fragment.ReconectionMemo.reading_Date;
import static com.example.disconrecon_library.fragment.ReconectionMemo.recon_Date;
import static com.example.disconrecon_library.values.constant.EXEL_PRINTER_CONNECTED;
import static com.example.disconrecon_library.values.constant.EXEL_PRINTER_CONNECTION;
import static com.example.disconrecon_library.values.constant.PRINTER_SUCCESS;

public class ExelPrinter {

    @SuppressLint("StaticFieldLeak")
    private static CieBluetoothPrinter mPrinter = CieBluetoothPrinter.INSTANCE;
    private FunctionCall functionCall = new FunctionCall();
    private Context context;
    private Handler handler;
    private static final int BARCODE_WIDTH = 400;
    private static final int BARCODE_HEIGHT = 60;
    public static boolean printerconnected = false;

    public ExelPrinter(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;

    }

    private Handler exelHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case EXEL_PRINTER_CONNECTED:
                    printerconnected = true;
                    handler.sendEmptyMessage(EXEL_PRINTER_CONNECTED);
                    break;

                case EXEL_PRINTER_CONNECTION:
                    mPrinter.connectToPrinter();
                    break;
            }
            return false;
        }
    });

    public void exel_printer_connection() {
        mPrinter.initService(context);
        mPrinter.connectToPrinter();
        startBroadcast();
    }


    private void startBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIPT_PRINTER_MESSAGES);
        LocalBroadcastManager.getInstance(context).registerReceiver(Receiver, filter);
    }

    private final BroadcastReceiver Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DebugLog.logTrace("Printer Message Received");
            Bundle b = intent.getExtras();

            switch (Objects.requireNonNull(b).getInt(RECEIPT_PRINTER_STATUS)) {
                case RECEIPT_PRINTER_CONN_STATE_NONE:
                    functionCall.logStatus("printer not connected");
                    break;
                case RECEIPT_PRINTER_CONN_STATE_LISTEN:
                    functionCall.logStatus("ready for connection");
                    break;
                case RECEIPT_PRINTER_CONN_STATE_CONNECTING:
                    functionCall.logStatus("printer connecting");
                    break;
                case RECEIPT_PRINTER_CONN_STATE_CONNECTED:
                    exelHandler.sendEmptyMessage(EXEL_PRINTER_CONNECTED);
                    functionCall.showToast(context, "Exel Printer Connected");
                    break;
                case RECEIPT_PRINTER_CONN_DEVICE_NAME:
                    savePrinterMac(Objects.requireNonNull(b.getString(RECEIPT_PRINTER_NAME)));
                    break;
                case RECEIPT_PRINTER_NOTIFICATION_ERROR_MSG:
                    String n = b.getString(RECEIPT_PRINTER_MSG);
                    functionCall.logStatus(n);
                    break;
                case RECEIPT_PRINTER_NOTIFICATION_MSG:
                    String m = b.getString(RECEIPT_PRINTER_MSG);
                    functionCall.logStatus(m);
                    break;
                case RECEIPT_PRINTER_NOT_CONNECTED:
                    functionCall.logStatus("Printer Not Connected");
                    break;
                case RECEIPT_PRINTER_NOT_FOUND:
                    functionCall.logStatus("Printer Not Found");
                    mPrinter.clearPreferredPrinter();
                    exelHandler.sendEmptyMessage(EXEL_PRINTER_CONNECTED);
                    break;
                case RECEIPT_PRINTER_SAVED:
                    functionCall.logStatus("printer_saved");
                    break;
            }
        }
    };

    private void savePrinterMac(String sMacAddr) {
        if (sMacAddr.length() > 4)
            functionCall.logStatus("Preferred Printer saved");
        else functionCall.logStatus("Preferred Printer cleared");
    }

    //------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    public class Excel_Bill_print extends AsyncTask<Void, Void, Void> {
        List<ReconMemo> reconlist;
        Handler handler;

        public Excel_Bill_print(List<ReconMemo> reconlist, Handler handler) {
            this.reconlist = reconlist;
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Execel_printanalogics(reconlist);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            functionCall.showToast(context, "Recon Print is Done");
            handler.sendEmptyMessage(PRINTER_SUCCESS);
        }


        public void Execel_printanalogics(List<ReconMemo> reconlist) {

            TextPaint var = new TextPaint();
            var.setColor(-16777216);
            byte var6 = 35;
            var.setTextSize((float) var6);


            TextPaint var4 = new TextPaint();
            var4.setColor(-16777216);
            byte var5 = 30;
            var4.setTextSize((float) var5);

            mPrinter.setPrinterWidth(PrinterWidth.PRINT_WIDTH_72MM);
            mPrinter.resetPrinter();
            mPrinter.setAlignmentCenter();

            TextPaint var2 = new TextPaint();
            var2.setColor(-16777216);
            byte var3 = 26;
            var2.setTextSize((float) var3);


            mPrinter.printUnicodeText(" HUBLI ELECTRICITY SUPPLY ", Layout.Alignment.ALIGN_CENTER, var);
            mPrinter.printUnicodeText(" COMPANY LTD ", Layout.Alignment.ALIGN_CENTER, var4);

            mPrinter.printUnicodeText("RECONNECTION MEMO", Layout.Alignment.ALIGN_CENTER, var);

            print_data_normal("Reconnection Date", recon_Date);
            print_data_normal(" Sub Division", reconlist.get(0).getSubdivcode());
            print_data_normal(" Section", reconlist.get(0).getSO());

            mPrinter.printUnicodeText("Account ID" + "       " + ":" + "      " + reconlist.get(0).getACCT_ID(), Layout.Alignment.ALIGN_NORMAL, var4);
            print_data_normal("RRNO", reconlist.get(0).getLEG_RRNO());

            mPrinter.printUnicodeText("Name and Address", Layout.Alignment.ALIGN_CENTER, var2);
            mPrinter.printUnicodeText("  " + reconlist.get(0).getCONSUMER_NAME(), Layout.Alignment.ALIGN_NORMAL, var2);


            print_data_normal("Tariff", reconlist.get(0).getTARIFF());
            print_data_normal("Reading Date", reading_Date);
            print_data_normal("MR Code", reconlist.get(0).getMRCODE());
            print_data_normal(" Paid Amount", reconlist.get(0).getPAID_AMOUNT());
            print_data_normal("Receipt No", reconlist.get(0).getRECEIPT_NO());
            print_data_normal("Mobile No", reconlist.get(0).getMOBILE_NO());
            print_data_normal(" Receipt Date", functionCall.Parse_Date7(date));

            mPrinter.printUnicodeText("" + "D&R Fee" + "  " + ":" + "    " +reconlist.get(0).getDR_FEE(), Layout.Alignment.ALIGN_CENTER, var);

           // mPrinter.printUnicodeText("  Sign", Layout.Alignment.ALIGN_CENTER, var2);

            mPrinter.printTextLine(functionCall.space("",40));
            mPrinter.printUnicodeText("  NOTE: Pay bill before due date to avoid", Layout.Alignment.ALIGN_NORMAL, var2);
            mPrinter.printUnicodeText("  Dis-Reconnection charges.", Layout.Alignment.ALIGN_NORMAL, var2);
            mPrinter.printLineFeed();
        }
    }

    private void print_data_normal(String value, String result) {
        String[] sCol1 = {"  " + value};
        PrintColumnParam pcp1stCol = new PrintColumnParam(sCol1, 55, Layout.Alignment.ALIGN_NORMAL, 24, Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        String[] sCol2 = {":"};
        PrintColumnParam pcp2ndCol = new PrintColumnParam(sCol2, 1, Layout.Alignment.ALIGN_CENTER, 24);
        String[] sCol3 = {result + "   "};
        PrintColumnParam pcp3rdCol = new PrintColumnParam(sCol3, 44, Layout.Alignment.ALIGN_CENTER, 24, Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        mPrinter.PrintTable(pcp1stCol, pcp2ndCol, pcp3rdCol);

    }


}
