package com.estsb.smartattendance.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.estsb.smartattendance.R;
import com.estsb.smartattendance.database.DatabaseHelper;
import com.estsb.smartattendance.interfaces.OnAttendanceMarked;
import com.estsb.smartattendance.models.Session;
import com.estsb.smartattendance.models.User;
import com.estsb.smartattendance.utils.Constants;
import com.estsb.smartattendance.utils.SessionManager;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * Scan QR activity - allows students to scan attendance QR codes.
 *
 * This Activity implements the {@link OnAttendanceMarked} Java interface,
 * which is one of the 3 mandatory interfaces required by the professor.
 * After scanning a QR code, the result is processed through the interface
 * methods {@code onAttendanceSuccess} and {@code onAttendanceFailure}.
 *
 * Flow:
 * 1. ZXing camera launches automatically via {@link ScanContract}
 * 2. Scanned QR data is validated against active sessions in SQLite
 * 3. Duplicate attendance is prevented via {@link DatabaseHelper#hasAttendance}
 * 4. Success/failure is communicated through the {@link OnAttendanceMarked} interface
 *
 * @author EST SB Smart Attendance
 */
public class ScanQRActivity extends AppCompatActivity implements OnAttendanceMarked {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUser;

    private TextView txtStatus, txtModuleName;
    private LinearLayout successSection, scanningSection;

    /**
     * ZXing activity result launcher — modern replacement for startActivityForResult.
     * Uses the ScanContract from zxing-android-embedded to handle camera intent.
     */
    private final ActivityResultLauncher<ScanOptions> scanLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    // QR code was successfully scanned
                    handleScanResult(result.getContents());
                } else {
                    // User cancelled scan — go back
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        // Initialize database and session helpers
        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();

        // Bind views
        txtStatus = findViewById(R.id.txtScanStatus);
        txtModuleName = findViewById(R.id.txtScannedModule);
        successSection = findViewById(R.id.successSection);
        scanningSection = findViewById(R.id.scanningSection);

        // Back button
        findViewById(R.id.btnScanBack).setOnClickListener(v -> finish());

        // Start camera scanning immediately
        startScan();
    }

    /**
     * Launch the ZXing QR scanner with custom options.
     * The scanner opens in portrait mode and only accepts QR codes.
     */
    private void startScan() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt(getString(R.string.scan_instruction));
        options.setCameraId(0);       // Back camera
        options.setBeepEnabled(true); // Beep on scan
        options.setBarcodeImageEnabled(false);
        options.setOrientationLocked(true);
        scanLauncher.launch(options);
    }

    /**
     * Handle the scanned QR code data.
     * Validates the session exists and is active, checks for duplicate
     * attendance, and marks attendance if all checks pass.
     *
     * @param qrData The raw data string from the scanned QR code
     */
    private void handleScanResult(String qrData) {
        // Step 1: Find matching active session in database
        Session session = dbHelper.getActiveSessionByQR(qrData);

        if (session == null) {
            // QR code doesn't match any active session
            onAttendanceFailure(getString(R.string.scan_invalid_qr_msg));
            return;
        }

        // Step 2: Check for duplicate attendance (prevent double-scanning)
        if (dbHelper.hasAttendance(currentUser.getId(), session.getId())) {
            onAttendanceFailure(getString(R.string.scan_duplicate_msg));
            return;
        }

        // Step 3: Mark attendance in SQLite database
        long result = dbHelper.markAttendance(
                currentUser.getId(),
                currentUser.getFullName(),
                session.getId(),
                session.getModuleName(),
                Constants.STATUS_PRESENT
        );

        // Step 4: Notify through interface callback
        if (result > 0) {
            onAttendanceSuccess(currentUser.getFullName(), session.getModuleName());
        } else {
            onAttendanceFailure("Impossible d'enregistrer la présence");
        }
    }

    // =========================================================================
    // OnAttendanceMarked Interface Implementation (mandatory interface #2)
    // =========================================================================

    /**
     * Called when attendance is successfully marked.
     * Shows the success UI with a checkmark and module name.
     * Automatically returns to the previous screen after 2.5 seconds.
     */
    @Override
    public void onAttendanceSuccess(String studentName, String moduleName) {
        scanningSection.setVisibility(LinearLayout.GONE);
        successSection.setVisibility(LinearLayout.VISIBLE);

        txtStatus.setText(getString(R.string.scan_success_title));
        txtModuleName.setText(moduleName);

        // Auto-navigate back after 2.5 seconds
        txtStatus.postDelayed(this::finish, 2500);
    }

    /**
     * Called when attendance marking fails.
     * Shows a toast error message and re-launches the scanner.
     */
    @Override
    public void onAttendanceFailure(String reason) {
        Toast.makeText(this, reason, Toast.LENGTH_LONG).show();
        // Allow re-scanning after a short delay
        txtStatus.postDelayed(this::startScan, 1500);
    }
}
