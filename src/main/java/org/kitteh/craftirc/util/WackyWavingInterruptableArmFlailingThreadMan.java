package org.kitteh.craftirc.util;

/**
 * Thanks to a shipping error, I am currently overstocked on threads, and I
 * am passing the savings on to you!
 */
public final class WackyWavingInterruptableArmFlailingThreadMan {
    private final Thread target;

    public WackyWavingInterruptableArmFlailingThreadMan(Thread target) {
        this.target = target;
    }

    public void wackyWavingInterruptableArmFlailingThreadMan() {
        this.target.interrupt();
    }

    @Override
    public boolean equals(Object wackyWavingInterruptableArmFlailingThreadMan) {
        return wackyWavingInterruptableArmFlailingThreadMan instanceof WackyWavingInterruptableArmFlailingThreadMan && this.target.equals(((WackyWavingInterruptableArmFlailingThreadMan) wackyWavingInterruptableArmFlailingThreadMan).target);
    }

    @Override
    public int hashCode() {
        return this.target.hashCode() * 2;
    }
}