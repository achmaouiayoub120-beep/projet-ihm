package com.estsb.smartattendance.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.estsb.smartattendance.R;
import com.estsb.smartattendance.adapters.AttendanceAdapter;
import com.estsb.smartattendance.database.DatabaseHelper;
import com.estsb.smartattendance.models.Attendance;
import com.estsb.smartattendance.utils.Constants;
import com.estsb.smartattendance.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * History activity - displays attendance records in a filterable list.
 * Uses RecyclerView with AttendanceAdapter for efficient list rendering.
 * Shows different data depending on user role (student vs professor).
 *
 * @author EST SB Smart Attendance
 */
public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendanceAdapter adapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private List<Attendance> allRecords = new ArrayList<>();
    private List<Attendance> filteredRecords = new ArrayList<>();
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        setupViews();
        loadData();
        setupFilters();
        setupSearch();
    }

    private void setupViews() {
        // Back button
        findViewById(R.id.btnHistoryBack).setOnClickListener(v -> finish());

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceAdapter(filteredRecords);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Load attendance data based on user role.
     */
    private void loadData() {
        String role = sessionManager.getUserRole();
        int userId = sessionManager.getUserId();

        if (role.equals(Constants.ROLE_STUDENT)) {
            // Student sees only their own records
            allRecords = dbHelper.getAttendanceByStudent(userId);
        } else {
            // Professor/Admin sees all records
            allRecords = dbHelper.getAllAttendance();
        }

        filteredRecords.clear();
        filteredRecords.addAll(allRecords);
        adapter.notifyDataSetChanged();

        updateStats();
    }

    /**
     * Setup filter chip listeners.
     */
    private void setupFilters() {
        TextView filterAll = findViewById(R.id.filterAll);
        TextView filterPresent = findViewById(R.id.filterPresent);
        TextView filterAbsent = findViewById(R.id.filterAbsent);
        TextView filterLate = findViewById(R.id.filterLate);

        filterAll.setOnClickListener(v -> applyFilter("all"));
        filterPresent.setOnClickListener(v -> applyFilter(Constants.STATUS_PRESENT));
        filterAbsent.setOnClickListener(v -> applyFilter(Constants.STATUS_ABSENT));
        filterLate.setOnClickListener(v -> applyFilter(Constants.STATUS_LATE));
    }

    /**
     * Setup search bar text change listener.
     */
    private void setupSearch() {
        EditText searchInput = findViewById(R.id.editSearch);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Apply status filter to the records list.
     */
    private void applyFilter(String filter) {
        currentFilter = filter;
        filteredRecords.clear();

        for (Attendance record : allRecords) {
            if (filter.equals("all") || record.getStatus().equals(filter)) {
                filteredRecords.add(record);
            }
        }

        adapter.notifyDataSetChanged();
        updateStats();
    }

    /**
     * Filter records by module name search query.
     */
    private void filterBySearch(String query) {
        filteredRecords.clear();

        for (Attendance record : allRecords) {
            boolean matchesFilter = currentFilter.equals("all") || record.getStatus().equals(currentFilter);
            boolean matchesSearch = query.isEmpty() ||
                    record.getModuleName().toLowerCase().contains(query.toLowerCase());

            if (matchesFilter && matchesSearch) {
                filteredRecords.add(record);
            }
        }

        adapter.notifyDataSetChanged();
        updateStats();
    }

    /**
     * Update the statistics summary bar.
     */
    private void updateStats() {
        int total = filteredRecords.size();
        int present = 0, absent = 0;
        for (Attendance r : filteredRecords) {
            if (r.getStatus().equals(Constants.STATUS_PRESENT)) present++;
            else if (r.getStatus().equals(Constants.STATUS_ABSENT)) absent++;
        }
        int rate = total > 0 ? (present * 100 / total) : 0;

        ((TextView) findViewById(R.id.txtHistTotal)).setText(String.valueOf(total));
        ((TextView) findViewById(R.id.txtHistPresent)).setText(String.valueOf(present));
        ((TextView) findViewById(R.id.txtHistAbsent)).setText(String.valueOf(absent));
        ((TextView) findViewById(R.id.txtHistRate)).setText(rate + "%");
    }
}
