package com.example.clover;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private LinearLayout notifList;
    private View tvEmpty;
    private TextView tvUnreadCount;
    private Button btnMarkAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        db = new DatabaseHelper(this);
        notifList = findViewById(R.id.notifList);
        tvEmpty = findViewById(R.id.tvNotifEmpty);
        tvUnreadCount = findViewById(R.id.tvUnreadCount);
        btnMarkAll = findViewById(R.id.btnMarkAllRead);

        btnMarkAll.setOnClickListener(v -> {
            db.markAllNotificationsAsRead();
            Toast.makeText(this, "All notifications marked as read ✓", Toast.LENGTH_SHORT).show();
            loadNotifications();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        List<Notification> notifications = db.getAllNotifications();
        int unreadCount = db.getUnreadNotificationCount();
        notifList.removeAllViews();

        tvUnreadCount.setText(unreadCount + " unread");
        btnMarkAll.setVisibility(unreadCount > 0 ? View.VISIBLE : View.GONE);

        if (notifications.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            findViewById(R.id.scrollNotif).setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            findViewById(R.id.scrollNotif).setVisibility(View.VISIBLE);

            for (Notification notif : notifications) {
                View item = LayoutInflater.from(this).inflate(R.layout.item_notification, notifList, false);

                TextView tvIcon = item.findViewById(R.id.tvNotifIcon);
                TextView tvTitle = item.findViewById(R.id.tvNotifTitle);
                TextView tvMessage = item.findViewById(R.id.tvNotifMessage);
                TextView tvTime = item.findViewById(R.id.tvNotifTime);
                View unreadDot = item.findViewById(R.id.viewUnreadDot);

                // Set icon and color based on notification type
                switch (notif.getType()) {
                    case "approved":
                        tvIcon.setText("🎉");
                        tvTitle.setTextColor(getResources().getColor(R.color.adopted_green));
                        break;
                    case "rejected":
                        tvIcon.setText("😔");
                        tvTitle.setTextColor(getResources().getColor(R.color.rejected_red));
                        break;
                    default:
                        tvIcon.setText("🔔");
                        tvTitle.setTextColor(getResources().getColor(R.color.primary));
                        break;
                }

                tvTitle.setText(notif.getTitle());
                tvMessage.setText(notif.getMessage());
                tvTime.setText(notif.getCreatedAt());

                // Show unread indicator
                unreadDot.setVisibility(notif.isRead() ? View.GONE : View.VISIBLE);

                // Style unread items differently
                if (!notif.isRead()) {
                    item.setBackgroundResource(R.drawable.card_bg_unread);
                } else {
                    item.setBackgroundResource(R.drawable.card_bg);
                }

                // Mark as read on click
                item.setOnClickListener(v -> {
                    if (!notif.isRead()) {
                        db.markNotificationAsRead(notif.getId());
                        loadNotifications();
                    }
                });

                notifList.addView(item);
            }
        }
    }
}
