package com.example.homeserviceprovidersystem.service;

import com.example.homeserviceprovidersystem.dto.subduty.SubDutyRequest;
import com.example.homeserviceprovidersystem.dto.subduty.SubDutyRequestWithBasePrice;
import com.example.homeserviceprovidersystem.dto.subduty.SubDutyRequestWithDescription;
import com.example.homeserviceprovidersystem.dto.subduty.SubDutyResponse;
import com.example.homeserviceprovidersystem.entity.SubDuty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SubDutyServiceTest {
    @Autowired
    SubDutyService subDutyService;

    @Test
    void save() {
        SubDutyRequest subDutyRequest = new SubDutyRequest();
        subDutyRequest.setNameDuty("programmer");
        subDutyRequest.setNameSubDuty("c++");
        subDutyRequest.setBasePrice(550.0);
        subDutyRequest.setDescription("programming language");
        SubDutyResponse subDutyResponse = subDutyService.save(subDutyRequest);
        assertNotNull(subDutyResponse);
        assertNotNull(subDutyResponse.getId());
        assertEquals("programmer", subDutyResponse.getDuty().getName());
        assertEquals("c++", subDutyResponse.getName());
        assertEquals(550.0, subDutyResponse.getBasePrice());
        assertEquals("programming language", subDutyResponse.getDescription());
    }

    @Test
    void updateDescription() {
        SubDutyRequestWithDescription request = new SubDutyRequestWithDescription();
        request.setNameSubDuty("c++");
        request.setDescription("programming***language");
        SubDutyResponse subDutyResponse = subDutyService.updateDescription(request);
        assertNotNull(subDutyResponse);
        assertNotNull(subDutyResponse.getId());
        assertEquals("programmer", subDutyResponse.getDuty().getName());
        assertEquals("c++", subDutyResponse.getName());
        assertEquals(550.0, subDutyResponse.getBasePrice());
        assertEquals("programming***language", subDutyResponse.getDescription());
    }

    @Test
    void updateBasePrice() {
        SubDutyRequestWithBasePrice request = new SubDutyRequestWithBasePrice();
        request.setNameSubDuty("c++");
        request.setBasePrice(560.0);
        SubDutyResponse subDutyResponse = subDutyService.updateBasePrice(request);
        assertNotNull(subDutyResponse);
        assertNotNull(subDutyResponse.getId());
        assertEquals("programmer", subDutyResponse.getDuty().getName());
        assertEquals("c++", subDutyResponse.getName());
        assertEquals(560.0, subDutyResponse.getBasePrice());
        assertEquals("programming***language", subDutyResponse.getDescription());
    }

    @Test
    void findById() {
        SubDuty subDuty = subDutyService.findById(3L);
        assertNotNull(subDuty);
        assertEquals(3, subDuty.getId());
        assertEquals("c++", subDuty.getName());
        assertEquals(560.0, subDuty.getBasePrice());
        assertEquals("programming***language", subDuty.getDescription());
        assertEquals("programmer", subDuty.getDuty().getName());
    }

    @Test
    void findByName() {
        SubDuty subDuty = subDutyService.findByName("c++");
        assertNotNull(subDuty);
        assertEquals(3, subDuty.getId());
        assertEquals("c++", subDuty.getName());
        assertEquals(560.0, subDuty.getBasePrice());
        assertEquals("programming***language", subDuty.getDescription());
        assertEquals("programmer", subDuty.getDuty().getName());
    }

    @Test
    void findAll() {
        List<SubDutyResponse> subDutyResponseList = subDutyService.findAll();
        assertNotNull(subDutyResponseList);
        assertFalse(subDutyResponseList.isEmpty());
    }
}