package com.estsb.smartattendance.models;

/**
 * User model representing a system user (Student, Professor, or Admin).
 * Maps to the 'users' table in the SQLite database.
 *
 * @author EST SB Smart Attendance
 */
public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role; // "student", "professor", "admin"

    // Default constructor
    public User() {}

    // Full constructor
    public User(int id, String firstName, String lastName, String email, String password, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Constructor without ID (for insertion)
    public User(String firstName, String lastName, String email, String password, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }

    /**
     * Returns the user's initials (first letter of first name + first letter of last name).
     * Used for avatar display in dashboards.
     */
    public String getInitials() {
        String initials = "";
        if (firstName != null && !firstName.isEmpty()) {
            initials += firstName.charAt(0);
        }
        if (lastName != null && !lastName.isEmpty()) {
            initials += lastName.charAt(0);
        }
        return initials.toUpperCase();
    }

    /**
     * Returns full display name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Returns the French label for the user's role.
     */
    public String getRoleLabel() {
        switch (role) {
            case "student": return "Étudiant";
            case "professor": return "Professeur";
            case "admin": return "Administrateur";
            default: return role;
        }
    }
}
