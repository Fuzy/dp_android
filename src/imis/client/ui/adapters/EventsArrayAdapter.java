package imis.client.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import imis.client.model.Block;
import imis.client.ui.BlockView;

import java.util.List;

/**
 *  Adapter which makes accessible events obtained from array.
 */
public class EventsArrayAdapter extends ArrayAdapter<Block> {
    private long date = 0;

    public EventsArrayAdapter(Context context, int textViewResourceId, List<Block> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Block block = getItem(position);
        BlockView blockView = new BlockView(getContext(), block.getArriveId(), block.getLeaveId(),
                block.getStartTime(), block.getEndTime(), block.getKod_po(), block.isDirty(), block.isError(), block.isPresence());
        return blockView;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDate() {
        return date;
    }
}
