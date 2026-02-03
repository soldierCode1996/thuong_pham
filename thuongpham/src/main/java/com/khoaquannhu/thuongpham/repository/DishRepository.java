package com.khoaquannhu.thuongpham.repository;

import com.khoaquannhu.thuongpham.entity.Dish;
import com.khoaquannhu.thuongpham.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    @Query("SELECT f FROM Dish f where lower(f.name) LIKE lower(concat('%', :name, '%') ) ")
    List<Dish> findByNameLike(@Param("name") String name);
}
