package com.example.disconrecon_library.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.disconrecon_library.R;
import com.example.disconrecon_library.TabActivity;
import com.example.disconrecon_library.values.FunctionCall;

import java.util.Objects;

import static com.example.disconrecon_library.SplashActivity.PACKAGE_NAME;
import static com.example.disconrecon_library.values.constant.NONRAPDRP_TEST_APP;

public class FirstFragment extends Fragment implements View.OnClickListener {
    RelativeLayout layout1, layout2, layout3;
    FunctionCall functionCall;
    LinearLayout layout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        initialize(view);
        setHasOptionsMenu(true);
        return view;
    }

    //-------------------------------------------------------------------------------------------------
    private void initialize(View view) {
        layout1 = view.findViewById(R.id.relative_discon);
        layout1.setOnClickListener(this);
        layout2 = view.findViewById(R.id.relative_recon);
        layout2.setOnClickListener(this);
        layout3 = view.findViewById(R.id.relative_collection);
        layout3.setOnClickListener(this);
        functionCall = new FunctionCall();
        layout = view.findViewById(R.id.lin_main);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.relative_discon) {
            if (functionCall.isInternetOn(Objects.requireNonNull(getActivity()))) {
                ((TabActivity) Objects.requireNonNull(getActivity())).startIntent(getActivity(), "11");
            } else functionCall.setSnackBar(getActivity(), layout, "Check Internet Connection");
        }
        if (view.getId() == R.id.relative_recon) {
            if (functionCall.isInternetOn(Objects.requireNonNull(getActivity()))) {
                ((TabActivity) Objects.requireNonNull(getActivity())).startIntent(getActivity(), "12");
            } else functionCall.setSnackBar(getActivity(), layout, "Check Internet Connection");
        }
        if (view.getId() == R.id.relative_collection) {
            try {
                Intent i;
                if (PACKAGE_NAME.equals(NONRAPDRP_TEST_APP))
                 i = Objects.requireNonNull(getActivity()).getPackageManager().getLaunchIntentForPackage("com.example.non_rapdrp_trm_collection_testing");
                else i = Objects.requireNonNull(getActivity()).getPackageManager().getLaunchIntentForPackage("com.transvision.trmcollection");

                startActivity(i);
            } catch (Exception e) {
                functionCall.showToast(getActivity(), "Please Install TRMCollection APK");
            }
        }
    }
}
