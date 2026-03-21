package com.example.maternalmindavr.activities;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.maternalmindavr.R;
import com.example.maternalmindavr.database.MoodDbHelper;

public class AssessmentActivity extends AppCompatActivity {
    private RadioGroup[] groups = new RadioGroup[7];
    private Button btnSubmit;
    private TextView tvResult;
    private View cardResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        groups[0] = findViewById(R.id.rgQ1);
        groups[1] = findViewById(R.id.rgQ2);
        groups[2] = findViewById(R.id.rgQ3);
        groups[3] = findViewById(R.id.rgQ4);
        groups[4] = findViewById(R.id.rgQ5);
        groups[5] = findViewById(R.id.rgQ6);
        groups[6] = findViewById(R.id.rgQ7);

        btnSubmit = findViewById(R.id.btnSubmitAssessment);
        tvResult = findViewById(R.id.tvAssessmentResult);
        cardResult = findViewById(R.id.cardResult);

        btnSubmit.setOnClickListener(v -> calculateScore());
    }

    private void calculateScore() {
        int totalScore = 0;
        for (RadioGroup rg : groups) {
            int selectedId = rg.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show();
                return;
            }
            View radioButton = rg.findViewById(selectedId);
            totalScore += Integer.parseInt(radioButton.getTag().toString());
        }

        String level;
        String message;

        if (totalScore < 7) {
            level = "Low";
            message = "Your score is low. You are coping well! Keep taking care of yourself.";
        } else if (totalScore < 13) {
            level = "Moderate";
            message = "Your score suggests some symptoms of distress. Consider talking to your doctor or a trusted friend.";
        } else {
            level = "High";
            message = "Your score is high. We strongly recommend reaching out to a mental health professional for support.";
        }

        tvResult.setText("EPDS Score: " + totalScore + "\nResult: " + level + " Risk\n\n" + message);
        cardResult.setVisibility(View.VISIBLE);

        saveToDb(totalScore, level);
        
        // Scroll to result
        cardResult.getParent().requestChildFocus(cardResult, cardResult);
    }

    private void saveToDb(int score, String level) {
        MoodDbHelper dbHelper = new MoodDbHelper(this);
        ContentValues values = new ContentValues();
        values.put(MoodDbHelper.COLUMN_SCORE, score);
        values.put(MoodDbHelper.COLUMN_RISK_LEVEL, level);
        values.put(MoodDbHelper.COLUMN_TIMESTAMP, System.currentTimeMillis());
        dbHelper.insertData(MoodDbHelper.TABLE_ASSESSMENTS, values);
        Toast.makeText(this, "Assessment saved securely.", Toast.LENGTH_SHORT).show();
    }
}
