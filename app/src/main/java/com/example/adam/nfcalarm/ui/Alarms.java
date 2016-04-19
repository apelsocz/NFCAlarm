package com.example.adam.nfcalarm.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.example.adam.nfcalarm.data.AlarmDataManager;
//import com.example.adam.nfcalarm.model.AlarmData;
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
    private AlarmDataManager alarmManager;
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
    public void onStart() {
        super.onStart();
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

    private void updateNext(){
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
/*
    private static final class CellViewHolder extends ViewHolder implements View.OnClickListener {

        private final TextView time;
        private final TextView repeat;
        private final ImageView icon;
        private final Switch isActive;

        private AlarmModel model;

        private CellViewHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.alarms_time);
            repeat = (TextView) view.findViewById(R.id.alarms_repeat);
            icon = (ImageView) view.findViewById(R.id.alarms_icon);
            isActive = (Switch) view.findViewById(R.id.alarms_active);
        }

        private void update(AlarmModel model) {
            if (model == null || model.isEmpty) {
                return;
            }
            this.model = model;

            itemView.setOnClickListener(this);

            time.setText(model.hour + ":" + model.minute);
*/
/*            if (DateFormat.is24HourFormat(context)) {
                time.setText(model.hour + ":" + model.minute);
            }
            else {
                //// TODO: 16-01-03
                ////    update this area to format per user locale
                int hour = Integer.parseInt(this.model.hour);
                hour = hour > 12 ? hour - 12 : hour;
                time.setText(hour +":"+ model.minute);
            }*//*


*/
/*            String days = "";
            if (model.sunday && model.monday && model.tuesday && model.wednesday && model.thursday && model.friday && model.saturday){
                days = "Daily";
            }
            else if (model.monday && model.tuesday && model.wednesday && model.thursday && model.friday) {
                days = "Weekdays";
            }
            else if (model.sunday && model.saturday) {
                days = "Weekends";
            }
            else {
                if (model.once) {
                    days = "Once";
                }
                else {
                    if (model.sunday) {
                        days.concat("Sun, ");
                    }
                    if (model.monday) {
                        days.concat("Mon, ");
                    }
                    if (model.tuesday) {
                        days.concat("Tue, ");
                    }
                    if (model.wednesday) {
                        days.concat("Wed, ");
                    }
                    if (model.thursday) {
                        days.concat("Thu, ");
                    }
                    if (model.friday) {
                        days.concat("Fri, ");
                    }
                    if (model.saturday) {
                        days.concat("Sat");
                    }

                    if (days.endsWith(", ")) {
                        days.substring(0, days.length() - 2);
                    }
                }
            }
            repeat.setText(days);

            icon.setImageDrawable( model.isActive ?
                    ContextCompat.getDrawable(this.itemView.getContext(), R.drawable.ic_alarm_on_white_48dp) :
                    ContextCompat.getDrawable(this.itemView.getContext(), R.drawable.ic_alarm_off_white_48dp)
            );*//*


            isActive.setChecked(model.isActive);
            isActive.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            AlarmsActivity activity = (AlarmsActivity) view.getContext();
            if (!Views.isActivityNull(activity)) {
                //create shallow copy of the model and pass it around
                if (isActive.equals(view)) {
                    AlarmModel currentModel = new AlarmModel(model.uniqueID, !model.isActive,
                            model.hour, model.minute, model.once, model.sunday, model.monday,
                            model.tuesday, model.wednesday, model.thursday, model.friday,
                            model.saturday);
                    update(currentModel);
                    activity.onActiveToggle(currentModel, CellViewHolder.this.getAdapterPosition());
                }
                else {
                    activity.onAlarmClick(model);
                }
            }
        }
    }

    private static final class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private final LayoutInflater layoutInflater;
        private final List<AlarmModel> items;
        //cache for quicker response
        private final int itemsSize;

        private Adapter(@NonNull Context context, List<AlarmModel> items) {
            layoutInflater = LayoutInflater.from(context);
            setHasStableIds(true);
            this.items = items != null ? items : Collections.<AlarmModel>emptyList();
            itemsSize = this.items.size();
        }

        @Override
        public int getItemViewType(final int position) {
            // if model is null and only one item, we treat this one as no data - will be used
            // to load R.layout.contacts_empty_cell. Otherwise regular layout
            //// TODO: 16-04-05 investigate cause of data showing up
            AlarmModel model = items.get(position);
//            return itemsSize == 1 && model.isEmpty ? R.layout.alarms_empty_cell : R.layout.alarms_cell;
            boolean retBoolean = model.isEmpty;
            int vtype = model.isEmpty ? R.layout.alarms_empty_cell : R.layout.alarms_cell;

            int alarmsCell = R.layout.alarms_cell;
            int emptyCell = R.layout.alarms_empty_cell;

//            return model.isEmpty ? R.layout.alarms_empty_cell : R.layout.alarms_cell;
            return vtype;
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup recyclerView, final int viewType) {
            View view = layoutInflater.inflate(viewType, recyclerView, false);

            boolean vTypeIsEmptyCell = viewType == R.layout.alarms_empty_cell;

            if (viewType == R.layout.alarms_empty_cell) {
                return new ViewHolder(view) {};
            }
            else {
                return new CellViewHolder(view);
            }
            //for empty cell use ViewHolder otherwise CellViewHolder
*/
/*            return R.layout.alarms_empty_cell == viewType ? new ViewHolder(view) {}
                    : new CellViewHolder(view);*//*

        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            if (viewHolder instanceof CellViewHolder) {
                ((CellViewHolder) viewHolder).update(items.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return itemsSize;
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }
    }
*/
}