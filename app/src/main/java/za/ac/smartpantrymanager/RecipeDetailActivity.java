package za.ac.smartpantrymanager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView txtRecipeName = findViewById(R.id.txtRecipeName);
        TextView txtRecipeIngredients =
                findViewById(R.id.txtRecipeIngredients);
        TextView txtRecipeInstructions =
                findViewById(R.id.txtRecipeInstructions);
        ImageView imgRecipeDetail =
                findViewById(R.id.imgRecipeDetail);
        Button btnBack = findViewById(R.id.btnBack);

        String recipeName =
                getIntent().getStringExtra("recipe_name");
        String ingredients =
                getIntent().getStringExtra("recipe_ingredients");
        String quantities =
                getIntent().getStringExtra("recipe_quantities");
        String instructions =
                getIntent().getStringExtra("recipe_instructions");

        if (recipeName == null || recipeName.trim().isEmpty()) {
            recipeName = "Recipe Details";
        }

        txtRecipeName.setText(recipeName);

        // Format ingredient names and quantities as individual rows.
        txtRecipeIngredients.setText(
                formatIngredients(ingredients, quantities)
        );

        // Format preparation instructions as numbered steps.
        txtRecipeInstructions.setText(
                formatInstructions(instructions)
        );

        // Select the recipe image using the existing drawable files.
        String name = recipeName.toLowerCase(Locale.ROOT);

        if (name.contains("omelette") || name.contains("omelet")) {
            imgRecipeDetail.setImageResource(R.drawable.recipe_omelette);
        } else if (name.contains("pasta") || name.contains("spaghetti")) {
            imgRecipeDetail.setImageResource(R.drawable.recipe_pasta);
        } else if (name.contains("stir")) {
            imgRecipeDetail.setImageResource(R.drawable.recipe_stirfry);
        } else if (name.contains("soup")) {
            imgRecipeDetail.setImageResource(R.drawable.recipe_soup);
        } else if (name.contains("rice")) {
            imgRecipeDetail.setImageResource(R.drawable.recipe_rice);
        } else {
            imgRecipeDetail.setImageResource(R.drawable.recipe_omelette);
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private String formatIngredients(
            String ingredients,
            String quantities
    ) {
        if (ingredients == null || ingredients.trim().isEmpty()) {
            return "No ingredients available.";
        }

        String[] names = ingredients.split("\\s*,\\s*");
        String[] amounts = quantities == null
                ? new String[0]
                : quantities.split("\\s*,\\s*");

        StringBuilder result = new StringBuilder();

        result.append("INGREDIENT").append("\n");
        result.append("────────────────────").append("\n\n");

        for (int i = 0; i < names.length; i++) {
            String ingredient = names[i].trim();

            if (ingredient.isEmpty()) {
                continue;
            }

            result.append("•  ");
            result.append(capitalise(ingredient));

            if (i < amounts.length && !amounts[i].trim().isEmpty()) {
                result.append("   —   ");
                result.append(amounts[i].trim());
            }

            result.append("\n\n");
        }

        return result.toString().trim();
    }

    private String formatInstructions(String instructions) {
        if (instructions == null || instructions.trim().isEmpty()) {
            return "Instructions are not available yet.";
        }

        String cleaned = instructions.trim();

        // Keep instructions that are already numbered on separate lines.
        String[] existingLines = cleaned.split("\\n+");
        boolean alreadyNumbered = existingLines.length > 1;

        if (alreadyNumbered) {
            for (String line : existingLines) {
                if (!line.trim().matches("^(\\d+[.)]|Step\\s+\\d+).*")) {
                    alreadyNumbered = false;
                    break;
                }
            }
        }

        if (alreadyNumbered) {
            return cleaned;
        }

        // Split instructions separated by full stops into numbered steps.
        String[] sentences = cleaned.split("(?<=[.!?])\\s+");
        List<String> steps = new ArrayList<>();

        for (String sentence : sentences) {
            String step = sentence.trim();

            if (!step.isEmpty()) {
                steps.add(step);
            }
        }

        if (steps.size() <= 1) {
            return cleaned;
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < steps.size(); i++) {
            result.append(i + 1)
                    .append(".  ")
                    .append(steps.get(i));

            if (i < steps.size() - 1) {
                result.append("\n\n");
            }
        }

        return result.toString();
    }

    private String capitalise(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        return value.substring(0, 1).toUpperCase(Locale.ROOT)
                + value.substring(1);
    }
}
