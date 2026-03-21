package com.example.maternalmindavr.ml;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.maternalmindavr.database.MoodDbHelper;
import com.example.maternalmindavr.models.MoodEntry;
import java.util.List;
import java.util.Random;

public class MLModelManager {

    /**
     * Enhanced prediction combining Active (Mood) and Passive (Sleep/Activity) data.
     */
    public RiskResult predictRisk(Context context, List<MoodEntry> moodEntries) {
        MoodDbHelper dbHelper = new MoodDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Fetch last passive data
        float sleepHours = 7.0f; 
        Cursor cursor = db.rawQuery("SELECT * FROM " + MoodDbHelper.TABLE_PASSIVE + " ORDER BY " + MoodDbHelper.COLUMN_TIMESTAMP + " DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            sleepHours = cursor.getFloat(cursor.getColumnIndexOrThrow(MoodDbHelper.COLUMN_SLEEP));
        }
        cursor.close();

        // Dummy Logic representing a trained model weights
        float riskScore = 0.5f;
        
        // Negative mood impact
        if (moodEntries.size() > 0) {
            String lastMood = moodEntries.get(0).getMood().toLowerCase();
            if (lastMood.contains("sad") || lastMood.contains("anxious")) riskScore += 0.2f;
            if (lastMood.contains("happy")) riskScore -= 0.1f;
        }

        // Sleep impact
        if (sleepHours < 5.0f) riskScore += 0.2f;
        if (sleepHours > 8.0f) riskScore -= 0.1f;

        // Clamp 0-1
        riskScore = Math.max(0, Math.min(1, riskScore));

        String level;
        String advice;
        if (riskScore < 0.3) {
            level = "Low";
            advice = "You're doing great! Keep maintaining your routine.";
        } else if (riskScore < 0.7) {
            level = "Moderate";
            advice = "Try relaxation exercises or talking to a friend today.";
        } else {
            level = "High";
            advice = "Please consider reaching out for professional support.";
        }

        return new RiskResult(riskScore, level, advice);
    }

    public String performLocalTraining() {
        return "Encrypted-Hash-" + System.currentTimeMillis();
    }

    public static class RiskResult {
        public float score;
        public String level;
        public String advice;

        public RiskResult(float score, String level, String advice) {
            this.score = score;
            this.level = level;
            this.advice = advice;
        }
    }
}
