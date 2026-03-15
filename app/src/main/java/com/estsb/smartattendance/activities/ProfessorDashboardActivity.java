package com.estsb.smartattendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.estsb.smartattendance.R;
import com.estsb.smartattendance.database.DatabaseHelper;
import com.estsb.smartattendance.interfaces.UserRoleNavigator;
import com.estsb.smartattendance.models.User;
import com.estsb.smartattendance.utils.Constants;
import com.estsb.smartattendance.utils.SessionManager;

/**
 * Professor Dashboard activity - main screen for professors after login.
 * Shows welcome section, statistics cards, and quick action buttons.
 * Implements UserRoleNavigator for consistent role-based navigation.
 *
 * @author EST SB Smart Attendance
 */
public class ProfessorDashboardActivity extends AppCompatActivity implements UserRoleNavigator {

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_dashboard);

        sessionManager = new SessionManager(this);
        dbHelper = DatabaseHelper.getInstance(this);
        currentUser = sessionManager.getCurrentUser();

        if (currentUser == null) {
            navigateToDashboard(Constants.ROLE_STUDENT);
            return;
        }

        setupHeader();
        setupStats();
        setupQuickActions();
        setupEntryAnimations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh stats when returning from other activities
        setupStats();
    }

    /**
     * Setup header section with user info and avatar.
     */
    private void setupHeader() {
        TextView txtWelcome = findViewById(R.id.txtWelcome);
        TextView txtUserName = findViewById(R.id.txtUserName);
        TextView txtUserEmail = findViewById(R.id.txtUserEmail);
        TextView txtAvatar = findViewById(R.id.txtAvatar);
        TextView txtRoleBadge = findViewById(R.id.txtRoleBadge);
        TextView btnLogout = findViewById(R.id.btnLogout);

        txtWelcome.setText(getString(R.string.welcome_prefix));
        txtUserName.setText(currentUser.getFullName());
        txtUserEmail.setText(currentUser.getEmail());
        txtAvatar.setText(currentUser.getInitials());
        txtRoleBadge.setText(currentUser.getRoleLabel());

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Setup statistics cards with data from the database.
     */
    private void setupStats() {
        int userId = currentUser.getId();
        int sessionCount = dbHelper.getSessionCount(userId);
        int totalAttendance = dbHelper.getTotalAttendanceCount(userId);
        int presentCount = dbHelper.getAttendanceCountByStatus(userId, Constants.STATUS_PRESENT);
        int rate = totalAttendance > 0 ? (presentCount * 100 / totalAttendance) : 0;

        TextView txtStatSessions = findViewById(R.id.txtStatSessions);
        TextView txtStatAttendance = findViewById(R.id.txtStatAttendance);
        TextView txtStatRate = findViewById(R.id.txtStatRate);

        txtStatSessions.setText(String.valueOf(sessionCount));
        txtStatAttendance.setText(String.valueOf(totalAttendance));
        txtStatRate.setText(rate + "%");
    }

    /**
     * Setup quick action buttons.
     */
    private void setupQuickActions() {
        findViewById(R.id.btnGenerateQR).setOnClickListener(v ->
                onQuickActionClicked("generate_qr"));

        findViewById(R.id.btnViewHistory).setOnClickListener(v ->
                onQuickActionClicked("view_history"));
    }

    /**
     * Entry animations for premium feel.
     */
    private void setupEntryAnimations() {
        View statsSection = findViewById(R.id.statsSection);
        View actionsSection = findViewById(R.id.actionsSection);

        AnimationSet statsAnim = new AnimationSet(true);
        AlphaAnimation fadeIn1 = new AlphaAnimation(0f, 1f);
        fadeIn1.setDuration(500);
        fadeIn1.setStartOffset(200);
        TranslateAnimation slideUp1 = new TranslateAnimation(0, 0, 40, 0);
        slideUp1.setDuration(500);
        slideUp1.setStartOffset(200);
        statsAnim.addAnimation(fadeIn1);
        statsAnim.addAnimation(slideUp1);
        statsSection.startAnimation(statsAnim);

        AnimationSet actionsAnim = new AnimationSet(true);
        AlphaAnimation fadeIn2 = new AlphaAnimation(0f, 1f);
        fadeIn2.setDuration(500);
        fadeIn2.setStartOffset(400);
        TranslateAnimation slideUp2 = new TranslateAnimation(0, 0, 40, 0);
        slideUp2.setDuration(500);
        slideUp2.setStartOffset(400);
        actionsAnim.addAnimation(fadeIn2);
        actionsAnim.addAnimation(slideUp2);
        actionsSection.startAnimation(actionsAnim);
    }

    // =========================================================================
    // UserRoleNavigator Interface Implementation
    // =========================================================================

    @Override
    public void navigateToDashboard(String role) {
        Intent intent;
        if (role.equals(Constants.ROLE_STUDENT)) {
            intent = new Intent(this, StudentDashboardActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public String[] getAvailableActions(String role) {
        return new String[]{"generate_qr", "view_history", "manage_sessions"};
    }

    @Override
    public void onQuickActionClicked(String actionId) {
        switch (actionId) {
            case "generate_qr":
                startActivity(new Intent(this, GenerateQRActivity.class));
                break;
            case "view_history":
                startActivity(new Intent(this, HistoryActivity.class));
                break;
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
