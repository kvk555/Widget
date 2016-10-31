package by.kononovich.vidgettest;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Arrays;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link NewAppWidgetConfigureActivity NewAppWidgetConfigureActivity}
 */
public class NewAppWidget extends AppWidgetProvider {

    public final static String EXTRA = "by.kononovich.vidgettest.ANY_NAME";
    public final static String UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
    public final static String URI = "uri";
    public final static String GET_URI = "from_broadcast";
    public final static int NOTIFICATION_ID = 111;

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, SharedPreferences sp, int widgetID) {

        String uriWidget = sp.getString(NewAppWidgetConfigureActivity.WIDGET_PICTURE + widgetID, null);
        if (uriWidget == null) {
            return;
        }
        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        Picasso.with(context).load(uriWidget).into(widgetView, R.id.widget_picture, new int[]{widgetID});
        final Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(EXTRA);
        intent.putExtra(URI, uriWidget);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        widgetView.setOnClickPendingIntent(R.id.widget_picture, pendingIntent);

        appWidgetManager.updateAppWidget(widgetID, widgetView);
        Toast.makeText(context, Arrays.asList(MyAdapter.imageUrls).indexOf(uriWidget) + " ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        SharedPreferences sp = context.getSharedPreferences(NewAppWidgetConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, sp, id);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // Удаляем Preferences
        SharedPreferences.Editor editor = context.getSharedPreferences(
                NewAppWidgetConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(NewAppWidgetConfigureActivity.WIDGET_PICTURE + widgetID);
        }
        editor.apply();
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        if (intent.getAction().equals(NewAppWidget.UPDATE)) {
            // извлекаем ID экземпляра
            int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

            }
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {

                String uriNotification = intent.getExtras().getString(GET_URI);

                SharedPreferences sp = context.getSharedPreferences(
                        NewAppWidgetConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(NewAppWidgetConfigureActivity.WIDGET_PICTURE + mAppWidgetId, uriNotification);
                editor.apply();
                // Обновляем виджет
                updateWidget(context, AppWidgetManager.getInstance(context), sp, mAppWidgetId);
            }
            notificationManagerCompat.cancel(NOTIFICATION_ID);
        }
    }
}

