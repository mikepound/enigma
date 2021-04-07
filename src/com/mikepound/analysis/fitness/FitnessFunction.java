package com.mikepound.analysis.fitness;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class FitnessFunction {
    protected final float epsilon = 3e-10f;

    public float score(char[] text) {
        return 0f;
    }
}
