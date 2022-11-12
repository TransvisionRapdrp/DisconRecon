package com.example.disconrecon_library.printers;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import com.analogics.thermalAPI.Bluetooth_Printer_3inch_prof_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.example.disconrecon_library.model.ReconMemo;
import com.example.disconrecon_library.values.FunctionCall;
import com.lvrenyang.io.Canvas;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;


import static com.example.disconrecon_library.fragment.ReconectionMemo.date;
import static com.example.disconrecon_library.fragment.ReconectionMemo.reading_Date;
import static com.example.disconrecon_library.fragment.ReconectionMemo.recon_Date;
import static com.example.disconrecon_library.values.constant.ALG_PRINTER_NAME;
import static com.example.disconrecon_library.values.constant.ALG_PRINTER_NAME_1;
import static com.example.disconrecon_library.values.constant.ANALOGICS_PRINTER_CONNECTED;
import static com.example.disconrecon_library.values.constant.ANALOGICS_PRINTER_DISCONNECTED;
import static com.example.disconrecon_library.values.constant.ANALOGICS_PRINTER_NOT_PAIRED;
import static com.example.disconrecon_library.values.constant.ANALOGICS_PRINTER_PAIRED;
import static com.example.disconrecon_library.fragment.FourthFragment.printerconnected;
import static com.example.disconrecon_library.values.constant.PRINTER_SUCCESS;

public class AnalogicsPrinter {
    public static Bluetooth_Printer_3inch_prof_ThermalAPI api;
    private BluetoothAdapter deviceadapter;
    private static AnalogicsThermalPrinter conn;
    private static String printer_address = "";
    private Canvas canvas = new Canvas();
    private FunctionCall functionsCall = new FunctionCall();
    private Context context;
    private AppCompatActivity activity;
    private Handler handler;

    private boolean connected = false, paired = false;


    //-------------------------49to 146 line for analogies printer pairing and connection--------------------------------------
    public AnalogicsPrinter(Context context, Handler handler, AppCompatActivity activity) {
        this.context = context;
        this.handler = handler;
        this.activity = activity;
    }

    private Handler analogicsHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ANALOGICS_PRINTER_CONNECTED:
                    printerconnected = true;
                    api = new Bluetooth_Printer_3inch_prof_ThermalAPI();
                    handler.sendEmptyMessage(ANALOGICS_PRINTER_CONNECTED);
                    break;

                case ANALOGICS_PRINTER_DISCONNECTED:
                    printerconnected = false;
                    handler.sendEmptyMessage(ANALOGICS_PRINTER_DISCONNECTED);
                    break;

                case ANALOGICS_PRINTER_PAIRED:
                    handler.sendEmptyMessage(ANALOGICS_PRINTER_PAIRED);
                    break;
            }
            return false;
        }
    });

    public void analogics_Printer_connection() {
        new Thread(new Check_Analogics_connection()).start();
        deviceadapter = BluetoothAdapter.getDefaultAdapter();
        conn = new AnalogicsThermalPrinter();
        new Connection().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class Connection extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getPairedDevices();
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevice = deviceadapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice)
                if (StringUtils.startsWithIgnoreCase(device.getName(), ALG_PRINTER_NAME)) {
                    paired = true;
                    printer_connection(device);
                } else if (StringUtils.startsWithIgnoreCase(device.getName(), ALG_PRINTER_NAME_1)) {
                    paired = true;
                    printer_connection(device);
                }
            if (!paired)
                analogicsHandler.sendEmptyMessage(ANALOGICS_PRINTER_NOT_PAIRED);
        } else analogicsHandler.sendEmptyMessage(ANALOGICS_PRINTER_NOT_PAIRED);
    }

    private void printer_connection(BluetoothDevice device) {
        printer_address = device.getAddress();
        try {
            conn.openBT(device.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Check_Analogics_connection implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                doWork();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doWork() {
        activity.runOnUiThread(() -> {
            if (!connected) {
                if (conn.isConnected()) {
                    connected = true;
                    analogicsHandler.sendEmptyMessage(ANALOGICS_PRINTER_CONNECTED);
                }
            }
        });
    }
    //------------------------------------------------------------------------------------------------------

    @SuppressLint("StaticFieldLeak")
    public class Analogics_Bill_print extends AsyncTask<Void, Void, Void> {
        List<ReconMemo> reconlist;
        Handler handler;

        public Analogics_Bill_print(List<ReconMemo> reconlist, Handler handler) {
            this.reconlist = reconlist;
            this.handler = handler;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            printanalogics(reconlist);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            functionsCall.showToast(context, "Recon Print is Done");
            handler.sendEmptyMessage(PRINTER_SUCCESS);
        }
    }

    public void printanalogics(List<ReconMemo> reconlist) {

        StringBuilder stringBuilder = new StringBuilder();
        analogics_header__double_print(functionsCall.aligncenter("HUBLI ELECTRICITY SUPPLY COMPANY LTD", 38), 4);
        analogics_header__double_print(functionsCall.aligncenter("RECONNECTION MEMO", 38), 4);
        analogicsprint(functionsCall.space("", 12), 4);
        analogicsprint(functionsCall.space(" Reconnection Date", 12) + ":" + " " + recon_Date, 4);
        analogicsprint(functionsCall.space(" Sub Division", 12) + ":" + " " + reconlist.get(0).getSubdivcode(), 4);
        analogicsprint(functionsCall.space(" Section", 12) + ":" + " " + reconlist.get(0).getSO(), 4);
        analogics_double_print(functionsCall.space(" Account ID", 12) + ":" + " " + reconlist.get(0).getACCT_ID(), 4);
        analogicsprint(functionsCall.space(" RRNO", 12) + ":" + " " + reconlist.get(0).getLEG_RRNO(), 4);
        analogics_48_print(functionsCall.aligncenter("Name and Address", 48), 6);
        analogics_48_print("  " + reconlist.get(0).getCONSUMER_NAME(), 3);
        analogics_48_print(reconlist.get(0).getADDRESS1(), 3);
        analogics_48_print(reconlist.get(0).getADDRESS2(), 3);
        analogicsprint(functionsCall.space(" Mobile No", 12) + ":" + " " + reconlist.get(0).getMOBILE_NO(), 4);
        analogicsprint(functionsCall.space(" Tariff", 12) + ":" + " " + reconlist.get(0).getTARIFF(), 4);
        analogicsprint(functionsCall.space(" Reading Date", 12) + ":" + " " + reading_Date, 4);
        analogicsprint(functionsCall.space(" MR Code", 12) + ":" + " " + reconlist.get(0).getMRCODE(), 4);
        analogicsprint(functionsCall.space(" Paid Amount", 12) + ":" + " " + reconlist.get(0).getPAID_AMOUNT(), 4);
        analogicsprint(functionsCall.space(" Receipt No", 12) + ":" + " " + reconlist.get(0).getRECEIPT_NO(), 4);
        analogicsprint(functionsCall.space(" Receipt Date", 12) + ":" + " " + functionsCall.Parse_Date7(date), 4);
            analogics_double_print(functionsCall.space(" D&R Fee", 12) + ":" + " " + reconlist.get(0).getDR_FEE() + ".00", 4);

        analogicsprint(functionsCall.space("", 12), 4);
        stringBuilder.setLength(0);
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        analogicsprint(functionsCall.space("                    sign", 12) + " ", 4);
        analogics_48_print("-----------------------------------------------", 3);
        analogics_48_print("  NOTE: Pay bill before due date to avoid", 3);
        analogics_48_print("  Dis-Reconnection charges.", 3);

        stringBuilder.setLength(0);
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        analogicsprint(stringBuilder.toString(), 4);
    }

    public void analogicsprint(String Printdata, int feed_line) {
        conn.printData(api.font_Courier_30_VIP(Printdata));
        text_line_spacing(feed_line);
    }

    public void analogics_double_print(String Printdata, int feed_line) {
        conn.printData(api.font_Double_Height_On_VIP());
        analogicsprint(Printdata, feed_line);
        conn.printData(api.font_Double_Height_Off_VIP());
    }

    public void analogics_header_print(String Printdata, int feed_line) {
        conn.printData(api.font_Courier_38_VIP(Printdata));
        text_line_spacing(feed_line);
    }

    public void analogics_header__double_print(String Printdata, int feed_line) {
        conn.printData(api.font_Double_Height_On_VIP());
        analogics_header_print(Printdata, feed_line);
        conn.printData(api.font_Double_Height_Off_VIP());
    }

    public void analogics_48_print(String Printdata, int feed_line) {
        conn.printData(api.font_Courier_48_VIP(Printdata));
        text_line_spacing(feed_line);
    }

    public void text_line_spacing(int space) {
        conn.printData(api.variable_Size_Line_Feed_VIP(space));
    }

}
