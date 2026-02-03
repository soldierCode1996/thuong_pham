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
@Table(name = "dishes")
public class Dish {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "protein")
    private Double protein;
    @Column(name = "lipid")
    private Double lipid;
    @Column(name = "glucide")
    private Double glucide;
    @Column(name = "volumeSuggest")
    private Double volumeSuggest;
}
