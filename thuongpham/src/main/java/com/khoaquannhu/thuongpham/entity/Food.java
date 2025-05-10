package com.khoaquannhu.thuongpham.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "foods")
public class Food {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "ordinal_numbers")
    private Integer ordinalNumbers;
    @Column(name = "food_group")
    private String group;
    @Column(name = "protein")
    private Double protein;
    @Column(name = "lipid")
    private Double lipid;
    @Column(name = "carbohydrate")
    private Double carbohydrate;
    @Column(name = "image")
    private String image;

}
