package com.example.mealplanner.ui.splash;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private com.example.mealplanner.repository.MealRepository mealRepository;

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
        mealRepository = com.example.mealplanner.repository.MealRepositoryImpl.getInstance();

        // Navigate to home after 2.5 seconds
        view.findViewById(R.id.iv_logo).animate().rotation(360f).setDuration(1000).start();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                checkUserSession(view);
            }
        }, 2500);
    }

    private void checkUserSession(View view) {
        System.out.println("[SPLASH] Checking user session...");

        // Check network connectivity
        boolean isNetworkAvailable = isNetworkAvailable();
        System.out.println("[SPLASH] Network available: " + isNetworkAvailable);

        disposables.add(userRepository.isUserRemembered()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isRemembered -> {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            System.out.println("[SPLASH] isUserRemembered: " + isRemembered);
                            System.out.println("[SPLASH] Firebase currentUser: "
                                    + (currentUser != null ? currentUser.getUid() : "null"));

                            // Force login if offline, even if remembered
                            if (!isNetworkAvailable) {
                                System.out.println("[SPLASH] ⚠️ No network connection, forcing login");
                                Navigation.findNavController(view)
                                        .navigate(R.id.action_splashFragment_to_loginFragment);
                            } else if (isRemembered && currentUser != null) {
                                System.out.println("[SPLASH] User is logged in, starting sync...");
                                // Sync data from Firestore before navigating to home
                                syncDataFromFirestore(view, currentUser.getUid());
                            } else {
                                System.out.println("[SPLASH] User not logged in, navigating to login");
                                Navigation.findNavController(view)
                                        .navigate(R.id.action_splashFragment_to_loginFragment);
                            }
                        },
                        throwable -> {
                            // Fallback to login on error
                            System.err.println("[SPLASH ERROR] Error checking user session: " + throwable.getMessage());
                            throwable.printStackTrace();
                            Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_loginFragment);
                        }));
    }

    private void syncDataFromFirestore(View view, String userId) {
        System.out.println("[SPLASH] Starting Firestore sync for user: " + userId);
        // Sync saved meals and planned meals from Firestore
        disposables.add(
                mealRepository.syncSavedMealsFromFirestore(userId)
                        .doOnComplete(() -> System.out.println("[SPLASH] Saved meals sync completed"))
                        .doOnError(error -> System.err
                                .println("[SPLASH ERROR] Saved meals sync failed: " + error.getMessage()))
                        .andThen(mealRepository.syncPlannedMealsFromFirestore(userId))
                        .doOnComplete(() -> System.out.println("[SPLASH] Planned meals sync completed"))
                        .doOnError(error -> System.err
                                .println("[SPLASH ERROR] Planned meals sync failed: " + error.getMessage()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    // Sync complete, navigate to home
                                    System.out.println("[SPLASH] All sync completed successfully, navigating to home");
                                    if (isAdded()) {
                                        Navigation.findNavController(view)
                                                .navigate(R.id.action_splashFragment_to_homeFragment);
                                    }
                                },
                                throwable -> {
                                    // Even if sync fails, navigate to home (local data is still available)
                                    System.err.println(
                                            "[SPLASH ERROR] Error syncing from Firestore: " + throwable.getMessage());
                                    throwable.printStackTrace();
                                    if (isAdded()) {
                                        Navigation.findNavController(view)
                                                .navigate(R.id.action_splashFragment_to_homeFragment);
                                    }
                                }));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}
