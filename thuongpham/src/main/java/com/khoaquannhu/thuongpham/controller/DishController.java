package com.khoaquannhu.thuongpham.controller;

import com.khoaquannhu.thuongpham.entity.Dish;
import com.khoaquannhu.thuongpham.sevice.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/khoaquannhu/dishes")
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;

    @GetMapping("/all")
    public ResponseEntity<List<Dish>> getAll() {
        List<Dish> dishList = dishService.listDishes();
        return ResponseEntity.ok(dishList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Dish>> search(@RequestParam String name){
        List<Dish> dishList = dishService.searchByName(name);
        return ResponseEntity.ok(dishList);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            dishService.importExcel(file);
            return ResponseEntity.ok("Import thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
