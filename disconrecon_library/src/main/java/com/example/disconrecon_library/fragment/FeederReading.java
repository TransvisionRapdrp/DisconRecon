package com.example.disconrecon_library.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.disconrecon_library.adapter.FeederAdapter;
import com.example.disconrecon_library.api.RegisterAPI;
import com.example.disconrecon_library.api.RetroClient1;
import com.example.disconrecon_library.model.Feeder;
import com.example.disconrecon_library.model.LoginDetails;
import com.example.disconrecon_library.model.Result;
import com.example.disconrecon_library.values.ClassGPS;
import com.example.disconrecon_library.values.FunctionCall;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.disconrecon_library.api.RetroClient1.Andkey;
import static com.example.disconrecon_library.values.constant.FEEDER_DETAILS_UPDATE_DIALOG;
import static com.example.disconrecon_library.values.constant.INSERT_FAILURE;
import static com.example.disconrecon_library.values.constant.INSERT_SUCCESS;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_FAILURE;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_SUCCESS;

public class FeederReading extends Fragment implements View.OnClickListener, FeederAdapter.OnItemClickListener {
    Toolbar toolbar;
    List<LoginDetails> loginList;
    ProgressDialog progressDialog;
    FunctionCall functionCall;
    RecyclerView recyclerView;
    private int day, month, year;
    private Calendar mcalender;
    private String dd, date;
    TextView tv_date, tv_calender;
    List<Feeder> feederList;
    FeederAdapter adapter;
    ClassGPS classGPS;
    String gps_lat = "", gps_long = "";
    LinearLayout layout;
    AlertDialog alertDialog;
    List<Result> resultList;
    ImageView imageView;

    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
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
                    getFeederDetails(loginList.get(0).getSUBDIVCODE(), date);
                    break;

                case INSERT_FAILURE:
                    progressDialog.dismiss();
                    alertDialog.dismiss();
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
        toolbar.setTitle(getResources().getString(R.string.feeder_reading));
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
        feederList = new ArrayList<>();
        resultList = new ArrayList<>();
        imageView = view.findViewById(R.id.img_no_data);
        layout = view.findViewById(R.id.lin_main);
        classGPS = new ClassGPS(getActivity());
        recyclerView = view.findViewById(R.id.recycler);
        tv_date = view.findViewById(R.id.txt_date);
        tv_calender = view.findViewById(R.id.txt_calender);
        tv_calender.setOnClickListener(this);
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
            functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
            getFeederDetails(loginList.get(0).getSUBDIVCODE(), date);
        };
        DatePickerDialog dpdialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), listener, year, month, day);
        mcalender.add(Calendar.MONTH, -1);
        dpdialog.show();
    }

    //-------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater mi = Objects.requireNonNull(getActivity()).getMenuInflater();
        mi.inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.nav_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search....");
        search(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void getFeederDetails(String SUBDIV, String DATE) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.getFeederDetails(SUBDIV, DATE, Andkey).enqueue(new Callback<List<Feeder>>() {
            @Override
            public void onResponse(@NonNull Call<List<Feeder>> call, @NonNull Response<List<Feeder>> response) {
                if (response.isSuccessful()) {
                    feederList.clear();
                    feederList = response.body();
                    adapter = new FeederAdapter(feederList, getActivity());
                    adapter.setOnItemClickListener(FeederReading.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else
                    handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Feeder>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }

    //***********************************************************************************************************************************************
    private void GPSlocation() {
        if (classGPS.canGetLocation()) {
            double latitude = classGPS.getLatitude();
            double longitude = classGPS.getLongitude();
            gps_lat = "" + latitude;
            gps_long = "" + longitude;
        }
    }

    //***********************************************************************************************************************************************
    public void show_fdr_details_update_dialog(int id, Feeder feeder) {
        if (id == FEEDER_DETAILS_UPDATE_DIALOG) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            dialog.setTitle("FDR UPDATE");
            dialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = Objects.requireNonNull(inflater).inflate(R.layout.feeder_details_update_layout, null);
            dialog.setView(view);
            final TextView fdr_name = view.findViewById(R.id.txt_fdr_name);
            final TextView fdr_code = view.findViewById(R.id.txt_fdr_code);
            final TextView fdr_ir = view.findViewById(R.id.txt_fdr_ir);
            final EditText current_reading = view.findViewById(R.id.edit_current_reading);
            final EditText srtpv_input = view.findViewById(R.id.edit_srtpv_input);
            final EditText boundary_export = view.findViewById(R.id.edit_boundary_export);
            final Button cancel_button = view.findViewById(R.id.dialog_negative_btn);
            final Button update_button = view.findViewById(R.id.dialog_positive_btn);
            alertDialog = dialog.create();
            GPSlocation();
            fdr_name.setText(feeder.getFDRNAME());
            fdr_code.setText(feeder.getFDRCODE());
            fdr_ir.setText(feeder.getFDRIR());
            current_reading.setText(feeder.getFDRFR());
            srtpv_input.setText(feeder.getSRTPV_INPUT());
            boundary_export.setText(feeder.getBoundary_Mtr_Export());
            update_button.setOnClickListener(view1 -> {
                if (Double.parseDouble(feeder.getFDRIR()) >= Double.parseDouble(current_reading.getText().toString())) {
                    functionCall.setSnackBar(getActivity(), layout, "Current Reading should be greater than Previous Reading");
                    return;
                }
                functionCall.showprogressdialog("Please wait to complete", "Data Inserting", progressDialog);
                feederUpdate(feeder.getFDRCODE(), date, current_reading.getText().toString(), gps_lat, gps_long, srtpv_input.getText().toString(),
                        boundary_export.getText().toString());
            });
            cancel_button.setOnClickListener(view1 -> alertDialog.dismiss());
            alertDialog.show();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void feederUpdate(String FDRCODE, String DATE, String FDRFR, String Latitude, String Longitude, String SRTPV, String Boundary_Export) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.feederUpdate(FDRCODE, DATE, FDRFR, Latitude, Longitude, SRTPV, Boundary_Export, Andkey).enqueue(new Callback<List<Result>>() {
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
