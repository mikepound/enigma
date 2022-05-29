package com.mikepound.enigma;

public class RotorState {

    public final RotorType rotorType;
    private final int ringOffset;

    private int rotorPosition;

    public RotorState(RotorType rotorType, int ringOffset, int initialRotorPosition) {
        this.rotorType = rotorType;
        this.ringOffset = ringOffset;
        this.rotorPosition = initialRotorPosition;
    }

    public int forward(int c) {
        return this.rotorType.encipher(c, this.ringOffset, this.rotorPosition, true);
    }

    public int backward(int c) {
        return this.rotorType.encipher(c, this.ringOffset, this.rotorPosition, false);
    }

    public boolean isAtNotch() {
        return this.rotorType.isAtNotch(this.rotorPosition);
    }

    public void turnover() {
        this.rotorPosition = (this.rotorPosition + 1) % 26;
    }
}
