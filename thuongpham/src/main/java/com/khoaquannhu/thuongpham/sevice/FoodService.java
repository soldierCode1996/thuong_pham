package com.khoaquannhu.thuongpham.sevice;

import com.khoaquannhu.thuongpham.entity.Food;
import com.khoaquannhu.thuongpham.repository.FoodRepository;
import com.khoaquannhu.thuongpham.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FoodService {
    private final FoodRepository foodRepository;


    public FoodService(FoodRepository foodRepository){
        this.foodRepository = foodRepository;
    }

    public Food findByName(String name){
        return foodRepository.findByName(name);
    }

    public Food findById(Long id){
        return foodRepository.findById(id).get();
    }

    public void deleteById(Long id){
        foodRepository.deleteById(id);
    }

    public Food save(Food food){
        return foodRepository.save(food);
    }

    public List<Food> getRandomFoodList(int size){
        Long minId = foodRepository.getMinId();
        Long maxId = foodRepository.getMaxId();
        long count = foodRepository.count();
        if(minId == null || maxId == null){
            return Collections.emptyList();
        }
        if(count<size){
            return foodRepository.findAll();
        }


        Set<Long> randomIds = new HashSet<>(); // tạo ra một list id để truyền vào phương thức ở repo
        int maxTries = size * 5;
        int tries = 0;

        while(randomIds.size()<size && tries<maxTries){
            Long numberId = RandomUtils.randomInt(minId, maxId);
            tries++;
            randomIds.add(numberId);
        }

        List<Food> foodList = foodRepository.findByIds(new ArrayList<>(randomIds));
        Set<Long> alreadyRandomId = randomIds;
        if (foodList.size()<size){
            while(foodList.size()<size && tries<maxTries){
                Long numberId = RandomUtils.randomInt(minId, maxId);
                tries++;
                if(!alreadyRandomId.contains(numberId)){
                    if(foodRepository.existsById(numberId)){
                        foodRepository.findById(numberId).ifPresent(food -> {
                            foodList.add(food);
                            alreadyRandomId.add(numberId);
                        });
                    }
                }
            }
        }
        return foodList;
    }

    public List<Food> searchByName(String name){
        return foodRepository.findByNameLike(name);
    }

}
