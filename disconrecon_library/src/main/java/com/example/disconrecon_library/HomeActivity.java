package com.example.disconrecon_library;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.disconrecon_library.fragment.DTCMapping;
import com.example.disconrecon_library.fragment.Disconnection;
import com.example.disconrecon_library.fragment.FeederReading;
import com.example.disconrecon_library.fragment.Reconnection;
import com.example.disconrecon_library.fragment.TCReading1;
import com.example.disconrecon_library.fragment.TCReading2;
import com.example.disconrecon_library.fragment.ReconectionMemo;
import com.example.disconrecon_library.model.LoginDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private Fragment fragment;
    List<LoginDetails> loginList;
    String layout;
    FragmentManager fm;

    //--------------------------------------------------------------------------------------------------------------------------
    public enum Steps {
        FORM0(Disconnection.class),
        FORM1(Reconnection.class),
        FORM2(FeederReading.class),
        FORM3(TCReading1.class),
        FORM4(TCReading2.class),
        FORM5(DTCMapping.class),
        FORM6(ReconectionMemo.class);
        private Class clazz;

        Steps(Class clazz) {
            this.clazz = clazz;
        }

        public Class getFragClass() {
            return clazz;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        if (intent != null) {
            loginList = (ArrayList<LoginDetails>) intent.getSerializableExtra("loginList");
            layout = Objects.requireNonNull(intent.getExtras()).getString("layout");
        }
        fm = getSupportFragmentManager();

        if (!TextUtils.isEmpty(layout)) {
            if ("11".equals(layout)) {
                switchContent(Steps.FORM0);
            }
            if (("12").equals(layout)) {
                switchContent(Steps.FORM1);
            }
            if ("21".equals(layout)) {
                switchContent(Steps.FORM2);
            }
            if (("22").equals(layout)) {
                switchContent(Steps.FORM3);
            }
            if (("23").equals(layout)) {
                switchContent(Steps.FORM4);
            }
            if (("24").equals(layout)) {
                switchContent(Steps.FORM5);
            }
            if (("31").equals(layout)){
                switchContent(Steps.FORM6);
            }
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    public void switchContent(HomeActivity.Steps currentForm) {
        try {
            fragment = (Fragment) currentForm.getFragClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("loginList", (Serializable) loginList);
        fragment.setArguments(bundle);
        ft.replace(R.id.main_container, fragment, currentForm.name());
        ft.commit();
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    public void switchContentBack(HomeActivity.Steps currentForm) {
        try {
            fragment = (Fragment) currentForm.getFragClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("loginList", (Serializable) loginList);
        fragment.setArguments(bundle);
        ft.replace(R.id.main_container, fragment, currentForm.name());
        ft.addToBackStack(currentForm.name());
        ft.commit();
    }
}
