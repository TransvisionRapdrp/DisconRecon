package com.example.disconrecon_library.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.disconrecon_library.api.RegisterAPI;
import com.example.disconrecon_library.api.RetroClient1;
import com.example.disconrecon_library.model.DTC;
import com.example.disconrecon_library.model.Feeder;
import com.example.disconrecon_library.model.LoginDetails;
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
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_FAILURE;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_SUCCESS;

public class TCReading1 extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, DTCAdapter.OnItemClickListener {
    Toolbar toolbar;
    List<LoginDetails> loginList;
    ProgressDialog progressDialog;
    FunctionCall functionCall;
    RecyclerView recyclerView;
    TextView tv_date, tv_calender;
    private int day, month, year;
    private Calendar mcalender;
    private String dd, date;
    Spinner spinner;
    FDRNameAdapter adapter;
    List<Feeder> feederList;
    ImageView imageView, img_dtc;
    LinearLayout layout;
    String FEEDER = "";
    DTCAdapter DTCAdapter;
    List<DTC> dtcList;
    Feeder feeder;
    AlertDialog alertDialog;
    List<Result> resultList;

    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case FEEDER_NAME_SUCCESS:
                    setHasOptionsMenu(true);
                    spinner.setVisibility(View.VISIBLE);
                    break;

                case FEEDER_NAME_FAILURE:
                    setHasOptionsMenu(false);
                    spinner.setVisibility(View.GONE);
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "Data Not Found");
                    break;

                case REQUEST_RESULT_SUCCESS:
                    progressDialog.dismiss();
                    setHasOptionsMenu(true);
                    recyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    setHasOptionsMenu(false);
                    recyclerView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, "Data Not Found");
                    break;

                case INSERT_SUCCESS:
                    progressDialog.dismiss();
                    alertDialog.dismiss();
                    functionCall.setSnackBar(Objects.requireNonNull(getActivity()), layout, resultList.get(0).getMessage());
                    functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
                    getDTCDetails(loginList.get(0).getSUBDIVCODE(), date, FEEDER);
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
        toolbar.setTitle(getResources().getString(R.string.tc_reading));
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
        spinner = view.findViewById(R.id.sp_feeder);
        spinner.setOnItemSelectedListener(this);
        feederList = new ArrayList<>();
        imageView = view.findViewById(R.id.img_no_data);
        layout = view.findViewById(R.id.lin_main);
        dtcList = new ArrayList<>();
        resultList = new ArrayList<>();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txt_calender) {
            DateDialog();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------
    private void DateDialog() {
        mcalender = Calendar.getInstance();
        day = mcalender.get(Calendar.DAY_OF_MONTH);
        year = mcalender.get(Calendar.YEAR);
        month = mcalender.get(Calendar.MONTH);

        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            dd = (year + "/" + (month + 1) + "/" + dayOfMonth);
            date = functionCall.getYearMonth(dd);
            tv_date.setText(date);
            getFDRNames(loginList.get(0).getSUBDIVCODE(), date);
        };
        DatePickerDialog dpdialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), listener, year, month, day);
        mcalender.add(Calendar.MONTH, -1);
        dpdialog.show();
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
                DTCAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        feeder = new Feeder();
        if (adapterView.getId() == R.id.sp_feeder) {
            feeder = feederList.get(i);
            FEEDER = feeder.getFDRCODE().substring(0, 11);
            functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
            getDTCDetails(loginList.get(0).getSUBDIVCODE(), date, FEEDER);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                    spinner.setAdapter(adapter);
                    spinner.setSelection(1);
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
                    DTCAdapter = new DTCAdapter(dtcList, getActivity());
                    DTCAdapter.setOnItemClickListener(TCReading1.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(DTCAdapter);
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

    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    public void show_dtc_details_update_dialog(int id, DTC dtc) {
        if (id == DTC_DETAILS_UPDATE_DIALOG) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            dialog.setTitle("DTC UPDATE");
            dialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = Objects.requireNonNull(inflater).inflate(R.layout.dtc_details_update_layout, null);
            dialog.setView(view);
            final TextView dtc_name = view.findViewById(R.id.txt_dtc_name);
            final TextView dtc_code = view.findViewById(R.id.txt_dtc_code);
            final TextView dtc_ir = view.findViewById(R.id.txt_dtc_ir);
            final EditText current_reading = view.findViewById(R.id.et_current_reading);
            img_dtc = view.findViewById(R.id.img_dtc);
            img_dtc.setVisibility(View.GONE);
            final Button cancel_button = view.findViewById(R.id.dialog_negative_btn);
            final Button update_button = view.findViewById(R.id.dialog_positive_btn);
            alertDialog = dialog.create();
            dtc_name.setText(dtc.getDTCNAME());
            dtc_code.setText(dtc.getTCCODE());
            dtc_ir.setText(dtc.getTCIR());
            current_reading.setText(dtc.getTCFR());
            update_button.setOnClickListener(view1 -> {
                if (Double.parseDouble(dtc.getTCIR()) >= Double.parseDouble(current_reading.getText().toString())) {
                    functionCall.setSnackBar(getActivity(), layout, "Current Reading should be greater than Previous Reading");
                    return;
                }
                functionCall.showprogressdialog("Please wait to complete", "Data Inserting", progressDialog);
                dtcUpdate(dtc.getTCCODE(), date, current_reading.getText().toString());
            });
            cancel_button.setOnClickListener(view1 -> alertDialog.dismiss());
            alertDialog.show();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void dtcUpdate(String DTCCODE, String DATE, String DTCFR) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.dtcUpdate(DTCCODE, DATE, DTCFR, Andkey).enqueue(new Callback<List<Result>>() {
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
