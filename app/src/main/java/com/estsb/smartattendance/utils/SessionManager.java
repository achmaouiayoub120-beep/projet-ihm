package com.estsb.smartattendance.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.estsb.smartattendance.models.User;

/**
 * Session manager using SharedPreferences.
 * Handles login state persistence, "remember me" functionality,
 * and current user data storage.
 *
 * @author EST SB Smart Attendance
 */
public class SessionManager {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    /**
     * Constructor - initializes SharedPreferences.
     *
     * @param context Application or Activity context
     */
    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Save user login session.
     * Stores user data in SharedPreferences for session persistence.
     *
     * @param user       The authenticated user
     * @param rememberMe Whether to persist the session across app restarts
     */
    public void createLoginSession(User user, boolean rememberMe) {
        editor.putBoolean(Constants.PREF_IS_LOGGED_IN, true);
        editor.putInt(Constants.PREF_USER_ID, user.getId());
        editor.putString(Constants.PREF_USER_EMAIL, user.getEmail());
        editor.putString(Constants.PREF_USER_FIRST_NAME, user.getFirstName());
        editor.putString(Constants.PREF_USER_LAST_NAME, user.getLastName());
        editor.putString(Constants.PREF_USER_ROLE, user.getRole());
        editor.putBoolean(Constants.PREF_REMEMBER_ME, rememberMe);
        editor.apply();
    }

    /**
     * Check if user is currently logged in.
     *
     * @return true if a valid login session exists
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.PREF_IS_LOGGED_IN, false);
    }

    /**
     * Get the currently logged-in user's data from SharedPreferences.
     *
     * @return User object with stored data, or null if not logged in
     */
    public User getCurrentUser() {
        if (!isLoggedIn()) return null;

        User user = new User();
        user.setId(prefs.getInt(Constants.PREF_USER_ID, -1));
        user.setEmail(prefs.getString(Constants.PREF_USER_EMAIL, ""));
        user.setFirstName(prefs.getString(Constants.PREF_USER_FIRST_NAME, ""));
        user.setLastName(prefs.getString(Constants.PREF_USER_LAST_NAME, ""));
        user.setRole(prefs.getString(Constants.PREF_USER_ROLE, ""));
        return user;
    }

    /**
     * Get the current user's role.
     *
     * @return Role string ("student", "professor", "admin"), or empty string
     */
    public String getUserRole() {
        return prefs.getString(Constants.PREF_USER_ROLE, "");
    }

    /**
     * Get the current user's ID.
     *
     * @return User ID, or -1 if not logged in
     */
    public int getUserId() {
        return prefs.getInt(Constants.PREF_USER_ID, -1);
    }

    /**
     * Check if "remember me" is enabled.
     *
     * @return true if the user opted for persistent login
     */
    public boolean isRememberMe() {
        return prefs.getBoolean(Constants.PREF_REMEMBER_ME, false);
    }

    /**
     * Clear the login session (logout).
     * Removes all stored user data from SharedPreferences.
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
