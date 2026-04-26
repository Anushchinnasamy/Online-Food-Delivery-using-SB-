package com.fooddelivery.restaurant.mapper;

import com.fooddelivery.restaurant.dto.request.RestaurantCreateRequestDTO;
import com.fooddelivery.restaurant.dto.request.RestaurantUpdateRequestDTO;
import com.fooddelivery.restaurant.dto.response.RestaurantDetailResponseDTO;
import com.fooddelivery.restaurant.dto.response.RestaurantResponseDTO;
import com.fooddelivery.restaurant.entity.Restaurant;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-27T21:01:09+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class RestaurantMapperImpl implements RestaurantMapper {

    @Override
    public Restaurant toEntity(RestaurantCreateRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Restaurant restaurant = new Restaurant();

        restaurant.setName( dto.getName() );
        restaurant.setDescription( dto.getDescription() );
        restaurant.setAddress( dto.getAddress() );
        restaurant.setCity( dto.getCity() );
        restaurant.setPincode( dto.getPincode() );
        restaurant.setLatitude( dto.getLatitude() );
        restaurant.setLongitude( dto.getLongitude() );
        restaurant.setPhone( dto.getPhone() );
        restaurant.setEmail( dto.getEmail() );
        restaurant.setCuisineType( dto.getCuisineType() );
        restaurant.setImageUrl( dto.getImageUrl() );
        restaurant.setDeliveryTimeMinutes( dto.getDeliveryTimeMinutes() );
        restaurant.setMinimumOrderAmount( dto.getMinimumOrderAmount() );
        restaurant.setDeliveryFee( dto.getDeliveryFee() );
        restaurant.setOpeningTime( dto.getOpeningTime() );
        restaurant.setClosingTime( dto.getClosingTime() );

        restaurant.setRating( new BigDecimal( "0.0" ) );
        restaurant.setTotalReviews( 0 );
        restaurant.setIsActive( true );
        restaurant.setIsApproved( false );
        restaurant.setIsOpen( false );

        return restaurant;
    }

    @Override
    public void updateEntityFromDTO(RestaurantUpdateRequestDTO dto, Restaurant restaurant) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            restaurant.setName( dto.getName() );
        }
        if ( dto.getDescription() != null ) {
            restaurant.setDescription( dto.getDescription() );
        }
        if ( dto.getAddress() != null ) {
            restaurant.setAddress( dto.getAddress() );
        }
        if ( dto.getPhone() != null ) {
            restaurant.setPhone( dto.getPhone() );
        }
        if ( dto.getEmail() != null ) {
            restaurant.setEmail( dto.getEmail() );
        }
        if ( dto.getCuisineType() != null ) {
            restaurant.setCuisineType( dto.getCuisineType() );
        }
        if ( dto.getImageUrl() != null ) {
            restaurant.setImageUrl( dto.getImageUrl() );
        }
        if ( dto.getIsOpen() != null ) {
            restaurant.setIsOpen( dto.getIsOpen() );
        }
        if ( dto.getOpeningTime() != null ) {
            restaurant.setOpeningTime( dto.getOpeningTime() );
        }
        if ( dto.getClosingTime() != null ) {
            restaurant.setClosingTime( dto.getClosingTime() );
        }
    }

    @Override
    public RestaurantResponseDTO toResponseDTO(Restaurant restaurant) {
        if ( restaurant == null ) {
            return null;
        }

        RestaurantResponseDTO restaurantResponseDTO = new RestaurantResponseDTO();

        restaurantResponseDTO.setId( restaurant.getId() );
        restaurantResponseDTO.setName( restaurant.getName() );
        restaurantResponseDTO.setCity( restaurant.getCity() );
        restaurantResponseDTO.setCuisineType( restaurant.getCuisineType() );
        restaurantResponseDTO.setRating( restaurant.getRating() );
        restaurantResponseDTO.setDeliveryTimeMinutes( restaurant.getDeliveryTimeMinutes() );
        restaurantResponseDTO.setDeliveryFee( restaurant.getDeliveryFee() );
        restaurantResponseDTO.setImageUrl( restaurant.getImageUrl() );
        restaurantResponseDTO.setMinimumOrderAmount( restaurant.getMinimumOrderAmount() );

        restaurantResponseDTO.setIsOpen( restaurant.isCurrentlyOpen() );

        return restaurantResponseDTO;
    }

    @Override
    public RestaurantDetailResponseDTO toDetailResponseDTO(Restaurant restaurant) {
        if ( restaurant == null ) {
            return null;
        }

        RestaurantDetailResponseDTO restaurantDetailResponseDTO = new RestaurantDetailResponseDTO();

        restaurantDetailResponseDTO.setId( restaurant.getId() );
        restaurantDetailResponseDTO.setName( restaurant.getName() );
        restaurantDetailResponseDTO.setDescription( restaurant.getDescription() );
        restaurantDetailResponseDTO.setAddress( restaurant.getAddress() );
        restaurantDetailResponseDTO.setCity( restaurant.getCity() );
        restaurantDetailResponseDTO.setPincode( restaurant.getPincode() );
        restaurantDetailResponseDTO.setLatitude( restaurant.getLatitude() );
        restaurantDetailResponseDTO.setLongitude( restaurant.getLongitude() );
        restaurantDetailResponseDTO.setPhone( restaurant.getPhone() );
        restaurantDetailResponseDTO.setEmail( restaurant.getEmail() );
        restaurantDetailResponseDTO.setCuisineType( restaurant.getCuisineType() );
        restaurantDetailResponseDTO.setImageUrl( restaurant.getImageUrl() );
        restaurantDetailResponseDTO.setRating( restaurant.getRating() );
        restaurantDetailResponseDTO.setTotalReviews( restaurant.getTotalReviews() );
        restaurantDetailResponseDTO.setDeliveryTimeMinutes( restaurant.getDeliveryTimeMinutes() );
        restaurantDetailResponseDTO.setMinimumOrderAmount( restaurant.getMinimumOrderAmount() );
        restaurantDetailResponseDTO.setDeliveryFee( restaurant.getDeliveryFee() );
        restaurantDetailResponseDTO.setIsActive( restaurant.getIsActive() );
        restaurantDetailResponseDTO.setIsApproved( restaurant.getIsApproved() );
        restaurantDetailResponseDTO.setIsOpen( restaurant.getIsOpen() );
        restaurantDetailResponseDTO.setOpeningTime( restaurant.getOpeningTime() );
        restaurantDetailResponseDTO.setClosingTime( restaurant.getClosingTime() );
        restaurantDetailResponseDTO.setCreatedAt( restaurant.getCreatedAt() );
        restaurantDetailResponseDTO.setUpdatedAt( restaurant.getUpdatedAt() );

        return restaurantDetailResponseDTO;
    }

    @Override
    public List<RestaurantResponseDTO> toResponseDTOList(List<Restaurant> restaurants) {
        if ( restaurants == null ) {
            return null;
        }

        List<RestaurantResponseDTO> list = new ArrayList<RestaurantResponseDTO>( restaurants.size() );
        for ( Restaurant restaurant : restaurants ) {
            list.add( toResponseDTO( restaurant ) );
        }

        return list;
    }

    @Override
    public List<RestaurantDetailResponseDTO> toDetailResponseDTOList(List<Restaurant> restaurants) {
        if ( restaurants == null ) {
            return null;
        }

        List<RestaurantDetailResponseDTO> list = new ArrayList<RestaurantDetailResponseDTO>( restaurants.size() );
        for ( Restaurant restaurant : restaurants ) {
            list.add( toDetailResponseDTO( restaurant ) );
        }

        return list;
    }
}
