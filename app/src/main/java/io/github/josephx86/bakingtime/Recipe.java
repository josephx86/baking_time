package io.github.josephx86.bakingtime;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable {
    private String name = "",
            image = "";
    private int id, servings;
    private List<RecipeStep> steps = new ArrayList<>();
    private List<Ingredient> ingredients = new ArrayList<>();

    Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image = in.readString();
        servings = in.readInt();
        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }
        Parcelable[] parcelableIngredients = in.readParcelableArray(Ingredient.class.getClassLoader());
        for (Parcelable p : parcelableIngredients) {
            Ingredient ing = (Ingredient) p;
            ingredients.add(ing);
        }
        if (steps == null) {
            steps = new ArrayList<>();
        }
        Parcelable[] parcelableSteps = in.readParcelableArray(RecipeStep.class.getClassLoader());
        for (Parcelable p : parcelableSteps) {
            RecipeStep rs = (RecipeStep) p;
            steps.add(rs);
        }
    }

    Recipe() {
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void addIngredient(Ingredient ing) {
        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }
        this.ingredients.add(ing);
    }

    public List<RecipeStep> getSteps() {
        return steps;
    }

    public void addStep(RecipeStep recipeStep) {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        steps.add(recipeStep);
    }

    public void setNextSteps() {
        // For each step, assign the one that comes after it... like a linked list.
        if (steps == null) {
            steps = new ArrayList<>();
        }
        int limit = steps.size() - 1;
        for (int i = 0; i < limit; i++) {
            steps.get(i).setNextStep(steps.get(i + 1));
        }
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeInt(servings);
        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }
        parcel.writeParcelableArray(ingredients.toArray(new Parcelable[ingredients.size()]), i);
        if (steps == null) {
            steps = new ArrayList<>();
        }
        parcel.writeParcelableArray(steps.toArray(new Parcelable[steps.size()]), i);
    }

    public String getIngredientsString() {
        StringBuilder buffer = new StringBuilder();
        for (Ingredient ingredient : getIngredients()) {
            String line = ingredient.getQuantity() + " " + ingredient.getMeasure() + " of " + ingredient.getIngredientName();
            buffer.append(line);
            buffer.append("\n\n");
        }
        return buffer.toString();
    }
}
