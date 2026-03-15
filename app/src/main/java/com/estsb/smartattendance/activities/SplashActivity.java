package com.estsb.smartattendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.estsb.smartattendance.R;
import com.estsb.smartattendance.utils.SessionManager;

/**
 * Splash screen activity - first screen displayed when the app launches.
 * Shows the EST SB logo and app name with a premium fade-in animation,
 * then navigates to either the dashboard (if remembered) or login screen.
 *
 * @author EST SB Smart Attendance
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get views
        ImageView logoImage = findViewById(R.id.splashLogo);
        TextView titleText = findViewById(R.id.splashTitle);
        TextView subtitleText = findViewById(R.id.splashSubtitle);
        TextView footerText = findViewById(R.id.splashFooter);

        // Fade-in + slide-up animation for logo
        AnimationSet logoAnim = new AnimationSet(true);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        TranslateAnimation slideUp = new TranslateAnimation(0, 0, 60, 0);
        slideUp.setDuration(1000);
        logoAnim.addAnimation(fadeIn);
        logoAnim.addAnimation(slideUp);
        logoImage.startAnimation(logoAnim);

        // Delayed fade-in for title
        AlphaAnimation titleFade = new AlphaAnimation(0f, 1f);
        titleFade.setDuration(800);
        titleFade.setStartOffset(500);
        titleFade.setFillAfter(true);
        titleText.startAnimation(titleFade);

        // Delayed fade-in for subtitle
        AlphaAnimation subtitleFade = new AlphaAnimation(0f, 1f);
        subtitleFade.setDuration(800);
        subtitleFade.setStartOffset(800);
        subtitleFade.setFillAfter(true);
        subtitleText.startAnimation(subtitleFade);

        // Delayed fade-in for footer
        AlphaAnimation footerFade = new AlphaAnimation(0f, 1f);
        footerFade.setDuration(600);
        footerFade.setStartOffset(1200);
        footerFade.setFillAfter(true);
        footerText.startAnimation(footerFade);

        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);

            Intent intent;
            if (sessionManager.isLoggedIn() && sessionManager.isRememberMe()) {
                // User is remembered - go directly to dashboard
                String role = sessionManager.getUserRole();
                switch (role) {
                    case "professor":
                        intent = new Intent(this, ProfessorDashboardActivity.class);
                        break;
                    case "admin":
                        intent = new Intent(this, ProfessorDashboardActivity.class);
                        break;
                    default:
                        intent = new Intent(this, StudentDashboardActivity.class);
                        break;
                }
            } else {
                intent = new Intent(this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DURATION);
    }
}
