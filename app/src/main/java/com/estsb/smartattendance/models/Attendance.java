package com.estsb.smartattendance.models;

/**
 * Attendance model representing a student's attendance record for a session.
 * Maps to the 'attendance' table in the SQLite database.
 * Links a student to a session with a status (present, absent, late, excused).
 *
 * @author EST SB Smart Attendance
 */
public class Attendance {

    private int id;
    private int studentId;
    private String studentName;
    private int sessionId;
    private String moduleName;
    private String status; // "present", "absent", "late", "excused"
    private String scannedAt;

    // Default constructor
    public Attendance() {}

    // Full constructor
    public Attendance(int id, int studentId, String studentName, int sessionId,
                      String moduleName, String status, String scannedAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.sessionId = sessionId;
        this.moduleName = moduleName;
        this.status = status;
        this.scannedAt = scannedAt;
    }

    // Constructor for insertion
    public Attendance(int studentId, String studentName, int sessionId,
                      String moduleName, String status, String scannedAt) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.sessionId = sessionId;
        this.moduleName = moduleName;
        this.status = status;
        this.scannedAt = scannedAt;
    }

    // --- Getters ---
    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public int getSessionId() { return sessionId; }
    public String getModuleName() { return moduleName; }
    public String getStatus() { return status; }
    public String getScannedAt() { return scannedAt; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public void setStatus(String status) { this.status = status; }
    public void setScannedAt(String scannedAt) { this.scannedAt = scannedAt; }

    /**
     * Returns the French label for the attendance status.
     */
    public String getStatusLabel() {
        switch (status) {
            case "present": return "Présent";
            case "absent": return "Absent";
            case "late": return "Retard";
            case "excused": return "Excusé";
            default: return status;
        }
    }
}
