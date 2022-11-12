package com.example.disconrecon_library.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.disconrecon_library.R;

public class ReconnectionReport extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reading, container, false);
        initialize(view);
        return view;
    }

    //---------------------------------------------------------------------------------------------------------------------
    private void initialize(View view){

    }
}
