package com.example.disconrecon_library;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;
    public static String PACKAGE_NAME="";
    TextView txtAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_splash);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        if (Build.VERSION.SDK_INT > 24) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        txtAppName = findViewById(R.id.txt_app_name);
        txtAppName.animate().rotationY(360f).setDuration(2000);
        new Handler().postDelayed(this::checkPermissionsMandAbove, 3000); // WAIT FOR 3 SECONDS
    }

    @TargetApi(23)
    public void checkPermissionsMandAbove() {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            if (checkPermission()) {
                movetoLogin();
            } else {
                requestPermission();
            }
        } else {
            movetoLogin();
        }
    }

    private void movetoLogin() {
        Intent in = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(in);
        finish();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{
                READ_PHONE_STATE,
                WRITE_EXTERNAL_STORAGE,
                ACCESS_FINE_LOCATION,
                CAMERA
        }, RequestPermissionCode);
    }

    private boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean ReadPhoneStatePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean ReadStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean ReadLocationPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                boolean ReadLogsPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                if (ReadPhoneStatePermission && ReadStoragePermission && ReadLocationPermission && ReadLogsPermission) {
                    movetoLogin();
                } else {
                    Toast.makeText(SplashActivity.this, "Required All Permissions to granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}