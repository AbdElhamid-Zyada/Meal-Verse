package com.example.mealplanner.ui.planner.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mealplanner.R;
import com.example.mealplanner.ui.planner.presenter.PlannerContract;
import com.example.mealplanner.ui.planner.presenter.PlannerPresenter;

public class PlannerFragment extends Fragment implements PlannerContract.View {
    private PlannerContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planner, container, false);
    }
}

