package com.app.mapper;

import org.springframework.stereotype.Component;

import com.app.dto.response.item.ArtResponse;
import com.app.dto.response.item.BookResponse;
import com.app.dto.response.item.CollectibleResponse;
import com.app.dto.response.item.ElectronicsResponse;
import com.app.dto.response.item.FashionResponse;
import com.app.dto.response.item.ItemResponse;
import com.app.dto.response.item.JewelryResponse;
import com.app.dto.response.item.VehicleResponse;
import com.app.entity.item.Art;
import com.app.entity.item.Book;
import com.app.entity.item.Collectible;
import com.app.entity.item.Electronics;
import com.app.entity.item.Fashion;
import com.app.entity.item.ItemEntity;
import com.app.entity.item.Jewelry;
import com.app.entity.item.Vehicle;

@Component
public class ItemMapper {
    
    public ItemResponse toResponse(ItemEntity item){

        if (item instanceof Electronics e){
            ElectronicsResponse response = new ElectronicsResponse();

            response.setBrand(e.getBrand());

            response.setModel(e.getModel());

            response.setColor(e.getColor());

            response.setStorage(e.getStorage());

            response.setConditionStatus(e.getConditionStatus());

            response.setWarrantyMonths(e.getWarrantyMonths());

            return response;
        }

        if (item instanceof Vehicle e){
            VehicleResponse response = new VehicleResponse();

            response.setBrand(e.getBrand());

            response.setModel(e.getModel());

            response.setColor(e.getColor());

            response.setFuelType(e.getFuelType());

            response.setMileage(e.getMileage());

            response.setYear(e.getYear());

            return response;
        }

        if (item instanceof Art e){

            ArtResponse response = new ArtResponse();

            response.setArtist(e.getArtist());

            response.setStyle(e.getStyle());

            return response;
        }

        if (item instanceof Fashion e){

            FashionResponse response = new FashionResponse();

            response.setBrand(e.getBrand());

            response.setModel(e.getModel());

            response.setColor(e.getColor());

            response.setMaterial(e.getMaterial());

            response.setSize(e.getSize());

            return response;
        }

        if (item instanceof Collectible e){

            CollectibleResponse response = new CollectibleResponse();

            response.setCategory(e.getCagetory());

            response.setRarity(e.getRarity());

            response.setProductionYear(e.getProductionYear());

            return response;
        }

        if (item instanceof Jewelry e){

            JewelryResponse response = new JewelryResponse();

            response.setBrand(e.getBrand());

            response.setModel(e.getModel());

            response.setMaterial(e.getMaterial());

            response.setWeight(e.getWeight());

            return response;
        }

        if (item instanceof Book e){

            BookResponse response = new BookResponse();

            response.setAuthor(e.getAuthor());

            response.setPublisher(e.getPublisher());

            response.setPublishYear(e.getPublishYear());

            return response;
        }

        return null;
    }
}
