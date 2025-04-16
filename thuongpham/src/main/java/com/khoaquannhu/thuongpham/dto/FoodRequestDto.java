package com.khoaquannhu.thuongpham.dto;

import lombok.Data;

@Data
public class FoodRequestDto {
    private String name;
    private String group;
    private double protein;
    private double lipid;
    private double carbohydrate;
}
