package com.estsb.smartattendance.utils;

/**
 * Application-wide constants for EST SB Smart Attendance.
 * Centralizes database names, table names, column names,
 * SharedPreferences keys, and other app-wide values.
 *
 * @author EST SB Smart Attendance
 */
public final class Constants {

    // Prevent instantiation
    private Constants() {}

    // --- Database ---
    public static final String DB_NAME = "smart_attendance.db";
    public static final int DB_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_SESSIONS = "sessions";
    public static final String TABLE_ATTENDANCE = "attendance";

    // Users table columns
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_FIRST_NAME = "first_name";
    public static final String COL_USER_LAST_NAME = "last_name";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_ROLE = "role";

    // Sessions table columns
    public static final String COL_SESSION_ID = "id";
    public static final String COL_SESSION_MODULE = "module_name";
    public static final String COL_SESSION_GROUP = "group_name";
    public static final String COL_SESSION_QR_DATA = "qr_data";
    public static final String COL_SESSION_CREATED_AT = "created_at";
    public static final String COL_SESSION_PROFESSOR_ID = "professor_id";
    public static final String COL_SESSION_PROFESSOR_NAME = "professor_name";
    public static final String COL_SESSION_IS_ACTIVE = "is_active";

    // Attendance table columns
    public static final String COL_ATT_ID = "id";
    public static final String COL_ATT_STUDENT_ID = "student_id";
    public static final String COL_ATT_STUDENT_NAME = "student_name";
    public static final String COL_ATT_SESSION_ID = "session_id";
    public static final String COL_ATT_MODULE_NAME = "module_name";
    public static final String COL_ATT_STATUS = "status";
    public static final String COL_ATT_SCANNED_AT = "scanned_at";

    // --- SharedPreferences ---
    public static final String PREF_NAME = "estsb_prefs";
    public static final String PREF_IS_LOGGED_IN = "is_logged_in";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_USER_FIRST_NAME = "user_first_name";
    public static final String PREF_USER_LAST_NAME = "user_last_name";
    public static final String PREF_USER_ROLE = "user_role";
    public static final String PREF_REMEMBER_ME = "remember_me";

    // --- Roles ---
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_PROFESSOR = "professor";
    public static final String ROLE_ADMIN = "admin";

    // --- Attendance Status ---
    public static final String STATUS_PRESENT = "present";
    public static final String STATUS_ABSENT = "absent";
    public static final String STATUS_LATE = "late";
    public static final String STATUS_EXCUSED = "excused";

    // --- Intent Extras ---
    public static final String EXTRA_SESSION_ID = "session_id";
    public static final String EXTRA_MODULE_NAME = "module_name";
    public static final String EXTRA_QR_DATA = "qr_data";

    // --- QR Code ---
    public static final int QR_CODE_SIZE = 512; // pixels
    public static final int QR_EXPIRY_SECONDS = 120; // 2 minutes
}
