package com.estsb.smartattendance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.estsb.smartattendance.R;
import com.estsb.smartattendance.models.Attendance;
import com.estsb.smartattendance.utils.Constants;

import java.util.List;

/**
 * RecyclerView adapter for displaying attendance records.
 *
 * This class follows the standard Android RecyclerView + Adapter + ViewHolder
 * pattern. Each attendance record is displayed as a card with:
 * - Avatar (student initials)
 * - Student name
 * - Module name
 * - Date
 * - Color-coded status badge (green=Present, red=Absent, amber=Late)
 *
 * Used by: HistoryActivity, SessionDetailsActivity
 * Layout: item_attendance.xml
 *
 * @author EST SB Smart Attendance
 */
public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private final List<Attendance> records;

    public AttendanceAdapter(List<Attendance> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attendance record = records.get(position);

        // Set student name and initials
        holder.txtStudentName.setText(record.getStudentName());
        String initials = getInitials(record.getStudentName());
        holder.txtAvatar.setText(initials);

        // Set module and date
        holder.txtModule.setText(record.getModuleName());
        holder.txtDate.setText(record.getScannedAt());

        // Set status badge with color coding
        holder.txtStatus.setText(record.getStatusLabel());
        int statusColor;
        int statusBg;
        switch (record.getStatus()) {
            case Constants.STATUS_PRESENT:
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.emerald, null);
                statusBg = holder.itemView.getContext().getResources().getColor(R.color.successLight, null);
                break;
            case Constants.STATUS_ABSENT:
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.error, null);
                statusBg = holder.itemView.getContext().getResources().getColor(R.color.errorLight, null);
                break;
            case Constants.STATUS_LATE:
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.amber, null);
                statusBg = holder.itemView.getContext().getResources().getColor(R.color.warningLight, null);
                break;
            default:
                statusColor = holder.itemView.getContext().getResources().getColor(R.color.textTertiary, null);
                statusBg = holder.itemView.getContext().getResources().getColor(R.color.background, null);
                break;
        }
        holder.txtStatus.setTextColor(statusColor);
        holder.txtStatus.getBackground().setTint(statusBg);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    /**
     * Extract initials from a full name string.
     */
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    /**
     * ViewHolder for attendance list items.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAvatar, txtStudentName, txtModule, txtDate, txtStatus;
        CardView card;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.attendanceCard);
            txtAvatar = itemView.findViewById(R.id.txtItemAvatar);
            txtStudentName = itemView.findViewById(R.id.txtItemStudent);
            txtModule = itemView.findViewById(R.id.txtItemModule);
            txtDate = itemView.findViewById(R.id.txtItemDate);
            txtStatus = itemView.findViewById(R.id.txtItemStatus);
        }
    }
}
