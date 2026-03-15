package com.estsb.smartattendance.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.estsb.smartattendance.models.Attendance;
import com.estsb.smartattendance.models.Session;
import com.estsb.smartattendance.models.User;
import com.estsb.smartattendance.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * SQLite database helper for EST SB Smart Attendance.
 *
 * This class extends {@link SQLiteOpenHelper} and is the ONLY data layer
 * in the application — there is NO Firebase, NO API, NO Room, NO Retrofit.
 * All data is stored locally in SQLite and sessions persist via SharedPreferences.
 *
 * Responsibilities:
 * - Database creation with 3 tables (users, sessions, attendance)
 * - Demo data seeding (3 demo accounts + sample records)
 * - CRUD operations for all entities
 * - Login validation
 * - Attendance duplicate checking
 *
 * Design pattern: Singleton — ensures a single DB instance across the app.
 *
 * @author EST SB Smart Attendance
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /** Singleton instance — only one database connection used throughout the app */
    private static DatabaseHelper instance;

    /**
     * Singleton pattern — ensures a single database instance across the app.
     * Using application context prevents memory leaks from Activity references.
     *
     * @param context Any context (Activity, Application, etc.)
     * @return The singleton DatabaseHelper instance
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /** Private constructor — forces use of getInstance() */
    private DatabaseHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    /**
     * Called when the database is first created.
     * Creates all 3 tables and seeds demo data for presentation.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // =====================================================================
        // TABLE 1: USERS — stores student, professor, and admin accounts
        // =====================================================================
        db.execSQL("CREATE TABLE " + Constants.TABLE_USERS + " ("
                + Constants.COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Constants.COL_USER_FIRST_NAME + " TEXT NOT NULL, "
                + Constants.COL_USER_LAST_NAME + " TEXT NOT NULL, "
                + Constants.COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, "
                + Constants.COL_USER_PASSWORD + " TEXT NOT NULL, "
                + Constants.COL_USER_ROLE + " TEXT NOT NULL)");

        // =====================================================================
        // TABLE 2: SESSIONS — stores attendance sessions created by professors
        // =====================================================================
        db.execSQL("CREATE TABLE " + Constants.TABLE_SESSIONS + " ("
                + Constants.COL_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Constants.COL_SESSION_MODULE + " TEXT NOT NULL, "
                + Constants.COL_SESSION_GROUP + " TEXT NOT NULL, "
                + Constants.COL_SESSION_QR_DATA + " TEXT, "
                + Constants.COL_SESSION_CREATED_AT + " TEXT NOT NULL, "
                + Constants.COL_SESSION_PROFESSOR_ID + " INTEGER, "
                + Constants.COL_SESSION_PROFESSOR_NAME + " TEXT, "
                + Constants.COL_SESSION_IS_ACTIVE + " INTEGER DEFAULT 1)");

        // =====================================================================
        // TABLE 3: ATTENDANCE — links students to sessions with status
        // =====================================================================
        db.execSQL("CREATE TABLE " + Constants.TABLE_ATTENDANCE + " ("
                + Constants.COL_ATT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Constants.COL_ATT_STUDENT_ID + " INTEGER NOT NULL, "
                + Constants.COL_ATT_STUDENT_NAME + " TEXT, "
                + Constants.COL_ATT_SESSION_ID + " INTEGER NOT NULL, "
                + Constants.COL_ATT_MODULE_NAME + " TEXT, "
                + Constants.COL_ATT_STATUS + " TEXT NOT NULL, "
                + Constants.COL_ATT_SCANNED_AT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + Constants.COL_ATT_SESSION_ID + ") REFERENCES "
                + Constants.TABLE_SESSIONS + "(" + Constants.COL_SESSION_ID + "))");

        // Seed demo data for presentation
        seedDemoData(db);
    }

    /**
     * Called when the database version is upgraded.
     * Drops all tables and recreates them (acceptable for demo app).
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_USERS);
        onCreate(db);
    }

    // =========================================================================
    // DEMO DATA SEEDING — creates demo accounts and sample records
    // =========================================================================

    /**
     * Insert demo accounts and sample data for demonstration purposes.
     * Creates 3 main demo users and 3 additional students for realistic
     * presentation. Also creates sample sessions and attendance records.
     *
     * Demo credentials:
     * - student@estsb.ma / password (Étudiant)
     * - prof@estsb.ma / password (Professeur)
     * - admin@estsb.ma / password (Administrateur)
     */
    private void seedDemoData(SQLiteDatabase db) {
        // 3 main demo accounts (ID: 1, 2, 3)
        insertUser(db, "Ahmed", "Étudiant", "student@estsb.ma", "password", Constants.ROLE_STUDENT);
        insertUser(db, "Mohammed", "Professeur", "prof@estsb.ma", "password", Constants.ROLE_PROFESSOR);
        insertUser(db, "Fatima", "Admin", "admin@estsb.ma", "password", Constants.ROLE_ADMIN);

        // Additional demo students for realistic presentation (ID: 4, 5, 6)
        insertUser(db, "Youssef", "Bennani", "youssef@estsb.ma", "password", Constants.ROLE_STUDENT);
        insertUser(db, "Sara", "El Idrissi", "sara@estsb.ma", "password", Constants.ROLE_STUDENT);
        insertUser(db, "Karim", "Alaoui", "karim@estsb.ma", "password", Constants.ROLE_STUDENT);

        // Demo sessions (created by professor ID 2)
        String now = getCurrentDateTime();
        insertSession(db, "Programmation Java", "GI-2A", "ESTSB-ATT-Java-GI2A-demo1", now, 2, "Mohammed Professeur", 0);
        insertSession(db, "Bases de données", "GI-2A", "ESTSB-ATT-BDD-GI2A-demo2", now, 2, "Mohammed Professeur", 0);
        insertSession(db, "Réseaux informatiques", "GI-2B", "ESTSB-ATT-Reseaux-GI2B-demo3", now, 2, "Mohammed Professeur", 0);

        // Demo attendance records (linked to sessions above)
        insertAttendance(db, 1, "Ahmed Étudiant", 1, "Programmation Java", Constants.STATUS_PRESENT, now);
        insertAttendance(db, 4, "Youssef Bennani", 1, "Programmation Java", Constants.STATUS_PRESENT, now);
        insertAttendance(db, 5, "Sara El Idrissi", 1, "Programmation Java", Constants.STATUS_PRESENT, now);
        insertAttendance(db, 6, "Karim Alaoui", 1, "Programmation Java", Constants.STATUS_ABSENT, now);
        insertAttendance(db, 1, "Ahmed Étudiant", 2, "Bases de données", Constants.STATUS_PRESENT, now);
        insertAttendance(db, 4, "Youssef Bennani", 2, "Bases de données", Constants.STATUS_LATE, now);
        insertAttendance(db, 1, "Ahmed Étudiant", 3, "Réseaux informatiques", Constants.STATUS_ABSENT, now);
    }

    /** Helper: insert a user into the database */
    private void insertUser(SQLiteDatabase db, String firstName, String lastName,
                            String email, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(Constants.COL_USER_FIRST_NAME, firstName);
        values.put(Constants.COL_USER_LAST_NAME, lastName);
        values.put(Constants.COL_USER_EMAIL, email);
        values.put(Constants.COL_USER_PASSWORD, password);
        values.put(Constants.COL_USER_ROLE, role);
        db.insert(Constants.TABLE_USERS, null, values);
    }

    /** Helper: insert a session into the database */
    private void insertSession(SQLiteDatabase db, String module, String group,
                               String qrData, String createdAt, int profId,
                               String profName, int isActive) {
        ContentValues values = new ContentValues();
        values.put(Constants.COL_SESSION_MODULE, module);
        values.put(Constants.COL_SESSION_GROUP, group);
        values.put(Constants.COL_SESSION_QR_DATA, qrData);
        values.put(Constants.COL_SESSION_CREATED_AT, createdAt);
        values.put(Constants.COL_SESSION_PROFESSOR_ID, profId);
        values.put(Constants.COL_SESSION_PROFESSOR_NAME, profName);
        values.put(Constants.COL_SESSION_IS_ACTIVE, isActive);
        db.insert(Constants.TABLE_SESSIONS, null, values);
    }

    /** Helper: insert an attendance record into the database */
    private void insertAttendance(SQLiteDatabase db, int studentId, String studentName,
                                  int sessionId, String moduleName, String status, String scannedAt) {
        ContentValues values = new ContentValues();
        values.put(Constants.COL_ATT_STUDENT_ID, studentId);
        values.put(Constants.COL_ATT_STUDENT_NAME, studentName);
        values.put(Constants.COL_ATT_SESSION_ID, sessionId);
        values.put(Constants.COL_ATT_MODULE_NAME, moduleName);
        values.put(Constants.COL_ATT_STATUS, status);
        values.put(Constants.COL_ATT_SCANNED_AT, scannedAt);
        db.insert(Constants.TABLE_ATTENDANCE, null, values);
    }

    // =========================================================================
    // USER OPERATIONS — login validation and user retrieval
    // =========================================================================

    /**
     * Validate login credentials against the database.
     * Queries the users table for matching email + password combination.
     *
     * @param email    User's email address
     * @param password User's password (plaintext — acceptable for demo)
     * @return User object if credentials are valid, null otherwise
     */
    public User validateLogin(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_USERS, null,
                Constants.COL_USER_EMAIL + "=? AND " + Constants.COL_USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    /**
     * Get a user by their database ID.
     */
    public User getUserById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_USERS, null,
                Constants.COL_USER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        return user;
    }

    // =========================================================================
    // SESSION OPERATIONS — CRUD for attendance sessions
    // =========================================================================

    /**
     * Create a new attendance session in the database.
     *
     * @return The auto-generated ID of the newly created session
     */
    public long createSession(String moduleName, String groupName, String qrData,
                              int professorId, String professorName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COL_SESSION_MODULE, moduleName);
        values.put(Constants.COL_SESSION_GROUP, groupName);
        values.put(Constants.COL_SESSION_QR_DATA, qrData);
        values.put(Constants.COL_SESSION_CREATED_AT, getCurrentDateTime());
        values.put(Constants.COL_SESSION_PROFESSOR_ID, professorId);
        values.put(Constants.COL_SESSION_PROFESSOR_NAME, professorName);
        values.put(Constants.COL_SESSION_IS_ACTIVE, 1);
        return db.insert(Constants.TABLE_SESSIONS, null, values);
    }

    /**
     * Update the QR data for a session (used when regenerating QR code).
     */
    public void updateSessionQR(int sessionId, String newQrData) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COL_SESSION_QR_DATA, newQrData);
        db.update(Constants.TABLE_SESSIONS, values,
                Constants.COL_SESSION_ID + "=?",
                new String[]{String.valueOf(sessionId)});
    }

    /**
     * End/deactivate a session (sets is_active = 0).
     */
    public void endSession(int sessionId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COL_SESSION_IS_ACTIVE, 0);
        db.update(Constants.TABLE_SESSIONS, values,
                Constants.COL_SESSION_ID + "=?",
                new String[]{String.valueOf(sessionId)});
    }

    /**
     * Get a session by its database ID.
     */
    public Session getSessionById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_SESSIONS, null,
                Constants.COL_SESSION_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Session session = null;
        if (cursor.moveToFirst()) {
            session = cursorToSession(cursor);
        }
        cursor.close();
        return session;
    }

    /**
     * Find an active session that matches the scanned QR data.
     * Used by ScanQRActivity to validate scanned codes.
     *
     * @param qrData The raw QR data string from the scanner
     * @return Matching active Session, or null if not found/expired
     */
    public Session getActiveSessionByQR(String qrData) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_SESSIONS, null,
                Constants.COL_SESSION_QR_DATA + "=? AND " + Constants.COL_SESSION_IS_ACTIVE + "=1",
                new String[]{qrData}, null, null, null);

        Session session = null;
        if (cursor.moveToFirst()) {
            session = cursorToSession(cursor);
        }
        cursor.close();
        return session;
    }

    /**
     * Get all sessions for a professor (ordered by most recent first).
     */
    public List<Session> getSessionsByProfessor(int professorId) {
        List<Session> sessions = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_SESSIONS, null,
                Constants.COL_SESSION_PROFESSOR_ID + "=?",
                new String[]{String.valueOf(professorId)},
                null, null, Constants.COL_SESSION_CREATED_AT + " DESC");

        while (cursor.moveToNext()) {
            sessions.add(cursorToSession(cursor));
        }
        cursor.close();
        return sessions;
    }

    /**
     * Get total session count for a professor (used for dashboard stats).
     */
    public int getSessionCount(int professorId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + Constants.TABLE_SESSIONS
                        + " WHERE " + Constants.COL_SESSION_PROFESSOR_ID + "=?",
                new String[]{String.valueOf(professorId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // =========================================================================
    // ATTENDANCE OPERATIONS — marking, querying, and counting attendance
    // =========================================================================

    /**
     * Mark attendance for a student in a session.
     * Includes built-in duplicate prevention.
     *
     * @return The auto-generated ID of the new attendance record, or -1 if duplicate
     */
    public long markAttendance(int studentId, String studentName, int sessionId,
                               String moduleName, String status) {
        // Prevent duplicate attendance for the same student + session
        if (hasAttendance(studentId, sessionId)) {
            return -1; // Already marked
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COL_ATT_STUDENT_ID, studentId);
        values.put(Constants.COL_ATT_STUDENT_NAME, studentName);
        values.put(Constants.COL_ATT_SESSION_ID, sessionId);
        values.put(Constants.COL_ATT_MODULE_NAME, moduleName);
        values.put(Constants.COL_ATT_STATUS, status);
        values.put(Constants.COL_ATT_SCANNED_AT, getCurrentDateTime());
        return db.insert(Constants.TABLE_ATTENDANCE, null, values);
    }

    /**
     * Check if a student already has an attendance record for a session.
     * Used to prevent duplicate QR scans.
     */
    public boolean hasAttendance(int studentId, int sessionId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_ATTENDANCE, null,
                Constants.COL_ATT_STUDENT_ID + "=? AND " + Constants.COL_ATT_SESSION_ID + "=?",
                new String[]{String.valueOf(studentId), String.valueOf(sessionId)},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * Get all attendance records for a specific student (sorted most recent first).
     * Used by StudentDashboardActivity and HistoryActivity.
     */
    public List<Attendance> getAttendanceByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_ATTENDANCE, null,
                Constants.COL_ATT_STUDENT_ID + "=?",
                new String[]{String.valueOf(studentId)},
                null, null, Constants.COL_ATT_SCANNED_AT + " DESC");

        while (cursor.moveToNext()) {
            list.add(cursorToAttendance(cursor));
        }
        cursor.close();
        return list;
    }

    /**
     * Get all attendance records for a specific session.
     * Used by SessionDetailsActivity.
     */
    public List<Attendance> getAttendanceBySession(int sessionId) {
        List<Attendance> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_ATTENDANCE, null,
                Constants.COL_ATT_SESSION_ID + "=?",
                new String[]{String.valueOf(sessionId)},
                null, null, Constants.COL_ATT_SCANNED_AT + " DESC");

        while (cursor.moveToNext()) {
            list.add(cursorToAttendance(cursor));
        }
        cursor.close();
        return list;
    }

    /**
     * Get ALL attendance records (for professor/admin history view).
     */
    public List<Attendance> getAllAttendance() {
        List<Attendance> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Constants.TABLE_ATTENDANCE, null,
                null, null, null, null,
                Constants.COL_ATT_SCANNED_AT + " DESC");

        while (cursor.moveToNext()) {
            list.add(cursorToAttendance(cursor));
        }
        cursor.close();
        return list;
    }

    /**
     * Count attendance records by status for a professor's sessions.
     * Uses an INNER JOIN between attendance and sessions tables.
     * Used for dashboard statistics (e.g., "12 Présents").
     */
    public int getAttendanceCountByStatus(int professorId, String status) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + Constants.TABLE_ATTENDANCE + " a "
                        + "INNER JOIN " + Constants.TABLE_SESSIONS + " s "
                        + "ON a." + Constants.COL_ATT_SESSION_ID + " = s." + Constants.COL_SESSION_ID
                        + " WHERE s." + Constants.COL_SESSION_PROFESSOR_ID + "=?"
                        + " AND a." + Constants.COL_ATT_STATUS + "=?",
                new String[]{String.valueOf(professorId), status});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * Get total attendance count for a professor (all statuses).
     * Used for calculating attendance rate percentage.
     */
    public int getTotalAttendanceCount(int professorId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + Constants.TABLE_ATTENDANCE + " a "
                        + "INNER JOIN " + Constants.TABLE_SESSIONS + " s "
                        + "ON a." + Constants.COL_ATT_SESSION_ID + " = s." + Constants.COL_SESSION_ID
                        + " WHERE s." + Constants.COL_SESSION_PROFESSOR_ID + "=?",
                new String[]{String.valueOf(professorId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // =========================================================================
    // CURSOR CONVERSION HELPERS — convert database rows to Java objects
    // =========================================================================

    /** Convert a Cursor row to a User object */
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COL_USER_ID)));
        user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_USER_FIRST_NAME)));
        user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_USER_LAST_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_USER_EMAIL)));
        user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_USER_PASSWORD)));
        user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_USER_ROLE)));
        return user;
    }

    /** Convert a Cursor row to a Session object */
    private Session cursorToSession(Cursor cursor) {
        Session session = new Session();
        session.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COL_SESSION_ID)));
        session.setModuleName(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_SESSION_MODULE)));
        session.setGroupName(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_SESSION_GROUP)));
        session.setQrData(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_SESSION_QR_DATA)));
        session.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_SESSION_CREATED_AT)));
        session.setProfessorId(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COL_SESSION_PROFESSOR_ID)));
        session.setProfessorName(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_SESSION_PROFESSOR_NAME)));
        session.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COL_SESSION_IS_ACTIVE)) == 1);
        return session;
    }

    /** Convert a Cursor row to an Attendance object */
    private Attendance cursorToAttendance(Cursor cursor) {
        Attendance att = new Attendance();
        att.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COL_ATT_ID)));
        att.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COL_ATT_STUDENT_ID)));
        att.setStudentName(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_ATT_STUDENT_NAME)));
        att.setSessionId(cursor.getInt(cursor.getColumnIndexOrThrow(Constants.COL_ATT_SESSION_ID)));
        att.setModuleName(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_ATT_MODULE_NAME)));
        att.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_ATT_STATUS)));
        att.setScannedAt(cursor.getString(cursor.getColumnIndexOrThrow(Constants.COL_ATT_SCANNED_AT)));
        return att;
    }

    // =========================================================================
    // UTILITY
    // =========================================================================

    /**
     * Get the current date and time formatted for database storage.
     * Format: "yyyy-MM-dd HH:mm:ss"
     */
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
