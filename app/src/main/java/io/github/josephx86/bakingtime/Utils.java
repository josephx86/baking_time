package io.github.josephx86.bakingtime;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Utils {
    private static RequestQueue requestQueue;
    private static final int WIDE_SCREEN_THRESHOLD = 580;
    private static final float GRID_COLUMN_THRESHOLD = 295;

    public static RecyclerView.LayoutManager getLayoutManager(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        double deviceWidth = metrics.widthPixels;
        double densityScaledWidth = deviceWidth / metrics.density;

        // On wide screens/tablets use a grid layout.
        if (densityScaledWidth > WIDE_SCREEN_THRESHOLD) {
            int columnCount = (int) (densityScaledWidth / GRID_COLUMN_THRESHOLD);
            return new GridLayoutManager(context, columnCount, LinearLayoutManager.VERTICAL, false);
        } else {
            return new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        }
    }

    public static void QueueRequest(StringRequest request, Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        requestQueue.add(request);
    }
}
