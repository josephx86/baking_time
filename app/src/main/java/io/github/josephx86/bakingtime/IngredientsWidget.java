package io.github.josephx86.bakingtime;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.HashMap;

import io.github.josephx86.bakingtime.data.RecipeEntryColumns;
import io.github.josephx86.bakingtime.data.RecipesProvider;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidget extends AppWidgetProvider {
    private static Recipe recipe;

    public static final String ACTION_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        if (recipe != null) {
            widgetText = recipe.getName() + " Ingredients\n\n"
                    + recipe.getIngredientsString().replace("\n\n", "\n");
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Project specs do not require widget to open app
        /*
        if (recipe != null) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            String key = context.getString(R.string.recipe_parcelable_key);
            mainActivityIntent.putExtra(key, recipe);

            PendingIntent instructionsIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);

            views.setOnClickPendingIntent(R.id.appwidget_text, instructionsIntent);
        }
        */

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Get the ingredients
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String key = context.getString(R.string.recipe_parcelable_key);
            if (extras.containsKey(key)) {
                recipe = extras.getParcelable(key);

                // Update widgets
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName componentName = new ComponentName(context, IngredientsWidget.class);
                int[] ids = appWidgetManager.getAppWidgetIds(componentName);
                onUpdate(context, appWidgetManager, ids);
            }
        }
    }
}

