package com.pelsoczi.adam.tapthat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.pelsoczi.adam.tapthat.data.AlarmDAO;
import com.pelsoczi.adam.tapthat.model.AlarmModel;
import com.pelsoczi.adam.tapthat.ui.Adapter;
import com.pelsoczi.adam.tapthat.ui.Alarms;
import com.pelsoczi.adam.tapthat.ui.Edit;

import org.json.JSONArray;

/**
 * The primary activity which controls the user's interactions.
 */
public class AlarmsActivity extends AppCompatActivity {

    private static final String LOG_TAG = AlarmsActivity.class.getSimpleName();

    private FloatingActionButton mFAB;

    /**
     * A data access object encapsulating a singleton data layer below it.
     */
    private AlarmDAO mAlarmDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_application);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mFAB = (FloatingActionButton) findViewById(R.id.floating_action_btn);
        mAlarmDAO = new AlarmDAO();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new Alarms(), Alarms.NAME)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // animate the fab back into view
        mFAB.show();
    }

    /**
     * <p>Presents the user with a {@link Edit} fragment to modify an alarm.</p>
     * Called by {@link Alarms} fragment when the user clicks the {@code FloatingActionButton},
     * and by {@link Adapter} a {@code RecyclerView.Adapter} when the user
     * clicks a {@code ViewHolder}.
     * @param model the {@link AlarmModel} being passed around.
     */
    public void onAlarmClick(AlarmModel model) {
        mFAB.hide();

        // remove previous and add new instance
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(Edit.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragmentContainer, Edit.newInstance(model), Edit.NAME)
                .addToBackStack(Edit.NAME)
                .commit();
    }

    /**
     * <p>Presents the user with a {@link Alarms} fragment to navigate to the app's root.</p>
     * Called by {@link Edit} fragment when the user is done modifying the {@link AlarmModel}
     * being passed around.
     */
    public void onEditUpdate() {
        mFAB.show();

        // remove previous and add new instance
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(Edit.NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Alarms alarms = (Alarms) fragmentManager.findFragmentByTag(Alarms.NAME);
        if (alarms != null) {
            alarms.update();
        }
    }

    /**
     * <p>Updates the {@link AlarmModel} within {@link Alarms} {@code List<>} of models.</p>
     * Called by {@link Adapter} a {@code RecyclerView.Adapter} when the alarms active state
     * is toggled by the {@code ViewHolder}'s {@code Switch}.
     * @param model the model to pass to {@link Alarms} for updating.
     */
    public void onActiveToggle(AlarmModel model) {
        Alarms alarms = (Alarms) getSupportFragmentManager().findFragmentByTag(Alarms.NAME);
        if (alarms != null) {
            alarms.toggleAlarm(model);
        }
    }

    /**
     * <p>Triggers the {@link AlarmDAO} data access object to commit the app's data.</p>
     * Called by {@link Edit} when the user commits modifications to the {@link AlarmModel}
     * received by {code Edit} fragment.
     * @param json the {@code JSONArray} representing all the alarms created by the user.
     */
    public void doAlarmsUpdate(JSONArray json) {
        mAlarmDAO.setModels(json);
    }
}