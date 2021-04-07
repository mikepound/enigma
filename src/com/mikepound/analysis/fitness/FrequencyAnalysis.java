package com.mikepound.analysis.fitness;

public class FrequencyAnalysis {
    private float[] counts;
    private int total;

    public FrequencyAnalysis() {
        this.total = 0;
        this.counts = new float[26];
    }

    public void analyse(byte[] text) {
        for (byte b: text) {
            this.counts[b]++;
        }
        this.total += text.length;
    }

    public float[] frequencies() {
        float[] freq = new float[26];
        for (int i = 0; i < freq.length; i++) {
            freq[i] = this.counts[i] / total;
        }
        return freq;
    }
}
