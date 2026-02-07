package com.example.mealplanner.ui.planner;

public class PlannerPresenter implements PlannerContract.Presenter {
    private PlannerContract.View mView;

    public PlannerPresenter(PlannerContract.View view) {
        this.mView = view;
    }
}
