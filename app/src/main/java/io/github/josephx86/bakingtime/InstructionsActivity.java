package io.github.josephx86.bakingtime;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstructionsActivity extends AppCompatActivity implements FragmentParent {
    private InstructionsFragment fragment;
    private Recipe recipe = new Recipe();
    private int currentStep = 0;

    @BindView(R.id.next_b)
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        ButterKnife.bind(this);

        // Get bundled data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            // Get the step number
            String stepNumberKey = getString(R.string.step_number_key);
            if (extras.containsKey(stepNumberKey)) {
                currentStep = extras.getInt(stepNumberKey);
            }

            // Get recipe
            String recipeKey = getString(R.string.recipe_parcelable_key);
            if (extras.containsKey(recipeKey)) {
                recipe = extras.getParcelable(recipeKey);
                if (recipe == null) {
                    Toast.makeText(this, "Failed to load step!", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        addStepFragment();

        setStepData();

        // In landscape mode, video (in fragment) will be full screen
        // Hide the button and actionbar
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            nextButton.setVisibility(View.GONE);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
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

    private void addStepFragment() {
        // Add the fragment for current step
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            fragment = new InstructionsFragment();
            fragment.setParent(this);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container, fragment);
            transaction.commit();
        }
        if (fragment == null) {
            Toast.makeText(this, "Failed to load step!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setStepData() {
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

        setTitle();
    }


    private void setTitle() {
        // First row is ingredients
        String title = recipe.getName() + " - Step";
        if (currentStep == 0) {
            title = recipe.getName() + " - Ingredients";
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }

    public void nextStepButton_Click(View view) {
        if (fragment != null) {
            fragment.moveNext();
        }
    }

    @Override
    public void moveNext() {
        // Move to next step by changing fragment
        currentStep++;
        InstructionsFragment nextFragment = new InstructionsFragment();
        nextFragment.setParent(this);
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container, nextFragment);
            transaction.addToBackStack("StepFragment");
            transaction.commit();
        }
        fragment = nextFragment;
        setStepData();

        // If current step is last step, hide next button
        int limit = recipe.getSteps().size() - 1;
        if (currentStep >= limit) {
            nextButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
        }
    }
}
