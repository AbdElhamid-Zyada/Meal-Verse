package com.example.mealplanner.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mealplanner.R;
import com.example.mealplanner.repository.UserRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SplashFragment extends Fragment {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private com.example.mealplanner.repository.UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userRepository = UserRepositoryImpl.getInstance(requireContext());

        // Navigate to home after 2.5 seconds
        view.findViewById(R.id.iv_logo).animate().rotation(360f).setDuration(1000).start();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                checkUserSession(view);
            }
        }, 2500);
    }

    private void checkUserSession(View view) {
        disposables.add(userRepository.isUserRemembered()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isRemembered -> {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (isRemembered && currentUser != null) {
                                Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_homeFragment);
                            } else {
                                Navigation.findNavController(view)
                                        .navigate(R.id.action_splashFragment_to_loginFragment);
                            }
                        },
                        throwable -> {
                            // Fallback to login on error
                            Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_loginFragment);
                        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}
