package com.example.disconrecon_library.fragment;

import android.annotation.SuppressLint;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;


import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.example.disconrecon_library.R;
import com.example.disconrecon_library.api.RegisterAPI;
import com.example.disconrecon_library.api.RetroClient1;
import com.example.disconrecon_library.model.DisconData;

import com.example.disconrecon_library.model.LoginDetails;
import com.example.disconrecon_library.model.ReconMemo;
import com.example.disconrecon_library.model.Result;
import com.example.disconrecon_library.printers.AnalogicsPrinter;
import com.example.disconrecon_library.printers.ExelPrinter;
import com.example.disconrecon_library.printers.GPTPrinter;
import com.example.disconrecon_library.values.FunctionCall;
import com.example.disconrecon_library.values.constant;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.example.disconrecon_library.LoginActivity.device_id;

import static com.example.disconrecon_library.api.RetroClient1.Andkey;
import static com.example.disconrecon_library.fragment.FourthFragment.PRINTER;
import static com.example.disconrecon_library.values.constant.ALG_PRINTER;

import static com.example.disconrecon_library.values.constant.EXEL_PRINTER;
import static com.example.disconrecon_library.values.constant.GPT_PRINTER;
import static com.example.disconrecon_library.values.constant.INSERT_FAILURE;
import static com.example.disconrecon_library.values.constant.INSERT_SUCCESS;
import static com.example.disconrecon_library.values.constant.PRINTER_SUCCESS;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_FAILURE;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_SUCCESS;

public class ReconectionMemo extends Fragment implements View.OnClickListener {
    EditText et_account_id, et_recpt_no, et_mobile, et_paid_amt;
    List<LoginDetails> loginList;
    FunctionCall functionCall;
    Button cancel, print;
    TextView tv_acc_id, tv_rr_no, tv_subdiv, tv_name, tv_address, tv_tariff, tv_read_date, tv_mrcode, tv_recon_date, tv_dr_fee, tv_section, tv_recpt_date;
    List<ReconMemo> reconMemoList;
    ProgressDialog progressDialog;
    LinearLayout layout;
    FrameLayout frameLayout;
    List<Result> resultList;
    private int day, month, year;
    private Calendar mcalender;
    public static String dd, date;
    DisconData ticket;
    List<DisconData> ticketList;
    LinearLayout summary;
    public static AnalogicsThermalPrinter conn;
    public String paidamount, recptno, memo_mblno;
    public String rep_address_1 = "", rep_address_2 = "", address = "";
    ArrayList<String> addresslist;
    AnalogicsPrinter analogiesBillprint;
    GPTPrinter gptPrinter;
    ExelPrinter excelPrinter;
    public static String reading_Date = "", recon_Date = "";
    ScrollView scrollView;
   // public  static  final  String Andkey="";

    String selected_printer = "";
    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case REQUEST_RESULT_SUCCESS:

                    progressDialog.dismiss();
                    layout.setVisibility(View.VISIBLE);
                    setData();
                    frameLayout.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "Data Not Found");
                    scrollView.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.GONE);
                    break;

                case INSERT_SUCCESS:
                    progressDialog.dismiss();
                    print_bill();
                    functionCall.showToast(getActivity(), "Success");
                    break;

                case INSERT_FAILURE:
                    progressDialog.dismiss();
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "try once again");
                    break;

                case PRINTER_SUCCESS:
                    progressDialog.dismiss();
                    refresh_Fragment();

            }
            return false;
        }
    });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reconection_memo, container, false);
        initialize(view);
        setHasOptionsMenu(true);
        return view;
    }

    //-------------------------------------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    private void initialize(View view) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            loginList = (ArrayList<LoginDetails>) bundle.getSerializable("loginList");
        }
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        functionCall = new FunctionCall();
        tv_acc_id = view.findViewById(R.id.txt_acc_id);
        tv_rr_no = view.findViewById(R.id.txt_rrno);
        tv_subdiv = view.findViewById(R.id.txt_subdiv_code);
        tv_name = view.findViewById(R.id.txt_name);
        tv_address = view.findViewById(R.id.txt_address);
        tv_tariff = view.findViewById(R.id.txt_tariff);
        tv_read_date = view.findViewById(R.id.txt_readdate);
        tv_mrcode = view.findViewById(R.id.txt_mrcode);
        tv_recon_date = view.findViewById(R.id.txt_recon_date);
        tv_dr_fee = view.findViewById(R.id.txt_dr_fee);
        tv_section = view.findViewById(R.id.txt_section);
        tv_recpt_date = view.findViewById(R.id.txt_rcpt_date);
        tv_recpt_date.setOnClickListener(this);
        et_recpt_no = view.findViewById(R.id.edt_rcpt_nbr);
        et_mobile = view.findViewById(R.id.edt_mblno);
        et_paid_amt = view.findViewById(R.id.edt_bill_amt);
        reconMemoList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());
        layout = view.findViewById(R.id.lin_main);
        cancel = view.findViewById(R.id.dialog_negative_btn);
        cancel.setVisibility(View.GONE);
        print = view.findViewById(R.id.dialog_positive_btn);
        print.setText("Print");
        print.setOnClickListener(this);
        frameLayout = view.findViewById(R.id.frame_layout);
        resultList = new ArrayList<>();
        ticket = new DisconData();
        ticketList = new ArrayList<>();
        addresslist = new ArrayList<>();

        summary = view.findViewById(R.id.lin_main);
        scrollView=view.findViewById(R.id.sv_reconmemo);

        et_account_id = view.findViewById(R.id.et_acc_id);
        et_account_id.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH || !TextUtils.isEmpty(et_account_id.getText())) {
              /*  if (TextUtils.isEmpty(et_account_id.getText())) {
                    functionCall.setSnackBar(getActivity(), layout, "Please Enter Account ID");
                    return;
                }*/
                functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
                getReconMemoData(et_account_id.getText().toString(), loginList.get(0).getSUBDIVCODE());
            }
            return true;
        });
        functionCall.splitString(address, 40, addresslist);
        if (addresslist.size() > 0) {
            rep_address_1 = "  " + addresslist.get(0);
            if (addresslist.size() > 1) {
                rep_address_2 = "  " + addresslist.get(1);
            }
        }

    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void getReconMemoData(String ACC_ID, String SUBDIV) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.getReconaMemoData(ACC_ID, SUBDIV, loginList.get(0).getMRCODE(), loginList.get(0).getPASSWORD(), device_id, Andkey).enqueue(new Callback<List<com.example.disconrecon_library.model.ReconMemo>>() {
            @Override
            public void onResponse(@NonNull Call<List<com.example.disconrecon_library.model.ReconMemo>> call, @NonNull Response<List<com.example.disconrecon_library.model.ReconMemo>> response) {
                if (response.isSuccessful()) {
                    reconMemoList = response.body();
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else
                    handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<com.example.disconrecon_library.model.ReconMemo>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void setData() {
        tv_acc_id.setText(reconMemoList.get(0).getACCT_ID());
        tv_rr_no.setText(reconMemoList.get(0).getLEG_RRNO());
        tv_subdiv.setText(reconMemoList.get(0).getSubdivcode());
        tv_name.setText(reconMemoList.get(0).getCONSUMER_NAME());
        tv_tariff.setText(reconMemoList.get(0).getTARIFF());
        String s = reconMemoList.get(0).getDate1();
        reading_Date = s.substring(0, 10);
        tv_read_date.setText(reading_Date);
        tv_mrcode.setText(reconMemoList.get(0).getMRCODE());
        String r = reconMemoList.get(0).getRE_DATE();
        recon_Date = r.substring(0, 10);
        tv_recon_date.setText(recon_Date);
        tv_dr_fee.setText(reconMemoList.get(0).getDR_FEE());
        tv_section.setText(reconMemoList.get(0).getSO());
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dialog_positive_btn) {
            validate();
        }
        if (view.getId() == R.id.txt_rcpt_date) {
            DateDialog();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void validate() {
        if (TextUtils.isEmpty(tv_recpt_date.getText())) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "Select Receipt date");
            return;
        }
        if (TextUtils.isEmpty(et_recpt_no.getText())) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "Enter Receipt Number");
            return;
        }
        if (TextUtils.isEmpty(et_mobile.getText())) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "Enter Mobile Number");
            return;
        }
        if (TextUtils.isEmpty(et_paid_amt.getText())) {
            functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "Enter Paid Amount");
            return;
        }
        functionCall.showprogressdialog("Please wait to complete", "Data Inserting", progressDialog);
        paidamount = et_paid_amt.getText().toString();
        recptno = et_recpt_no.getText().toString();
        memo_mblno = et_mobile.getText().toString();


        reconMemoList.get(0).setMOBILE_NO(memo_mblno);
        reconMemoList.get(0).setPAID_AMOUNT(paidamount);
        reconMemoList.get(0).setRECEIPT_NO(recptno);
        reconMemoList.get(0).setADDRESS1(rep_address_1);
        reconMemoList.get(0).setADDRESS2(rep_address_2);

        reconMemoUpdate(tv_acc_id.getText().toString(), paidamount, recptno);
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void reconMemoUpdate(String Acc_id, String paid_amount, String rcpt_num) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.reconMemoUpdate(Acc_id, paid_amount, rcpt_num, loginList.get(0).getMRCODE(), loginList.get(0).getPASSWORD(), device_id, Andkey).enqueue(new Callback<List<Result>>() {
            @Override
            public void onResponse(@NonNull Call<List<Result>> call, @NonNull Response<List<Result>> response) {
                if (response.isSuccessful()) {
                    resultList.clear();
                    resultList = response.body();
                    handler.sendEmptyMessage(INSERT_SUCCESS);
                } else
                    handler.sendEmptyMessage(INSERT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Result>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(INSERT_FAILURE);
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------------------
    private void DateDialog() {
        mcalender = Calendar.getInstance();
        day = mcalender.get(Calendar.DAY_OF_MONTH);
        year = mcalender.get(Calendar.YEAR);
        month = mcalender.get(Calendar.MONTH);

        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            dd = (year + "-" + (month + 1) + "-" + dayOfMonth);
            date = functionCall.Parse_Date3(dd);
            tv_recpt_date.setText(date);
        };
        DatePickerDialog dpdialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), listener, year, month, day);
        mcalender.add(Calendar.MONTH, -1);
        dpdialog.show();
    }

    private void refresh_Fragment() {
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

    private void print_bill() {
        switch (PRINTER) {
            case ALG_PRINTER:
                analogiesBillprint = FourthFragment.analogicsPrinter;
                analogiesBillprint.new Analogics_Bill_print(reconMemoList, handler).execute();
                break;

            case GPT_PRINTER:
                gptPrinter = FourthFragment.gptPrinter;
                gptPrinter.new Gpt_Bill_print(reconMemoList, handler).execute();
                break;

            case EXEL_PRINTER:
                excelPrinter = FourthFragment.exelPrinter;
                excelPrinter.new Excel_Bill_print(reconMemoList, handler).execute();
                break;
        }

    }
}
