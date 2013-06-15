package imis.client.ui.fragments;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import imis.client.ui.activities.DayTimelineActivity;
import imis.client.ui.adapters.EventsCursorAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.6.13
 * Time: 10:40
 */
public class DayTimelineListFragment extends ListFragment {
    private static final String TAG = DayTimelineListFragment.class.getSimpleName();
    //TODO ikonka

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
    }

    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            Log.d(TAG, "onChanged()");
            adapter.swapCursor(mActivity.getCursor());
            Log.d(TAG, "onChanged() mActivity.getCursor() " +  mActivity.getCursor().getCount());
            Log.d(TAG, "onChanged() adapter " +  adapter.getCount());
            setListAdapter(adapter);
        }
    };
}
