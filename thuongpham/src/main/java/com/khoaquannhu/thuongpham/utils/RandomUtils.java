package com.khoaquannhu.thuongpham.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Random;

public class RandomUtils {
    private static final Random rand = new Random();

    public static Long randomInt(Long min, Long max) {
        return rand.nextLong(max - min+1) + min;
    }
}
