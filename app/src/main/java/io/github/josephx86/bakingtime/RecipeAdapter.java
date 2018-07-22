package io.github.josephx86.bakingtime;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    private List<Recipe> recipes;
    private RecipeClickListener recipeClickListener;

    public RecipeAdapter(List<Recipe> recipes, RecipeClickListener recipeClickListener) {
        if (recipes == null) {
            this.recipes = new ArrayList<>();
        } else {
            this.recipes = recipes;
        }
        this.recipeClickListener = recipeClickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_item, parent, false);
        final RecipeViewHolder holder = new RecipeViewHolder(view);
        if (recipeClickListener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recipeClickListener.recipeClicked(holder);
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        if (recipes == null) {
            recipes = new ArrayList<>();
        }
        int limit = recipes.size();
        if (limit == 0) {
            return; // List is empty
        }
        position = position % limit;
        Recipe r = recipes.get(position);
        holder.setName(r.getName());
        holder.setServingSize(r.getServings());
        holder.setId(r.getId());
    }

    @Override
    public int getItemCount() {
        if (recipes == null) {
            recipes = new ArrayList<>();
        }
        return recipes.size();
    }

    public Recipe getRecipeById(int id) {
        if (recipes == null) {
            recipes = new ArrayList<>();
        }
        for (Recipe r : recipes) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    public void swapRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        if (recipes != null) {
            notifyDataSetChanged();
        }
    }

    public List<Recipe> getRecipes() {
        if (recipes == null) {
            recipes = new ArrayList<>();
        }
        return recipes;
    }
}
