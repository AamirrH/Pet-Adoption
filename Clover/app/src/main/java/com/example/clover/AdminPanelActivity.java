package com.example.clover;

import android.app.*;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {
    private static final String CH_ID = "clover_admin_channel";
    private DatabaseHelper db;
    private LinearLayout appsList;
    private TextView tvEmpty, tvPendingCount, tvApprovedCount, tvRejectedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        db = new DatabaseHelper(this);
        appsList = findViewById(R.id.adminAppsList);
        tvEmpty = findViewById(R.id.tvAdminEmpty);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvApprovedCount = findViewById(R.id.tvApprovedCount);
        tvRejectedCount = findViewById(R.id.tvRejectedCount);

        createNotificationChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications();
        updateCounts();
    }

    private void updateCounts() {
        tvPendingCount.setText(String.valueOf(db.getApplicationCountByStatus("Pending")));
        tvApprovedCount.setText(String.valueOf(db.getApplicationCountByStatus("Approved")));
        tvRejectedCount.setText(String.valueOf(db.getApplicationCountByStatus("Rejected")));
    }

    private void loadApplications() {
        List<AdoptionApplication> apps = db.getAllApplications();
        appsList.removeAllViews();

        if (apps.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            findViewById(R.id.scrollAdminApps).setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            findViewById(R.id.scrollAdminApps).setVisibility(View.VISIBLE);

            for (AdoptionApplication app : apps) {
                View item = LayoutInflater.from(this).inflate(R.layout.item_admin_application, appsList, false);

                String emoji = db.getPetEmojiByName(app.getPetName());
                ((TextView) item.findViewById(R.id.tvAdminAppEmoji)).setText(emoji);
                ((TextView) item.findViewById(R.id.tvAdminAppPetName)).setText(app.getPetName());
                ((TextView) item.findViewById(R.id.tvAdminAppAdopter)).setText("by " + app.getAdopterName());
                ((TextView) item.findViewById(R.id.tvAdminAppDate)).setText(app.getDate());
                ((TextView) item.findViewById(R.id.tvAdminAppPhone)).setText("📞 " + app.getPhone());
                ((TextView) item.findViewById(R.id.tvAdminAppHousing)).setText("🏠 " + app.getHousingType());
                ((TextView) item.findViewById(R.id.tvAdminAppExperience)).setText("⭐ " + app.getExperience());
                ((TextView) item.findViewById(R.id.tvAdminAppReason)).setText("\"" + app.getReason() + "\"");

                TextView tvStatus = item.findViewById(R.id.tvAdminAppStatus);
                tvStatus.setText(app.getStatus());
                styleStatusBadge(tvStatus, app.getStatus());

                Button btnApprove = item.findViewById(R.id.btnApprove);
                Button btnReject = item.findViewById(R.id.btnReject);

                if ("Pending".equals(app.getStatus())) {
                    btnApprove.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.VISIBLE);

                    btnApprove.setOnClickListener(v -> {
                        new AlertDialog.Builder(this)
                                .setTitle("Approve Application")
                                .setMessage("Approve " + app.getAdopterName() + "'s application for " + app.getPetName() + "?")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton("Approve", (d, w) -> {
                                    db.updateApplicationStatus(app.getId(), "Approved");
                                    sendStatusNotification(app, "Approved");
                                    Toast.makeText(this, "✅ Application approved!", Toast.LENGTH_SHORT).show();
                                    loadApplications();
                                    updateCounts();
                                })
                                .setNegativeButton(R.string.cancel, (d, w) -> d.dismiss())
                                .show();
                    });

                    btnReject.setOnClickListener(v -> {
                        new AlertDialog.Builder(this)
                                .setTitle("Reject Application")
                                .setMessage("Reject " + app.getAdopterName() + "'s application for " + app.getPetName() + "?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Reject", (d, w) -> {
                                    db.updateApplicationStatus(app.getId(), "Rejected");
                                    db.markNotAdopted(app.getPetId());
                                    sendStatusNotification(app, "Rejected");
                                    Toast.makeText(this, "❌ Application rejected", Toast.LENGTH_SHORT).show();
                                    loadApplications();
                                    updateCounts();
                                })
                                .setNegativeButton(R.string.cancel, (d, w) -> d.dismiss())
                                .show();
                    });
                } else {
                    btnApprove.setVisibility(View.GONE);
                    btnReject.setVisibility(View.GONE);
                }

                appsList.addView(item);
            }
        }
    }

    private void styleStatusBadge(TextView tv, String status) {
        switch (status) {
            case "Pending":
                tv.setTextColor(getResources().getColor(R.color.pending_amber));
                tv.setBackgroundColor(getResources().getColor(R.color.pending_amber_light));
                break;
            case "Approved":
                tv.setTextColor(getResources().getColor(R.color.adopted_green));
                tv.setBackgroundColor(getResources().getColor(R.color.adopted_green_light));
                break;
            case "Rejected":
                tv.setTextColor(getResources().getColor(R.color.rejected_red));
                tv.setBackgroundColor(getResources().getColor(R.color.rejected_red_light));
                break;
        }
    }

    private void sendStatusNotification(AdoptionApplication app, String status) {
        Intent intent = new Intent(this, MyApplicationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, (int) app.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String title, text;
        int icon;
        if ("Approved".equals(status)) {
            title = "🎉 Application Approved!";
            text = "Great news! Your adoption application for " + app.getPetName() + " has been approved! Welcome your new furry friend!";
            icon = android.R.drawable.ic_dialog_info;
        } else {
            title = "😔 Application Rejected";
            text = "Unfortunately, your adoption application for " + app.getPetName() + " has been rejected. Don't give up — browse more pets!";
            icon = android.R.drawable.ic_dialog_alert;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CH_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setAutoCancel(true);

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .notify((int) (100 + app.getId()), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CH_ID, "Admin Decisions",
                    NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Notifications for admin accept/reject decisions");
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        }
    }
}
