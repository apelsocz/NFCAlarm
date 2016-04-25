package com.example.adam.nfcalarm.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adam.nfcalarm.AlarmsActivity;
import com.example.adam.nfcalarm.R;
import com.example.adam.nfcalarm.data.AlarmDAO;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.util.Data;
import com.example.adam.nfcalarm.util.Format;
import com.example.adam.nfcalarm.util.Views;

import java.util.List;

public class Alarms extends Fragment {
    public static final String NAME = Alarms.class.getSimpleName();

    private RecyclerView mRecycler;
    private Adapter mAdapter;
    private FloatingActionButton mFAB;
    private AlarmDAO mAlarmDAO;
    private List<AlarmModel> mList;
    private TextView mNextDate;
    private TextView mNextTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarms_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNextDate = (TextView) view.findViewById(R.id.ac_next_date);
        mNextTime = (TextView) view.findViewById(R.id.ac_next_time);

        update();
        mAdapter = new Adapter(getActivity(), mList);
        mRecycler = (RecyclerView) view.findViewById(R.id.alarm_container_recycler);
        mRecycler.setHasFixedSize(false);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager( new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecycler.setItemAnimator( new DefaultItemAnimator() );

        setHasOptionsMenu(true);
        Log.d(NAME, "onViewCreated()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(NAME, "onResume()");
        Activity activity = getActivity();
        if (!Views.isActivityNull(activity)) {
            mFAB = (FloatingActionButton) activity.findViewById(R.id.floating_action_btn);
            mFAB.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create alarm model and pass it around
                    ((AlarmsActivity)getActivity()).onAlarmClick(AlarmModel.EMPTY);
                }
            });
        }
    }

    public void update() {
        mAlarmDAO = new AlarmDAO();

        mList = mAlarmDAO.getModelsAsList();
        Log.d(NAME, "update(), modelList.size:" + String.valueOf(mList.size()));

        if (mList.size() == 0) {
            //no data - add 'null' to fetch R.layout.alarms_empty_cell
            mList.add(AlarmModel.EMPTY);
        }

        if (mRecycler != null) {
            mAdapter = new Adapter(getActivity(), mList);
            mRecycler.swapAdapter(mAdapter, false);
        }

        updateNext();
    }

    private void updateNext() {
        if (!Data.activeModelInList(mList)) {
            mNextDate.setText("Nothing Scheduled");

            mNextTime.setText("");
        }
        else {
            long millis = mAlarmDAO.scheduledMillis();
            mNextDate.setText(Format.formatDate(millis));
            mNextTime.setText(Format.formatTime(millis));
        }
    }

    public void toggleAlarm(AlarmModel model) {
        Log.d("Launched", "toggleAlarm()");
        mAlarmDAO.updateModel(model);
        mRecycler.swapAdapter( new Adapter(getActivity(), mAlarmDAO.getModelsAsList()), false );
        updateNext();
    }
}