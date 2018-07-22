package io.github.josephx86.bakingtime.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import static io.github.josephx86.bakingtime.data.RecipesDatabase.RECIPES;

@ContentProvider(authority = RecipesProvider.AUTHORITY, database = RecipesDatabase.class)
public final class RecipesProvider {

    public static final String AUTHORITY = "io.github.josephx86.bakingtime.data.RecipesProvider";

    @TableEndpoint(table = RECIPES)
    public static class Recipes {

        @ContentUri(
                path = "recipes",
                type = "vnd.android.cursor.dir/recipe",
                defaultSort = RecipeEntryColumns._ID + " ASC")
        public static final Uri RECIPES = Uri.parse("content://" + AUTHORITY + "/recipes");
    }
}
