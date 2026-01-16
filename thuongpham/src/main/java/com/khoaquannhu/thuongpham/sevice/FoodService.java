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
    public List<Food> searchByName(String name){
        return foodRepository.findByNameLike(name);
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
        Integer OrdinalNuoMam = 503;
        Integer OrdinalGao = 4;
        Integer OrdinalOilPlants = 266;
        Integer OrdinalOilAnimal = 268;
        Integer OrdinalTofu = 74;
        Integer OrdinalDuongKinh = 474;
        Integer OrdinalMilk = 431;
        Integer OrdChicken = 291;
        Integer OrdPorkLoin = 296;
        Integer OrdLeanPork = 295;
        Integer OrdBeef = 281;
        Integer OrdSalt = 492;
        Integer OrdMilkConsider = 438;

        Food MiChinh = new Food();
        MiChinh.setName("Mì chính (Bột ngọt)");
        MiChinh.setOrdinalNumbers(527);
        MiChinh.setProtein(99.8);
        MiChinh.setLipid(0.0);
        MiChinh.setCarbohydrate(0.0);

        Set<Integer> Veges = Set.of(83,84,85,89,91,92,95,96,97,98,99,101,103,111,112,115,118,136,137,148,152,155,156,158,165,168,178,180,181,193,194,195);
        Set<Integer> Meat = Set.of(280,289,301,302,319,331,333,334,335,347,358);
        Set<Integer> SeaFood = Set.of(363,374,384,392,400,411,412);
        Set<Integer> Egg = Set.of(420,423);
        Set<Integer> Dessert = Set.of(209,210,214,215,219,222,225,235,243,248,252,255,258,260,263);
        Set<Integer> Bean = Set.of(53,59,72);

        double energy_main = energy + energy * 10/100;
        double m_protein = (energy_main * 17/100)/4;
        double m_lipid = (energy_main * 23/100)/9;
        double m_glucose = (energy_main * 60/100)/4;
        double m_protein_animal = m_protein/2;
        double m_protein_plants = m_protein - m_protein_animal;
        double m_lipid_animal = m_lipid/2;
        double m_lipid_plants = m_lipid - m_lipid_animal;

        Food Gao = findByOrdinalNumber(OrdinalGao);
        Food NuoMam = findByOrdinalNumber(OrdinalNuoMam);
        Food Milk = findByOrdinalNumber(OrdinalMilk);
        Food PorkLoin = findByOrdinalNumber(OrdPorkLoin);
        Food LeanPork = findByOrdinalNumber(OrdLeanPork);
        Food Chicken = findByOrdinalNumber(OrdChicken);
        Food Beef = findByOrdinalNumber(OrdBeef);
        Food MoDV = findByOrdinalNumber(OrdinalOilAnimal);
        Food DauTV = findByOrdinalNumber(OrdinalOilPlants);
        Food DuongKinh = findByOrdinalNumber(OrdinalDuongKinh);
        Food DauPhu = findByOrdinalNumber(OrdinalTofu);
        Food Salt = findByOrdinalNumber(OrdSalt);
        Food MilkConsider = findByOrdinalNumber(OrdMilkConsider);

        CalculateFood CalDuongKinh = new CalculateFood(DuongKinh, 0.0);
        CalculateFood CalGao = new CalculateFood(Gao, 0.0);
        CalculateFood CalPorkLoin = new CalculateFood(PorkLoin, 0.0);
        CalculateFood CalLeanPork = new CalculateFood(LeanPork, 0.0);
        CalculateFood CalChicken = new CalculateFood(Chicken, 0.0);
        CalculateFood CalBeef = new CalculateFood(Beef, 0.0);
        CalculateFood CalMilk = new CalculateFood(Milk, 0.0);
        CalculateFood CalMoDV = new CalculateFood(MoDV, 0.0);
        CalculateFood CalDauTV = new CalculateFood(DauTV, 0.0);
        CalculateFood CalDauPhu = new CalculateFood(DauPhu, 0.0);
        CalculateFood CalNuoMam = new CalculateFood(NuoMam, 40.0);
        CalculateFood CalMiChinh = new CalculateFood(MiChinh, 2.0);
        CalculateFood CalSalt = new CalculateFood(Salt, 20.0);
        CalculateFood CalMilkConsider = new CalculateFood(MilkConsider, 0.0);

        List<Integer> Desertlist = pickRandom(Dessert, 3);
        Food Desert_1 = findByOrdinalNumber(Desertlist.get(0));
        Food Desert_2 = findByOrdinalNumber(Desertlist.get(1));
        Food Desert_3 = findByOrdinalNumber(Desertlist.get(2));
        CalculateFood CalDesert_1 = new CalculateFood(Desert_1, 150.0);
        CalculateFood CalDesert_2 = new CalculateFood(Desert_2, 150.0);
        CalculateFood CalDesert_3 = new CalculateFood(Desert_3, 0.0);

        List<Integer> Raulist = pickRandom(Veges, 4);
        Food Rau_1 = findByOrdinalNumber(Raulist.get(0));
        Food Rau_2 = findByOrdinalNumber(Raulist.get(1));
        Food Rau_3 = findByOrdinalNumber(Raulist.get(2));
        Food Rau_4 = findByOrdinalNumber(Raulist.get(3));
        CalculateFood CalRau_1 = new CalculateFood(Rau_1, 0.0);
        CalculateFood CalRau_2 = new CalculateFood(Rau_2, 0.0);
        CalculateFood CalRau_3 = new CalculateFood(Rau_3, 0.0);
        CalculateFood CalRau_4 = new CalculateFood(Rau_4, 0.0);

        List<Integer> Meatlist = pickRandom(Meat, 4);
        Food Meat_1 = findByOrdinalNumber(Meatlist.get(0));
        Food Meat_2 = findByOrdinalNumber(Meatlist.get(1));
        Food Meat_3 = findByOrdinalNumber(Meatlist.get(2));
        Food Meat_4 = findByOrdinalNumber(Meatlist.get(3));
        CalculateFood CalMeat1 = new CalculateFood(Meat_1, 0.0);
        CalculateFood CalMeat2 = new CalculateFood(Meat_2, 0.0);
        CalculateFood CalMeat3 = new CalculateFood(Meat_3, 0.0);
        CalculateFood CalMeat4 = new CalculateFood(Meat_4, 0.0);

        List<Integer> SeaFoodlist = pickRandom(SeaFood, 2);
        Food fish = findByOrdinalNumber(SeaFoodlist.get(0));
        Food fish_2 = findByOrdinalNumber(SeaFoodlist.get(1));
        CalculateFood CalFish = new CalculateFood(fish, 150.0);
        CalculateFood CalFish_2 = new CalculateFood(fish_2,0.0);

        List<Integer> EggList = pickRandom(Egg,1);
        Food egg = findByOrdinalNumber(EggList.get(0));
        CalculateFood CalEgg = new CalculateFood(egg, 50.0);

        List<Integer> BeanList = pickRandom(Bean, 1);
        Food bean_1 = findByOrdinalNumber(BeanList.get(0));
        CalculateFood CalBean = new CalculateFood(bean_1, 0.0);

        if(energy>=2500 && energy<3000){
            if(energy>=2500 && energy<2700){
                CalGao = CalGao.withWeight(500);
            }else if(energy>=2700 && energy<2800){
                CalGao = CalGao.withWeight(530);
            }else{
                CalGao = CalGao.withWeight(580);
            }
            CalLeanPork = CalLeanPork.withWeight(60);
            CalChicken = CalChicken.withWeight(50);
            CalBeef = CalBeef.withWeight(40);
            CalFish = CalFish.withWeight(120);
            CalRau_1 = CalRau_1.withWeight(100);
            CalRau_2 = CalRau_2.withWeight(150);
            CalRau_3 = CalRau_3.withWeight(150);

        }else if(energy>=3000 && energy<3500){
            CalGao = CalGao.withWeight(620);
            CalLeanPork = CalLeanPork.withWeight(100);
            CalPorkLoin = CalPorkLoin.withWeight(40);
            CalChicken = CalChicken.withWeight(60);
            CalBeef = CalBeef.withWeight(50);
            CalFish = CalFish.withWeight(120);
            CalRau_1 = CalRau_1.withWeight(150);
            CalRau_2 = CalRau_2.withWeight(200);
            CalRau_3 = CalRau_3.withWeight(150);
            CalDesert_1 = CalDesert_1.withWeight(160);
            CalDesert_2 = CalDesert_2.withWeight(160);
        }else if(energy>=3500 && energy<4000){

            if(energy<3800){
                CalGao = CalGao.withWeight(680);
            }else {
                CalGao = CalGao.withWeight(700);

            }
            CalLeanPork = CalLeanPork.withWeight(100);
//            CalPorkLoin = CalPorkLoin.withWeight(40);
            CalChicken = CalChicken.withWeight(80);
            CalBeef = CalBeef.withWeight(80);
            CalFish = CalFish.withWeight(100);
            CalRau_1 = CalRau_1.withWeight(200);
            CalRau_2 = CalRau_2.withWeight(200);
            CalRau_3 = CalRau_3.withWeight(150);
            CalDesert_1 = CalDesert_1.withWeight(150);
            CalDesert_2 = CalDesert_2.withWeight(150);
            CalDesert_3 = CalDesert_3.withWeight(100);
            CalMilk = CalMilk.withWeight(180);
            CalBean = CalBean.withWeight(20);
        }else if(energy>=4000 && energy<4500){

            CalGao = CalGao.withWeight(720);
            CalLeanPork = CalLeanPork.withWeight(80);
//            CalPorkLoin = CalPorkLoin.withWeight(40);
            CalChicken = CalChicken.withWeight(100);
            CalBeef = CalBeef.withWeight(100);
            CalFish = CalFish.withWeight(130);
            CalRau_1 = CalRau_1.withWeight(150);
            CalRau_2 = CalRau_2.withWeight(200);
            CalRau_3 = CalRau_3.withWeight(150);
            CalRau_4 = CalRau_4.withWeight(100);
            CalDesert_1 = CalDesert_1.withWeight(150);
            CalDesert_2 = CalDesert_2.withWeight(150);
            CalDesert_3 = CalDesert_3.withWeight(150);
            CalMilk = CalMilk.withWeight(360);
            CalBean = CalBean.withWeight(20);
            CalMilkConsider = CalMilkConsider.withWeight(30);
        }else if(energy>=4500 && energy<=4860){

            CalGao = CalGao.withWeight(740);
            CalLeanPork = CalLeanPork.withWeight(100);
//            CalPorkLoin = CalPorkLoin.withWeight(60);
            CalChicken = CalChicken.withWeight(120);
            CalBeef = CalBeef.withWeight(100);
            CalFish = CalFish.withWeight(180);
            CalRau_1 = CalRau_1.withWeight(150);
            CalRau_2 = CalRau_2.withWeight(200);
            CalRau_3 = CalRau_3.withWeight(150);
            CalRau_4 = CalRau_4.withWeight(100);
            CalDesert_1 = CalDesert_1.withWeight(200);
            CalDesert_2 = CalDesert_2.withWeight(200);
            CalDesert_3 = CalDesert_3.withWeight(200);
            CalMilk = CalMilk.withWeight(360);
            CalBean = CalBean.withWeight(30);
            CalMilkConsider = CalMilkConsider.withWeight(30);
        }else{
            throw new GlobalExceptionHandler.DataNotEnoughException("Dữ liệu năng lượng không phù hợp");
        }

        double m_Glu_conti = m_glucose
                - CalGao.getGlucid()
                - CalMiChinh.getGlucid()
                - CalRau_1.getGlucid()
                - CalRau_2.getGlucid()
                - CalRau_3.getGlucid()
                - CalRau_4.getGlucid()
                - CalDesert_1.getGlucid()
                - CalDesert_2.getGlucid()
                - CalDesert_3.getGlucid()
                - CalBean.getGlucid()
                - CalMilk.getGlucid()
                - CalMilkConsider.getGlucid();
        if (m_Glu_conti > 0){
            CalDuongKinh = CalDuongKinh.withWeight(m_Glu_conti*100/DuongKinh.getCarbohydrate());
        }

        double m_proTV_conti = m_protein_plants
                - CalGao.getProtein()
                - CalMiChinh.getProtein()
                - CalRau_1.getProtein()
                - CalRau_2.getProtein()
                - CalRau_3.getProtein()
                - CalRau_4.getProtein()
                - CalDesert_1.getProtein()
                - CalDesert_2.getProtein()
                - CalDesert_3.getProtein()
                - CalBean.getProtein();
        if(m_proTV_conti>0){
            CalDauPhu = CalDauPhu.withWeight(m_proTV_conti*100/DauPhu.getProtein());
        }

        double m_proDV_conti = m_protein_animal
                - CalNuoMam.getProtein()
                - CalLeanPork.getProtein()
                - CalPorkLoin.getProtein()
                - CalBeef.getProtein()
                - CalChicken.getProtein()
                - CalFish.getProtein()
                - CalEgg.getProtein()
                - CalMilk.getProtein()
                - CalMilkConsider.getProtein();
        System.out.println(m_proDV_conti);
        if(m_proDV_conti>5){
            CalMeat1 = CalMeat1.withWeight(m_proDV_conti*100/Meat_1.getProtein());
        }
        double m_LipidTV_conti = m_lipid_plants
                - CalGao.getLipid()
                - CalMiChinh.getLipid()
                - CalRau_1.getLipid()
                - CalRau_2.getLipid()
                - CalRau_3.getLipid()
                - CalRau_4.getLipid()
                - CalDesert_1.getLipid()
                - CalDesert_2.getLipid()
                - CalDesert_3.getLipid()
                - CalBean.getLipid()
                - CalDauPhu.getLipid();
        double m_LipidDV_conti = m_lipid_animal
                - CalNuoMam.getLipid()
                - CalLeanPork.getLipid()
                - CalPorkLoin.getLipid()
                - CalBeef.getLipid()
                - CalChicken.getLipid()
                - CalMeat1.getLipid()
                - CalFish.getLipid()
                - CalEgg.getLipid()
                - CalMilk.getLipid()
                - CalMiChinh.getLipid()
                - CalMilkConsider.getLipid();
        if (m_LipidTV_conti>0){
            CalDauTV = CalDauTV.withWeight(m_LipidTV_conti*100/DauTV.getLipid());
        }
        if (m_LipidDV_conti>0){
            CalMoDV = CalMoDV.withWeight(m_LipidDV_conti*100/MoDV.getLipid());
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
        MapFoodList.put(Desert_3, CalDesert_3.getWeight());
        MapFoodList.put(DauPhu, CalDauPhu.getWeight());
        MapFoodList.put(DuongKinh, CalDuongKinh.getWeight());
        MapFoodList.put(LeanPork, CalLeanPork.getWeight());
        MapFoodList.put(PorkLoin, CalPorkLoin.getWeight());
        MapFoodList.put(Beef, CalBeef.getWeight());
        MapFoodList.put(Chicken, CalChicken.getWeight());
        MapFoodList.put(Meat_1, CalMeat1.getWeight());
        MapFoodList.put(fish, CalFish.getWeight());
        MapFoodList.put(egg, CalEgg.getWeight());
        MapFoodList.put(DauTV, CalDauTV.getWeight());
        MapFoodList.put(MoDV, CalMoDV.getWeight());
        MapFoodList.put(Milk, CalMilk.getWeight());
        MapFoodList.put(bean_1, CalBean.getWeight());
        MapFoodList.put(Salt, CalSalt.getWeight());
        MapFoodList.put(MilkConsider, CalMilkConsider.getWeight());
        return MapFoodList;
    }
}
