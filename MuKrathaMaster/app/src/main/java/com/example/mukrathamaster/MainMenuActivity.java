package com.example.mukrathamaster;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // เชื่อมต่อปุ่ม
        ImageButton btnStartGame = findViewById(R.id.btnStartGame);
        ImageButton btnContinueGame = findViewById(R.id.btnContinueGame);
        ImageButton btnHistory = findViewById(R.id.btnHistory);

        // ปุ่มเริ่มเกมใหม่
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                intent.putExtra("IS_CONTINUE", false);
                startActivity(intent);
            }
        });

        // ปุ่มเล่นต่อ (เช็คว่ามีเซฟไหม)
        btnContinueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
                if (prefs.getBoolean("HAS_SAVE", false)) {
                    Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                    intent.putExtra("IS_CONTINUE", true);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainMenuActivity.this, "ไม่พบประวัติการเล่น", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ปุ่มดูประวัติ
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistoryDialog();
            }
        });
    }

    // ฟังก์ชันแสดงหน้าต่างประวัติ
    private void showHistoryDialog() {
        // 1. ดึงข้อมูลจากเครื่อง
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        int highScore = prefs.getInt("HIGH_SCORE", 0);
        int lastScore = prefs.getInt("SAVE_SCORE", 0);
        int lastGrilled = prefs.getInt("SAVE_GRILLED", 0);

        // 2. สร้าง Dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_history);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true); // กดข้างนอกเพื่อปิดได้

        // 3. เชื่อมตัวแปรและแสดงผล
        TextView tvHighScore = dialog.findViewById(R.id.tvHighScore);
        TextView tvLastStats = dialog.findViewById(R.id.tvLastStats);
        ImageButton btnCloseHistory = dialog.findViewById(R.id.btnCloseHistory);

        tvHighScore.setText(String.valueOf(highScore));
        tvLastStats.setText("คะแนนล่าสุด: " + lastScore + "\nปิ้งไปทั้งหมด: " + lastGrilled + " ชิ้น");

        // 4. ปุ่มปิด
        btnCloseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}