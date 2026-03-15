package com.estsb.smartattendance.utils;

import android.graphics.Bitmap;

import com.estsb.smartattendance.interfaces.OnQRCodeGenerated;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.UUID;

/**
 * Utility class for QR code generation using the ZXing library.
 *
 * This class demonstrates the use of the {@link OnQRCodeGenerated} Java interface
 * to communicate QR generation results back to the calling Activity
 * (callback pattern / observer pattern).
 *
 * Called by: {@link com.estsb.smartattendance.activities.GenerateQRActivity}
 * Interface used: {@link OnQRCodeGenerated}
 *
 * @author EST SB Smart Attendance
 */
public class QRCodeHelper {

    /**
     * Generate a QR code bitmap from the given data string.
     * Uses the ZXing BarcodeEncoder to create a Bitmap, then notifies
     * the callback listener of success or failure through the
     * {@link OnQRCodeGenerated} interface.
     *
     * @param data     The data string to encode in the QR code
     * @param size     The width and height of the QR code in pixels
     * @param listener Callback listener that receives the generated Bitmap or error
     */
    public static void generateQRCode(String data, int size, OnQRCodeGenerated listener) {
        try {
            // BarcodeEncoder from zxing-android-embedded handles encoding + bitmap creation
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, size, size);

            // Notify success through the interface callback
            if (listener != null) {
                listener.onQRCodeGenerated(bitmap, data);
            }
        } catch (WriterException e) {
            // Notify error through the interface callback
            if (listener != null) {
                listener.onQRCodeError("Erreur de génération QR: " + e.getMessage());
            }
        }
    }

    /**
     * Generate a unique QR data string for a new attendance session.
     * Combines the app prefix, module name, group name, and a UUID
     * to guarantee uniqueness across sessions.
     *
     * Format: ESTSB-ATT-{module}-{group}-{uuid8}
     *
     * @param moduleName Module name for the session
     * @param groupName  Group name for the session
     * @return Unique QR data string
     */
    public static String generateQRData(String moduleName, String groupName) {
        // UUID substring for short unique identifier
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "ESTSB-ATT-" + moduleName.replace(" ", "_")
                + "-" + groupName.replace(" ", "_")
                + "-" + uuid;
    }

    /**
     * Generate a QR code bitmap synchronously (without callback).
     * Useful for simple display scenarios where no error handling is needed.
     *
     * @param data The data to encode
     * @param size The bitmap size in pixels (width = height)
     * @return Generated QR code Bitmap, or null on error
     */
    public static Bitmap generateQRBitmap(String data, int size) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, size, size);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
