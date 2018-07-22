package io.github.josephx86.bakingtime.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = RecipesDatabase.VERSION)
public final class RecipesDatabase {

    public static final int VERSION = 1;

    @Table(RecipeEntryColumns.class) public static final String RECIPES = "recipes";
}
