package com.example.disconrecon_library.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.disconrecon_library.R;
import com.example.disconrecon_library.ViewAllLocation;
import com.example.disconrecon_library.adapter.MRCodeAdapter;
import com.example.disconrecon_library.adapter.ReconAdapter;
import com.example.disconrecon_library.api.RegisterAPI;
import com.example.disconrecon_library.api.RetroClient1;
import com.example.disconrecon_library.model.DisconData;
import com.example.disconrecon_library.model.LoginDetails;
import com.example.disconrecon_library.model.MRcode;
import com.example.disconrecon_library.model.Result;
import com.example.disconrecon_library.repository.Repository;
import com.example.disconrecon_library.values.FunctionCall;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.disconrecon_library.LoginActivity.device_id;

import static com.example.disconrecon_library.SplashActivity.PACKAGE_NAME;
import static com.example.disconrecon_library.api.RetroClient1.Andkey;
import static com.example.disconrecon_library.values.constant.CONNECTION_UPDATE_DIALOG;
import static com.example.disconrecon_library.values.constant.INSERT_FAILURE;
import static com.example.disconrecon_library.values.constant.INSERT_SUCCESS;
import static com.example.disconrecon_library.values.constant.NONRAPDRP_TEST_APP;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_FAILURE;
import static com.example.disconrecon_library.values.constant.REQUEST_RESULT_SUCCESS;

public class Reconnection extends Fragment implements View.OnClickListener, ReconAdapter.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private TextView tv_total, tv_reconnected, tv_remaining, tv_conn_label, tv_date, tv_calender;
    private RecyclerView recyclerView;
    Toolbar toolbar;
    ProgressDialog progressDialog;
    List<LoginDetails> loginList;
    FunctionCall functionCall;
    private int day, month, year;
    private Calendar mcalender;
    private String dd, date;
    List<DisconData> detailsList;
    ReconAdapter adapter;
    ImageView imageView;
    AlertDialog alertDialog;
    Spinner spinner;
    MRcode mRcode;
    String REMARK = "";
    List<MRcode> mRcodeList;
    List<Result> resultList;
    LinearLayout layout,linear_serach;
    Repository repository;
    EditText et_acc_id;
    Button bt_reconnect;

    private Handler handler = new Handler(new Handler.Callback() {
        @SuppressLint("RestrictedApi")
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case REQUEST_RESULT_SUCCESS:
                    progressDialog.dismiss();
                    setHasOptionsMenu(true);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    getCount(detailsList);
//                    insertData();
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    setHasOptionsMenu(false);
                    imageView.setVisibility(View.VISIBLE);
                    bottomNavigationView.setVisibility(View.GONE);
                    break;

                case INSERT_SUCCESS:
                    progressDialog.dismiss();
                    alertDialog.dismiss();
                    functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
                    getRecondata(loginList.get(0).getMRCODE(), date);
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection, container, false);
        initialize(view);
        return view;
    }

    //---------------------------------------------------------------------------------------------------------------------
    private void initialize(View view) {
        toolbar = view.findViewById(R.id.main_toolbar);
        toolbar.setTitle(getResources().getString(R.string.reconnection));
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
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        tv_total = view.findViewById(R.id.txt_total);
        tv_reconnected = view.findViewById(R.id.txt_connected);
        tv_remaining = view.findViewById(R.id.txt_remaining);
        tv_conn_label = view.findViewById(R.id.txt_conn_label);
        tv_conn_label.setText(getActivity().getResources().getText(R.string.reconnected));
        recyclerView = view.findViewById(R.id.recycler);
        tv_date = view.findViewById(R.id.txt_date);
        tv_calender = view.findViewById(R.id.txt_calender);
        tv_calender.setOnClickListener(this);
        detailsList = new ArrayList<>();
        imageView = view.findViewById(R.id.img_no_data);
        mRcodeList = new ArrayList<>();
        resultList = new ArrayList<>();
        layout = view.findViewById(R.id.lin_main);
        repository = new Repository(getActivity());
        et_acc_id = view.findViewById(R.id.et_acc_id);
        bt_reconnect = view.findViewById(R.id.bt_reconnect);
        linear_serach = view.findViewById(R.id.linear_serach);

        if (PACKAGE_NAME.equals(NONRAPDRP_TEST_APP)){
            linear_serach.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
        } else layout.setVisibility(View.VISIBLE);

        bt_reconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(et_acc_id.getText().toString()))
                    getReconSearch(functionCall.getAccountID(et_acc_id));
                else et_acc_id.setError("Please Enter Account ID");
            }
        });
    }

    //-------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuInflater mi = Objects.requireNonNull(getActivity()).getMenuInflater();
        mi.inflate(R.menu.search_location_menu, menu);
        MenuItem search = menu.findItem(R.id.nav_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search....");
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        search(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_location) {
            Intent intent = new Intent(getActivity(), ViewAllLocation.class);
            intent.putExtra("list", (Serializable) detailsList);
            startActivity(intent);
        }
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

    //------------------------------------------------------------------------------------------------------------------------------
    private void DateDialog() {
        mcalender = Calendar.getInstance();
        day = mcalender.get(Calendar.DAY_OF_MONTH);
        year = mcalender.get(Calendar.YEAR);
        month = mcalender.get(Calendar.MONTH);

        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            dd = (year + "-" + (month + 1) + "-" + dayOfMonth);
            date = functionCall.Parse_Date4(dd);
            tv_date.setText(date);
            functionCall.showprogressdialog("Please wait to complete", "Data Loading", progressDialog);
//            updateTaskList();
            getRecondata(loginList.get(0).getMRCODE(), date);
        };
        DatePickerDialog dpdialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), listener, year, month, day);
        mcalender.add(Calendar.MONTH, -1);
        dpdialog.show();
    }

    //--------------------------------------------------------------------------------------------------------------------------
    private void insertData() {
        for (int i = 0; i < detailsList.size(); i++) {
            DisconData details = detailsList.get(i);
            repository.insertReData(details);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.txt_calender) {
            DateDialog();
        }
    }

    //------------------------------------------------------------------------------------------------------------------------
    private void getCount(List<DisconData> detailsList) {
        int REMAINING = 0;
        for (int i = 0; i < detailsList.size(); i++) {
            if (TextUtils.isEmpty(detailsList.get(i).getMTR_READ()))
                REMAINING = REMAINING + 1;
        }
        tv_total.setText(String.valueOf(detailsList.size()));
        tv_reconnected.setText(String.valueOf(Integer.parseInt(String.valueOf(detailsList.size())) - REMAINING));
        tv_remaining.setText(String.valueOf(REMAINING));
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void getRecondata(String MRCODE, String DATE) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.getReconList(MRCODE, DATE, loginList.get(0).getPASSWORD(), device_id, Andkey).enqueue(new Callback<List<DisconData>>() {
            @Override
            public void onResponse(@NonNull Call<List<DisconData>> call, @NonNull Response<List<DisconData>> response) {
                if (response.isSuccessful()) {
                    detailsList.clear();
                    detailsList = response.body();
                    adapter = new ReconAdapter(getActivity(), detailsList);
                    adapter.setOnItemClickListener(Reconnection.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(adapter);
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else
                    handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<DisconData>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void getReconSearch(String AccountID) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.getReconMessage(AccountID,loginList.get(0).getMRCODE(),loginList.get(0).getPASSWORD(),
                device_id,Andkey).enqueue(new Callback<List<Result>>() {
            @Override
            public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                if (response.isSuccessful()){
                    functionCall.setSnackBar(getContext(),layout,response.body().get(0).getMessage());
                    Toast.makeText(getContext(),response.body().get(0).getMessage(),Toast.LENGTH_LONG).show();
                }else Toast.makeText(getActivity(), "No Data Found!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Result>> call, Throwable t) {
                Toast.makeText(getActivity(), "No Data Found!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void show_connection_update_dialog(int id, DisconData details) {
        if (id == CONNECTION_UPDATE_DIALOG) {
            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            dialog.setTitle("RECONNECTION");
            dialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = Objects.requireNonNull(inflater).inflate(R.layout.connection_layout, null);
            dialog.setView(view);
            final LinearLayout snackbar=view.findViewById(R.id.lin_snackbar);
            final TextView tv_cons_no = view.findViewById(R.id.txt_account_no);
            tv_cons_no.setText(details.getACCT_ID());
            final TextView tv_cons_name = view.findViewById(R.id.txt_cons_name);
            tv_cons_name.setText(details.getCONSUMER_NAME());
            final TextView tv_cons_address = view.findViewById(R.id.txt_address);
            tv_cons_address.setText(details.getADD1());
            final TextView tv_arrears = view.findViewById(R.id.txt_arrears);
            tv_arrears.setText(details.getARREARS());
            final TextView tv_dis_date = view.findViewById(R.id.txt_discon_date);
            tv_dis_date.setText(details.getRE_DATE().substring(0, 11));
            final TextView tv_prev_read = view.findViewById(R.id.txt_prev_read);
            tv_prev_read.setText(details.getPREVREAD());
            final EditText curr_reading = view.findViewById(R.id.et_curr_read);
            final EditText comment = view.findViewById(R.id.et_comments);
            spinner = view.findViewById(R.id.sp_remark);
            spinner.setOnItemSelectedListener(this);
            getRemark();
            final Button cancel_button = view.findViewById(R.id.dialog_negative_btn);
            final Button update_button = view.findViewById(R.id.dialog_positive_btn);
            update_button.setText(getActivity().getText(R.string.reconnect));
            alertDialog = dialog.create();
            update_button.setOnClickListener(view1 -> {
                if (TextUtils.isEmpty(curr_reading.getText().toString())) {
                    functionCall.setSnackBar(getActivity(), snackbar, "Enter Current Reading");
                    return;
                }
                if (Double.parseDouble(details.getPREVREAD()) >= Double.parseDouble(curr_reading.getText().toString())) {
                    functionCall.setSnackBar(getActivity(), snackbar, "Current Reading should be greater than Previous Reading");
                    return;
                }
                if (REMARK.equals("SELECT")) {
                    functionCall.setSnackBar(getActivity(), snackbar, "Select Remark");
                    return;
                }
                if (TextUtils.isEmpty(comment.getText().toString())) {
                    functionCall.setSnackBar(getActivity(), snackbar, "Enter Comment");
                    return;
                }
                functionCall.showprogressdialog("Please wait to complete", "Data Inserting", progressDialog);
                reconUpdate(tv_cons_no.getText().toString(), date, curr_reading.getText().toString(), REMARK, comment.getText().toString());
            });
            cancel_button.setOnClickListener(view1 -> {
                alertDialog.dismiss();
            });
        }
        alertDialog.show();
    }

    //---------------------------------------------------------------------------------------------------------------------------------------
    private void getRemark() {
        mRcodeList.clear();
        for (int i = 0; i < getResources().getStringArray(R.array.remark).length; i++) {
            MRcode mRcode = new MRcode();
            mRcode.setMRCODE(getResources().getStringArray(R.array.remark)[i]);
            mRcodeList.add(mRcode);
            MRCodeAdapter roleAdapter = new MRCodeAdapter(mRcodeList, getActivity());
            roleAdapter.notifyDataSetChanged();
            spinner.setAdapter(roleAdapter);
            spinner.setSelection(0);
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.sp_remark) {
            mRcode = mRcodeList.get(i);
            REMARK = mRcode.getMRCODE();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    private void reconUpdate(String Acc_id, String Dis_Date, String CURREAD, String Remarks, String Comment) {
        RetroClient1 retroClient = new RetroClient1();
        RegisterAPI api = retroClient.getClient().create(RegisterAPI.class);
        api.reconUpdate(Acc_id, Dis_Date, CURREAD, Remarks, Comment, loginList.get(0).getMRCODE(), loginList.get(0).getPASSWORD(), device_id, Andkey).enqueue(new Callback<List<Result>>() {
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

    //-----------------------------------------------------------------------------------------------------------------------
    private void updateTaskList() {
        repository.getReData().observe(this, notes -> {
            assert notes != null;
            if (notes.size() > 0) {
                detailsList = notes;
                adapter = new ReconAdapter(getActivity(), notes);
                adapter.setOnItemClickListener(Reconnection.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(adapter);
                handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
            } else
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
        });
    }
}
