package com.khoaquannhu.thuongpham.dto;

import com.khoaquannhu.thuongpham.entity.Food;

public class FoodWithValueDto {
    private Food food;
    private double value;

    public FoodWithValueDto() {
    }

    public FoodWithValueDto(Food food, double value) {
        this.food = food;
        this.value = value;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
