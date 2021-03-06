package imis.client.ui.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import imis.client.model.Event;
import imis.client.ui.activities.DayTimelineActivity;
import imis.client.ui.adapters.EventsCursorAdapter;

/**
 * Fragment showing list of attendance events.
 */
public class DayTimelineListFragment extends ListFragment {
    private static final String TAG = DayTimelineListFragment.class.getSimpleName();

    private DayTimelineActivity mActivity;
    private EventsCursorAdapter adapter;

    public DayTimelineListFragment() {
        Log.d(TAG, "DayTimelineListFragment()");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach()");
        mActivity = (DayTimelineActivity) activity;
        mActivity.registerDataSetObserver(mObserver);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new EventsCursorAdapter(mActivity, null, -1);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick()");
        startEditActivity(position);
    }

    private void startEditActivity(int position) {
        Event actEvent, scndEvent = null;
        int arriveId = -1, leaveId = -1;
        actEvent = adapter.getItem(position);
        Cursor cursor = adapter.getCursor();
        if (actEvent.isDruhArrival()) {
            arriveId = actEvent.get_id();
            scndEvent = mActivity.getProcessor().getNextEvent(cursor, Event.DRUH_LEAVE);
            if (scndEvent != null) leaveId = scndEvent.get_id();
        } else if (actEvent.isDruhLeave()) {
            leaveId = actEvent.get_id();
            scndEvent = mActivity.getProcessor().getPrevEvent(cursor, Event.DRUH_ARRIVAL);
            if (scndEvent != null) arriveId = scndEvent.get_id();
        }
        mActivity.startEditActivity(arriveId, leaveId);
    }

    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            adapter.swapCursor(mActivity.getCursor());
        }
    };
}
