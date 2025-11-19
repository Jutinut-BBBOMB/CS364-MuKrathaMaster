package com.example.mukrathamaster;

public abstract class Meat {
    protected MeatState state = MeatState.RAW; // เริ่มต้นเป็น ดิบ เสมอ
    protected int currentCookingTime = 0;      // เวลาที่ปิ้งไปแล้ว (วินาที)

    // ฟังก์ชันสำหรับอัปเดตเวลาปิ้ง (ให้ลูกหลานไปเขียนกติกาความสุกเอง)
    public abstract void updateCooking(int seconds);

    // ฟังก์ชันคำนวณคะแนน
    public int getScore() {
        if (state == MeatState.BURNT) {
            return -50; // กติกา: ถ้าไหม้ ลบ 50 คะแนนทันที
        }
        return calculateScoreByDoneness(); // ถ้าไม่ไหม้ ให้ไปดูคะแนนตามความสุก
    }

    // ฟังก์ชันย่อยให้ลูกหลานกำหนดคะแนนเอง
    protected abstract int calculateScoreByDoneness();

    // ฟังก์ชันดึงสถานะปัจจุบัน (เอาไว้ให้หน้าจอเช็คว่าจะแสดงรูปอะไร)
    public MeatState getState() {
        return state;
    }

    // ฟังก์ชันรีเซ็ต (เผื่อหยิบชิ้นใหม่)
    public void reset() {
        state = MeatState.RAW;
        currentCookingTime = 0;
    }
}
