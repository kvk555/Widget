package by.kononovich.vidgettest;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by user on 13.10.2016.
 */

public class NotificationReceiver extends BroadcastReceiver {

    private int mAppWidgetId;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(NewAppWidget.EXTRA)) {

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(android.R.drawable.stat_notify_chat);
            builder.setContentTitle("Изменить картинку?");
            builder.setContentText("Выберите способ");

            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
            }

            Intent buttonNext = new Intent(context, NewAppWidget.class);
            String getUri = intent.getExtras().getString(NewAppWidget.URI);
            int nextPosition;
            if (Arrays.asList(MyAdapter.imageUrls).indexOf(getUri) == MyAdapter.imageUrls.length - 1) {
                nextPosition = 0;
                Toast.makeText(context, "Это была последня картинка!", Toast.LENGTH_LONG).show();
            } else {
                nextPosition = Arrays.asList(MyAdapter.imageUrls).indexOf(getUri) + 1;
            }
            String nextUri = MyAdapter.imageUrls[nextPosition];
            buttonNext.setAction(NewAppWidget.UPDATE);
            buttonNext.putExtra(NewAppWidget.GET_URI, nextUri);
            buttonNext.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 1, buttonNext, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(android.R.drawable.ic_popup_sync, "Next", pendingIntentNext);

            Intent buttonRandom = new Intent(context, NewAppWidget.class);
            int randomPosition = (int) (Math.random() * MyAdapter.imageUrls.length + 1);
            String randomUri = MyAdapter.imageUrls[randomPosition];
            buttonRandom.setAction(NewAppWidget.UPDATE);
            buttonRandom.putExtra(NewAppWidget.GET_URI, randomUri);
            buttonRandom.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            PendingIntent pendingIntentRandom = PendingIntent.getBroadcast(context, 2, buttonRandom, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(android.R.drawable.btn_star, "Random", pendingIntentRandom);

            notificationManagerCompat.notify(NewAppWidget.NOTIFICATION_ID, builder.build());
        }
    }
}
