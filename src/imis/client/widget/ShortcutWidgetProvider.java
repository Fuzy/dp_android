package imis.client.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import imis.client.AppConsts;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Event;
import imis.client.persistent.EventManager;
import imis.client.ui.ColorUtil;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 27.5.13
 * Time: 19:51
 */
public class ShortcutWidgetProvider extends AppWidgetProvider {
    private static final String TAG = ShortcutWidgetProvider.class.getSimpleName();
    //TODO init color util

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "onUpdate() appWidgetIds " + Arrays.toString(appWidgetIds));

        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.shortcut_widget);

        Event lastEvent = EventManager.getLastEvent(context);
        Log.d(TAG, "updateAppWidget() lastEvent " + lastEvent);
        views.setInt(R.id.emp_kod_po, "setBackgroundColor", ColorUtil.getColor(lastEvent.getKod_po()));
        String last = lastEvent.getDruh() + " " + AppUtil.formatEmpDate(lastEvent.getDatum() + lastEvent.getCas());
        views.setTextViewText(R.id.emp_time, last);

        // Register an onClickListener
        Intent intent = new Intent(context, ShortcutWidgetReceiver.class);

        intent.setAction(AppConsts.SHORTCUT_ADD_ACTION);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_add_event, pendingIntent);


        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}