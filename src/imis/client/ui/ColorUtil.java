package imis.client.ui;

import android.graphics.Color;
import imis.client.model.Event;
import imis.client.model.Record;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 4.4.13
 * Time: 17:52
 */
public class ColorUtil {
    private static final String TAG = "ColorUtil";

    private static final Map<String, Integer> colors = new HashMap<>();


    public static Map<String, Integer> getColors() {
        return colors;
    }

    public static void setColor(String key, int value) {
        List<String> eventCodes = Arrays.asList(Event.KOD_PO_VALUES);
        List<String> recordCodes =  Arrays.asList(Record.TYPE_VALUES);
        if (eventCodes.indexOf(key) == -1 && recordCodes.indexOf(key) == -1) key = Event.KOD_PO_OTHERS;
        //Log.d(TAG, "getColor() key " + key);
        colors.put(key, value);
    }

    public static int getColor(String key) {
        if (key == null) return Color.GRAY;
        List<String> eventCodes = Arrays.asList(Event.KOD_PO_VALUES);
        List<String> recordCodes =  Arrays.asList(Record.TYPE_VALUES);
        if (eventCodes.indexOf(key) == -1 && recordCodes.indexOf(key) == -1) key = Event.KOD_PO_OTHERS;
        //Log.d(TAG, "getColor() key " + key);
        return colors.get(key);
    }


}
