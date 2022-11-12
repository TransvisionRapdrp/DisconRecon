package com.example.disconrecon_library;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disconrecon_library.adapter.ViewPagerAdapter;
import com.example.disconrecon_library.fragment.FirstFragment;
import com.example.disconrecon_library.fragment.FourthFragment;
import com.example.disconrecon_library.fragment.SecondFragment;
import com.example.disconrecon_library.model.LoginDetails;
import com.example.disconrecon_library.repository.Repository;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.disconrecon_library.values.constant.PREFS_LOGIN_ROLE;
import static com.example.disconrecon_library.SplashActivity.PACKAGE_NAME;

public class TabActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<LoginDetails> loginList;
    private static final int DIALOG_PROFILE_DETAILS = 1;
    String main_curr_version = null;
    private boolean isFirstBackPressed = false;
    Repository repository;

    SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Intent intent = getIntent();
        if (intent != null) {
            loginList = (ArrayList<LoginDetails>) intent.getSerializableExtra("loginList");
        }
        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_person));
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            showdialog(DIALOG_PROFILE_DETAILS);
        });
        repository = new Repository(this);
        viewPager = findViewById(R.id.view_pager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        setupViewPager(viewPager);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        //--------------------------------------------------------------------------------------------------------------------------
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo != null) {
            main_curr_version = pInfo.versionName;
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        String ROLE2 = sharedPreferences.getString(PREFS_LOGIN_ROLE, "");

        if (loginList.get(0).getROLE().equals("MR") && (StringUtils.startsWithIgnoreCase(sharedPreferences.getString(PREFS_LOGIN_ROLE, ""), "MR"))) {
            adapter.addFragment(new FirstFragment(), "CONNECTION");
            adapter.addFragment(new SecondFragment(), "READING");
        } else {
            adapter.addFragment(new FourthFragment(), "RECONNECTION MEMO");
        }
        viewPager.setAdapter(adapter);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_notification) {
        }
        return super.onOptionsItemSelected(item);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    public void startIntent(Context context, String layout) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("layout", layout);
        intent.putExtra("loginList", (Serializable) loginList);
        startActivity(intent);
    }

    //------------------------------------Method for alert dialog---------------------------------------------------------------//
    private void showdialog(int id) {
        final AlertDialog login_dialog;
        if (id == DIALOG_PROFILE_DETAILS) {
            AlertDialog.Builder login_dlg = new AlertDialog.Builder(this);
            @SuppressLint("InflateParams") LinearLayout dlg_linear = (LinearLayout) getLayoutInflater().inflate(R.layout.profile_layout, null);
            login_dlg.setView(dlg_linear);
            final TextView tv_user = dlg_linear.findViewById(R.id.txt_user_name);
            tv_user.setText(loginList.get(0).getUSERNAME());
            final TextView tv_mrcode = dlg_linear.findViewById(R.id.txt_mrcode);
            tv_mrcode.setText(loginList.get(0).getMRCODE());
            final TextView tv_version = dlg_linear.findViewById(R.id.txt_vesrion);
            tv_version.setText(main_curr_version);
            final TextView tv_logout = dlg_linear.findViewById(R.id.txt_logout);
            tv_logout.setOnClickListener(view -> {
                logout();
            });
            login_dialog = login_dlg.create();
            login_dialog.show();
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------
    private void logout() {
        Intent intent = new Intent(TabActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //--------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            super.onBackPressed();
        } else {
            if (isFirstBackPressed) {
                super.onBackPressed();
            } else {
                isFirstBackPressed = true;
                Toast.makeText(this, "Press again to close app", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> isFirstBackPressed = false, 2000);
            }
        }
    }


}
