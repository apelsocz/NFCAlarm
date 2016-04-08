package com.example.adam.nfcalarm.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import com.example.adam.nfcalarm.AlarmsActivity;
import com.example.adam.nfcalarm.R;
import com.example.adam.nfcalarm.model.AlarmModel;
import com.example.adam.nfcalarm.util.Views;
import java.util.Collections;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    private final LayoutInflater layoutInflater;
    private final List<AlarmModel> items;
    //cache for quicker response
    private final int itemsSize;

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
            );*/

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

    public Adapter(@NonNull Context context, List<AlarmModel> items) {
        layoutInflater = LayoutInflater.from(context);
        setHasStableIds(true);
        this.items = items != null ? items : Collections.<AlarmModel> emptyList();
        itemsSize = this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(viewType, parent, false);

        boolean vTypeIsEmptyCell = viewType == R.layout.alarms_empty_cell;

        if (viewType == R.layout.alarms_empty_cell) {
            return new ViewHolder(view) {};
        }
        else {
            return new CellViewHolder(view);
        }
        //for empty cell use ViewHolder otherwise CellViewHolder
/*            return R.layout.alarms_empty_cell == viewType ? new ViewHolder(view) {}
                    : new CellViewHolder(view);*/
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CellViewHolder) {
            ((CellViewHolder) holder).update(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return itemsSize;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}