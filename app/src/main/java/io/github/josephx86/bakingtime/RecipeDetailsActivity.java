package io.github.josephx86.bakingtime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity {
    private boolean twoPanes;
    private Recipe recipe = new Recipe();
    private InstructionsFragment fragment;
    private int currentStep = 0;

    @BindView(R.id.steps_lv)
    ListView stepsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        twoPanes = getResources().getBoolean(R.bool.two_panes);

        stepsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long rowId) {
                currentStep = (int) rowId;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Recipe will be resubmitted with intent when activity is started.
        // However, current step may change as user navigates through the steps.
        // Save current step
        String key = getString(R.string.step_number_key);
        outState.putInt(key, currentStep);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String key = getString(R.string.step_number_key);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(key)) {
                currentStep = savedInstanceState.getInt(key);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        addStepFragment();
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

            setStepData();

        }
    }

    private void setStepData() {
        if (twoPanes) {
            if (currentStep == 0) {
                fragment.setTitle("Ingredients");
                fragment.setInstructionss(recipe.getIngredientsString());
                fragment.setPlayerVisible(false, null);
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
                    if (!videoUrl.isEmpty()) {
                        fragment.setPlayerVisible(true, videoUrl);
                    } else {
                        fragment.setPlayerVisible(false, null);
                    }

                    // Set title and instructions
                    String instructions = recipeStep.getDescription();
                    String title = recipeStep.getShortDescription();
                    if (title.equals(instructions)) {
                        fragment.setTitle(title);
                        fragment.setInstructionss("");
                    } else {
                        fragment.setTitle(title);
                        fragment.setInstructionss(instructions);
                    }
                } else {
                    Toast.makeText(this, "Failed to load step!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
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
