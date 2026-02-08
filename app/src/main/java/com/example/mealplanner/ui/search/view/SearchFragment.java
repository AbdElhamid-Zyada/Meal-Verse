package com.example.mealplanner.ui.search.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.model.FilterType;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;
import com.example.mealplanner.ui.search.presenter.SearchContract;
import com.example.mealplanner.ui.search.presenter.SearchPresenter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment implements SearchContract.View {

    private SearchContract.Presenter presenter;
    private RecyclerView rvResults;
    private SearchAdapter adapter;
    private EditText etSearch;
    private TextView tvResultsCount;
    private TextView tvClearAll;
    private ChipGroup chipGroupFilters;
    private MaterialButton btnCategory, btnArea, btnIngredient;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        rvResults = view.findViewById(R.id.rv_search_results);
        etSearch = view.findViewById(R.id.et_search);
        tvResultsCount = view.findViewById(R.id.tv_results_count);
        tvClearAll = view.findViewById(R.id.tv_clear_all);
        chipGroupFilters = view.findViewById(R.id.chip_group_filters);
        btnCategory = view.findViewById(R.id.btn_filter_category);
        btnArea = view.findViewById(R.id.btn_filter_area);
        btnIngredient = view.findViewById(R.id.btn_filter_ingredient);
        btnBack = view.findViewById(R.id.btn_back);

        // Setup RecyclerView
        rvResults.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new SearchAdapter();
        rvResults.setAdapter(adapter);

        presenter = new SearchPresenter(this);

        // Listeners
        btnBack.setOnClickListener(v -> presenter.onBackClicked());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvClearAll.setOnClickListener(v -> presenter.onClearAllFilters());

        btnCategory.setOnClickListener(v -> presenter.onFilterClicked(FilterType.CATEGORY));
        btnArea.setOnClickListener(v -> presenter.onFilterClicked(FilterType.AREA));
        btnIngredient.setOnClickListener(v -> presenter.onFilterClicked(FilterType.INGREDIENT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dispose();
    }

    @Override
    public void showSearchResults(List<Meal> meals) {
        adapter.setMeals(meals);
    }

    @Override
    public void showResultCount(int count) {
        tvResultsCount.setText("FOUND " + count + " RESULTS");
    }

    @Override
    public void showFilterOptions(List<String> options, FilterType type) {
        View anchor = null;
        switch (type) {
            case CATEGORY:
                anchor = btnCategory;
                break;
            case AREA:
                anchor = btnArea;
                break;
            case INGREDIENT:
                anchor = btnIngredient;
                break;
        }

        if (anchor == null)
            return;

        PopupMenu popup = new PopupMenu(getContext(), anchor);
        for (String option : options) {
            popup.getMenu().add(option);
        }
        popup.setOnMenuItemClickListener(item -> {
            presenter.onOptionSelected(item.getTitle().toString(), type);
            return true;
        });
        popup.show();
    }

    @Override
    public void addFilterChip(String label, FilterType type) {
        // Check if chip already exists
        for (int i = 0; i < chipGroupFilters.getChildCount(); i++) {
            Chip existingChip = (Chip) chipGroupFilters.getChildAt(i);
            if (existingChip.getTag() == type) {
                chipGroupFilters.removeView(existingChip);
                break;
            }
        }

        Chip chip = new Chip(getContext());
        chip.setText(label);
        chip.setCloseIconVisible(true);
        chip.setTag(type); // Store type in tag to identify later
        chip.setOnCloseIconClickListener(v -> presenter.onRemoveFilter(type));

        // Style (Optional, usually better in XML style)
        chip.setChipBackgroundColorResource(R.color.green_primary_light); // Using a light green if available or default

        chipGroupFilters.addView(chip);
    }

    @Override
    public void removeFilterChip(FilterType type) {
        for (int i = 0; i < chipGroupFilters.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupFilters.getChildAt(i);
            if (chip.getTag() == type) {
                chipGroupFilters.removeView(chip);
                break;
            }
        }
    }

    @Override
    public void clearAllChips() {
        chipGroupFilters.removeAllViews();
    }

    @Override
    public void showClearAllButton(boolean visible) {
        tvClearAll.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showDatePicker(List<Date> availableDates, Meal meal) {
        String[] dateStrings = new String[availableDates.size()];
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        for (int i = 0; i < availableDates.size(); i++) {
            dateStrings[i] = sdf.format(availableDates.get(i));
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Select Date")
                .setItems(dateStrings, (dialog, which) -> {
                    presenter.onDateSelected(meal, availableDates.get(which));
                })
                .show();
    }

    @Override
    public void showMealTypePicker(Meal meal, Date date) {
        String[] types = { "Breakfast", "Lunch", "Dinner" };
        new AlertDialog.Builder(getContext())
                .setTitle("Select Meal Type")
                .setItems(types, (dialog, which) -> {
                    MealType type = MealType.values()[which];
                    presenter.onMealTypeSelected(meal, date, type);
                })
                .show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateBack() {
        Navigation.findNavController(getView()).navigateUp();
    }

    @Override
    public void navigateToMealDetails(String mealId) {
        Bundle args = new Bundle();
        args.putString("ARG_MEAL_ID", mealId);
        Navigation.findNavController(requireView()).navigate(R.id.action_searchFragment_to_mealDetailsFragment, args);
    }

    @Override
    public void updateMeal(Meal meal) {
        // Find the index of the meal in the adapter list
        // Since we are passing the reference, we can just notifyDataSetChanged or find
        // index
        // For efficiency, let's find index
        // But for MVP simplicity often notifyDataSetChanged is used.
        // Let's implement a specific update method in adapter.
        adapter.updateMeal(meal);
    }

    // Adapter
    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
        private List<Meal> meals = new ArrayList<>();

        public void setMeals(List<Meal> meals) {
            this.meals = meals;
            notifyDataSetChanged();
        }

        public void updateMeal(Meal meal) {
            int index = -1;
            for (int i = 0; i < meals.size(); i++) {
                if (meals.get(i).getId().equals(meal.getId())) {
                    index = i;
                    // Update the object in list (though if reference is same, it might be already
                    // updated)
                    meals.set(i, meal);
                    break;
                }
            }
            if (index != -1) {
                notifyItemChanged(index);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_search, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Meal meal = meals.get(position);
            holder.tvName.setText(meal.getName());
            holder.tvDetails.setText((meal.getArea() != null ? meal.getArea() : "Unknown") + " • "
                    + (meal.getCategory() != null ? meal.getCategory() : "Miscellaneous"));
            holder.ivImage.setImageResource(meal.getImageResId());

            if (meal.isFavorite()) {
                holder.btnFavorite.setImageResource(R.drawable.ic_favorite);
                holder.btnFavorite.setColorFilter(android.graphics.Color.RED);
            } else {
                holder.btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                holder.btnFavorite.clearColorFilter();
                // Or use specific tint if set in XML, but clearing color filter is safer if we
                // set RED above
                // Actually better to use app:tint or setTint.
                // Let's assume ic_favorite is usually red and ic_favorite_border is gray/black.
                // If using tint:
                holder.btnFavorite
                        .setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
            }

            holder.btnFavorite.setOnClickListener(v -> presenter.onFavoriteClicked(meal));
            holder.btnAddPlan.setOnClickListener(v -> presenter.onAddToPlanClicked(meal));
            holder.itemView.setOnClickListener(v -> presenter.onMealClicked(meal));
        }

        @Override
        public int getItemCount() {
            return meals.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage, btnFavorite, btnAddPlan;
            TextView tvName, tvDetails;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.iv_meal_image);
                tvName = itemView.findViewById(R.id.tv_meal_name);
                tvDetails = itemView.findViewById(R.id.tv_meal_details);
                btnFavorite = itemView.findViewById(R.id.btn_favorite);
                btnAddPlan = itemView.findViewById(R.id.btn_add_to_plan);
            }
        }
    }
}
