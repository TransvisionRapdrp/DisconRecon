package com.example.disconrecon_library.printers;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import com.example.disconrecon_library.model.ReconMemo;
import com.example.disconrecon_library.values.FunctionCall;
import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.Canvas;
import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.disconrecon_library.fragment.ReconectionMemo.date;
import static com.example.disconrecon_library.fragment.ReconectionMemo.reading_Date;
import static com.example.disconrecon_library.fragment.ReconectionMemo.recon_Date;
import static com.example.disconrecon_library.values.constant.GPT_PRINTER_CONNECTED;
import static com.example.disconrecon_library.values.constant.GPT_PRINTER_DISCONNECTED;
import static com.example.disconrecon_library.values.constant.GPT_PRINTER_NAME;
import static com.example.disconrecon_library.values.constant.GPT_PRINTER_NOT_PAIRED;
import static com.example.disconrecon_library.values.constant.PRINTER_SUCCESS;


public class GPTPrinter implements IOCallBack {

    private BluetoothAdapter mBluetoothAdapter;
    private float yaxis = 0;

    private BTPrinting mBt = new BTPrinting();
    private static Canvas mCanvas = new Canvas();

    private static ExecutorService es = Executors.newScheduledThreadPool(30);

    public static boolean printerconnected = false;

    private FunctionCall functionCall = new FunctionCall();
    private Context context;
    private Handler handler;
    private boolean paired = false;
    private static Pos pos = new Pos();


    public GPTPrinter(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    private Handler gptHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GPT_PRINTER_CONNECTED:
                    printerconnected = true;
                    handler.sendEmptyMessage(GPT_PRINTER_CONNECTED);
                    break;

                case GPT_PRINTER_DISCONNECTED:
                    printerconnected = false;
                    handler.sendEmptyMessage(GPT_PRINTER_DISCONNECTED);
                    break;

                case GPT_PRINTER_NOT_PAIRED:
                    handler.sendEmptyMessage(GPT_PRINTER_NOT_PAIRED);
                    break;
            }
            return false;
        }
    });

    public void gpt_printer_connection() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pos.Set(mBt);
        mCanvas.Set(mBt);
        mBt.SetCallBack(this);

        getPairedDevices();
    }

    @SuppressLint("MissingPermission")
    private void getPairedDevices() {
        @SuppressLint("MissingPermission")
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice)
                if (StringUtils.startsWithIgnoreCase(device.getName(), GPT_PRINTER_NAME)) {
                    paired = true;
                    es.submit(new TaskOpen(mBt, device.getAddress(), context));
                }
            if (!paired)
                gptHandler.sendEmptyMessage(GPT_PRINTER_NOT_PAIRED);
        } else gptHandler.sendEmptyMessage(GPT_PRINTER_NOT_PAIRED);
    }

    @Override
    public void OnOpen() {
        gptHandler.sendEmptyMessage(GPT_PRINTER_CONNECTED);
    }

    @Override
    public void OnOpenFailed() {
        gptHandler.sendEmptyMessage(GPT_PRINTER_DISCONNECTED);
    }

    @Override
    public void OnClose() {
        gptHandler.sendEmptyMessage(GPT_PRINTER_DISCONNECTED);
    }

    private class TaskOpen implements Runnable {
        BTPrinting bt;
        String address;
        Context context;

        private TaskOpen(BTPrinting bt, String address, Context context) {
            this.bt = bt;
            this.address = address;
            this.context = context;
        }

        @Override
        public void run() {
            bt.Open(address, context);
        }
    }


        //--------------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    public class Gpt_Bill_print extends AsyncTask<Void, Void, Void> {
        List<ReconMemo> reconlist;
        Handler handler;


        public Gpt_Bill_print(List<ReconMemo> reconlist,  Handler handler) {
            this.reconlist = reconlist;
            this.handler=handler;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            PrintGpt(reconlist);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            functionCall.showToast(context, "Recon Print is Done");
            handler.sendEmptyMessage(PRINTER_SUCCESS);
        }



    public void PrintGpt(List<ReconMemo> reconlist) {
        boolean bPrintResult;
        int pre_normal_text_length = 21;
        pos.POS_FeedLine();
        pos.POS_S_Align(1);
        printdoubleText("HUBLI ELECTRICITY SUPPLY COMPANY LTD");
        printdoubleText("RECONNECTION MEMO");
        printText("");
        pos.POS_S_Align(0);
        printText(functionCall.space("  Reconnection Date", pre_normal_text_length) + ":" + " " +recon_Date);
        printText(functionCall.space("  Sub Division", pre_normal_text_length) + ":" + " " + reconlist.get(0).getSubdivcode());
        printText(functionCall.space("  Section", pre_normal_text_length) + ":" + " " + reconlist.get(0).getSO());
        printdoubleText(functionCall.space("  Account ID", pre_normal_text_length) + ":" + " " + reconlist.get(0).getACCT_ID());
        printText(functionCall.space("  RR NO", pre_normal_text_length) + ":" + " " + reconlist.get(0).getLEG_RRNO());
        pos.POS_S_Align(1);
        printText("Name and Address");
        pos.POS_S_Align(0);
        printText("  " + reconlist.get(0).getCONSUMER_NAME());
        printText( reconlist.get(0).getADDRESS1());
        printText( reconlist.get(0).getADDRESS2());
        printText(functionCall.space("  Mobile No", pre_normal_text_length) + ":" + " " + reconlist.get(0).getMOBILE_NO());
        printText(functionCall.space("  Tariff", pre_normal_text_length) + ":" + " " + reconlist.get(0).getTARIFF());
        printText(functionCall.space("  Reading Date", pre_normal_text_length) + ":" + " " + reading_Date);
        printText(functionCall.space("  MR Code", pre_normal_text_length) + ":" + " " + reconlist.get(0).getMRCODE());
        printText(functionCall.space("  Paid Amount", pre_normal_text_length) + ":" + " " + reconlist.get(0).getPAID_AMOUNT());
        printText(functionCall.space("  Receipt No", pre_normal_text_length) + ":" + " " + reconlist.get(0).getRECEIPT_NO());
        printText(functionCall.space("  Receipt Date", pre_normal_text_length) + ":" + " " + functionCall.Parse_Date7(date));
        printdoubleText(functionCall.space("  D & R Fee", pre_normal_text_length) + ":" + " " + reconlist.get(0).getDR_FEE() + ".00");

        pos.POS_FeedLine();
        pos.POS_FeedLine();
        printText(functionCall.space("                                 sign",pre_normal_text_length));

        printText("---------------------------------------------");
        printText("  " + "NOTE: Pay bill before due date to avoid");
        printText("  " + "Dis-Reconnection charges.");
        pos.POS_FeedLine();
        pos.POS_FeedLine();
        bPrintResult = pos.GetIO().IsOpened();
    }
    private void printText(String msg) {
        pos.POS_S_TextOut(msg + "\r\n", 0, 0, 0, 0, 4);
    }

    private void printdoubleText(String msg) {
        pos.POS_S_TextOut(msg + "\r\n", 0, 0, 1, 0, 4);
    }
}

}
