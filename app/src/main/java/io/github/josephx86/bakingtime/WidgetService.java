package io.github.josephx86.bakingtime;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class WidgetService extends IntentService {
    private static final String NAME = "Baking App Widget Service";

    public WidgetService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Get the recipe
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String key = getString(R.string.recipe_parcelable_key);
                if (extras.containsKey(key)) {
                    Recipe recipe = extras.getParcelable(key);
                    if (recipe != null) {
                        // Broadcast so that widgets will receive.
                        Intent broadcastIntent = new Intent(IngredientsWidget.ACTION_UPDATE);
                        broadcastIntent.putExtra(key, recipe);
                        sendBroadcast(broadcastIntent);
                    }
                }
            }
        }
    }
}
