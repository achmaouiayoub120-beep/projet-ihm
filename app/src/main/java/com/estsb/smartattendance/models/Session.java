package com.estsb.smartattendance.models;

/**
 * Session model representing an attendance session created by a professor.
 * Maps to the 'sessions' table in the SQLite database.
 * Each session has a unique QR code that students scan to mark attendance.
 *
 * @author EST SB Smart Attendance
 */
public class Session {

    private int id;
    private String moduleName;
    private String groupName;
    private String qrData;
    private String createdAt;
    private int professorId;
    private String professorName;
    private boolean isActive;

    // Default constructor
    public Session() {}

    // Full constructor
    public Session(int id, String moduleName, String groupName, String qrData,
                   String createdAt, int professorId, String professorName, boolean isActive) {
        this.id = id;
        this.moduleName = moduleName;
        this.groupName = groupName;
        this.qrData = qrData;
        this.createdAt = createdAt;
        this.professorId = professorId;
        this.professorName = professorName;
        this.isActive = isActive;
    }

    // Constructor for creation (without ID)
    public Session(String moduleName, String groupName, String qrData,
                   int professorId, String professorName) {
        this.moduleName = moduleName;
        this.groupName = groupName;
        this.qrData = qrData;
        this.professorId = professorId;
        this.professorName = professorName;
        this.isActive = true;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getModuleName() { return moduleName; }
    public String getGroupName() { return groupName; }
    public String getQrData() { return qrData; }
    public String getCreatedAt() { return createdAt; }
    public int getProfessorId() { return professorId; }
    public String getProfessorName() { return professorName; }
    public boolean isActive() { return isActive; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public void setQrData(String qrData) { this.qrData = qrData; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setProfessorId(int professorId) { this.professorId = professorId; }
    public void setProfessorName(String professorName) { this.professorName = professorName; }
    public void setActive(boolean active) { isActive = active; }
}
