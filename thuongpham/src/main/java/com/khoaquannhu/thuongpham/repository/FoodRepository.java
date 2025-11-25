package com.khoaquannhu.thuongpham.repository;

import com.khoaquannhu.thuongpham.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    Food findByName(String name);

    @Query("SELECT min(f.id) from Food f")
    Long getMinId();

    @Query("SELECT max(f.id) from Food f")
    Long getMaxId();

    @Query("SELECT f FROM Food f WHERE f.id IN :ids")
    List<Food> findByIds(@Param("ids") List<Long> ids);

    @Query("SELECT f FROM Food f where lower(f.name) LIKE lower(concat('%', :name, '%') ) ")
    List<Food> findByNameLike(@Param("name") String name);

    Optional<Food> findByOrdinalNumbers(Integer ordinalNumbers);


}
