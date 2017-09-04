package com.pelsoczi.adam.tapthat.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.pelsoczi.adam.tapthat.R;
import com.pelsoczi.adam.tapthat.kotlin.ui.AlarmActivity;
import com.pelsoczi.adam.tapthat.util.Format;
import com.pelsoczi.adam.tapthat.util.Views;
import com.pelsoczi.data.Alarm;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    private final LayoutInflater layoutInflater;
    private final List<Alarm> items;
    //cache for quicker response
    private final int itemsSize;

    private static final class CellViewHolder extends ViewHolder implements View.OnClickListener {

        /** model representing the ViewHolder's data */
        private Alarm model;

        private final TextView time;
        private final TextView schedule;
        private final ImageView icon;
        private final Switch isActive;

        private CellViewHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.alarms_time);
            schedule = (TextView) view.findViewById(R.id.alarms_repeat);
            icon = (ImageView) view.findViewById(R.id.alarms_icon);
            isActive = (Switch) view.findViewById(R.id.alarms_active);
        }

        private void update(Alarm model) {
            //todo model isEmpty
            
            this.model = model;
            itemView.setOnClickListener(this);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(0L);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(model.getHour()));
            calendar.set(Calendar.MINUTE, Integer.valueOf(model.getMinute()));

            time.setText(Format.formatTime(calendar.getTimeInMillis()));

            String days = "";
            if (model.getSunday() && model.getMonday() && model.getTuesday() && model.getWednesday()
                    && model.getThursday() && model.getFriday() && model.getSaturday()) {
                days = "Daily";
            }
            else if (!model.getSunday() && model.getMonday() && model.getTuesday() && model.getWednesday()
                    && model.getThursday() && model.getFriday() && !model.getSaturday()) {
                days = "Weekdays";
            }
            else if (model.getSunday() && !model.getMonday() && !model.getTuesday() && !model.getWednesday()
                    && !model.getThursday() && !model.getFriday() && model.getSaturday()) {
                days = "Weekends";
            }
            else {
                if (model.getOnce()) {
                    days = "Once";
                }
                else {
                    if (model.getSunday()) {
                        days += "Sun, ";
                    }
                    if (model.getMonday()) {
                        days += "Mon, ";
                    }
                    if (model.getTuesday()) {
                        days += "Tue, ";
                    }
                    if (model.getWednesday()) {
                        days += "Wed, ";
                    }
                    if (model.getThursday()) {
                        days += "Thu, ";
                    }
                    if (model.getFriday()) {
                        days += "Fri, ";
                    }
                    if (model.getSaturday()) {
                        days += "Sat";
                    }

                    if (days.endsWith(", ")) {
                        days = days.substring(0, days.length() - 2);
                    }
                }
            }
            schedule.setText(days);

            Context context = itemView.getContext();
            int id = model.getActive() ? R.drawable.ic_alarm_on_white_48dp :
                    R.drawable.ic_alarm_off_white_48dp;
            icon.setImageDrawable(ContextCompat.getDrawable(context, id));

            isActive.setChecked(model.getActive());
            isActive.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            AlarmActivity activity = (AlarmActivity) view.getContext();
            if (!Views.isActivityNull(activity)) {
                //create shallow copy of the model and pass it around
                if (isActive.equals(view)) {
                    Alarm currentModel = new Alarm(model.getId(), model.getHour(), model.getMinute(),
                            model.getActive(), model.getOnce(), model.getSunday(), model.getMonday(),
                            model.getTuesday(), model.getWednesday(), model.getThursday(),
                            model.getFriday(), model.getSaturday());
                    update(currentModel);
                    activity.onActiveToggle(model);
                }
                else {
                    activity.onAlarmClick(model);
                }
            }
        }
    }

    public Adapter(@NonNull Context context, List<Alarm> items) {
        layoutInflater = LayoutInflater.from(context);
        setHasStableIds(true);
        this.items = items != null ? items : Collections.<Alarm> emptyList();
        itemsSize = this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        // if model is null and only one item, we treat this one as no data - will be used
        // to load R.layout.contacts_empty_cell. Otherwise regular layout.
        Alarm model = items.get(position);
        int vType = model.equals(Alarm.Companion.getEMPTY()) ?
                R.layout.alarms_empty_cell : R.layout.alarms_cell;

        return vType;
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