package com.estsb.smartattendance.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.estsb.smartattendance.R;
import com.estsb.smartattendance.adapters.AttendanceAdapter;
import com.estsb.smartattendance.database.DatabaseHelper;
import com.estsb.smartattendance.models.Attendance;
import com.estsb.smartattendance.models.Session;
import com.estsb.smartattendance.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Session Details activity - displays full info for a specific session.
 * Shows session metadata, attendance statistics, and a list of
 * present/absent students.
 *
 * @author EST SB Smart Attendance
 */
public class SessionDetailsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        dbHelper = DatabaseHelper.getInstance(this);

        // Back button
        findViewById(R.id.btnDetailsBack).setOnClickListener(v -> finish());

        // Get session ID from intent
        int sessionId = getIntent().getIntExtra(Constants.EXTRA_SESSION_ID, -1);
        if (sessionId == -1) {
            finish();
            return;
        }

        loadSessionDetails(sessionId);
    }

    /**
     * Load and display session details and attendance records.
     */
    private void loadSessionDetails(int sessionId) {
        Session session = dbHelper.getSessionById(sessionId);
        if (session == null) {
            finish();
            return;
        }

        // Session info
        ((TextView) findViewById(R.id.txtDetailModule)).setText(session.getModuleName());
        ((TextView) findViewById(R.id.txtDetailGroup)).setText(session.getGroupName());
        ((TextView) findViewById(R.id.txtDetailProfessor)).setText(session.getProfessorName());
        ((TextView) findViewById(R.id.txtDetailDate)).setText(session.getCreatedAt());

        // Status badge
        TextView statusBadge = findViewById(R.id.txtDetailStatus);
        if (session.isActive()) {
            statusBadge.setText("Active");
            statusBadge.setBackgroundTintList(getResources().getColorStateList(R.color.emerald, null));
        } else {
            statusBadge.setText("Terminée");
            statusBadge.setBackgroundTintList(getResources().getColorStateList(R.color.textTertiary, null));
        }

        // Attendance records
        List<Attendance> records = dbHelper.getAttendanceBySession(sessionId);

        // Count stats
        int presentCount = 0, absentCount = 0;
        List<Attendance> presentList = new ArrayList<>();
        List<Attendance> absentList = new ArrayList<>();

        for (Attendance r : records) {
            if (r.getStatus().equals(Constants.STATUS_PRESENT) || r.getStatus().equals(Constants.STATUS_LATE)) {
                presentCount++;
                presentList.add(r);
            } else {
                absentCount++;
                absentList.add(r);
            }
        }

        ((TextView) findViewById(R.id.txtDetailPresent)).setText(String.valueOf(presentCount));
        ((TextView) findViewById(R.id.txtDetailAbsent)).setText(String.valueOf(absentCount));
        ((TextView) findViewById(R.id.txtDetailTotal)).setText(String.valueOf(records.size()));

        // Attendance list
        RecyclerView recyclerView = findViewById(R.id.recyclerSessionAttendance);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AttendanceAdapter(records));
    }
}
