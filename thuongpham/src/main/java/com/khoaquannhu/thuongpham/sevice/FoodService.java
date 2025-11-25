package com.khoaquannhu.thuongpham.sevice;

import com.khoaquannhu.thuongpham.dto.CalculateFood;
import com.khoaquannhu.thuongpham.entity.Food;
import com.khoaquannhu.thuongpham.exception.GlobalExceptionHandler;
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
        return foodRepository.findById(id).orElseThrow(() ->
                new GlobalExceptionHandler.DataNotEnoughException("Food not found"));
    }

    public void deleteById(Long id){
        foodRepository.deleteById(id);
    }

    public Food save(Food food){
        return foodRepository.save(food);
    }
    public Food findByOrdinalNumber (Integer ordinalNumber){
        return foodRepository.findByOrdinalNumbers(ordinalNumber).orElseThrow(()->new GlobalExceptionHandler.DataNotEnoughException("Không đủ dữ kiệu để thực hiện chương trình đúng"));
    }

    public Optional<Food> findByOrdinalNumberOrd(Integer ord){
        return foodRepository.findByOrdinalNumbers(ord);
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


    public static List<Integer> pickRandom(Set<Integer> integerSet, int count){
        if(count>integerSet.size()){
            throw new IllegalArgumentException("Số phần tử yêu cầu lớn hơn kích thước của tập");
        }
        List<Integer> integerList = new ArrayList<>(integerSet);
        Collections.shuffle(integerList);
        List<Integer> subList = integerList.subList(0,count);
        return subList;
    }

    public Map<Food, Double> renderMenuFoodByEnergy(double energy){
        Map<Food, Double> MapFoodList = new HashMap<>();
        Integer OrdinalNuoMam = 502;
        Integer OrdinalGao = 4;
        Integer OrdinalOilPlants = 266;
        Integer OrdinalOilAnimal = 268;
        Integer OrdinalTofu = 74;
        Integer OrdinalDuongKinh = 474;
        Integer OrdinalMilk = 431;
        Food MiChinh = new Food();
        MiChinh.setName("Mì chính (Bột ngọt)");
        MiChinh.setOrdinalNumbers(527);
        MiChinh.setProtein(99.8);
        MiChinh.setLipid(0.0);
        MiChinh.setCarbohydrate(0.0);

        Set<Integer> Veges = Set.of(36,37,83,84,85,89,91,92,95,96,97,98,99,101,103,111,112,115,118,136,137,148,152,155,156,158,165,168,178,180,181,193,194,195);
        Set<Integer> Meat = Set.of(280,281,282,291,296,295,301,306,310,331,335,342,343,346,347,348,358);
        Set<Integer> SeaFood = Set.of(363,374,384,289,390,392,400,411,412);
        Set<Integer> Egg = Set.of(420,423,426);
        Set<Integer> Dessert = Set.of(209,210,214,215,219,222,225,235,243,248,252,255,258,260,263);

        double energy_main = energy * 10/100;
        double m_protein = (energy_main * 17/100)/4;
        double m_lipid = (energy_main * 18/100)/9;
        double m_glucose = (energy_main * 65/100)/4;
        double m_protein_animal = m_protein * 50/100;
        double m_protein_plants = m_protein - m_protein_animal;
        double m_lipid_animal = m_lipid *50/100;
        double m_lipid_plants = m_lipid - m_lipid_animal;

        Food Gao = findByOrdinalNumber(OrdinalGao);
        Food NuoMam = findByOrdinalNumber(OrdinalNuoMam);
        Food Milk = findByOrdinalNumber(OrdinalMilk);
        CalculateFood CalGao = new CalculateFood(Gao, 0.0);
        CalculateFood CalNuoMam = new CalculateFood(NuoMam, 40.0);
        CalculateFood CalMiChinh = new CalculateFood(MiChinh, 2.0);
        CalculateFood CalMilk = new CalculateFood(Milk, 0.0);

        if(energy>=2500 &&  energy<3500){

            if(energy<2800){
                CalGao.setWeight(600.0);
            }else if(energy>=2800 && energy<3200){
                CalGao.setWeight(650.0);
            }else{
                CalGao.setWeight(680.0);
            }

            List<Integer> Raulist = pickRandom(Veges, 3);
            Food Rau_1 = findByOrdinalNumber(Raulist.get(0));
            Food Rau_2 = findByOrdinalNumber(Raulist.get(1));
            Food Rau_3 = findByOrdinalNumber(Raulist.get(2));

            CalculateFood CalRau_1 = new CalculateFood(Rau_1, 100.0);
            CalculateFood CalRau_2 = new CalculateFood(Rau_2, 150.0);
            CalculateFood CalRau_3 = new CalculateFood(Rau_3, 150.0);
            List<Integer> Desertlist = pickRandom(Dessert, 2);
            Food Desert_1 = findByOrdinalNumber(Desertlist.get(0));
            Food Desert_2 = findByOrdinalNumber(Desertlist.get(1));
            CalculateFood CalDesert_1 = new CalculateFood(Desert_1, 150.0);
            CalculateFood CalDesert_2 = new CalculateFood(Desert_2, 150.0);
            double m_Glu_conti = m_glucose - CalGao.getGlucid()-CalMiChinh.getGlucid()
                                - CalRau_1.getGlucid() - CalRau_2.getGlucid() - CalRau_3.getGlucid()
                                - CalDesert_1.getGlucid() - CalDesert_2.getGlucid();
            Food DuongKinh = findByOrdinalNumber(OrdinalDuongKinh);
            CalculateFood  CalDuongKinh = new CalculateFood(DuongKinh, 0.0);
            if (m_Glu_conti>0){
                CalDuongKinh.setWeight(m_Glu_conti*100/DuongKinh.getCarbohydrate());
            }
            MapFoodList.put(Gao, CalGao.getWeight());
            MapFoodList.put(NuoMam,CalNuoMam.getWeight());
            MapFoodList.put(MiChinh,CalMiChinh.getWeight());
            MapFoodList.put(Rau_1,CalRau_1.getWeight());
            MapFoodList.put(Rau_2,CalRau_2.getWeight());
            MapFoodList.put(Rau_3, CalRau_3.getWeight());
            MapFoodList.put(Desert_1, CalDesert_1.getWeight());
            MapFoodList.put(Desert_2, CalDesert_2.getWeight());
            MapFoodList.put(DuongKinh, CalDuongKinh.getWeight());
            double m_proTV_conti = m_protein_plants-CalGao.getProtein()
                                    - CalMiChinh.getProtein()
                                    - CalRau_1.getProtein()
                                    - CalRau_2.getProtein()
                                    - CalRau_3.getProtein()
                                    - CalDesert_1.getProtein()
                                    - CalDesert_2.getProtein();
            Food DauPhu = findByOrdinalNumber(OrdinalTofu);
            CalculateFood CalDauPhu = new CalculateFood(DauPhu, m_proTV_conti*100/DauPhu.getProtein());
            MapFoodList.put(DauPhu, CalDauPhu.getWeight());
            List<Integer> Meatlist = pickRandom(Meat, 4);
            Food Meat_1 = findByOrdinalNumber(Meatlist.get(0));
            Food Meat_2 = findByOrdinalNumber(Meatlist.get(1));
            Food Meat_3 = findByOrdinalNumber(Meatlist.get(2));
            Food Meat_4 = findByOrdinalNumber(Meatlist.get(3));
            List<Integer> SeaFoodlist = pickRandom(SeaFood, 1);
            List<Integer> EggList = pickRandom(Egg,1);
            Food fish = findByOrdinalNumber(SeaFoodlist.get(0));
            Food egg = findByOrdinalNumber(EggList.get(0));
            CalculateFood CalMeat1 = new CalculateFood(Meat_1, 80.0);
            CalculateFood CalMeat2 = new CalculateFood(Meat_2, 50.0);
            CalculateFood CalMeat3 = new CalculateFood(Meat_3, 50.0);
            CalculateFood CalMeat4 = new CalculateFood(Meat_4, 0.0);
            CalculateFood CalFish = new CalculateFood(fish, 150.0);
            CalculateFood CalEgg = new CalculateFood(egg, 50.0);
            double m_proDV_conti = m_protein_animal - CalNuoMam.getProtein()
                                    - CalMeat1.getProtein()
                                    - CalMeat2.getProtein()
                                    - CalMeat3.getProtein()
                                    - CalFish.getProtein()
                                    - CalEgg.getProtein();
            if(m_proDV_conti>0){
                CalMeat4.setWeight(m_proDV_conti*100/ CalMeat4.getProtein());
            }
            MapFoodList.put(Meat_1, CalMeat1.getWeight());
            MapFoodList.put(Meat_2, CalMeat2.getWeight());
            MapFoodList.put(Meat_3, CalMeat3.getWeight());
            MapFoodList.put(Meat_4, CalMeat4.getWeight());
            MapFoodList.put(fish, CalFish.getWeight());
            MapFoodList.put(egg, CalEgg.getWeight());
            double m_LipidTV_conti = m_lipid_plants-CalGao.getLipid()
                    - CalMiChinh.getLipid()
                    - CalRau_1.getLipid()
                    - CalRau_2.getLipid()
                    - CalRau_3.getLipid()
                    - CalDesert_1.getLipid()
                    - CalDesert_2.getLipid()
                    - CalDauPhu.getLipid();
            double m_LipidDV_conti = m_lipid_animal - CalNuoMam.getLipid()
                    - CalMeat1.getLipid()
                    - CalMeat2.getLipid()
                    - CalMeat3.getLipid()
                    - CalMeat4.getLipid()
                    - CalFish.getLipid()
                    - CalEgg.getLipid();
            Food MoDV = findByOrdinalNumber(OrdinalOilAnimal);
            Food DauTV = findByOrdinalNumber(OrdinalOilPlants);
            CalculateFood CalMoDV = new CalculateFood(MoDV, 0.0);
            CalculateFood  CalDauTV = new CalculateFood(DauTV, 0.0);
            if (m_LipidTV_conti>0){
                CalDauTV.setWeight(m_LipidTV_conti*100/DauTV.getLipid());
            }
            if (m_LipidDV_conti>0){
                CalMoDV.setWeight(m_LipidDV_conti*100/MoDV.getLipid());
            }
            MapFoodList.put(DauTV, CalDauTV.getWeight());
            MapFoodList.put(MoDV, CalMoDV.getWeight());
        }else if(energy>=3500 && energy<=4860){
            if(energy<4000){
                CalGao.setWeight(700.0);
            }else if(energy>=4000 && energy<4500){
                CalGao.setWeight(740.0);
            }else{
                CalGao.setWeight(750.0);
            }

            List<Integer> Raulist = pickRandom(Veges, 4);
            Food Rau_1 = findByOrdinalNumber(Raulist.get(0));
            Food Rau_2 = findByOrdinalNumber(Raulist.get(1));
            Food Rau_3 = findByOrdinalNumber(Raulist.get(2));
            Food Rau_4 = findByOrdinalNumber(Raulist.get(3));

            CalculateFood CalRau_1 = new CalculateFood(Rau_1, 100.0);
            CalculateFood CalRau_2 = new CalculateFood(Rau_2, 150.0);
            CalculateFood CalRau_3 = new CalculateFood(Rau_3, 150.0);
            CalculateFood CalRau_4 = new CalculateFood(Rau_3, 100.0);


            List<Integer> Desertlist = pickRandom(Dessert, 2);
            Food Desert_1 = findByOrdinalNumber(Desertlist.get(0));
            Food Desert_2 = findByOrdinalNumber(Desertlist.get(1));
            CalculateFood CalDesert_1 = new CalculateFood(Desert_1, 200.0);
            CalculateFood CalDesert_2 = new CalculateFood(Desert_2, 200.0);

            if(energy<4000){
                CalMilk.setWeight(180.0);
            }else {
                CalMilk.setWeight(360.0);
            }

            double m_Glu_conti = m_glucose - CalGao.getGlucid()-CalMiChinh.getGlucid()
                    - CalRau_1.getGlucid() - CalRau_2.getGlucid()
                    - CalRau_3.getGlucid() - CalRau_4.getGlucid()
                    - CalDesert_1.getGlucid() - CalDesert_2.getGlucid()-CalMilk.getGlucid();
            Food DuongKinh = findByOrdinalNumber(OrdinalDuongKinh);
            CalculateFood  CalDuongKinh = new CalculateFood(DuongKinh, 0.0);
            if (m_Glu_conti>0){
                CalDuongKinh.setWeight(m_Glu_conti*100/DuongKinh.getCarbohydrate());
            }

            MapFoodList.put(Gao, CalGao.getWeight());
            MapFoodList.put(NuoMam,CalNuoMam.getWeight());
            MapFoodList.put(MiChinh,CalMiChinh.getWeight());
            MapFoodList.put(Rau_1,CalRau_1.getWeight());
            MapFoodList.put(Rau_2,CalRau_2.getWeight());
            MapFoodList.put(Rau_3, CalRau_3.getWeight());
            MapFoodList.put(Rau_4, CalRau_4.getWeight());
            MapFoodList.put(Desert_1, CalDesert_1.getWeight());
            MapFoodList.put(Desert_2, CalDesert_2.getWeight());
            MapFoodList.put(Milk, CalMilk.getWeight());
            MapFoodList.put(DuongKinh, CalDuongKinh.getWeight());

            double m_proTV_conti = m_protein_plants-CalGao.getProtein()
                    - CalMiChinh.getProtein()
                    - CalRau_1.getProtein()
                    - CalRau_2.getProtein()
                    - CalRau_3.getProtein()
                    - CalRau_4.getProtein()
                    - CalDesert_1.getProtein()
                    - CalDesert_2.getProtein();
            Food DauPhu = findByOrdinalNumber(OrdinalTofu);
            CalculateFood CalDauPhu = new CalculateFood(DauPhu, m_proTV_conti*100/DauPhu.getProtein());
            MapFoodList.put(DauPhu, CalDauPhu.getWeight());
            List<Integer> Meatlist = pickRandom(Meat, 5);
            Food Meat_1 = findByOrdinalNumber(Meatlist.get(0));
            Food Meat_2 = findByOrdinalNumber(Meatlist.get(1));
            Food Meat_3 = findByOrdinalNumber(Meatlist.get(2));
            Food Meat_4 = findByOrdinalNumber(Meatlist.get(3));
            Food Meat_5 = findByOrdinalNumber(Meatlist.get(4));
            List<Integer> SeaFoodlist = pickRandom(SeaFood, 2);
            List<Integer> EggList = pickRandom(Egg,1);
            Food fish_1 = findByOrdinalNumber(SeaFoodlist.get(0));
            Food fish_2 = findByOrdinalNumber(SeaFoodlist.get(1));
            Food egg = findByOrdinalNumber(EggList.get(0));
            CalculateFood CalMeat1 = new CalculateFood(Meat_1, 80.0);
            CalculateFood CalMeat2 = new CalculateFood(Meat_2, 50.0);
            CalculateFood CalMeat3 = new CalculateFood(Meat_3, 50.0);
            CalculateFood CalMeat4 = new CalculateFood(Meat_4, 50.0);
            CalculateFood CalMeat5 = new CalculateFood(Meat_5, 0.0);
            CalculateFood CalFish_1 = new CalculateFood(fish_1, 150.0);
            CalculateFood CalFish_2 = new CalculateFood(fish_2, 150.0);
            CalculateFood CalEgg = new CalculateFood(egg, 50.0);
            double m_proDV_conti = m_protein_animal - CalNuoMam.getProtein()
                    - CalMeat1.getProtein()
                    - CalMeat2.getProtein()
                    - CalMeat3.getProtein()
                    - CalMeat4.getProtein()
                    - CalFish_1.getProtein()
                    - CalFish_2.getProtein()
                    - CalEgg.getProtein()
                    - CalMilk.getProtein();
            if(m_proDV_conti>0){
                CalMeat5.setWeight(m_proDV_conti*100/ CalMeat4.getProtein());
            }
            MapFoodList.put(Meat_1, CalMeat1.getWeight());
            MapFoodList.put(Meat_2, CalMeat2.getWeight());
            MapFoodList.put(Meat_3, CalMeat3.getWeight());
            MapFoodList.put(Meat_4, CalMeat4.getWeight());
            MapFoodList.put(Meat_5, CalMeat5.getWeight());
            MapFoodList.put(fish_1, CalFish_1.getWeight());
            MapFoodList.put(fish_2, CalFish_2.getWeight());
            MapFoodList.put(egg, CalEgg.getWeight());
            double m_LipidTV_conti = m_lipid_plants-CalGao.getLipid()
                    - CalMiChinh.getLipid()
                    - CalRau_1.getLipid()
                    - CalRau_2.getLipid()
                    - CalRau_3.getLipid()
                    - CalRau_4.getLipid()
                    - CalDesert_1.getLipid()
                    - CalDesert_2.getLipid()
                    - CalDauPhu.getLipid();
            double m_LipidDV_conti = m_lipid_animal - CalNuoMam.getLipid()
                    - CalMeat1.getLipid()
                    - CalMeat2.getLipid()
                    - CalMeat3.getLipid()
                    - CalMeat4.getLipid()
                    - CalMeat5.getLipid()
                    - CalFish_1.getLipid()
                    - CalFish_2.getLipid()
                    - CalEgg.getLipid()
                    - CalMilk.getLipid();
            Food MoDV = findByOrdinalNumber(OrdinalOilAnimal);
            Food DauTV = findByOrdinalNumber(OrdinalOilPlants);
            CalculateFood CalMoDV = new CalculateFood(MoDV, 0.0);
            CalculateFood  CalDauTV = new CalculateFood(DauTV, 0.0);
            if (m_LipidTV_conti>0){
                CalDauTV.setWeight(m_LipidTV_conti*100/DauTV.getLipid());
            }
            if (m_LipidDV_conti>0){
                CalMoDV.setWeight(m_LipidDV_conti*100/MoDV.getLipid());
            }
            MapFoodList.put(DauTV, CalDauTV.getWeight());
            MapFoodList.put(MoDV, CalMoDV.getWeight());
        }else{
            throw new GlobalExceptionHandler.DataNotEnoughException("Dữ liệu năng lượng không phù hợp");
        }
        return MapFoodList;
    }
}
