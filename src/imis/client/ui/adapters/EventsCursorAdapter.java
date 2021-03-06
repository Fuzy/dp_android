package imis.client.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.model.Event;

import java.util.Map;

/**
 *  Adapter which makes accessible events obtained from cursor.
 */
public class EventsCursorAdapter extends CursorAdapter {
    private static final String TAG = EventsCursorAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Map<String, String> codes;

    public EventsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        codes = AppUtil.getCodes(context);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        Log.d(TAG, "newView()");
        return inflater.inflate(R.layout.event_row, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(TAG, "bindView()");
        Event event = Event.cursorToEvent(cursor);
        String description = codes.get(event.getKod_po());
        TextView tt = (TextView) view.findViewById(R.id.event_type);
        String type = event.getDruh() + " " + description;
        tt.setText(type);
        tt = (TextView) view.findViewById(R.id.event_time);
        String time = TimeUtil.formatEmpDate(event.getDatum())
                + " " + TimeUtil.formatTimeInNonLimitHour(event.getCas());
        tt.setText(time);

        LayerDrawable buttonDrawable;
        if (event.isError()) {
            buttonDrawable = (LayerDrawable) context.getResources().getDrawable(
                    R.drawable.event_item_error);
        } else if (event.isDirty()) {
            buttonDrawable = (LayerDrawable) context.getResources().getDrawable(
                    R.drawable.event_item_dirty);
        } else {
            buttonDrawable = (LayerDrawable) context.getResources().getDrawable(
                    R.drawable.event_item_not_dirty);
        }
        view.setBackground(buttonDrawable);

    }

    @Override
    public Event getItem(int position) {
        return Event.cursorToEvent((Cursor) super.getItem(position));
    }
}
