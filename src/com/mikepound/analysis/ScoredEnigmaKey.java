package com.mikepound.analysis;

import com.mikepound.enigma.Enigma;

public class ScoredEnigmaKey extends EnigmaKey implements Comparable<ScoredEnigmaKey> {
    float score;

    public ScoredEnigmaKey(EnigmaKey key, float score) {
        super(key);
        this.score = score;
    }

    public float getScore() { return this.score; }

    @Override
    public int compareTo(ScoredEnigmaKey o) {
        return Float.compare(this.score, o.score);
    }
}
