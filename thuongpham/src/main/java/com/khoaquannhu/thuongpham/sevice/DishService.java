package com.khoaquannhu.thuongpham.sevice;

import com.khoaquannhu.thuongpham.entity.Dish;
import com.khoaquannhu.thuongpham.repository.DishRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> listDishes(){
        List<Dish> dishList = dishRepository.findAll();
        return dishList;
    }

    public List<Dish> searchByName(String name){
        List<Dish> dishList = dishRepository.findByNameLike(name);
        return dishList;
    }

    public void importExcel(MultipartFile file) throws Exception {

        List<Dish> dishes = new ArrayList<>();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // bỏ header
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Dish dish = new Dish();
            dish.setName(row.getCell(0).getStringCellValue());
            dish.setProtein(row.getCell(1).getNumericCellValue());
            dish.setLipid(row.getCell(2).getNumericCellValue());
            dish.setGlucide(row.getCell(3).getNumericCellValue());
            dish.setVolumeSuggest(row.getCell(4).getNumericCellValue());

            dishes.add(dish);
        }
        dishRepository.saveAll(dishes);
        workbook.close();
    }

}
