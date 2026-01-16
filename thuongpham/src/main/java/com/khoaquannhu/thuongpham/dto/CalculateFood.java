package com.khoaquannhu.thuongpham.dto;

import com.khoaquannhu.thuongpham.entity.Food;

public final class CalculateFood {

    private final Food food;
    private final double weight; // gram

    public CalculateFood(Food food, double weight) {
        if (food == null) {
            throw new IllegalArgumentException("Food cannot be null");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        this.food = food;
        this.weight = weight;
    }

    public Food getFood() {
        return food;
    }

    public double getWeight() {
        return weight;
    }

    // --- On-the-fly calculation ---
    public double getProtein() {
        return food.getProtein() * weight / 100.0;
    }

    public double getLipid() {
        return food.getLipid() * weight / 100.0;
    }

    public double getGlucid() {
        return food.getCarbohydrate() * weight / 100.0;
    }

    public double getEnergyKcal() {
        return getProtein() * 4 + getGlucid() * 4 + getLipid() * 9;
    }

    // Clone with new weight (immutable style)
    public CalculateFood withWeight(double newWeight) {
        return new CalculateFood(this.food, newWeight);
    }
}
