package com.estsb.smartattendance.interfaces;

/**
 * Interface for attendance marking callbacks.
 * Used by ScanQRActivity and the data layer to communicate
 * the result of attendance validation operations.
 *
 * This interface satisfies the professor's requirement for Java interfaces
 * in the mini-project, demonstrating observer-pattern communication.
 *
 * @author EST SB Smart Attendance
 */
public interface OnAttendanceMarked {

    /**
     * Called when attendance has been successfully recorded.
     *
     * @param studentName The name of the student whose attendance was marked
     * @param moduleName  The name of the module/course
     */
    void onAttendanceSuccess(String studentName, String moduleName);

    /**
     * Called when attendance marking fails.
     *
     * @param reason Description of why the attendance could not be marked
     */
    void onAttendanceFailure(String reason);
}
