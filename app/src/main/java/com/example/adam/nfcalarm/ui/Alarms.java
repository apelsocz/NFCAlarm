package com.example.adam.nfcalarm.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.adam.nfcalarm.ApplicationActivity;
import com.example.adam.nfcalarm.R;
import com.example.adam.nfcalarm.data.AlarmDataManager;
//import com.example.adam.nfcalarm.model.AlarmData;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.util.Views;

import java.util.Collections;
import java.util.List;

/**
 * Created by adam on 15-12-26.
 */
public class Alarms extends Fragment {
    public static final String NAME = Alarms.class.getSimpleName();

    private static final class CellViewHolder extends ViewHolder implements View.OnClickListener {

        private final TextView time;
        private final Switch isActive;

        private AlarmModel model;

        private CellViewHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.alarms_time);
            isActive = (Switch) view.findViewById(R.id.alarms_active);
        }

        private void update(AlarmModel model) {
            if (model == null || model.isEmpty) {
                return;
            }
            this.model = model;

            itemView.setOnClickListener(this);

            Context context = this.itemView.getContext();

            time.setText(model.hour + ":" + model.minute);
/*            if (DateFormat.is24HourFormat(context)) {
                time.setText(model.hour + ":" + model.minute);
            }
            else {
                //// TODO: 16-01-03
                ////    update this area to format per user locale
                int hour = Integer.parseInt(this.model.hour);
                hour = hour > 12 ? hour - 12 : hour;
                time.setText(hour +":"+ model.minute);
            }*/
            isActive.setChecked(model.isActive);

            isActive.setOnClickListener(this);
//            isActive.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(final View view) {
            ApplicationActivity activity = (ApplicationActivity) view.getContext();
            if (!Views.isActivityNull(activity)) {
                //create shallow copy of the model and pass it around
                if (isActive.equals(view)) {
//                    ((Switch)view).setChecked(true);
//                    activity.onActiveToggle();
//                    AlarmModel currentModel = new AlarmModel(!model.isActive, model.hour, model.minute);
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
        public ViewHolder onCreateViewHolder(final ViewGroup recyclerView, final int viewType) {
            View view = layoutInflater.inflate(viewType, recyclerView, false);

            //for empty cell use ViewHolder otherwise CellViewHolder
            return R.layout.alarms_empty_cell == viewType ? new ViewHolder(view) {
            } : new CellViewHolder(view);
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
        public int getItemViewType(final int position) {
            // if model is null and only one item, we treat this one as no data - will be used
            // to load R.layout.contacts_empty_cell. Otherwise regular layout
            AlarmModel model = items.get(position);
            return itemsSize == 1 && model.isEmpty ? R.layout.alarms_empty_cell : R.layout.alarms_cell;
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }
    }

    private RecyclerView list;
    private FloatingActionButton fab;
//    private AlarmData alarmData;
    private AlarmDataManager alarmManager;

    @Override
    public void onStart() {
        super.onStart();

        final Activity activity = getActivity();
        if (!Views.isActivityNull(activity)) {
            fab = (FloatingActionButton) activity.findViewById(R.id.floating_action_btn);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, "Thank you!", Snackbar.LENGTH_SHORT)
                            .show();
                    //TODO
                    //show time set alertdialog here and validate that OK is being pressed,
                    //create alarm model and pass it around
//                    ((ApplicationActivity)activity).onAlarmClick(null);
                    ((ApplicationActivity)getActivity()).onAlarmClick(AlarmModel.EMPTY);

                    /*
                    Calendar c = Calendar.getInstance();
                    boolean is24HourFormat = DateFormat.is24HourFormat(getActivity());
                    int hour = is24HourFormat ? c.get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR);
                    int min = c.get(Calendar.MINUTE);
                    String time = hour+":"+min;

                    DialogFragment dialog = TimePickerDialogFragment.newInstance(hour, min, is24HourFormat);
                    dialog.show(getFragmentManager(), TimePickerDialogFragment.NAME);
                    */
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarms, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alarmManager = AlarmDataManager.getInstance();

        /*
//        alarmData = new AlarmData(getActivity());
        alarmData = new AlarmData(getActivity().getApplicationContext());
        Activity appActivity = ((ApplicationActivity)getActivity()).getApplicationActivity();
        alarmData.setApplicationActivity(appActivity);
        */

        list = (RecyclerView) view;
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.setItemAnimator(new DefaultItemAnimator());

        setHasOptionsMenu(true);

        update();
    }

    public void update() {
        Activity activity = getActivity();

        if (!Views.isActivityNull(activity)) {
//            List<AlarmModel> modelList = alarmData.toList();
            List<AlarmModel> modelList = alarmManager.getAlarmsList();

            if (modelList.size() == 0) {
                //no data - add 'null' to fetch R.layout.alarms_empty_cell
                modelList.add(AlarmModel.EMPTY);
            }

            list.setAdapter(new Adapter(activity, modelList));
        }
    }

    public void toggleAlarm(AlarmModel model, int position) {
        Log.d("Launched", "toggleAlarm()");
        /*
//        Activity activity = getActivity();

        JSONArray alarms = alarmData.toJSONArray();
        try {
            alarms.put(position, model.json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        alarmData.setAlarms(alarms);
        list.swapAdapter(new Adapter(activity, alarmData.toList()), false);
        */
        Activity activity = getActivity();
        if (!Views.isActivityNull(activity)) {
            alarmManager.doIndexUpdate(model, position);
            list.swapAdapter(
                    new Adapter(activity, alarmManager.getAlarmsList()),
                    false
            );
        }
    }
}