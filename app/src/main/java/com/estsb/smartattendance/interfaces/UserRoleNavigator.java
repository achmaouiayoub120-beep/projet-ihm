package com.estsb.smartattendance.interfaces;

/**
 * Interface for role-based navigation and dashboard actions.
 * Used by dashboard activities to handle navigation logic
 * according to the user's role (Student, Professor, Admin).
 *
 * This interface satisfies the professor's requirement for Java interfaces
 * in the mini-project, demonstrating polymorphic role-based behavior.
 *
 * @author EST SB Smart Attendance
 */
public interface UserRoleNavigator {

    /**
     * Navigate to the appropriate dashboard based on the user's role.
     *
     * @param role The user's role ("student", "professor", "admin")
     */
    void navigateToDashboard(String role);

    /**
     * Get the list of available action labels for a specific role.
     * Different roles have access to different features.
     *
     * @param role The user's role
     * @return Array of available action labels for this role
     */
    String[] getAvailableActions(String role);

    /**
     * Handle a quick action button press on the dashboard.
     *
     * @param actionId The identifier of the action to perform
     */
    void onQuickActionClicked(String actionId);
}
