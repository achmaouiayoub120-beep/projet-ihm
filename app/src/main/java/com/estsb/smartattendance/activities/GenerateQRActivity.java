package com.estsb.smartattendance.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.estsb.smartattendance.R;
import com.estsb.smartattendance.database.DatabaseHelper;
import com.estsb.smartattendance.interfaces.OnQRCodeGenerated;
import com.estsb.smartattendance.models.User;
import com.estsb.smartattendance.utils.Constants;
import com.estsb.smartattendance.utils.QRCodeHelper;
import com.estsb.smartattendance.utils.SessionManager;

import java.util.Locale;

/**
 * Generate QR activity - allows professors to create attendance sessions.
 *
 * This Activity implements the {@link OnQRCodeGenerated} Java interface,
 * which is one of the 3 mandatory interfaces required by the professor.
 * When {@link QRCodeHelper#generateQRCode} succeeds or fails, it calls
 * back into this Activity via the interface methods.
 *
 * Features:
 * - Module/group input form
 * - QR code display with 2-minute countdown timer
 * - Auto-regenerate when timer expires
 * - End session button
 *
 * @author EST SB Smart Attendance
 */
public class GenerateQRActivity extends AppCompatActivity implements OnQRCodeGenerated {

    private EditText editModuleName, editGroupName;
    private ImageView imgQRCode;
    private TextView txtTimer, txtTimerLabel;
    private TextView btnGenerate, btnRegenerate, btnEndSession;
    private LinearLayout formSection, qrSection;
    private CardView qrCard;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private User currentUser;

    private int currentSessionId = -1;
    private String currentQRData = "";
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        currentUser = sessionManager.getCurrentUser();

        bindViews();
        setupListeners();
    }

    private void bindViews() {
        editModuleName = findViewById(R.id.editModuleName);
        editGroupName = findViewById(R.id.editGroupName);
        imgQRCode = findViewById(R.id.imgQRCode);
        txtTimer = findViewById(R.id.txtTimer);
        txtTimerLabel = findViewById(R.id.txtTimerLabel);
        btnGenerate = findViewById(R.id.btnGenerateQR);
        btnRegenerate = findViewById(R.id.btnRegenerateQR);
        btnEndSession = findViewById(R.id.btnEndSession);
        formSection = findViewById(R.id.formSection);
        qrSection = findViewById(R.id.qrSection);
        qrCard = findViewById(R.id.qrCard);

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Initially hide QR section
        qrSection.setVisibility(LinearLayout.GONE);
    }

    private void setupListeners() {
        // Generate QR button
        btnGenerate.setOnClickListener(v -> generateNewSession());

        // Regenerate QR button
        btnRegenerate.setOnClickListener(v -> regenerateQR());

        // End session button
        btnEndSession.setOnClickListener(v -> endSession());
    }

    /**
     * Create a new session and generate QR code.
     */
    private void generateNewSession() {
        String moduleName = editModuleName.getText().toString().trim();
        String groupName = editGroupName.getText().toString().trim();

        if (moduleName.isEmpty() || groupName.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique QR data
        currentQRData = QRCodeHelper.generateQRData(moduleName, groupName);

        // Save session to database
        long sessionId = dbHelper.createSession(
                moduleName, groupName, currentQRData,
                currentUser.getId(), currentUser.getFullName()
        );
        currentSessionId = (int) sessionId;

        // Generate QR code bitmap using the interface callback
        QRCodeHelper.generateQRCode(currentQRData, Constants.QR_CODE_SIZE, this);

        // Show QR section, hide form section
        formSection.setVisibility(LinearLayout.GONE);
        qrSection.setVisibility(LinearLayout.VISIBLE);

        // Start countdown timer
        startTimer();
    }

    /**
     * Regenerate QR code for the current session.
     */
    private void regenerateQR() {
        if (currentSessionId == -1) return;

        String moduleName = editModuleName.getText().toString().trim();
        String groupName = editGroupName.getText().toString().trim();

        // Generate new QR data
        currentQRData = QRCodeHelper.generateQRData(moduleName, groupName);

        // Update in database
        dbHelper.updateSessionQR(currentSessionId, currentQRData);

        // Generate new QR bitmap through callback
        QRCodeHelper.generateQRCode(currentQRData, Constants.QR_CODE_SIZE, this);

        // Restart timer
        if (countDownTimer != null) countDownTimer.cancel();
        startTimer();

        Toast.makeText(this, "QR code régénéré", Toast.LENGTH_SHORT).show();
    }

    /**
     * End the current session and go back.
     */
    private void endSession() {
        if (currentSessionId != -1) {
            dbHelper.endSession(currentSessionId);
        }
        if (countDownTimer != null) countDownTimer.cancel();
        finish();
    }

    /**
     * Start the 2-minute countdown timer.
     */
    private void startTimer() {
        countDownTimer = new CountDownTimer(Constants.QR_EXPIRY_SECONDS * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long mins = seconds / 60;
                long secs = seconds % 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
                txtTimer.setText(time);

                // Change color when less than 30 seconds
                if (seconds < 30) {
                    txtTimer.setTextColor(getResources().getColor(R.color.error, null));
                } else {
                    txtTimer.setTextColor(getResources().getColor(R.color.primaryBlue, null));
                }
            }

            @Override
            public void onFinish() {
                // Auto-regenerate when timer expires
                regenerateQR();
            }
        }.start();
    }

    // =========================================================================
    // OnQRCodeGenerated Interface Implementation
    // =========================================================================

    /**
     * Called when QR code has been successfully generated.
     * Displays the QR bitmap in the ImageView.
     */
    @Override
    public void onQRCodeGenerated(Bitmap qrBitmap, String qrData) {
        imgQRCode.setImageBitmap(qrBitmap);
    }

    /**
     * Called when QR code generation fails.
     * Shows an error toast message.
     */
    @Override
    public void onQRCodeError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
