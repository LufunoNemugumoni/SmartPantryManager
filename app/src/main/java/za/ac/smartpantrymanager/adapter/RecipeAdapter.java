package za.ac.smartpantrymanager.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;
import za.ac.smartpantrymanager.R;
import za.ac.smartpantrymanager.RecipeDetailActivity;
import za.ac.smartpantrymanager.model.Recipe;
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.H> {
    private final Context context;
    private final List<Recipe> recipes;
    public RecipeAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }
    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new H(view);
    }
    @Override
    public void onBindViewHolder(H holder, int position) {
        Recipe recipe = recipes.get(position);
        String name = recipe.getName() == null
                ? ""
                : recipe.getName().trim().toLowerCase(Locale.ROOT);
        holder.title.setText(recipe.getName());
        holder.image.setImageResource(getRecipeImage(name));
        holder.info.setText("View ingredients and instructions");
        if (holder.time != null) {
            holder.time.setText(getRecipeTime(name) + " • Easy");
        }
        holder.itemView.setContentDescription(
                "Open recipe " + recipe.getName()
        );
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(
                    context,
                    RecipeDetailActivity.class
            );
            intent.putExtra("recipe_name", recipe.getName());
            intent.putExtra("recipe_ingredients", recipe.getIngredients());
            intent.putExtra("recipe_quantities", recipe.getQuantities());
            intent.putExtra("recipe_units", "");
            intent.putExtra("recipe_instructions", recipe.getInstructions());
            context.startActivity(intent);
        });
    }
    private int getRecipeImage(String name) {
        if (name.contains("banana") && name.contains("pancake")) {
            return R.drawable.recipe_pancakes;
        } else if (name.contains("pancake")) {
            return R.drawable.recipe_pancakes;
        } else if (name.contains("garlic") && name.contains("bread")) {
            return R.drawable.recipe_garlic_bread;
        } else if (name.contains("grilled cheese")) {
            return R.drawable.recipe_grilled_cheese;
        } else if (name.contains("tuna") && name.contains("sandwich")) {
            return R.drawable.recipe_tuna_sandwich;
        } else if (name.contains("sandwich")) {
            return R.drawable.recipe_tuna_sandwich;
        } else if (name.contains("omelette")) {
            return R.drawable.recipe_omelette;
        } else if (name.contains("scrambled")) {
            return R.drawable.recipe_scrambled_eggs;
        } else if (name.contains("pasta primavera")) {
            return R.drawable.recipe_pasta;
        } else if (name.contains("pasta"))  {
            return R.drawable.recipe_tomatopasta;
        } else if (name.contains("chicken") || name.contains("stir")) {
            return R.drawable.recipe_stirfry;
        } else if (name.contains("soup")) {
            return R.drawable.recipe_soup;
        } else if (name.contains("rice") || name.contains("bean")) {
            return R.drawable.recipe_rice;
        } else if (name.contains("curry")) {
            return R.drawable.recipe_stirfry;
        } else if (name.contains("salad")) {
            return R.drawable.recipe_salad;
        } else {
            return R.drawable.recipe_omelette;
        }
    }
    private String getRecipeTime(String name) {
        if (name.contains("salad") || name.contains("sandwich")) {
            return "10 min";
        } else if (name.contains("omelette")
                || name.contains("scrambled")
                || name.contains("pancake")) {
            return "15 min";
        } else if (name.contains("soup") || name.contains("curry")) {
            return "30 min";
        } else if (name.contains("chicken")) {
            return "25 min";
        } else if (name.contains("garlic bread")) {
            return "20 min";
        } else {
            return "20 min";
        }
    }
    @Override
    public int getItemCount() {
        return recipes == null ? 0 : recipes.size();
    }
    static class H extends RecyclerView.ViewHolder {
        TextView title;
        TextView info;
        TextView time;
        ImageView image;
        H(View view) {
            super(view);
            title = view.findViewById(R.id.txtRecipeName);
            info = view.findViewById(R.id.txtRecipeInfo);
            time = view.findViewById(R.id.txtRecipeTime);
            image = view.findViewById(R.id.imgRecipe);
        }
    }
}