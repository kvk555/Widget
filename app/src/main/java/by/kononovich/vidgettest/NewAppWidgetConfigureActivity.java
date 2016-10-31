package by.kononovich.vidgettest;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;

/**
 * The configuration screen for the {@link NewAppWidget NewAppWidget} AppWidget.
 */
public class NewAppWidgetConfigureActivity extends Activity implements RecyclerItemClickSupport.OnItemClickListener {

    private MyAdapter myAdapter;
    public final static String WIDGET_PREF = "widget_pref";
    public final static String WIDGET_PICTURE = "widget_picture";
    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // извлекаем ID конфигурируемого виджета
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // и проверяем его корректность
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // формируем intent ответа
        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

        // отрицательный ответ
        setResult(RESULT_CANCELED, resultValue);

        setContentView(R.layout.new_app_widget_configure);
        RecyclerView listView = (RecyclerView) findViewById(R.id.list_view);
        listView.setLayoutManager(new GridLayoutManager(this, 2));
        listView.setHasFixedSize(true);

        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        RecyclerItemClickSupport.addTo(listView).setOnItemClickListener(this);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        String uriPicture = MyAdapter.imageUrls[position];
        SharedPreferences sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(WIDGET_PICTURE + widgetID, uriPicture);
        editor.apply();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        NewAppWidget.updateWidget(this, appWidgetManager, sp, widgetID);

        // положительный ответ
        setResult(RESULT_OK, resultValue);

        finish();
    }
}



