package com.emperor.triggerbot.util;

/**
 * Tıklama zamanlayıcısı + gerçek CPS sayacı (Minecraft'a bağımlı değil).
 *
 * Minecraft girdiyi 50 ms'lik tick'lerde işlediği için "şu kadar ms sonra vur" mantığı tick sınırına
 * takılıp hızı düşürür (10 CPS hedefi 6-7 CPS'e iner). Burada bir sonraki vuruş zamanı bir önceki
 * PLANLANAN zamandan hesaplanır; tick gecikmesi bir sonraki aralıktan düşülür ve ortalama hız korunur.
 */
public final class ClickTimer {
    private static final int RING = 64;
    private final long[] clicks = new long[RING];
    private int head;
    private int size;
    private long nextAt;

    public boolean due(long now) {
        return now >= nextAt;
    }

    /** Vuruş yapıldı: sayaca ekle ve sonraki vuruşu planla. */
    public void fired(long now, long intervalMs) {
        long base = (nextAt > 0 && now - nextAt < 100) ? nextAt : now;
        nextAt = base + intervalMs;
        clicks[head] = now;
        head = (head + 1) & (RING - 1);
        if (size < RING) size++;
    }

    /** Sonraki vuruşu en az {@code ms} kadar ertele (insansı duraksama / ıska için). */
    public void delay(long now, long ms) {
        nextAt = Math.max(nextAt, now + ms);
    }

    /** Son 1 saniyedeki gerçek tıklama sayısı. */
    public int cps(long now) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            int idx = (head - 1 - i) & (RING - 1);
            if (now - clicks[idx] <= 1000) count++;
            else break;
        }
        return count;
    }

    /** Zamanlamayı sıfırlar; CPS geçmişi kalır ki HUD sayısı birden sıfırlanmasın. */
    public void resetSchedule() {
        nextAt = 0;
    }
}
