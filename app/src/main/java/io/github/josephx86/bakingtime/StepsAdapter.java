package io.github.josephx86.bakingtime;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StepsAdapter extends ArrayAdapter {
    private List<RecipeStep> recipeSteps;


    StepsAdapter(@NonNull Context context, List<RecipeStep> steps) {
        super(context, 0);
        if (steps == null) {
            recipeSteps = new ArrayList<>();
        } else {
            recipeSteps = steps;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (recipeSteps == null) {
            recipeSteps = new ArrayList<>();
        }
        position = position % recipeSteps.size();
        RecipeStep step = recipeSteps.get(position);
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_step_item, parent, false);
        TextView titleTextView = convertView.findViewById(R.id.title_tv);
        if (titleTextView != null) {
            titleTextView.setText(step.getShortDescription());
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return recipeSteps.size();
    }
}
