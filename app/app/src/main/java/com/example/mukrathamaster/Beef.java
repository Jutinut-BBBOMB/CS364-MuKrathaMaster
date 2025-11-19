package com.example.mukrathamaster;

public class Beef extends Meat {

    @Override
    public void updateCooking(int seconds) {
        currentCookingTime += seconds;

        // กำหนดช่วงเวลาความสุกของเนื้อวัว (หน่วย: วินาที)
        if (currentCookingTime > 40) {
            state = MeatState.BURNT;        // เกิน 40 วิ = ไหม้
        } else if (currentCookingTime > 30) {
            state = MeatState.WELL_DONE;    // 31-40 วิ = สุกมาก
        } else if (currentCookingTime > 20) {
            state = MeatState.MEDIUM_RARE;  // 21-30 วิ = กำลังดี
        } else if (currentCookingTime > 10) {
            state = MeatState.RARE;         // 11-20 วิ = สุกๆ ดิบๆ
        } else {
            state = MeatState.RAW;          // 0-10 วิ = ดิบ
        }
    }

    @Override
    protected int calculateScoreByDoneness() {
        // กำหนดคะแนนตามความยากง่าย
        switch (state) {
            case MEDIUM_RARE: return 150; // Perfect!
            case WELL_DONE:   return 100;
            case RARE:        return 50;
            default:          return 0;   // ดิบ ไม่ได้คะแนน
        }
    }
}