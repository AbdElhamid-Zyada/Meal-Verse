package com.example.mealplanner.ui.details.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;
import com.example.mealplanner.ui.details.presenter.MealDetailsContract;
import com.example.mealplanner.ui.details.presenter.MealDetailsPresenter;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealDetailsFragment extends Fragment implements MealDetailsContract.View {

    private MealDetailsContract.Presenter presenter;

    private ImageView ivMealImage;
    private TextView tvTitle, tvDesc;
    private Chip chipArea, chipCategory, chipOffline;
    private RecyclerView rvIngredients, rvInstructions;
    private WebView webViewYouTube;
    private FloatingActionButton fabFavorite, fabAddPlan;
    private Toolbar toolbar;

    private IngredientsAdapter ingredientsAdapter;
    private InstructionsAdapter instructionsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        ivMealImage = view.findViewById(R.id.iv_meal_detail);
        tvTitle = view.findViewById(R.id.tv_meal_title);
        tvDesc = view.findViewById(R.id.tv_meal_desc);
        chipArea = view.findViewById(R.id.chip_area);
        chipCategory = view.findViewById(R.id.chip_category);
        chipOffline = view.findViewById(R.id.chip_offline);
        rvIngredients = view.findViewById(R.id.rv_ingredients);
        rvInstructions = view.findViewById(R.id.rv_instructions);
        webViewYouTube = view.findViewById(R.id.webview_youtube);
        fabFavorite = view.findViewById(R.id.fab_favorite);
        fabAddPlan = view.findViewById(R.id.fab_add_plan);
        toolbar = view.findViewById(R.id.toolbar);

        // Configure WebView
        WebSettings webSettings = webViewYouTube.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webViewYouTube.setWebViewClient(new WebViewClient());

        // Initialize Presenter
        // Initialize Presenter
        presenter = new MealDetailsPresenter(this,
                com.example.mealplanner.repository.UserRepositoryImpl.getInstance(requireContext()));

        // Setup Toolbar
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // Setup Listeners
        fabFavorite.setOnClickListener(v -> presenter.onFavoriteClicked());
        fabAddPlan.setOnClickListener(v -> presenter.onAddToPlanClicked());

        // Setup RecyclerViews
        ingredientsAdapter = new IngredientsAdapter();
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvIngredients.setAdapter(ingredientsAdapter);

        instructionsAdapter = new InstructionsAdapter();
        rvInstructions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInstructions.setAdapter(instructionsAdapter);

        // Load Data
        String mealId = null;
        if (getArguments() != null) {
            // Try both possible argument keys
            mealId = getArguments().getString("mealId");
            if (mealId == null) {
                mealId = getArguments().getString("ARG_MEAL_ID");
            }
        }

        if (mealId != null) {
            presenter.loadMealDetails(mealId);
        } else {
            showMessage("No meal ID provided");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dispose();
    }

    @Override
    public void showMealDetails(Meal meal) {
        tvTitle.setText(meal.getName());

        // Load meal image with Glide
        if (meal.getImageUrl() != null && !meal.getImageUrl().isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                    .load(meal.getImageUrl())
                    .placeholder(R.drawable.login_hero_image)
                    .error(R.drawable.login_hero_image)
                    .centerCrop()
                    .into(ivMealImage);
        } else {
            ivMealImage.setImageResource(R.drawable.login_hero_image);
        }

        chipArea.setText(meal.getArea() != null ? meal.getArea() : "Global");
        chipCategory.setText(meal.getCategory() != null ? meal.getCategory() : "Misc");

        // Tagline
        tvDesc.setText("A delicious " + (meal.getArea() != null ? meal.getArea() : "") + " "
                + (meal.getCategory() != null ? meal.getCategory() : "") + " dish.");
    }

    @Override
    public void showIngredients(List<String> ingredients, List<String> measures) {
        ingredientsAdapter.setData(ingredients, measures);
    }

    @Override
    public void showInstructions(List<String> steps) {
        instructionsAdapter.setSteps(steps);
    }

    @Override
    public void showVideo(String videoId) {
        String embedUrl = "https://www.youtube.com/embed/" + videoId;
        webViewYouTube.loadUrl(embedUrl);
    }

    @Override
    public void setFavoriteState(boolean isFavorite) {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_favorite);
            fabFavorite.setColorFilter(android.graphics.Color.RED);
        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorite_border);
            fabFavorite.clearColorFilter();
            fabFavorite.setColorFilter(getResources().getColor(R.color.text_primary));
        }
    }

    @Override
    public void showOfflineBadge(boolean isVisible) {
        chipOffline.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
                    presenter.onDateSelected(availableDates.get(which));
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
                    presenter.onMealTypeSelected(date, type);
                })
                .show();
    }

    // Adapters

    private class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
        private List<String> ingredients = new ArrayList<>();
        private List<String> measures = new ArrayList<>();

        public void setData(List<String> ingredients, List<String> measures) {
            this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
            this.measures = measures != null ? measures : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_circle, parent,
                    false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String name = (ingredients.size() > position) ? ingredients.get(position) : "";
            String measure = (measures.size() > position) ? measures.get(position) : "";

            holder.tvName.setText(name);
            holder.tvMeasure.setText(measure);

            // Load ingredient thumbnail with Glide
            if (name != null && !name.isEmpty()) {
                String imageUrl = "https://www.themealdb.com/images/ingredients/"
                        + name + "-Small.png";

                com.bumptech.glide.Glide.with(MealDetailsFragment.this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_restaurant_menu)
                        .error(R.drawable.ic_restaurant_menu)
                        .centerCrop()
                        .into(holder.ivImage);
            } else {
                holder.ivImage.setImageResource(R.drawable.ic_restaurant_menu);
            }
        }

        @Override
        public int getItemCount() {
            return ingredients.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvName, tvMeasure;

            ViewHolder(View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.iv_ingredient);
                tvName = itemView.findViewById(R.id.tv_ingredient_name);
                tvMeasure = itemView.findViewById(R.id.tv_measure);
            }
        }
    }

    private class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.ViewHolder> {
        private List<String> steps = new ArrayList<>();

        public void setSteps(List<String> steps) {
            this.steps = steps;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_instruction_step, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvStepNumber.setText(String.valueOf(position + 1));
            holder.tvInstruction.setText(steps.get(position));
        }

        @Override
        public int getItemCount() {
            return steps.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvStepNumber, tvInstruction;

            ViewHolder(View itemView) {
                super(itemView);
                tvStepNumber = itemView.findViewById(R.id.tv_step_number);
                tvInstruction = itemView.findViewById(R.id.tv_instruction);
            }
        }
    }
}
