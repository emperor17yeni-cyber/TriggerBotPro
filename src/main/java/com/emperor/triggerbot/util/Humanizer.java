package com.emperor.triggerbot.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Tıklama ritmini ve tepki süresini insana benzetir (Minecraft'a bağımlı değil).
 *
 * İnsan modunda:
 *  - hız sabit değil, 1,5-4 sn'de bir yavaşça değişir (ritim kayması)
 *  - her aralık log-normal dağılır (düzgün/uniform değil), küçük gauss sapması eklenir
 *  - arada kısa duraksamalar olur
 *  - tepki süresi ortada yoğunlaşır, ara sıra yavaş tepki gelir
 *  - arada bir vuruş fırsatı kaçırılır
 */
public final class Humanizer {
    private double driftCps;
    private double curCps;
    private long driftEnd;

    public void reset() {
        curCps = 0;
        driftEnd = 0;
    }

    public long interval(double minCps, double maxCps, int jitterMs, boolean human, long now) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        if (!human) {
            double cps = r.nextDouble(minCps, maxCps + 0.0001);
            long ms = Math.max(50L, (long) (1000.0 / cps));
            if (jitterMs > 0) ms = Math.max(50L, ms + r.nextLong(-jitterMs, jitterMs + 1L));
            return ms;
        }

        if (curCps <= 0 || now >= driftEnd) {
            driftCps = r.nextDouble(minCps, maxCps + 0.0001);
            driftEnd = now + r.nextLong(1500, 4000);
            if (curCps <= 0) curCps = driftCps;
        }
        curCps += (driftCps - curCps) * 0.35;

        double ms = 1000.0 / curCps * Math.exp(r.nextGaussian() * 0.10);
        if (jitterMs > 0) ms += r.nextGaussian() * jitterMs * 0.5;
        if (r.nextDouble() < 0.035) ms += r.nextLong(110, 300);
        return Math.max(50L, Math.min(800L, Math.round(ms)));
    }

    public int reaction(int minMs, int maxMs, boolean human) {
        if (maxMs <= minMs) return minMs;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        if (!human) return r.nextInt(minMs, maxMs + 1);
        double t = (r.nextDouble() + r.nextDouble()) / 2.0;
        int ms = minMs + (int) Math.round(t * (maxMs - minMs));
        if (r.nextDouble() < 0.08) ms += r.nextInt(40, 120);
        return ms;
    }

    /** İnsan modunda vuruş fırsatının bir kısmı kaçırılır. */
    public boolean shouldSkip() {
        return ThreadLocalRandom.current().nextDouble() < 0.035;
    }

    public long skipDelay() {
        return ThreadLocalRandom.current().nextLong(90, 220);
    }
}
