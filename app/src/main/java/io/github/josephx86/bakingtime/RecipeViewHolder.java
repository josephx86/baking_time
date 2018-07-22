package io.github.josephx86.bakingtime;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

class RecipeViewHolder extends RecyclerView.ViewHolder {
    private int id;

    @BindView(R.id.recipe_name_tv)
    TextView nameTextView;

    @BindView(R.id.serving_tv)
    TextView servingTextView;

    public RecipeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        nameTextView.setText(name);
    }

    public void setServingSize(int size) {
        if (size <= 0) {
            size = 1; // Must serve at least 1 person... obviously
        }
        String stringValue = String.valueOf(size);
        servingTextView.setText(stringValue);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
