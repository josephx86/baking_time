package io.github.josephx86.bakingtime;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.josephx86.bakingtime.data.RecipeEntryColumns;
import io.github.josephx86.bakingtime.data.RecipesProvider;

public class MainActivity extends AppCompatActivity implements RecipeClickListener {
    private final String TAG = "Baking Time";
    private Handler handler;
    private RecipeAdapter adapter;
    public static int DISPLAY_FLAG;

    @BindView(R.id.wait_for_recipes_pb)
    ProgressBar waitProgressBar;

    @BindView(R.id.retry_b)
    Button retryButton;

    @BindView(R.id.message_tv)
    TextView messageTextView;

    @BindView(R.id.recipes_rv)
    RecyclerView recipesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handler = new Handler(getMainLooper());



        // Setup recyclerview
        recipesRecyclerView.setLayoutManager(Utils.getLayoutManager(this));
        adapter = new RecipeAdapter(null, this);
        recipesRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Download if list is empty
        // On rotation, recipes will be preserved; no need to re-download them again.
        if (adapter.getItemCount() == 0) {
            downloadRecipes();
        } else {
            showRecipes(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save scroll position in recycler view
        Parcelable state = recipesRecyclerView.getLayoutManager().onSaveInstanceState();
        String key = getString(R.string.recycler_view_state_key);
        outState.putParcelable(key, state);

        // Save recipes
        if (adapter != null) {
            ArrayList<Recipe> recipes = (ArrayList<Recipe>) adapter.getRecipes();
            if (recipes != null) {
                key = getString(R.string.recipes_list_key);
                outState.putParcelableArrayList(key, recipes);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore scroll position
        String key = getString(R.string.recycler_view_state_key);
        if (savedInstanceState.containsKey(key)) {
            Parcelable state = savedInstanceState.getParcelable(key);
            if (state != null) {
                recipesRecyclerView.getLayoutManager().onRestoreInstanceState(state);
            }
        }

        // Restore recipes
        key = getString(R.string.recipes_list_key);
        if (savedInstanceState.containsKey(key)) {
            ArrayList<Recipe> recipes = savedInstanceState.getParcelableArrayList(key);
            if (adapter == null) {
                adapter = new RecipeAdapter(recipes, this);
                adapter.notifyDataSetChanged();
            } else {
                adapter.swapRecipes(recipes);
            }
        }
    }

    private void showRetryButton() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Show retry button & hide progress bar
                messageTextView.setText(getString(R.string.failed_to_download_recipes_message));
                messageTextView.setVisibility(View.VISIBLE);
                waitProgressBar.setVisibility(View.GONE);
                retryButton.setVisibility(View.VISIBLE);
                recipesRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void showRecipes(final boolean fromDatabase) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Show recipes grid
                messageTextView.setText("");
                messageTextView.setVisibility(View.GONE);
                waitProgressBar.setVisibility(View.GONE);
                retryButton.setVisibility(View.GONE);
                recipesRecyclerView.setVisibility(View.VISIBLE);

                if (fromDatabase) {
                    Toast.makeText(MainActivity.this, getString(R.string.loaded_from_backup), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void downloadRecipes() {
        // Hide retry button & show progress bar
        messageTextView.setText(getString(R.string.getting_recipes));
        messageTextView.setVisibility(View.VISIBLE);
        waitProgressBar.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.GONE);
        recipesRecyclerView.setVisibility(View.GONE);

        String url = getString(R.string.recipes_repo);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        saveToDatabase(response);
                        List<Recipe> recipes = parseRecipes(response);
                        adapter.swapRecipes(recipes);
                        showRecipes(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // First try to get what has been saved in database.
                        boolean recipesLoaded = false;
                        String json = recoverFromDatabase();
                        if ((json != null) && (!json.isEmpty())) {
                            List<Recipe> recipes = parseRecipes(json);
                            if ((recipes != null) && (recipes.size() > 0)) {
                                adapter.swapRecipes(recipes);
                                showRecipes(true);
                                recipesLoaded = true;
                            }
                        }

                        if (!recipesLoaded) {
                            Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                            showRetryButton();
                        }
                    }
                });
        Utils.QueueRequest(request, this);
    }

    private void saveToDatabase(String json) {
        ContentResolver resolver = MainActivity.this.getContentResolver();
        if (resolver != null) {
            Uri uri = RecipesProvider.Recipes.RECIPES;

            // First delete whatever wass in database.
            resolver.delete(uri, null, null);

            // ... then save new JSON
            ContentValues cv = new ContentValues();
            cv.put(RecipeEntryColumns.JSON, json);
            resolver.insert(uri, cv);
        }
    }

    private String recoverFromDatabase() {
        String json = "";
        ContentResolver resolver = MainActivity.this.getContentResolver();
        if (resolver != null) {
            Cursor cursor = resolver.query(RecipesProvider.Recipes.RECIPES, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    // Db will have only 1 record.
                    boolean hasJson = cursor.moveToFirst();
                    if (hasJson) {
                        json = cursor.getString(cursor.getColumnIndex(RecipeEntryColumns.JSON));
                    }
                }
                cursor.close();
            }
        }
        return json;
    }

    private List<Recipe> parseRecipes(String json) {
        List<Recipe> recipes = new ArrayList<>();
        try {
            JSONArray recipesJsonArray = new JSONArray(json);
            for (int i = 0; i < recipesJsonArray.length(); i++) {
                JSONObject recipeJsonObject = recipesJsonArray.getJSONObject(i);
                int id = recipeJsonObject.getInt("id");
                String name = recipeJsonObject.getString("name");
                String image = recipeJsonObject.getString("image");
                int servings = recipeJsonObject.getInt("servings");
                Recipe r = new Recipe();
                r.setId(id);
                r.setName(name);
                r.setImage(image);
                r.setServings(servings);

                // Get the ingredients
                JSONArray ingredientsArray = recipeJsonObject.getJSONArray("ingredients");
                for (int j = 0; j < ingredientsArray.length(); j++) {
                    JSONObject ingObject = ingredientsArray.getJSONObject(j);
                    double quantity = ingObject.getDouble("quantity");
                    String measure = ingObject.getString("measure");
                    String ingName = ingObject.getString("ingredient");
                    Ingredient ingredient = new Ingredient(quantity, measure, ingName);
                    r.addIngredient(ingredient);
                }

                // First item in list (or 'step' must be the ingredients.
                r.addStep(new RecipeStep(-1, "Ingredients", "", "", ""));

                // Get the recipe steps
                JSONArray stepsArray = recipeJsonObject.getJSONArray("steps");
                for (int j = 0; j < stepsArray.length(); j++) {
                    JSONObject stepObject = stepsArray.getJSONObject(j);
                    id = stepObject.getInt("id");
                    String shortDescription = stepObject.getString("shortDescription");
                    String description = stepObject.getString("description");
                    String videoURL = stepObject.getString("videoURL");
                    String thumbnailURL = stepObject.getString("thumbnailURL");
                    RecipeStep step = new RecipeStep(id, shortDescription, description, videoURL, thumbnailURL);
                    r.addStep(step);
                }
                r.setNextSteps();
                recipes.add(r);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showRetryButton();
        }

        return recipes;
    }

    public void retryButton_Click(View view) {
        downloadRecipes();
    }

    @Override
    public void recipeClicked(RecipeViewHolder recipeViewHolder) {
        if (adapter != null) {
            Recipe recipe = adapter.getRecipeById(recipeViewHolder.getId());
            if (recipe != null) {
                Context context = MainActivity.this;
                Intent detailsActivityIntent = new Intent(context, RecipeDetailsActivity.class);
                String key = context.getString(R.string.recipe_parcelable_key);
                detailsActivityIntent.putExtra(key, recipe);
                startActivity(detailsActivityIntent);
            }
        }
    }
}
