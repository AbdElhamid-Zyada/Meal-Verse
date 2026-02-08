package com.example.mealplanner.ui.saved.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.ui.saved.presenter.SavedMealsContract;
import com.example.mealplanner.ui.saved.presenter.SavedMealsPresenter;

import java.util.ArrayList;
import java.util.List;

public class SavedMealsFragment extends Fragment implements SavedMealsContract.View {

    private SavedMealsContract.Presenter presenter;
    private RecyclerView rvSavedMeals;
    private SavedMealsAdapter adapter;
    private TextView tvSavedCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_meals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSavedMeals = view.findViewById(R.id.rv_saved_meals);
        tvSavedCount = view.findViewById(R.id.tv_saved_count);

        rvSavedMeals.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SavedMealsAdapter();
        rvSavedMeals.setAdapter(adapter);

        presenter = new SavedMealsPresenter(this);
        presenter.loadSavedMeals();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dispose();
    }

    @Override
    public void showSavedMeals(List<Meal> meals) {
        adapter.setMeals(meals);
        tvSavedCount.setText(meals.size() + " Saved Meals");
    }

    @Override
    public void showEmptyState() {
        adapter.setMeals(new ArrayList<>());
        tvSavedCount.setText("0 Saved Meals");
        // Optional: Show empty illustration
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        // Show loading if implemented
    }

    @Override
    public void hideLoading() {
        // Hide loading
    }

    @Override
    public void navigateToMealDetails(String mealId) {
        Bundle args = new Bundle();
        args.putString("ARG_MEAL_ID", mealId);
        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.action_savedMealsFragment_to_mealDetailsFragment, args);
    }

    private class SavedMealsAdapter extends RecyclerView.Adapter<SavedMealsAdapter.ViewHolder> {
        private List<Meal> meals = new ArrayList<>();

        public void setMeals(List<Meal> meals) {
            this.meals = meals;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal_saved, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Meal meal = meals.get(position);
            holder.tvName.setText(meal.getName());

            // Format info (assuming we have category/area if available, or just mocking)
            // For now using the mock info style
            // Assuming Meal class has getters if updated, but current model from before:
            // id, name, imageResId, calories, preparationTimeMinutes, type
            // It doesn't have area/category fields yet unless I add them.
            // I'll stick to what we have or add hardcoded placeholders for visuals as per
            // previous mock data.
            holder.tvInfo.setText(meal.getCalories() + " kcal • " + meal.getPreparationTimeMinutes() + " mins");

            holder.ivImage.setImageResource(meal.getImageResId());

            // Heart is filled
            holder.ivFavorite.setImageResource(R.drawable.ic_favorite);

            holder.ivFavorite.setOnClickListener(v -> {
                // Animate to outlined -> remove
                holder.ivFavorite.setImageResource(R.drawable.ic_favorite_border);
                presenter.removeMeal(meal);
            });

            holder.itemView.setOnClickListener(v -> presenter.onMealClicked(meal));
        }

        @Override
        public int getItemCount() {
            return meals.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage, ivFavorite;
            TextView tvName, tvInfo;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.iv_meal_image);
                ivFavorite = itemView.findViewById(R.id.iv_favorite);
                tvName = itemView.findViewById(R.id.tv_meal_name);
                tvInfo = itemView.findViewById(R.id.tv_meal_info);
            }
        }
    }
}
