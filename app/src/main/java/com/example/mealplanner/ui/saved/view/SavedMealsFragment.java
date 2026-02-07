package com.example.mealplanner.ui.saved.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mealplanner.R;
import com.example.mealplanner.ui.saved.presenter.SavedMealsContract;
import com.example.mealplanner.ui.saved.presenter.SavedMealsPresenter;

public class SavedMealsFragment extends Fragment implements SavedMealsContract.View {
    private SavedMealsContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_meals, container, false);
    }
}

