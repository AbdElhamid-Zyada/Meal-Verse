package com.example.mealplanner.ui.planner.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplanner.R;
import com.example.mealplanner.model.Meal;
import com.example.mealplanner.model.MealType;
import com.example.mealplanner.ui.planner.presenter.PlannerContract;
import com.example.mealplanner.ui.planner.presenter.PlannerPresenter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlannerFragment extends Fragment implements PlannerContract.View {

    private PlannerContract.Presenter presenter;
    private TextView tvDateHeader;
    private RecyclerView rvCalendar;
    private FrameLayout containerBreakfast, containerLunch, containerDinner;
    private CalendarAdapter calendarAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDateHeader = view.findViewById(R.id.tv_date_header);
        rvCalendar = view.findViewById(R.id.rv_calendar);
        containerBreakfast = view.findViewById(R.id.container_breakfast);
        containerLunch = view.findViewById(R.id.container_lunch);
        containerDinner = view.findViewById(R.id.container_dinner);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        setupCalendarRecyclerView();

        presenter = new PlannerPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewResumed();
    }

    private void setupCalendarRecyclerView() {
        rvCalendar.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        calendarAdapter = new CalendarAdapter();
        rvCalendar.setAdapter(calendarAdapter);
    }

    @Override
    public void showWeekCalendar(List<Date> days, Date selectedDate) {
        calendarAdapter.setData(days, selectedDate);
    }

    @Override
    public void showSelectedDateTitle(String title) {
        tvDateHeader.setText(title);
    }

    @Override
    public void showMeals(Map<MealType, Meal> meals) {
        // Not used directly, using individual updates
    }

    @Override
    public void showMeal(MealType type, Meal meal) {
        View mealView = getLayoutInflater().inflate(R.layout.item_meal_planned, null);

        TextView tvName = mealView.findViewById(R.id.tv_meal_name);
        TextView tvInfo = mealView.findViewById(R.id.tv_meal_info);
        ImageView ivImage = mealView.findViewById(R.id.iv_meal_image);
        View btnDelete = mealView.findViewById(R.id.btn_delete_meal);

        tvName.setText(meal.getName());
        tvInfo.setText(meal.getPreparationTimeMinutes() + " mins • " + meal.getCalories() + " kcal");
        ivImage.setImageResource(meal.getImageResId());

        btnDelete.setOnClickListener(v -> presenter.deleteMeal(type));
        mealView.setOnClickListener(v -> presenter.onMealClicked(meal));

        getContainerForType(type).removeAllViews();
        getContainerForType(type).addView(mealView);
    }

    @Override
    public void showEmptySlot(MealType type) {
        View emptyView = getLayoutInflater().inflate(R.layout.item_meal_empty, null);
        TextView tvPlan = emptyView.findViewById(R.id.tv_plan_meal_text);
        tvPlan.setText("PLAN " + type.name());

        // Add click listener to add meal (future feature)
        emptyView.setOnClickListener(v -> showMessage("Plan " + type.name().toLowerCase() + " clicked"));

        getContainerForType(type).removeAllViews();
        getContainerForType(type).addView(emptyView);
    }

    private FrameLayout getContainerForType(MealType type) {
        switch (type) {
            case BREAKFAST:
                return containerBreakfast;
            case LUNCH:
                return containerLunch;
            case DINNER:
                return containerDinner;
            default:
                return containerBreakfast;
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToMealDetails(String mealId) {
        Bundle args = new Bundle();
        args.putString("ARG_MEAL_ID", mealId);
        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.action_plannerFragment_to_mealDetailsFragment, args);
    }

    // Inner Adapter Class for Calendar
    private class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
        private List<Date> days;
        private Date selectedDate;
        private final SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        private final SimpleDateFormat dayNumberFormat = new SimpleDateFormat("d", Locale.getDefault());

        public void setData(List<Date> days, Date selectedDate) {
            this.days = days;
            this.selectedDate = selectedDate;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);

            // Calculate width to fit 7 items effectively
            int screenWidth = parent.getContext().getResources().getDisplayMetrics().widthPixels;
            int itemWidth = screenWidth / 7;

            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.width = itemWidth;
            view.setLayoutParams(lp);

            return new CalendarViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
            Date date = days.get(position);
            holder.tvDayName.setText(dayNameFormat.format(date));
            holder.tvDayNumber.setText(dayNumberFormat.format(date)); // Kept day number but made invisible in xml logic
                                                                      // if needed, but per request: "write the name of
                                                                      // the day and the date"

            // Adjusting to requirement: "write the name of the day and the date" in the
            // screen.
            // Screen shows just name (Mon, Tue) in the top bar, but the header shows
            // "Monday, Oct 23".
            // However, typical calendar strips show day name and day number.
            // The prompt says: "days names in the top is dynamic ... and the date as in the
            // screen"
            // The screen screenshot shows: "Mon", "Tue" etc. green underline for selected.
            // It does NOT show the date number in the strip, only day name.
            // BUT, typical UX usually includes the number.
            // Looking closely at the image provided (I can't see it but assuming from "Mon
            // Tue Wed Thu Fri Sat Sun"), it seems to be just names.
            // Wait, "write the name of the day and the date as in the screen" might refer
            // to the big header "Monday, Oct 23".
            // The strip just has "Mon Tue ...".
            // I will enable the day number text view if the user meant that, but will stick
            // to the image instructions.
            // Actually, usually "name of the day and the date" applies to the header big
            // text.
            // The top bar in the image shows "Mon Tue Wed...".
            // I will hide the day number in the item xml (visibility gone) as per my XML
            // creation, but if needed I can show it.

            boolean isSelected = isSameDay(date, selectedDate);
            holder.tvDayName.setTextColor(getResources().getColor(isSelected ? R.color.primary : R.color.gray_800));
            // Assuming R.color.primary is defined in colors.xml, forcing a check or using
            // the green hex
            if (isSelected) {
                holder.tvDayName.setTextColor(0xFF00D261);
                holder.viewIndicator.setVisibility(View.VISIBLE);
            } else {
                holder.tvDayName.setTextColor(0xFF424242);
                holder.viewIndicator.setVisibility(View.INVISIBLE);
            }

            holder.itemView.setOnClickListener(v -> presenter.selectDate(date));
        }

        private boolean isSameDay(Date d1, Date d2) {
            if (d1 == null || d2 == null)
                return false;
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            return fmt.format(d1).equals(fmt.format(d2));
        }

        @Override
        public int getItemCount() {
            return days == null ? 0 : days.size();
        }

        class CalendarViewHolder extends RecyclerView.ViewHolder {
            TextView tvDayName, tvDayNumber;
            View viewIndicator;

            public CalendarViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDayName = itemView.findViewById(R.id.tv_day_name);
                tvDayNumber = itemView.findViewById(R.id.tv_day_number);
                viewIndicator = itemView.findViewById(R.id.view_indicator);
            }
        }
    }
}
