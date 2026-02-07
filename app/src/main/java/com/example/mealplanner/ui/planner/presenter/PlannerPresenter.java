package com.example.mealplanner.ui.planner.presenter;

public class PlannerPresenter implements PlannerContract.Presenter {
    private PlannerContract.View mView;

    public PlannerPresenter(PlannerContract.View view) {
        this.mView = view;
    }
}
