package com.example.disconrecon_library.fragment;

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

public class SecondFragment extends Fragment implements View.OnClickListener {
    RelativeLayout layout1, layout2, layout3, layout4;
    FunctionCall functionCall;
    LinearLayout layout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        initialize(view);
        setHasOptionsMenu(true);
        return view;
    }

    //-------------------------------------------------------------------------------------------------
    private void initialize(View view) {
        layout1 = view.findViewById(R.id.relative_feeder);
        layout1.setOnClickListener(this);
        layout2 = view.findViewById(R.id.relative_tc1);
        layout2.setOnClickListener(this);
        layout3 = view.findViewById(R.id.relative_tc2);
        layout3.setOnClickListener(this);
        layout4 = view.findViewById(R.id.relative_mapping);
        layout4.setOnClickListener(this);
        functionCall = new FunctionCall();
        layout = view.findViewById(R.id.lin_main);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.relative_feeder) {
            if (functionCall.isInternetOn(Objects.requireNonNull(getActivity()))) {
                ((TabActivity) Objects.requireNonNull(getActivity())).startIntent(getActivity(), "21");
            } else functionCall.setSnackBar(getActivity(), layout, "Check Internet Connection");
        }
        if (view.getId() == R.id.relative_tc1) {
            if (functionCall.isInternetOn(Objects.requireNonNull(getActivity()))) {
                ((TabActivity) Objects.requireNonNull(getActivity())).startIntent(getActivity(), "22");
            } else functionCall.setSnackBar(getActivity(), layout, "Check Internet Connection");
        }
        if (view.getId() == R.id.relative_tc2) {
            if (functionCall.isInternetOn(Objects.requireNonNull(getActivity()))) {
                ((TabActivity) Objects.requireNonNull(getActivity())).startIntent(getActivity(), "23");
            } else functionCall.setSnackBar(getActivity(), layout, "Check Internet Connection");
        }
        if (view.getId() == R.id.relative_mapping) {
            if (functionCall.isInternetOn(Objects.requireNonNull(getActivity()))) {
                ((TabActivity) Objects.requireNonNull(getActivity())).startIntent(getActivity(), "24");
            } else functionCall.setSnackBar(getActivity(), layout, "Check Internet Connection");
        }
    }
}
