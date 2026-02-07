package com.example.mealplanner.ui.countries.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mealplanner.R;
import com.example.mealplanner.ui.countries.presenter.CountriesContract;
import com.example.mealplanner.ui.countries.presenter.CountriesPresenter;

public class CountriesFragment extends Fragment implements CountriesContract.View {
    private CountriesContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countries, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new CountriesPresenter(this);
    }
}

