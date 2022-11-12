package com.example.disconrecon_library.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;


import androidx.annotation.NonNull;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;





import com.example.disconrecon_library.R;
import com.example.disconrecon_library.TabActivity;


import com.example.disconrecon_library.adapter.RoleAdapter;
import com.example.disconrecon_library.model.DisconData;
import com.example.disconrecon_library.printers.AnalogicsPrinter;
import com.example.disconrecon_library.printers.ExelPrinter;
import com.example.disconrecon_library.printers.GPTPrinter;
import com.example.disconrecon_library.values.FunctionCall;




import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import static com.example.disconrecon_library.values.constant.ANALOGICS_PRINTER_CONNECTED;
import static com.example.disconrecon_library.values.constant.ANALOGICS_PRINTER_DISCONNECTED;
import static com.example.disconrecon_library.values.constant.ANALOGICS_PRINTER_NOT_PAIRED;



import static com.example.disconrecon_library.values.constant.EXEL_PRINTER_CONNECTED;

import static com.example.disconrecon_library.values.constant.GPT_PRINTER_CONNECTED;
import static com.example.disconrecon_library.values.constant.GPT_PRINTER_DISCONNECTED;
import static com.example.disconrecon_library.values.constant.GPT_PRINTER_NOT_PAIRED;



public class FourthFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button bt_recon_memo, bt_recon_print;
    FunctionCall functionCall;
    LinearLayout layout;

    private static final int DIALOG_PRINTER = 1;
    Spinner sp_printer;
    RoleAdapter roleAdapter;
    DisconData ticket;
    List<DisconData> ticketList;
    public static String PRINTER = "";
    public static boolean printerconnected = false;
    public static AnalogicsPrinter analogicsPrinter;
    @SuppressLint("SetTextI18n")
    public static GPTPrinter gptPrinter;
    public static ExelPrinter exelPrinter;


    private Handler navigationHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case ANALOGICS_PRINTER_CONNECTED:
                    functionCall.showToast(getActivity(), "Analogics Printer Connected");
                    break;

                case ANALOGICS_PRINTER_DISCONNECTED:
                    functionCall.showToast(getActivity(), "Analogics Printer Disconnected");
                    break;

                case ANALOGICS_PRINTER_NOT_PAIRED:
                    functionCall.showToast(getActivity(), "Analogics Printer Disconnected");
                    break;

                case GPT_PRINTER_CONNECTED:
                    functionCall.showToast(getActivity(), "GPT Printer Connected");
                    break;

                case GPT_PRINTER_DISCONNECTED:
                    functionCall.showToast(getActivity(), "GPT Printer DisConnected");
                    break;
/*
                case GPT_PRINTER_NOT_PAIRED:
                    functionCall.showToast(getActivity(), "GPT Printer DisConnected");
                    break;*/

                case EXEL_PRINTER_CONNECTED:
                    functionCall.showToast(getActivity(), "Exel Printer Connected");
                    break;
            }

            return true;
        }
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);
        initialize(view);
        setHasOptionsMenu(true);
        return view;
    }

    private void initialize(View view) {
        bt_recon_memo = view.findViewById(R.id.bt_reconnection);
        bt_recon_memo.setOnClickListener(this);
        functionCall = new FunctionCall();
        bt_recon_print = view.findViewById(R.id.bt_printer);
        bt_recon_print.setOnClickListener(this);
        layout = view.findViewById(R.id.lin_main);

    }

    @Override
    public void onResume() {
        super.onResume();
        printerconn();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_reconnection) {
            if (functionCall.isInternetOn(Objects.requireNonNull(getActivity()))) {
                if (!TextUtils.isEmpty(PRINTER)) {
                    ((TabActivity) Objects.requireNonNull(getActivity())).startIntent(getActivity(), "31");
                }else functionCall.setSnackBar(getActivity(), layout, "Please Connect Printer");
            } else functionCall.setSnackBar(getActivity(), layout, "Check Internet Connection");
        }
        if (v.getId() == R.id.bt_printer) {
            showdialog(DIALOG_PRINTER);
        }
    }


    private void showdialog(int id) {
        AlertDialog login_dialog;
        if (id == DIALOG_PRINTER) {
            AlertDialog.Builder login_dlg = new AlertDialog.Builder(getActivity());
            @SuppressLint("InflateParams") LinearLayout dlg_linear = (LinearLayout) getLayoutInflater().inflate(R.layout.print_selection, null);
            login_dlg.setView(dlg_linear);
            login_dlg.setTitle("Select Printer");
            login_dlg.setCancelable(false);
            sp_printer = dlg_linear.findViewById(R.id.sp_printer);
            sp_printer.setOnItemSelectedListener(this);
            ticketList = new ArrayList<>();
            getPrinterName();
            login_dlg.setPositiveButton("OK", (dialogInterface, i) -> {
                //connectPrinter();
                printerconn();
            });
            login_dialog = login_dlg.create();
            login_dialog.show();
        }

    }

    private void getPrinterName() {
        ticketList.clear();
        for (int i = 0; i < getResources().getStringArray(R.array.printer).length; i++) {
            ticket = new DisconData();
            ticket.setUSER_ROLE(getResources().getStringArray(R.array.printer)[i]);
            ticketList.add(ticket);
            roleAdapter = new RoleAdapter(ticketList, getActivity());
            roleAdapter.notifyDataSetChanged();
            sp_printer.setAdapter(roleAdapter);
        }
        sp_printer.setSelection(0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_printer) {
            ticket = ticketList.get(position);
            PRINTER = ticket.getUSER_ROLE();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void connectPrinter() {
        try {
            Fragment currentFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().findFragmentById(R.id.main_container);
            FragmentTransaction fragmentTransaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            fragmentTransaction.detach(Objects.requireNonNull(currentFragment));
            fragmentTransaction.attach(currentFragment);
            fragmentTransaction.commit();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    public void printerconn(){
        if (!printerconnected) {
            switch (PRINTER) {
                case "GPT":
                    gptPrinter = new GPTPrinter(getActivity(), navigationHandler);
                    gptPrinter.gpt_printer_connection();
                    break;

                case "ALG":
                    analogicsPrinter = new AnalogicsPrinter(getActivity(), navigationHandler, (AppCompatActivity) getActivity());
                    analogicsPrinter.analogics_Printer_connection();
                    break;

                case "EXEL":
                    exelPrinter = new ExelPrinter(getActivity(), navigationHandler);
                    exelPrinter.exel_printer_connection();
                    break;
            }
        }
    }

}



