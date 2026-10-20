package za.ac.smartpantrymanager.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import za.ac.smartpantrymanager.model.PantryItem;
import za.ac.smartpantrymanager.model.Recipe;

public class RecipeMatcher {

    public static ArrayList<Recipe> getMatchingRecipes(
            ArrayList<PantryItem> pantry,
            ArrayList<Recipe> recipes) {

        ArrayList<Recipe> matches = new ArrayList<>();

        if (pantry == null || recipes == null) {
            return matches;
        }

        Map<String, Double> available = new HashMap<>();

        // Collect pantry ingredients and combine duplicate names.
        for (PantryItem item : pantry) {
            if (item == null || item.getName() == null) {
                continue;
            }

            String name = normalize(item.getName());
            double quantity = Math.max(0, item.getQuantity());

            if (!name.isEmpty()) {
                available.put(
                        name,
                        available.getOrDefault(name, 0.0) + quantity
                );
            }
        }

        // Check every recipe against the available pantry ingredients.
        for (Recipe recipe : recipes) {
            if (recipe == null || recipe.getIngredients() == null) {
                continue;
            }

            String[] ingredients = recipe.getIngredients().split(",");
            String[] quantities = recipe.getQuantities() == null
                    ? new String[0]
                    : recipe.getQuantities().split(",");

            boolean canMake = ingredients.length > 0;
            boolean foundIngredient = false;

            for (int i = 0; i < ingredients.length; i++) {
                String ingredient = normalize(ingredients[i]);

                // Ignore empty entries in the recipe.
                if (ingredient.isEmpty()) {
                    continue;
                }

                foundIngredient = true;

                Double quantityAvailable = available.get(ingredient);

                if (quantityAvailable == null || quantityAvailable <= 0) {
                    canMake = false;
                    break;
                }

                if (i < quantities.length) {
                    try {
                        double required =
                                Double.parseDouble(quantities[i].trim());

                        if (required < 0 || quantityAvailable < required) {
                            canMake = false;
                            break;
                        }
                    } catch (NumberFormatException ignored) {
                        // If a required quantity is not numeric,
                        // ingredient presence is checked instead.
                    }
                }
            }

            if (canMake && foundIngredient) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    // Standardise common singular and plural ingredient names.
    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String name = value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        if (name.isEmpty()) {
            return "";
        }

        // Explicit exceptions for common ingredient names.
        switch (name) {
            case "tomatoes":
                return "tomato";
            case "potatoes":
                return "potato";
            case "onions":
                return "onion";
            case "eggs":
                return "egg";
            case "carrots":
                return "carrot";
            case "beans":
                return "bean";
            case "chillies":
            case "chilies":
                return "chili";
            case "leaves":
                return "leaf";
            default:
                break;
        }

        // Conservative fallback for regular plurals.
        if (name.length() > 3 && name.endsWith("ies")) {
            return name.substring(0, name.length() - 3) + "y";
        }

        if (name.length() > 3
                && name.endsWith("s")
                && !name.endsWith("ss")
                && !name.endsWith("us")
                && !name.endsWith("is")) {
            return name.substring(0, name.length() - 1);
        }

        return name;
    }
}

