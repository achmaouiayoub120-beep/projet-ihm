package com.estsb.smartattendance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.estsb.smartattendance.R;
import com.estsb.smartattendance.database.DatabaseHelper;
import com.estsb.smartattendance.interfaces.UserRoleNavigator;
import com.estsb.smartattendance.models.User;
import com.estsb.smartattendance.utils.Constants;
import com.estsb.smartattendance.utils.SessionManager;

/**
 * Login screen activity - authenticates users and routes to role-based dashboards.
 *
 * This Activity implements the {@link UserRoleNavigator} Java interface,
 * which is one of the 3 mandatory interfaces required by the professor.
 * After successful login, the {@code navigateToDashboard} method routes
 * the user to the correct dashboard based on their role.
 *
 * Features:
 * - Email/password authentication against SQLite database
 * - Show/hide password toggle
 * - "Se souvenir de moi" (Remember Me) for auto-login on next launch
 * - Demo account quick-fill buttons
 * - Premium glassmorphism entry animations
 *
 * @author EST SB Smart Attendance
 */
public class LoginActivity extends AppCompatActivity implements UserRoleNavigator {

    private EditText editEmail, editPassword;
    private CheckBox checkRemember;
    private TextView btnLogin;
    private ImageView btnTogglePassword;
    private LinearLayout loginCard;

    private boolean isPasswordVisible = false;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize helpers
        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        // Bind views
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        checkRemember = findViewById(R.id.checkRemember);
        btnLogin = findViewById(R.id.btnLogin);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        loginCard = findViewById(R.id.loginCard);

        // Setup animations
        setupEntryAnimations();

        // Toggle password visibility
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Login button click
        btnLogin.setOnClickListener(v -> attemptLogin());

        // Demo account quick-fill listeners
        findViewById(R.id.demoStudent).setOnClickListener(v -> fillDemoAccount("student@estsb.ma"));
        findViewById(R.id.demoProfessor).setOnClickListener(v -> fillDemoAccount("prof@estsb.ma"));
        findViewById(R.id.demoAdmin).setOnClickListener(v -> fillDemoAccount("admin@estsb.ma"));
    }

    /**
     * Premium entry animations: fade-in + slide-up for header and card.
     */
    private void setupEntryAnimations() {
        // Header animation
        View header = findViewById(R.id.headerSection);
        AnimationSet headerAnim = new AnimationSet(true);
        headerAnim.addAnimation(createFadeIn(0, 600));
        headerAnim.addAnimation(createSlideDown(0, 600));
        header.startAnimation(headerAnim);

        // Card slide-up animation
        AnimationSet cardAnim = new AnimationSet(true);
        cardAnim.addAnimation(createFadeIn(300, 500));
        cardAnim.addAnimation(createSlideUp(300, 500));
        loginCard.startAnimation(cardAnim);

        // Footer fade-in
        View footer = findViewById(R.id.footerText);
        footer.startAnimation(createFadeIn(600, 400));
    }

    private AlphaAnimation createFadeIn(long startOffset, long duration) {
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(duration);
        anim.setStartOffset(startOffset);
        anim.setFillAfter(true);
        return anim;
    }

    private TranslateAnimation createSlideUp(long startOffset, long duration) {
        TranslateAnimation anim = new TranslateAnimation(0, 0, 80, 0);
        anim.setDuration(duration);
        anim.setStartOffset(startOffset);
        anim.setFillAfter(true);
        return anim;
    }

    private TranslateAnimation createSlideDown(long startOffset, long duration) {
        TranslateAnimation anim = new TranslateAnimation(0, 0, -40, 0);
        anim.setDuration(duration);
        anim.setStartOffset(startOffset);
        anim.setFillAfter(true);
        return anim;
    }

    /**
     * Toggle password field visibility.
     */
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
        } else {
            editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(android.R.drawable.ic_secure);
        }
        editPassword.setSelection(editPassword.getText().length());
    }

    /**
     * Validate input and attempt login against the database.
     */
    private void attemptLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Validate empty fields
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.login_error_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate credentials against database
        User user = dbHelper.validateLogin(email, password);

        if (user != null) {
            // Login successful - save session
            boolean rememberMe = checkRemember.isChecked();
            sessionManager.createLoginSession(user, rememberMe);

            // Navigate to appropriate dashboard using the interface method
            navigateToDashboard(user.getRole());
        } else {
            // Login failed
            Toast.makeText(this, getString(R.string.login_error_invalid), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fill demo account email and password fields.
     */
    private void fillDemoAccount(String email) {
        editEmail.setText(email);
        editPassword.setText("password");
    }

    // =========================================================================
    // UserRoleNavigator Interface Implementation
    // =========================================================================

    /**
     * Navigate to the correct dashboard based on user role.
     * Implements UserRoleNavigator interface method.
     */
    @Override
    public void navigateToDashboard(String role) {
        Intent intent;
        switch (role) {
            case Constants.ROLE_PROFESSOR:
                intent = new Intent(this, ProfessorDashboardActivity.class);
                break;
            case Constants.ROLE_ADMIN:
                // Admin uses professor dashboard with extended access
                intent = new Intent(this, ProfessorDashboardActivity.class);
                break;
            default:
                intent = new Intent(this, StudentDashboardActivity.class);
                break;
        }
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Get available actions for each role.
     * Implements UserRoleNavigator interface method.
     */
    @Override
    public String[] getAvailableActions(String role) {
        switch (role) {
            case Constants.ROLE_PROFESSOR:
                return new String[]{"generate_qr", "view_history", "manage_sessions"};
            case Constants.ROLE_ADMIN:
                return new String[]{"generate_qr", "view_history", "manage_sessions", "admin_panel"};
            default: // student
                return new String[]{"scan_qr", "view_history"};
        }
    }

    /**
     * Handle quick action clicks (delegated to dashboard activities).
     * Implements UserRoleNavigator interface method.
     */
    @Override
    public void onQuickActionClicked(String actionId) {
        // This method is primarily used by dashboard activities
        // LoginActivity delegates to the appropriate dashboard
    }
}
