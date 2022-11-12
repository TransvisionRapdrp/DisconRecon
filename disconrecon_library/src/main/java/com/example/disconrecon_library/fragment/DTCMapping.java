package com.example.disconrecon_library.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disconrecon_library.R;
import com.example.disconrecon_library.adapter.FDRNameAdapter;
import com.example.disconrecon_library.adapter.DTCAdapter;
import com.example.disconrecon_library.adapter.MRCodeAdapter;
import com.example.disconrecon_library.api.RegisterAPI;
import com.example.disconrecon_library.api.RetroClient1;
import com.example.disconrecon_library.model.DTC;
import com.example.disconrecon_library.model.Feeder;
import com.example.disconrecon_library.model.LoginDetails;
import com.example.disconrecon_library.model.MRcode;
import com.example.disconrecon_library.model.Result;
import com.example.disconrecon_library.values.FunctionCall;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.disconrecon_library.api.RetroClient1.Andkey;
import static com.example.disconrecon_library.values.constant.DTC_DETAILS_UPDATE_DIALOG;
import static com.example.disconrecon_library.values.constant.FEEDER_NAME_FAILURE;
import static com.example.disconrecon_library.values.constant.FEEDER_NAME_SUCCESS;
import static com.example.disconrecon_library.values.constant.INSERT_FAILURE;
import static com.example.disconrecon_library.values.constant.INSERT_SUCCESS;
import static com.example.disconrecon_library.values.constant.MRCODE_FETCH_FAILURE;
import static com.example.disconrecon_library.values.constant.MRCODE_FETCH_SUCCESS;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_FAILURE;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_SUCCESS;

public class DTCMapping extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, DTCAdapter.OnItemClickListener {
    Toolbar toolbar;
    List<LoginDetails> loginList;
    ProgressDialog progressDialog;
    FunctionCall functionCall;
    RecyclerView recyclerView;
    private int day, month, year;
    private Calendar mcalender;
    private String dd, date1, date2;
    TextView tv_date, tv_calender;
    AlertDialog alertDialog;
    Spinner sp_feeder, sp_mrcode;
    ImageView img_no_data;
    LinearLayout layout;
    List<DTC> dtcList;
    List<Result> resultList;
    DTCAdapter dtcAdapter;
    FDRNameAdapter adapter;
    List<Feeder> feederList;
    List<MRcode> mRcodeList;
    String FEEDER = "", MRCODE = "";
    Feeder feeder;
    MRcode mRcode;
    TextView tv_map_date;
    MRCodeAdapter mrCodeAdapter;

    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case MRCODE_FETCH_SUCCESS:
                    break;

                case MRCODE_FETCH_FAILURE:
                    Objects.requireNonNull(getActivity()).finish();
                    break;

                case FEEDER_NAME_SUCCESS:
                    setHasOptionsMenu(true);
                    sp_feeder.setVisibility(View.VISIBLE);
                    break;

                case FEEDER_NAME_FAILURE:
                    setHasOptionsMenu(false);
                    sp_feeder.setVisibility(View.GONE);
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "Data Not Found");
                    break;

                case REQUEST_RESULT_SUCCESS:
                    progressDialog.dismiss();
                    setHasOptionsMenu(true);
                    recyclerView.setVisibility(View.VISIBLE);
                    img_no_data.setVisibility(View.GONE);
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    setHasOptionsMenu(false);
                    recyclerView.setVisibility(View.GONE);
                    img_no_data.setVisibility(View.VISIBLE);
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "Data Not Found");
                    break;

                case INSERT_SUCCESS:
                    progressDialog.dismiss();
                    alertDialog.dismiss();
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, resultList.get(0).getMessage());
                    functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
                    getDTCDetails(loginList.get(0).getSUBDIVCODE(), date1, FEEDER);
                    break;

                case INSERT_FAILURE:
                    alertDialog.dismiss();
                    progressDialog.dismiss();
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "try once again");
                    break;

            }
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reading, container, false);
        initialize(view);
        return view;
    }

    //---------------------------------------------------------------------------------------------------------------------
    private void initialize(View view) {
        toolbar = view.findViewById(R.id.main_toolbar);
        toolbar.setTitle(getResources().getString(R.string.dtc_mapping));
        toolbar.setTitleTextColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(getActivity().getResources().getDrawable(R.drawable.ic_back));
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());
        progressDialog = new ProgressDialog(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            loginList = (ArrayList<LoginDetails>) bundle.getSerializable("loginList");
        }
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        functionCall = new FunctionCall();
        recyclerView = view.findViewById(R.id.recycler);
        tv_date = view.findViewById(R.id.txt_date);
        tv_calender = view.findViewById(R.id.txt_calender);
        tv_calender.setOnClickListener(this);
        layout = view.findViewById(R.id.lin_main);
        img_no_data = view.findViewById(R.id.img_no_data);
        sp_feeder = view.findViewById(R.id.sp_feeder);
        sp_feeder.setOnItemSelectedListener(this);
        dtcList = new ArrayList<>();
        resultList = new ArrayList<>();
        feederList = new ArrayList<>();
        mRcodeList = new ArrayList<>();
        mRcode = new MRcode();
        feeder = new Feeder();

        getMRCode(loginList.get(0).getSUBDIVCODE());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txt_calender) {
            DateDialog();
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater mi = Objects.requireNonNull(getActivity()).getMenuInflater();
        mi.inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.nav_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search...");
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        search(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------------------------------------------------------------------------------------------
    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dtcAdapter.getFilter().filter(newText);
                return false;
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
            dd = (year + "/" + (month + 1) + "/" + dayOfMonth);
            date1 = functionCall.getYearMonth(dd);
            tv_date.setText(date1);
            getFDRNames(loginList.get(0).getSUBDIVCODE(), date1);
        };
        DatePickerDialog dpdialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), listener, year, month, day);
        mcalender.add(Calendar.MONTH, -1);
        dpdialog.show();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void getMRCode(String SUBDIV) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.getMRCode(SUBDIV, Andkey).enqueue(new Callback<List<MRcode>>() {
            @Override
            public void onResponse(@NonNull Call<List<MRcode>> call, @NonNull Response<List<MRcode>> response) {
                if (response.isSuccessful()) {
                    mRcodeList.clear();
                    mRcodeList = response.body();
                    handler.sendEmptyMessage(MRCODE_FETCH_SUCCESS);
                } else
                    handler.sendEmptyMessage(MRCODE_FETCH_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<MRcode>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(MRCODE_FETCH_FAILURE);
            }
        });
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void getFDRNames(String SUBDIV, String DATE) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.getFDRNames(SUBDIV, DATE, Andkey).enqueue(new Callback<List<Feeder>>() {
            @Override
            public void onResponse(@NonNull Call<List<Feeder>> call, @NonNull Response<List<Feeder>> response) {
                if (response.isSuccessful()) {
                    feederList.clear();
                    feederList = response.body();
                    adapter = new FDRNameAdapter(feederList, getActivity());
                    sp_feeder.setAdapter(adapter);
                    sp_feeder.setSelection(1);
                    handler.sendEmptyMessage(FEEDER_NAME_SUCCESS);
                } else
                    handler.sendEmptyMessage(FEEDER_NAME_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Feeder>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(FEEDER_NAME_FAILURE);
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void getDTCDetails(String SUBDIV, String DATE, String FDRCODE) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.getDTCDeatils(SUBDIV, DATE, FDRCODE, Andkey).enqueue(new Callback<List<DTC>>() {
            @Override
            public void onResponse(@NonNull Call<List<DTC>> call, @NonNull Response<List<DTC>> response) {
                if (response.isSuccessful()) {
                    dtcList.clear();
                    dtcList = response.body();
                    dtcAdapter = new DTCAdapter(dtcList, getActivity());
                    dtcAdapter.setOnItemClickListener(DTCMapping.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(dtcAdapter);
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else
                    handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<DTC>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.sp_feeder) {
            feeder = feederList.get(i);
            FEEDER = feeder.getFDRCODE().substring(0, 11);
            functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
            getDTCDetails(loginList.get(0).getSUBDIVCODE(), date1, FEEDER);
        }
        if (adapterView.getId() == R.id.sp_mr_code) {
            mRcode = mRcodeList.get(i);
            MRCODE = mRcode.getMRCODE();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void show_dtc_details_update_dialog(int id, DTC dtc) {
        if (id == DTC_DETAILS_UPDATE_DIALOG) {
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            dialog.setTitle("DTC MAPPING");
            dialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = Objects.requireNonNull(inflater).inflate(R.layout.dtc_mr_mapping_layout, null);
            dialog.setView(view);
            final TextView dtc_name = view.findViewById(R.id.txt_dtc_name);
            final TextView dtc_code = view.findViewById(R.id.txt_dtc_code);
            sp_mrcode = view.findViewById(R.id.sp_mr_code);
            sp_mrcode.setOnItemSelectedListener(this);
            tv_map_date = view.findViewById(R.id.txt_map_date);
            final Button cancel_button = view.findViewById(R.id.dialog_negative_btn);
            final Button update_button = view.findViewById(R.id.dialog_positive_btn);
            alertDialog = dialog.create();
            mrCodeAdapter = new MRCodeAdapter(mRcodeList, getActivity());
            sp_mrcode.setAdapter(mrCodeAdapter);
            tv_map_date.setOnClickListener(v -> DateDialogMap());
            dtc_name.setText(dtc.getDTCNAME());
            dtc_code.setText(dtc.getTCCODE());
            update_button.setOnClickListener(view1 -> {
                if (TextUtils.isEmpty(tv_map_date.getText().toString())) {
                    functionCall.setSnackBar(getActivity(), layout, "Select mapping date");
                    return;
                }
                functionCall.showprogressdialog("Please wait to complete", "Data Inserting", progressDialog);
                dtcMapping(MRCODE, dtc.getTCCODE(), tv_map_date.getText().toString());
            });
            cancel_button.setOnClickListener(view1 -> {
                alertDialog.dismiss();
            });
        }
        alertDialog.show();
    }

    //------------------------------------------------------------------------------------------------------------------------------
    private void DateDialogMap() {
        mcalender = Calendar.getInstance();
        day = mcalender.get(Calendar.DAY_OF_MONTH);
        year = mcalender.get(Calendar.YEAR);
        month = mcalender.get(Calendar.MONTH);

        DatePickerDialog.OnDateSetListener listener = (view12, year, month, dayOfMonth) -> {
            dd = (year + "-" + (month + 1) + "-" + dayOfMonth);
            date2 = functionCall.Parse_Date4(dd);
            tv_map_date.setText(date2);
        };
        DatePickerDialog dpdialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), listener, year, month, day);
        mcalender.add(Calendar.MONTH, -1);
        dpdialog.show();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void dtcMapping(String MRCODE, String DTCCODE, String READDATE) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.dtcMapping(MRCODE, DTCCODE, READDATE, Andkey).enqueue(new Callback<List<Result>>() {
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

}