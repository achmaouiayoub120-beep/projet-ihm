package com.estsb.smartattendance.interfaces;

import android.graphics.Bitmap;

/**
 * Interface for QR code generation callbacks.
 * Used by GenerateQRActivity and QRCodeHelper to communicate
 * the result of QR code generation operations.
 *
 * This interface satisfies the professor's requirement for Java interfaces
 * in the mini-project, demonstrating callback-based communication pattern.
 *
 * @author EST SB Smart Attendance
 */
public interface OnQRCodeGenerated {

    /**
     * Called when a QR code has been successfully generated.
     *
     * @param qrBitmap The generated QR code as a Bitmap image
     * @param qrData   The data encoded in the QR code
     */
    void onQRCodeGenerated(Bitmap qrBitmap, String qrData);

    /**
     * Called when QR code generation fails.
     *
     * @param error Description of the error that occurred
     */
    void onQRCodeError(String error);
}
