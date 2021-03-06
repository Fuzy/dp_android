package imis.client.processor;

import android.content.Context;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.TimeUtil;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.PieChartSerie;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Record;
import imis.client.ui.ColorConfig;
import imis.client.ui.activities.ControlActivity;

import java.util.*;

/**
 * Contains methods for processing set of event records for use in charts and statistics.
 */
public class RecordsProcessor {
    private static final String TAG = RecordsProcessor.class.getSimpleName();

    private Context context;

    public RecordsProcessor(Context context) {
        this.context = context;
    }

    public String[] recordsCodesInRecords(List<Record> records) {
        Set<String> codes = new HashSet<>();
        for (Record record : records) {
            codes.add(record.recordType());
        }
        return codes.toArray(new String[]{});
    }

    public StackedBarChartData countRecordsStackedBarChartData(List<Record> records, List<String> codes, Map<String, String> selectionArgs) {
        StackedBarChartData chartData = new StackedBarChartData();
        // count mix/ max

        chartData.setMinDay(Long.valueOf(selectionArgs.get(ControlActivity.PAR_FROM)));
        chartData.setMaxDay(Long.valueOf(selectionArgs.get(ControlActivity.PAR_TO)));

        final int numOfDays = (int) ((chartData.getMaxDay() - chartData.getMinDay()) / AppConsts.MS_IN_DAY) + 1;
        Log.d(TAG, "countRecordsStackedBarChartData() numOfDays " + numOfDays);

        Map<String, double[]> map = new HashMap<>();
        String type;
        for (Record record : records) {
            type = record.recordType();
            if (codes.contains(type) == false) continue; // Exclude not checked in checkbox
            int index = (int) ((record.getDatum() - chartData.getMinDay()) / AppConsts.MS_IN_DAY);
            double amount = (double) record.getMnozstvi_odved() / (double) AppConsts.MS_IN_HOUR;

            // If not exists yet, create new array
            boolean contains = map.containsKey(type);
            if (contains == false) {
                double[] values = new double[numOfDays];
                map.put(type, values);
            }

            // Update value
            double[] vaDoubles = map.get(type);
            double oldValue = vaDoubles[index];
            vaDoubles[index] += oldValue + amount;
            if (vaDoubles[index] > chartData.getyMax()) chartData.setyMax(vaDoubles[index]);
        }

        int size = map.size();
        int ind = 0;
        List<double[]> values = new ArrayList<>(size);
        String[] titles = new String[size];
        int[] colors = new int[size];

        for (Map.Entry<String, double[]> stringEntry : map.entrySet()) {
            Log.d(TAG, "countEventsStackedBarChartData() " + Arrays.toString(stringEntry.getValue()));
            values.add(stringEntry.getValue());
            titles[ind] = stringEntry.getKey();
            colors[ind] = ColorConfig.getColor(context, stringEntry.getKey());
            ind++;
        }

        chartData.setValues(values);
        chartData.setTitles(titles);
        chartData.setColors(colors);

        Log.d(TAG, "countRecordsStackedBarChartData() titles " + Arrays.toString(titles));
        for (double[] value : values) {
            Log.d(TAG, "countRecordsStackedBarChartData() value " + Arrays.toString(value));
        }
        Log.d(TAG, "countRecordsStackedBarChartData() min "
                + TimeUtil.formatAbbrDate(chartData.getMinDay()) + " max" + TimeUtil.formatAbbrDate(chartData.getMaxDay()));

        return chartData;
    }

    public PieChartData countRecordsPieChartData(List<Record> records, List<String> codes) {
        Log.d(TAG, "countRecordsPieChartData()");
        PieChartData pieChartData = new PieChartData();
        long total = 0L;

        Map<String, Long> statistics = new HashMap<>();
        for (Record record : records) {
            if (codes.contains(record.recordType()) == false) continue; // Exclude not checked in checkbox
            long amount = record.getMnozstvi_odved();
            total += amount;
            long count = statistics.containsKey(record.recordType()) ? statistics.get(record.recordType()) : 0;
            statistics.put(record.recordType(), count + amount);
        }

        long value;
        PieChartSerie serie;
        for (Map.Entry<String, Long> entry : statistics.entrySet()) {
            value = entry.getValue();
            double amount = (double) value / (double) AppConsts.MS_IN_HOUR;
            serie = new PieChartSerie(entry.getKey(), amount);
            serie.setColor(ColorConfig.getColor(context, entry.getKey()));
            serie.setTime(TimeUtil.formatTimeInNonLimitHour(value));
            int percent = (int) (((double) value / (double) total) * 100);
            serie.setPercent(percent);
            pieChartData.addSerie(serie);
        }

        pieChartData.setTotal(TimeUtil.formatTimeInNonLimitHour(total));
        Log.d(TAG, "countRecordsPieChartData() pieChartData " + pieChartData);
        return pieChartData;
    }
}
