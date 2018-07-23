package io.github.josephx86.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends PlayerActivity {
    private boolean twoPanes;
    private Recipe recipe = new Recipe();
    private InstructionsFragment fragment;
    private View lastSelectedItem;

    @BindView(R.id.steps_lv)
    ListView stepsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        twoPanes = getResources().getBoolean(R.bool.two_panes);

        stepsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private boolean firstTime = true;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long rowId) {
                // Ignore if item clicked has details already shown.
                if (rowId == currentStep) {
                    if (!firstTime) {
                        // First time this listener is called it by the app itself, not user selection.
                        firstTime = false;
                        return;
                    }
                }

                // Reset player position & enable autoplay (default).
                playerPosition = 0;
                playerWindow = 0;
                playWhenReady = true;

                selectItem((int) rowId, view);

                setStepData();
            }
        });

        // Get recipe
        String key = getString(R.string.recipe_parcelable_key);
        boolean loaded = false;
        Bundle extras = getIntent().getExtras();
        if ((extras != null) && (extras.containsKey(key))) {
            recipe = extras.getParcelable(key);
            if (recipe != null) {
                stepsListView.setAdapter(new StepsAdapter(this, recipe.getSteps()));
                loaded = true;
            }
            if (!loaded) {
                Toast.makeText(this, "Recipe could not be loaded!", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (recipe != null) {
                actionBar.setTitle(recipe.getName());
            }
        }

        updateWidgets();
    }

    private void updateWidgets() {
        Intent widgetServiceIntent = new Intent(this, WidgetService.class);
        String key = getString(R.string.recipe_parcelable_key);
        widgetServiceIntent.putExtra(key, recipe);
        startService(widgetServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (twoPanes) {
            addStepFragment();

            setStepData();

            stepsListView.setSelection(0);

            // highlight first item
            ListAdapter adapter = stepsListView.getAdapter();
            if (adapter != null) {
                if (adapter.getCount() > currentStep) {
                    View listItem = adapter.getView(currentStep, null, stepsListView);
                    if (listItem != null) {
                        stepsListView.performItemClick(listItem, currentStep, currentStep);

                    }
                }
            }
        }
    }

    private void selectItem(int index, View selectedView) {
        currentStep = index;

        // Select the item, deselect old one
        Context context = stepsListView.getContext();
        Drawable darkDrawable = ContextCompat.getDrawable(context, R.drawable.rect_dark);
        Drawable lightDrawable = ContextCompat.getDrawable(context, R.drawable.rect);
        if (lastSelectedItem != null) {
            lastSelectedItem.setBackground(lightDrawable);
        }

        lastSelectedItem = selectedView;
        if (selectedView != null) {
            selectedView.setBackground(darkDrawable);
        }
    }

    private void addStepFragment() {
        if (twoPanes) {
            // Add the fragment for current step
            FragmentManager manager = getSupportFragmentManager();
            if (manager != null) {
                fragment = new InstructionsFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.fragment_container, fragment);
                transaction.commit();
            }
            if (fragment == null) {
                Toast.makeText(this, "Failed to load step!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void setStepData() {
        if (twoPanes) {
            fragment.releasePlayer(false);
            if (currentStep == 0) {
                fragment.setTitle("Ingredients");
                fragment.setInstructionsa(recipe.getIngredientsString());
                fragment.setPlayerVisible(false, null, null, 0, 0, false);
            } else {
                boolean stepLoaded = false;
                RecipeStep recipeStep = null;
                if (currentStep < recipe.getSteps().size()) {
                    recipeStep = recipe.getSteps().get(currentStep);
                    if (recipeStep != null) {
                        stepLoaded = true;
                    }
                }
                if (stepLoaded) {

                    // Set video if available
                    String videoUrl = recipeStep.getVideoUrl();
                    String thumbnailUrl = recipeStep.getThumbnail();
                    if (!videoUrl.isEmpty()) {
                        fragment.setPlayerVisible(true, videoUrl, thumbnailUrl, playerPosition, playerWindow, true);
                    } else {
                        fragment.setPlayerVisible(false, null, null, 0, 0, false);
                    }

                    // Set title and instructions
                    String instructions = recipeStep.getDescription();
                    String title = recipeStep.getShortDescription();
                    if (title.equals(instructions)) {
                        fragment.setTitle(title);
                        fragment.setInstructionsa("");
                    } else {
                        fragment.setTitle(title);
                        fragment.setInstructionsa(instructions);
                    }
                } else {
                    Toast.makeText(this, "Failed to load step!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            fragment.showNextButton(false);
        } else {
            // Start instructions activity
            Context context = RecipeDetailsActivity.this;
            Intent instructionsIntent = new Intent(context, InstructionsActivity.class);
            String recipeKey = context.getString(R.string.recipe_parcelable_key);
            instructionsIntent.putExtra(recipeKey, recipe);
            String stepNumberKey = context.getString(R.string.step_number_key);
            instructionsIntent.putExtra(stepNumberKey, currentStep);

            startActivity(instructionsIntent);
        }
    }
}
