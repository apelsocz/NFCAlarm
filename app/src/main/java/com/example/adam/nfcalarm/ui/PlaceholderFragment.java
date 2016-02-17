package com.example.adam.nfcalarm.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adam.nfcalarm.R;

/**
 * Created by adam on 16-02-16.
 */
public class PlaceholderFragment extends Fragment {
    public PlaceholderFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.alarm_display, container, false);
        return rootView;
    }
}
