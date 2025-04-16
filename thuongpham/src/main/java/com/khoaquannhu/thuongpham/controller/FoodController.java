package com.khoaquannhu.thuongpham.controller;

import com.khoaquannhu.thuongpham.dto.FoodRequestDto;
import com.khoaquannhu.thuongpham.entity.Food;
import com.khoaquannhu.thuongpham.sevice.FoodService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;


@RestController
@RequestMapping("/api/khoaquannhu/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @PostMapping(value = "admin/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFood(@RequestPart("image") MultipartFile image, @RequestPart("data")FoodRequestDto foodRequestDto) throws IOException {

        String imageFoodUrl = null;
        if(image!=null && !image.isEmpty()){
            String uploadDir = new ClassPathResource("static/images/food").getFile().getAbsolutePath();
            String fileName = System.currentTimeMillis() + "-" + image.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            imageFoodUrl = "/images/food/" + fileName;
        }

        Food food = new Food();
        food.setName(foodRequestDto.getName());
        food.setGroup(foodRequestDto.getGroup());
        food.setProtein(foodRequestDto.getProtein());
        food.setLipid(foodRequestDto.getLipid());
        food.setCarbohydrate(foodRequestDto.getCarbohydrate());
        food.setImage(imageFoodUrl);
        foodService.save(food);
        return ResponseEntity.status(HttpStatus.CREATED).body(food);
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<?> deleteFood(@PathVariable Long id){
        foodService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("/random/{size}")
    public ResponseEntity<List<Food>> getRandomFoods(@PathVariable int size) {
        List<Food> foodList = foodService.getRandomFoodList(size);
        return ResponseEntity.ok(foodList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Food>> search(@RequestParam String name){
        List<Food> foodList = foodService.searchByName(name);
        return ResponseEntity.ok(foodList);
    }


    @GetMapping("/pdfs")
    public ResponseEntity<byte[]> searchPdfPage(@RequestParam String foodName, @RequestParam String foodGroup) throws IOException {
        String fileName = "static/pdfs" + foodGroup + ".pdf";
        ClassPathResource pdfResource = new ClassPathResource(fileName);

       try(PDDocument document = PDDocument.load(pdfResource.getInputStream())) {
           PDFTextStripper pdfStripper = new PDFTextStripper();

           for(int page =1; page <= document.getNumberOfPages(); page++){
               pdfStripper.setStartPage(page);
               pdfStripper.setEndPage(page);
               String text = pdfStripper.getText(document);
               if(text.toLowerCase().contains(foodName.toLowerCase())) {
                   PDDocument singlePageDoc = new PDDocument();
                   singlePageDoc.addPage(document.getPage(page - 1));

                   ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                   singlePageDoc.save(outputStream);
                   singlePageDoc.close();

                   HttpHeaders headers = new HttpHeaders();
                   headers.setContentType(MediaType.APPLICATION_PDF);
                   headers.setContentDispositionFormData("inline", foodName + ".pdf");
                   return ResponseEntity.ok().headers(headers).body(outputStream.toByteArray());
               }
           }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("Không tìm thấy thực phẩm: " + foodName).getBytes());
    }

}
