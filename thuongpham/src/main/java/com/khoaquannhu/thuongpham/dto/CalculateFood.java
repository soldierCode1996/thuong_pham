package com.khoaquannhu.thuongpham.dto;

import com.khoaquannhu.thuongpham.entity.Food;

public class CalculateFood {
    private String name;
    private Double weight;
    private Double protein;
    private Double lipid;
    private Double glucid;

    public CalculateFood(Food food, Double weight){
        this.name = food.getName();
        this.weight = weight;
        this.protein = food.getProtein() * weight/100;
        this.lipid = food.getLipid() * weight/100;
        this.glucid = food.getCarbohydrate() * weight/100;
    }

    public CalculateFood() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getLipid() {
        return lipid;
    }

    public void setLipid(Double lipid) {
        this.lipid = lipid;
    }

    public Double getGlucid() {
        return glucid;
    }

    public void setGlucid(Double glucid) {
        this.glucid = glucid;
    }
}
