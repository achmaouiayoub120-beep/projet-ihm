package com.estsb.smartattendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.estsb.smartattendance.R;
import com.estsb.smartattendance.database.DatabaseHelper;
import com.estsb.smartattendance.interfaces.UserRoleNavigator;
import com.estsb.smartattendance.models.Attendance;
import com.estsb.smartattendance.models.User;
import com.estsb.smartattendance.utils.Constants;
import com.estsb.smartattendance.utils.SessionManager;

import java.util.List;

/**
 * Student Dashboard activity - main screen for students after login.
 * Shows welcome section, attendance summary, and quick action buttons.
 * Implements UserRoleNavigator for consistent role-based navigation.
 *
 * @author EST SB Smart Attendance
 */
public class StudentDashboardActivity extends AppCompatActivity implements UserRoleNavigator {

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        sessionManager = new SessionManager(this);
        dbHelper = DatabaseHelper.getInstance(this);
        currentUser = sessionManager.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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
        setupStats();
    }

    private void setupHeader() {
        TextView txtUserName = findViewById(R.id.txtStudentName);
        TextView txtUserEmail = findViewById(R.id.txtStudentEmail);
        TextView txtAvatar = findViewById(R.id.txtStudentAvatar);
        TextView txtRoleBadge = findViewById(R.id.txtStudentRole);
        TextView btnLogout = findViewById(R.id.btnStudentLogout);

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

    private void setupStats() {
        List<Attendance> myAttendance = dbHelper.getAttendanceByStudent(currentUser.getId());
        int total = myAttendance.size();
        int present = 0, absent = 0;
        for (Attendance a : myAttendance) {
            if (a.getStatus().equals(Constants.STATUS_PRESENT)) present++;
            else if (a.getStatus().equals(Constants.STATUS_ABSENT)) absent++;
        }
        int rate = total > 0 ? (present * 100 / total) : 0;

        ((TextView) findViewById(R.id.txtStudentPresent)).setText(String.valueOf(present));
        ((TextView) findViewById(R.id.txtStudentAbsent)).setText(String.valueOf(absent));
        ((TextView) findViewById(R.id.txtStudentRate)).setText(rate + "%");
    }

    private void setupQuickActions() {
        findViewById(R.id.btnScanQR).setOnClickListener(v ->
                onQuickActionClicked("scan_qr"));

        findViewById(R.id.btnStudentHistory).setOnClickListener(v ->
                onQuickActionClicked("view_history"));
    }

    private void setupEntryAnimations() {
        AnimationSet statsAnim = new AnimationSet(true);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(500);
        fadeIn.setStartOffset(200);
        TranslateAnimation slideUp = new TranslateAnimation(0, 0, 40, 0);
        slideUp.setDuration(500);
        slideUp.setStartOffset(200);
        statsAnim.addAnimation(fadeIn);
        statsAnim.addAnimation(slideUp);
        findViewById(R.id.studentStatsSection).startAnimation(statsAnim);

        AnimationSet actionsAnim = new AnimationSet(true);
        AlphaAnimation fadeIn2 = new AlphaAnimation(0f, 1f);
        fadeIn2.setDuration(500);
        fadeIn2.setStartOffset(400);
        TranslateAnimation slideUp2 = new TranslateAnimation(0, 0, 40, 0);
        slideUp2.setDuration(500);
        slideUp2.setStartOffset(400);
        actionsAnim.addAnimation(fadeIn2);
        actionsAnim.addAnimation(slideUp2);
        findViewById(R.id.studentActionsSection).startAnimation(actionsAnim);
    }

    // UserRoleNavigator Implementation
    @Override
    public void navigateToDashboard(String role) {
        Intent intent;
        if (role.equals(Constants.ROLE_PROFESSOR) || role.equals(Constants.ROLE_ADMIN)) {
            intent = new Intent(this, ProfessorDashboardActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public String[] getAvailableActions(String role) {
        return new String[]{"scan_qr", "view_history"};
    }

    @Override
    public void onQuickActionClicked(String actionId) {
        switch (actionId) {
            case "scan_qr":
                startActivity(new Intent(this, ScanQRActivity.class));
                break;
            case "view_history":
                startActivity(new Intent(this, HistoryActivity.class));
                break;
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
