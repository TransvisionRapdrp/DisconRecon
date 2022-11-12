package com.example.disconrecon_library;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.example.disconrecon_library.adapter.RoleAdapter;
import com.example.disconrecon_library.api.RegisterAPI;
import com.example.disconrecon_library.api.RetroClient2;
import com.example.disconrecon_library.model.DisconData;
import com.example.disconrecon_library.model.LoginDetails;
import com.example.disconrecon_library.values.FTPAPI;
import com.example.disconrecon_library.values.FunctionCall;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.disconrecon_library.values.constant.APK_FILE_DOWNLOADED;
import static com.example.disconrecon_library.values.constant.APK_FILE_NOT_FOUND;
import static com.example.disconrecon_library.values.constant.LOGIN_FAILURE;
import static com.example.disconrecon_library.values.constant.LOGIN_SUCCESS;
import static com.example.disconrecon_library.values.constant.PREFS_LOGIN_ROLE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final int DLG_APK_UPDATE_SUCCESS = 1;
    DisconData details;
    ArrayList<DisconData> arrayList;
    RoleAdapter roleAdapter;
    Button login;
    Spinner sp_role;
    EditText userId, password;
    FunctionCall functionCall;
    LinearLayout layout;
    List<LoginDetails> loginList;
    ProgressDialog progressDialog;
    FTPAPI ftpapi;
    String main_curr_version = "", LOGIN_ROLE = "";
    public static String device_id = "";
    TextView tv_version;

    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case LOGIN_SUCCESS:
                    progressDialog.dismiss();
//                    moveToNext();
                    if (functionCall.compare(main_curr_version, loginList.get(0).getDISRECONN_VER()))
                        show_Dialog(DLG_APK_UPDATE_SUCCESS);
                    else {
                        moveToNext();
                    }
                    break;

                case LOGIN_FAILURE:
                    progressDialog.dismiss();
                    functionCall.setSnackBar(LoginActivity.this, layout, "Invalid Credentials");
                    break;

                case APK_FILE_DOWNLOADED:
                    progressDialog.dismiss();
                    functionCall.updateApp(LoginActivity.this, new File(functionCall.filepath("ApkFolder") +
                            File.separator + "Discon_Recon_" + loginList.get(0).getDISRECONN_VER() + ".apk"));
                    break;

                case APK_FILE_NOT_FOUND:
                    progressDialog.dismiss();
                    functionCall.setSnackBar(LoginActivity.this, layout, "APK Not Found");
                    break;

            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_login);
        initialize();
        userRole();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint({"SetTextI18n", "HardwareIds"})
    public void initialize() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo != null) {
            main_curr_version = pInfo.versionName;
        }
        ftpapi = new FTPAPI();
        progressDialog = new ProgressDialog(this);
        arrayList = new ArrayList<>();
        loginList = new ArrayList<>();
        sp_role = findViewById(R.id.spinner);
        sp_role.setOnItemSelectedListener(this);
        userId = findViewById(R.id.et_user_name);
        userId.setText("");
        password = findViewById(R.id.et_password);
        password.setText("");
        login = findViewById(R.id.btn_login);
        login.setOnClickListener(this);
        functionCall = new FunctionCall();
        layout = findViewById(R.id.lin_login);
        tv_version = findViewById(R.id.version_code);
        tv_version.setText("Version :" + main_curr_version);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (tm != null)
            try {
                device_id = tm.getDeviceId();
            }catch (Exception e){
                device_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }
        if (TextUtils.isEmpty(device_id))
            device_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        Toast.makeText(this, device_id, Toast.LENGTH_SHORT).show();

        //  device_id = "352514086238452";//54000231-MR
           // device_id = "862512030176888";//540003806//357869083548989
       // device_id = "357869083548989";//54000100-AEE//862512030176888


    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void userRole() {
        arrayList.clear();
        for (int i = 0; i < getResources().getStringArray(R.array.user_role).length; i++) {
            details = new DisconData();
            details.setUSER_ROLE(getResources().getStringArray(R.array.user_role)[i]);
            arrayList.add(details);
            roleAdapter = new RoleAdapter(arrayList, this);
            roleAdapter.notifyDataSetChanged();
            sp_role.setAdapter(roleAdapter);
        }
        sp_role.setSelection(0);
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) {
            if (functionCall.isInternetOn(this)) {
                loginValidation();
            } else
                functionCall.setSnackBar(LoginActivity.this, layout, "Check Internet Connection");
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void loginValidation() {
        if (LOGIN_ROLE.equals("SELECT")) {
            functionCall.setSnackBar(this, layout, "Please Select Role");
            return;
        }
        if (TextUtils.isEmpty(userId.getText())) {
            userId.setError("Please Enter UserID");
            return;
        }
        if (TextUtils.isEmpty(password.getText())) {
            password.setError("Please Enter Password");
            return;
        }
        SavePreferences(PREFS_LOGIN_ROLE, LOGIN_ROLE);
        functionCall.showprogressdialog("Please wait to complete.", "Logging In", progressDialog);
        loginDetails2(userId.getText().toString(), device_id, password.getText().toString());
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void moveToNext() {
        Intent intent = new Intent(LoginActivity.this, TabActivity.class);
        intent.putExtra("loginList", (Serializable) loginList);
        startActivity(intent);
        finish();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void loginDetails2(String USERNAME, String DEVICEID, String PASSWORD) {
        RetroClient2 retroClient2 = new RetroClient2();
        RegisterAPI api = retroClient2.getClient().create(RegisterAPI.class);
        api.getLoginDetails2(USERNAME, DEVICEID, PASSWORD).enqueue(new Callback<List<LoginDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<LoginDetails>> call, @NonNull Response<List<LoginDetails>> response) {
                if (response.isSuccessful()) {
                    loginList = response.body();
                    if (loginList.get(0).getROLE().equals(LOGIN_ROLE)) {
                        handler.sendEmptyMessage(LOGIN_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(LOGIN_FAILURE);
                    }
                } else
                    handler.sendEmptyMessage(LOGIN_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<LoginDetails>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(LOGIN_FAILURE);
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------------------
    private void show_Dialog(int id) {
        Dialog dialog;
        if (id == DLG_APK_UPDATE_SUCCESS) {
            AlertDialog.Builder appupdate = new AlertDialog.Builder(this);
            appupdate.setTitle("App Updates");
            appupdate.setCancelable(false);
            appupdate.setMessage("Your current version number : " + main_curr_version + "\n" + "\n" +
                    "New version is available : " + loginList.get(0).getDISRECONN_VER() + "\n");
            appupdate.setPositiveButton("UPDATE", (dialog1, which) -> {
                FTPAPI.Download_apk downloadApk = ftpapi.new Download_apk(handler, loginList.get(0).getDISRECONN_VER());
                downloadApk.execute();
            });
            dialog = appupdate.create();
            dialog.show();
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.spinner) {
            details = arrayList.get(i);
            LOGIN_ROLE = details.getUSER_ROLE();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
