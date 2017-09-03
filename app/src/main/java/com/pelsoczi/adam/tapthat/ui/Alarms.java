package com.pelsoczi.adam.tapthat.ui;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.pelsoczi.adam.tapthat.AlarmViewModel;
import com.pelsoczi.adam.tapthat.AlarmsActivity;
import com.pelsoczi.adam.tapthat.MyApplication;
import com.pelsoczi.adam.tapthat.R;
import com.pelsoczi.adam.tapthat.data.AlarmDAO;
import com.pelsoczi.adam.tapthat.model.AlarmModel;
import com.pelsoczi.adam.tapthat.util.Data;
import com.pelsoczi.adam.tapthat.util.Format;
import com.pelsoczi.adam.tapthat.util.Views;
import com.pelsoczi.data.Alarm;

import java.util.ArrayList;
import java.util.List;

public class Alarms extends Fragment {
    public static final String NAME = Alarms.class.getSimpleName();
    private static final String LOG_NAME = Alarms.class.getSimpleName();

    private RecyclerView mRecycler;
    private Adapter mAdapter;
    private FloatingActionButton mFAB;
    private AlarmDAO mAlarmDAO;
    private List<AlarmModel> mList;

    /**
     * Provides animation capabilities
     */
    private TextSwitcher mSwitcherDate;
    private TextSwitcher mSwitcherTime;

    /**
     * Supplies TextView's to TextSwitcher
     */
    private ViewFactory mFactory = new ViewFactory() {
        @Override
        public View makeView() {
            TextView t = new TextView(getContext());
            t.setTextAppearance(R.style.TextAppearance_AppCompat_Medium);
            return t;
        }
    };

    /**
     * Called to have the fragment instantiate it's user interface view.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarms_fragment, container, false);
    }

    /**
     * Called before any saved state has been restored, the fragments view hierarchy is not
     * attached to its parent yet.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Log.d(LOG_NAME, "onViewCreated()");

        setHasOptionsMenu(true);
        Context context = view.getContext();

        mSwitcherDate = (TextSwitcher) view.findViewById(R.id.ac_next_date);
        mSwitcherTime = (TextSwitcher) view.findViewById(R.id.ac_next_time);
        mSwitcherDate.setFactory(mFactory);
        mSwitcherTime.setFactory(mFactory);
        Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        mSwitcherDate.setInAnimation(in);
        mSwitcherTime.setInAnimation(in);
        mSwitcherDate.setOutAnimation(out);
        mSwitcherTime.setOutAnimation(out);

        // load any changes
        update();

        mAdapter = new Adapter(getActivity(), mList);
        mRecycler = (RecyclerView) view.findViewById(R.id.alarm_container_recycler);
        mRecycler.setHasFixedSize(false);
        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Fragment is visible to the user
     */
    @Override
    public void onResume() {
        super.onResume();
//        Log.d(LOG_NAME, "onResume()");

        Activity activity = getActivity();
        if (!Views.isActivityNull(activity)) {
            mFAB = (FloatingActionButton) activity.findViewById(R.id.floating_action_btn);
            mFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create alarm model and pass it around
                    ((AlarmsActivity) getActivity()).onAlarmClick(AlarmModel.EMPTY);
                }
            });
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlarmViewModel viewModel = MyApplication.getInstance().getAlarmViewModel();
                        LiveData<List<Alarm>> liveData =
                                MyApplication.getInstance().getAlarmViewModel().getliveAlarmData();
//
                        List<Alarm> inMemory = new ArrayList<Alarm>();
                        viewModel.populateRoom(new ArrayList<Alarm>() {{
                            add(new Alarm(0));
                            add(new Alarm(1));
                        }});



                    }
                });
            }
        }, 1000);
    }

    /**
     * <p>Initialize a new instance of Data Access Object, forcing an update to data set.</p>
     * Called by {@link Alarms#onViewCreated(View, Bundle)} and
     * {@link AlarmsActivity#onEditUpdate()}
     */
    public void update() {
        mAlarmDAO = new AlarmDAO();
        mList = mAlarmDAO.getModelsAsList();
//        Log.d(LOG_NAME, "update(), modelList.size: " + String.valueOf(mList.size()));

        //no data - add 'null' to later fetch R.layout.alarms_empty_cell
        if (mList.size() == 0) {
            mList.add(AlarmModel.EMPTY);
        }

        // won't be applied to first run
        if (mRecycler != null) {
            mAdapter = new Adapter(getActivity(), mList);
            mRecycler.swapAdapter(mAdapter, false);
        }

        updateNext();
    }

    /**
     * Updates data and swaps Adapter while allowing for ViewHolder animations
     */
    public void toggleAlarm(AlarmModel model) {
//        Log.d(LOG_NAME, "toggleAlarm()");

        mAlarmDAO.updateModel(model);
        mRecycler.swapAdapter(new Adapter(getActivity(), mAlarmDAO.getModelsAsList()), false);
        updateNext();
    }

    /**
     * Animates TextView which displays the next alarm which will ring
     */
    private void updateNext() {
        if (!Data.activeModelInList(mList)) {
            mSwitcherDate.setText(getString(R.string.title_next_empty));
            mSwitcherTime.setText(getResources().getString(R.string.empty));
        } else {
            long millis = mAlarmDAO.scheduledMillis();
            mSwitcherDate.setText(Format.formatDate(millis));
            mSwitcherTime.setText(Format.formatTime(millis));
        }
    }
}