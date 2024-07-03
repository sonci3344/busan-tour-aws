package org.zerock.project_dib.restaurant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.project_dib.restaurant.domain.Restaurant;
import org.zerock.project_dib.restaurant.domain.RestaurantImage;
import org.zerock.project_dib.restaurant.dto.PageRequestDTO;
import org.zerock.project_dib.restaurant.dto.PageResponseDTO;
import org.zerock.project_dib.restaurant.dto.RestaurantDTO;
import org.zerock.project_dib.restaurant.dto.uploadfile.UploadResultDTO;
import org.zerock.project_dib.mapper.RestaurantImageMapper;
import org.zerock.project_dib.mapper.RestaurantMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final ModelMapper modelMapper;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantImageMapper restaurantImageMapper;
    private final String uploadPath = "C:\\img";

    @Override
    @Transactional
    public int insertRestaurant(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = modelMapper.map(restaurantDTO, Restaurant.class);
        restaurantMapper.insertRest(restaurant);
        int rno = restaurant.getRno();
        log.info("Inserted Restaurant RNO: " + rno);
        return rno;
    }


    @Override
    @Transactional
    public void saveUploadFiles(List<UploadResultDTO> uploadResultDTOList, int rno) {
        if (uploadResultDTOList != null && !uploadResultDTOList.isEmpty()) {
            uploadResultDTOList.forEach(uploadResultDTO -> {
                RestaurantImage restaurantImage = RestaurantImage.builder()
                        .uuid(uploadResultDTO.getUuid())
                        .fileName(uploadResultDTO.getFileName())
                        .rno(rno)
                        .ord(uploadResultDTOList.indexOf(uploadResultDTO))
                        .build();
                restaurantImageMapper.insertFile(restaurantImage);
            });
        }
    }

    @Override
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantMapper.selectAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantDTO getOne(int rno) {
        Restaurant restaurant = restaurantMapper.readOne(rno);
        return entityToDto(restaurant);
    }

    @Override
    public void update(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = modelMapper.map(restaurantDTO, Restaurant.class);
        restaurantMapper.update(restaurant);
    }



    @Override
    public void delete(int rno) {
        restaurantImageMapper.deleteFile(rno);
        restaurantMapper.delete(rno);
    }

    public void updateImages(int rno, List<UploadResultDTO> uploadResults) {
        // 기존 이미지 삭제 로직 추가
        restaurantMapper.deleteImages(rno);
        // 새로운 이미지 저장 로직 추가
        for (UploadResultDTO uploadResult : uploadResults) {
            restaurantMapper.insertImage(rno, uploadResult);
        }
    }

//    public List<String> getFileNames(int rno) {
//        return restaurantMapper.getFileNames(rno);
//    }


    @Override
    public PageResponseDTO<RestaurantDTO> search(PageRequestDTO pageRequestDTO) {
        List<Restaurant> restaurants = restaurantMapper.search(pageRequestDTO);
        List<RestaurantDTO> dtoList = restaurants.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        int total = restaurantMapper.countTotal(pageRequestDTO);
        return PageResponseDTO.<RestaurantDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total(total)
                .build();
    }

//    @Override
//    public void updateImages(int rno, List<UploadResultDTO> uploadResults) {
//
//    }

    private Restaurant dtoToEntity(RestaurantDTO restaurantDTO) {
        return Restaurant.builder()
                .rno(restaurantDTO.getRno())
                .rest_name(restaurantDTO.getRest_name())
                .rest_exp(restaurantDTO.getRest_exp())
                .rest_exp2(restaurantDTO.getRest_exp2())
                .rest_loc(restaurantDTO.getRest_loc())
                .rest_phone(restaurantDTO.getRest_phone())
                .rest_menu(restaurantDTO.getRest_menu())
                .rest_time(restaurantDTO.getRest_time())
                .build();
    }

    private RestaurantDTO entityToDto(Restaurant restaurant) {
        List<String> fileNames = restaurant.getImageSet().stream()
                .map(restaurantImage -> "s_" + restaurantImage.getUuid() + "_" + restaurantImage.getFileName())
                .collect(Collectors.toList());

        return RestaurantDTO.builder()
                .rno(restaurant.getRno())
                .rest_name(restaurant.getRest_name())
                .rest_exp(restaurant.getRest_exp())
                .rest_exp2(restaurant.getRest_exp2())
                .rest_loc(restaurant.getRest_loc())
                .rest_phone(restaurant.getRest_phone())
                .rest_menu(restaurant.getRest_menu())
                .rest_time(restaurant.getRest_time())
                .fileNames(fileNames)
                .regdate(restaurant.getRegdate())
                .moddate(restaurant.getModdate())
                .build();
    }
}

