package com.example.mealplanner.ui.countries.view;

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
import com.example.mealplanner.model.Country;
import com.example.mealplanner.ui.countries.presenter.CountriesContract;
import com.example.mealplanner.ui.countries.presenter.CountriesPresenter;
import com.example.mealplanner.ui.search.view.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class CountriesFragment extends Fragment implements CountriesContract.View {

    private CountriesContract.Presenter presenter;
    private RecyclerView rvCountries;
    private CountriesAdapter adapter;
    private EditText etSearch;
    private ImageView btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCountries = view.findViewById(R.id.rv_countries);
        etSearch = view.findViewById(R.id.et_search_countries);
        btnBack = view.findViewById(R.id.btn_back);

        // Setup Grid with 2 columns
        rvCountries.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new CountriesAdapter();
        rvCountries.setAdapter(adapter);

        presenter = new CountriesPresenter(this);

        // Listeners
        btnBack.setOnClickListener(v -> presenter.onBackClicked());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.searchCountries(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Load initial data
        presenter.loadCountries();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dispose();
    }

    @Override
    public void showCountries(List<Country> countries) {
        adapter.setCountries(countries);
    }

    @Override
    public void showLoading() {
        // Optional
    }

    @Override
    public void hideLoading() {
        // Optional
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
    public void navigateToSearch(String countryName) {
        Bundle args = new Bundle();
        args.putSerializable(SearchFragment.ARG_FILTER_TYPE, com.example.mealplanner.model.FilterType.AREA);
        args.putString(SearchFragment.ARG_FILTER_VALUE, countryName);
        Navigation.findNavController(requireView()).navigate(R.id.action_countriesFragment_to_searchFragment, args);
    }

    // Adapter Class
    private class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.ViewHolder> {
        private List<Country> countries = new ArrayList<>();

        public void setCountries(List<Country> countries) {
            this.countries = countries;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Country country = countries.get(position);
            holder.tvName.setText(country.getName());
            holder.ivImage.setImageResource(country.getImageResource());

            holder.itemView.setOnClickListener(v -> presenter.onCountryClicked(country));
        }

        @Override
        public int getItemCount() {
            return countries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.iv_country_image);
                tvName = itemView.findViewById(R.id.tv_country_name);
            }
        }
    }
}
