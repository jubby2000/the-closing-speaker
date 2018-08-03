package com.the_closing_speaker;

/**
 * Created by jacob on 5/16/16.
 */

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

public class QuoteWidgetService extends RemoteViewsService
{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return (new QuoteWidgetRemoteViewsFactory(this.getApplicationContext(), intent));
    }


}