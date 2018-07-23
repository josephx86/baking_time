package io.github.josephx86.bakingtime;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

public class InstructionsActivity extends PlayerActivity implements FragmentParent {
    private InstructionsFragment fragment;
    private Recipe recipe = new Recipe();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

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
            //nextButton.setVisibility(View.GONE);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
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
        if (fragment != null) {

            // If current step is last step, hide next button
            int limit = recipe.getSteps().size() - 1;
            fragment.showNextButton(currentStep < limit);

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
                        fragment.setPlayerVisible(true, videoUrl, thumbnailUrl, playerPosition, playerWindow, playWhenReady);
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

    @Override
    public void moveNext() {
        // Reset player position & enable autoplay (default).
        playerPosition = 0;
        playerWindow = 0;
        playWhenReady = true;

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

    }
}
