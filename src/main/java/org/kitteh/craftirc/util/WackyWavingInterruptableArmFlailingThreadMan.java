/*
 * * Copyright (C) 2014 Matt Baxter http://kitteh.org
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

    public void interrupt() {
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