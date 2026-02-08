package com.example.mealplanner.ui.ingredients.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.model.Ingredient;
import com.example.mealplanner.ui.ingredients.presenter.IngredientsContract;
import com.example.mealplanner.ui.ingredients.presenter.IngredientsPresenter;
import com.example.mealplanner.ui.search.view.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class IngredientsFragment extends Fragment implements IngredientsContract.View {

    private IngredientsContract.Presenter presenter;
    private RecyclerView rvIngredients;
    private IngredientsAdapter adapter;
    private EditText etSearch;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingredients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvIngredients = view.findViewById(R.id.rv_ingredients);
        etSearch = view.findViewById(R.id.et_search_ingredients);
        btnBack = view.findViewById(R.id.btn_back);

        // Setup Grid with 2 columns
        rvIngredients.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new IngredientsAdapter();
        rvIngredients.setAdapter(adapter);

        presenter = new IngredientsPresenter(this);

        // Listeners
        btnBack.setOnClickListener(v -> presenter.onBackClicked());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.searchIngredients(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Load initial data
        presenter.loadIngredients();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dispose();
    }

    @Override
    public void showIngredients(List<Ingredient> ingredients) {
        adapter.setIngredients(ingredients);
    }

    @Override
    public void showLoading() {
        // Optional loading handling
    }

    @Override
    public void hideLoading() {
        // Optional loading handling
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateBack() {
        Navigation.findNavController(requireView()).navigateUp();
    }

    @Override
    public void navigateToSearch(String ingredientName) {
        Bundle args = new Bundle();
        args.putSerializable(SearchFragment.ARG_FILTER_TYPE, com.example.mealplanner.model.FilterType.INGREDIENT);
        args.putString(SearchFragment.ARG_FILTER_VALUE, ingredientName);
        Navigation.findNavController(requireView()).navigate(R.id.action_ingredientsFragment_to_searchFragment, args);
    }

    // Adapter Class
    private class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
        private List<Ingredient> ingredients = new ArrayList<>();

        public void setIngredients(List<Ingredient> ingredients) {
            this.ingredients = ingredients;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Ingredient ingredient = ingredients.get(position);
            holder.tvName.setText(ingredient.getName());
            holder.ivImage.setImageResource(ingredient.getImageResource());

            holder.itemView.setOnClickListener(v -> presenter.onIngredientClicked(ingredient));
        }

        @Override
        public int getItemCount() {
            return ingredients.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.iv_ingredient_image);
                tvName = itemView.findViewById(R.id.tv_ingredient_name);
            }
        }
    }
}
