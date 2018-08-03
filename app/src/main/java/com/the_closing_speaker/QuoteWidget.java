package com.the_closing_speaker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class QuoteWidget extends AppWidgetProvider {

    private static final String LOG_TAG = QuoteWidget.class.getSimpleName();
    public static String REFRESH_QUOTE_ACTION = "REFRESH_QUOTE_ACTION";

//    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
//                                       int appWidgetId) {
//
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quote_widget);
//
//        Intent refreshIntent = new Intent(context, QuoteWidget.class);
//        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                appWidgetId);
//        refreshIntent.setAction(REFRESH_QUOTE_ACTION);
//        PendingIntent pendingIntent = PendingIntent
//                .getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        views.setOnClickPendingIntent(R.id.appwidget_refresh_button, pendingIntent);
//
//        Intent intent = new Intent(context, QuoteWidgetService.class);
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                appWidgetId);
//        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//        views.setRemoteAdapter(R.id.appwidget_listview, intent);
//
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
//    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quote_widget);
        ComponentName watchWidget = new ComponentName(context, QuoteWidget.class);
        Intent intent = new Intent(context, QuoteWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetIds);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        Intent refreshIntent = new Intent(context, QuoteWidget.class);
        refreshIntent.setAction(REFRESH_QUOTE_ACTION);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_refresh_button, pendingIntent);
//        appWidgetManager.updateAppWidget(watchWidget, views);
        views.setRemoteAdapter(R.id.appwidget_listview, intent);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(watchWidget, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //TODO fix this so that the refresh button works on the widget
        if(intent.getAction().equals(REFRESH_QUOTE_ACTION)) {
            Log.v(LOG_TAG, "refresh quote action called");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quote_widget);
            ComponentName watchWidget = new ComponentName(context, QuoteWidget.class);

            Intent newIntent = new Intent(context, QuoteWidgetService.class);
//            newIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                    appWidgetIds);
            newIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.appwidget_listview, newIntent);

            appWidgetManager.updateAppWidget(watchWidget, views);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


