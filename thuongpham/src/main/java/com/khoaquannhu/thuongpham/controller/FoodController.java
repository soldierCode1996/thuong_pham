package com.khoaquannhu.thuongpham.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khoaquannhu.thuongpham.dto.FoodRequestDto;
import com.khoaquannhu.thuongpham.dto.FoodWithValueDto;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/khoaquannhu/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @PostMapping(value = "/admin/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFood(
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart("data") String foodDataJson) throws IOException {

        String imageFoodUrl = null;
        if (image != null && !image.isEmpty()) {
            String uploadDir = new ClassPathResource("static/api/images/food").getFile().getAbsolutePath();
            String fileName = System.currentTimeMillis() + "-" + image.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            imageFoodUrl = "/api/images/food/" + fileName;
        }
        // Parse JSON thủ công
        ObjectMapper mapper = new ObjectMapper();
        FoodRequestDto foodRequestDto = mapper.readValue(foodDataJson, FoodRequestDto.class);
        Food food = new Food();
        if(foodRequestDto.getId()==null){
            Optional<Food> foodOrd = foodService.findByOrdinalNumberOrd(foodRequestDto.getOrdinalNumbers());
            if(foodOrd.isPresent()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Số thứ tự thực phẩm đã tồn tại");
            }else {
                food.setName(foodRequestDto.getName());
                food.setGroup(foodRequestDto.getGroup());
                food.setOrdinalNumbers(foodRequestDto.getOrdinalNumbers());
                food.setProtein(foodRequestDto.getProtein());
                food.setLipid(foodRequestDto.getLipid());
                food.setCarbohydrate(foodRequestDto.getCarbohydrate());
                food.setImage(imageFoodUrl);
            }
        }else {
            food = foodService.findById(foodRequestDto.getId());
            food.setName(foodRequestDto.getName());
            food.setGroup(foodRequestDto.getGroup());
            food.setOrdinalNumbers(foodRequestDto.getOrdinalNumbers());
            food.setProtein(foodRequestDto.getProtein());
            food.setLipid(foodRequestDto.getLipid());
            food.setCarbohydrate(foodRequestDto.getCarbohydrate());
            food.setImage(imageFoodUrl);
        }
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
    public ResponseEntity<byte[]> searchPdfPage(
            @RequestParam int foodOrdinalNumbers,
            @RequestParam String foodGroup)  {

        // Xây dựng đường dẫn tới file PDF trong static
        String fileName = "static/pdfs/" + foodGroup + ".pdf";
        ClassPathResource pdfResource = new ClassPathResource(fileName);

        // Đảm bảo file tồn tại
        if (!pdfResource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Không tìm thấy file PDF cho nhóm: " + foodGroup).getBytes());
        }

        try (PDDocument document = PDDocument.load(pdfResource.getInputStream())) {

            for (int page = 1; page <= document.getNumberOfPages(); page++) {
                PDFTextStripper pdfStripper = new PDFTextStripper(); // <-- Tạo mới ở đây
                pdfStripper.setStartPage(page);
                pdfStripper.setEndPage(page);
                String text = pdfStripper.getText(document);

//                System.out.println(">>> Đang kiểm tra trang " + page);
//                System.out.println(text);

                if (text.toLowerCase().contains("stt: " + foodOrdinalNumbers)) {
//                    System.out.println(">>> Tìm thấy STT: " + foodOrdinalNumbers + " ở trang " + page);

                    PDDocument singlePageDoc = new PDDocument();
                    singlePageDoc.addPage(document.getPage(page - 1));

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    singlePageDoc.save(outputStream);
                    singlePageDoc.close();

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("inline", "stt-" + foodOrdinalNumbers + ".pdf");

                    return ResponseEntity.ok().headers(headers).body(outputStream.toByteArray());
                }
            }

        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Lỗi khi tìm file PDF").getBytes());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(("Không tìm thấy STT: " + foodOrdinalNumbers + " trong nhóm " + foodGroup).getBytes());
    }

    @GetMapping("/build/ration/{energy}")
    public ResponseEntity<?> getRation(@PathVariable double energy){
        if(energy<2500 && energy>4860){
            return ResponseEntity.badRequest().body("Dữ liệu năng lượng không phù hợp");
        }else {
            Map<Food, Double> MapFoodList = foodService.renderMenuFoodByEnergy(energy);
            List<FoodWithValueDto> foodWithValueDtoList = MapFoodList.entrySet()
                    .stream().map(e->new FoodWithValueDto(e.getKey(), e.getValue())).toList();
            return ResponseEntity.ok(foodWithValueDtoList);
        }
    }

}
