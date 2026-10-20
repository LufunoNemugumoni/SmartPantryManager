package za.ac.smartpantrymanager.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import za.ac.smartpantrymanager.R;
import za.ac.smartpantrymanager.model.PantryItem;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.H> {

    public interface C {
        void go(PantryItem item);
    }

    private final List<PantryItem> data;
    private final C edit;
    private final C delete;

    public PantryAdapter(List<PantryItem> data, C edit, C delete) {
        this.data = data;
        this.edit = edit;
        this.delete = delete;
    }

    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new H(view);
    }

    @Override
    public void onBindViewHolder(H holder, int position) {
        PantryItem item = data.get(position);

        holder.name.setText(item.getName());
        holder.quantity.setText(
                item.getQuantity() + " " + item.getUnit()
        );

        setIngredientImage(holder.image, item.getName());

        String expiry = item.getExpiryDate();

        if (expiry == null || expiry.trim().isEmpty()) {
            holder.expiry.setText("Expiry date not set");

            setStatus(
                    holder.status,
                    "No expiry date",
                    Color.rgb(75, 85, 75),
                    Color.rgb(232, 237, 231)
            );
        } else {
            holder.expiry.setText("Expires: " + expiry);
            setExpiryStatus(holder.status, expiry.trim());
        }

        // Tap the card to edit the ingredient.
        holder.itemView.setOnClickListener(v -> {
            if (edit != null) {
                edit.go(item);
            }
        });

        // Show Edit and Delete only when the menu is opened.
        holder.menu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.menu);

            popup.getMenu().add(0, 1, 0, "Edit ingredient");
            popup.getMenu().add(0, 2, 1, "Delete ingredient");

            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == 1) {
                    if (edit != null) {
                        edit.go(item);
                    }
                    return true;
                }

                if (menuItem.getItemId() == 2) {
                    if (delete != null) {
                        delete.go(item);
                    }
                    return true;
                }

                return false;
            });

            popup.show();
        });
    }


    private void setIngredientImage(ImageView image, String ingredientName) {
        String name = ingredientName == null
                ? ""
                : ingredientName.trim().toLowerCase(Locale.ROOT);

        String drawableName = "ingredient_default";

        if (name.contains("egg")) {
            drawableName = "ingredient_egg";
        } else if (name.contains("rice")) {
            drawableName = "ingredient_rice";
        } else if (name.contains("tomato")) {
            drawableName = "ingredient_tomato";
        } else if (name.contains("onion")) {
            drawableName = "ingredient_onion";
        } else if (name.contains("potato")) {
            drawableName = "ingredient_potato";
        } else if (name.contains("carrot")) {
            drawableName = "ingredient_carrot";
        } else if (name.contains("oil")) {
            drawableName = "ingredient_oil";
        } else if (name.contains("chicken")) {
            drawableName = "ingredient_chicken";
        } else if (name.contains("milk")) {
            drawableName = "ingredient_milk";
        } else if (name.contains("bread")) {
            drawableName = "ingredient_bread";
        } else if (name.contains("cheese")) {
            drawableName = "ingredient_cheese";
        } else if (name.contains("pasta")
                || name.contains("spaghetti")) {
            drawableName = "ingredient_pasta";
        } else if (name.contains("bean")) {
            drawableName = "ingredient_beans";
        } else if (name.contains("apple")) {
            drawableName = "ingredient_apple";
        } else if (name.contains("banana")) {
            drawableName = "ingredient_banana";
        } else if (name.contains("garlic")) {
            drawableName = "ingredient_garlic";
        } else if (name.contains("lettuce")) {
            drawableName = "ingredient_lettuce";
        } else if (name.contains("pepper")) {
            drawableName = "ingredient_pepper";
        } else if (name.contains("salt")) {
            drawableName = "ingredient_salt";
        } else if (name.contains("tuna")) {
            drawableName = "ingredient_tuna";
        } else if (name.contains("butter")) {
            drawableName = "ingredient_butter";
        } else if (name.contains("flour")) {
            drawableName = "ingredient_flour";
        }



        int resourceId = image.getContext().getResources().getIdentifier(
                drawableName,
                "drawable",
                image.getContext().getPackageName()
        );

        if (resourceId == 0) {
            resourceId = image.getContext().getResources().getIdentifier(
                    "ic_pantry",
                    "drawable",
                    image.getContext().getPackageName()
            );
        }

        if (resourceId != 0) {
            image.setImageResource(resourceId);
        } else {
            image.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setContentDescription(
                ingredientName == null
                        ? "Ingredient picture"
                        : ingredientName + " picture"
        );
    }

    private void setExpiryStatus(TextView status, String expiry) {
        Date expiryDate = parseDate(expiry);

        if (expiryDate == null) {
            setStatus(
                    status,
                    "Check expiry date",
                    Color.rgb(75, 85, 75),
                    Color.rgb(232, 237, 231)
            );
            return;
        }

        Calendar today = Calendar.getInstance();
        clearTime(today);

        Calendar expiryDay = Calendar.getInstance();
        expiryDay.setTime(expiryDate);
        clearTime(expiryDay);

        long difference = expiryDay.getTimeInMillis()
                - today.getTimeInMillis();

        long daysLeft = difference / (24L * 60L * 60L * 1000L);

        if (daysLeft < 0) {
            setStatus(
                    status,
                    "Expired",
                    Color.WHITE,
                    Color.rgb(198, 40, 40)
            );
        } else if (daysLeft <= 3) {
            String message = daysLeft == 0
                    ? "Expires today"
                    : "Expiring soon · " + daysLeft
                    + (daysLeft == 1 ? " day left" : " days left");

            setStatus(
                    status,
                    message,
                    Color.rgb(110, 65, 0),
                    Color.rgb(255, 224, 130)
            );
        } else {
            setStatus(
                    status,
                    "Fresh · " + daysLeft + " days left",
                    Color.rgb(27, 94, 32),
                    Color.rgb(200, 230, 201)
            );
        }
    }

    private void setStatus(
            TextView status,
            String message,
            int textColor,
            int backgroundColor
    ) {
        status.setText(message);
        status.setTextColor(textColor);
        status.setPadding(18, 8, 18, 8);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(24);
        shape.setColor(backgroundColor);

        status.setBackground(shape);
    }

    private Date parseDate(String value) {
        String[] patterns = {
                "yyyy-MM-dd",
                "yyyy-MM-dd HH:mm:ss",
                "dd/MM/yyyy",
                "d/M/yyyy",
                "MM/dd/yyyy",
                "dd-MM-yyyy",
                "d-M-yyyy"
        };

        for (String pattern : patterns) {
            SimpleDateFormat format =
                    new SimpleDateFormat(pattern, Locale.US);

            format.setLenient(false);

            try {
                Date date = format.parse(value);

                if (date != null) {
                    return date;
                }
            } catch (ParseException ignored) {
                // Try the next date format.
            }
        }

        return null;
    }

    private void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class H extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, quantity, expiry, status, menu;

        H(View view) {
            super(view);

            image = view.findViewById(R.id.imgIngredient);
            name = view.findViewById(R.id.txtName);
            quantity = view.findViewById(R.id.txtQuantity);
            expiry = view.findViewById(R.id.txtExpiry);
            status = view.findViewById(R.id.txtExpiryStatus);
            menu = view.findViewById(R.id.btnIngredientMenu);
        }
    }
}
