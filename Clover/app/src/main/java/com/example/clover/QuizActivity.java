package com.example.clover;

import android.app.*;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class QuizActivity extends AppCompatActivity {
    private static final String CH_ID = "clover_quiz_channel";
    private RadioGroup rgQ1, rgQ2, rgQ3, rgQ4, rgQ5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        rgQ1 = findViewById(R.id.rgQ1);
        rgQ2 = findViewById(R.id.rgQ2);
        rgQ3 = findViewById(R.id.rgQ3);
        rgQ4 = findViewById(R.id.rgQ4);
        rgQ5 = findViewById(R.id.rgQ5);

        createNotificationChannel();

        findViewById(R.id.btnSubmitQuiz).setOnClickListener(v -> {
            if (rgQ1.getCheckedRadioButtonId() == -1 || rgQ2.getCheckedRadioButtonId() == -1 ||
                    rgQ3.getCheckedRadioButtonId() == -1 || rgQ4.getCheckedRadioButtonId() == -1 ||
                    rgQ5.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, getString(R.string.toast_answer_all), Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_quiz_title))
                    .setMessage(getString(R.string.dialog_quiz_msg))
                    .setPositiveButton(R.string.yes, (d, w) -> submitQuiz())
                    .setNegativeButton(R.string.cancel, (d, w) -> d.dismiss())
                    .show();
        });
    }

    private void submitQuiz() {
        int score = 0;
        if (rgQ1.getCheckedRadioButtonId() == R.id.rbQ1A) score++;
        if (rgQ2.getCheckedRadioButtonId() == R.id.rbQ2A) score++;
        if (rgQ3.getCheckedRadioButtonId() == R.id.rbQ3B) score++;
        if (rgQ4.getCheckedRadioButtonId() == R.id.rbQ4A) score++;
        if (rgQ5.getCheckedRadioButtonId() == R.id.rbQ5A) score++;

        SharedPreferences prefs = getSharedPreferences("CloverPrefs", MODE_PRIVATE);
        int high = prefs.getInt("quiz_high_score", 0);
        if (score > high) prefs.edit().putInt("quiz_high_score", score).apply();

        findViewById(R.id.scoreCard).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tvScore)).setText(score + " / 5");

        String msg;
        if (score == 5) msg = "🌟 Perfect! You're a pet care expert!";
        else if (score >= 3) msg = "👍 Good job! You know your pets well.";
        else msg = "📖 Keep learning about pet care!";
        ((TextView) findViewById(R.id.tvScoreMsg)).setText(msg);

        findViewById(R.id.btnSubmitQuiz).setEnabled(false);
        ((Button) findViewById(R.id.btnSubmitQuiz)).setText("Submitted ✓");

        sendScoreNotification(score);
    }

    private void sendScoreNotification(int score) {
        Intent intent = new Intent(this, QuizActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CH_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.notif_quiz_title))
                .setContentText(String.format(getString(R.string.notif_quiz_text), score))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setAutoCancel(true);

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(2, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CH_ID, "Quiz Results", NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        }
    }
}
