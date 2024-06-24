package com.example.homeserviceprovidersystem.service;

import com.example.homeserviceprovidersystem.dto.duty.DutyRequest;
import com.example.homeserviceprovidersystem.dto.duty.DutyResponse;
import com.example.homeserviceprovidersystem.entity.Duty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DutyServiceTest {

    @Autowired
    DutyService dutyService;

    @Test
    void save() {
        DutyRequest dutyRequest = new DutyRequest();
        dutyRequest.setName("facilities");
        DutyResponse dutyResponse = dutyService.save(dutyRequest);
        assertNotNull(dutyResponse);
        assertNotNull(dutyResponse.getId());
        assertEquals("facilities", dutyResponse.getName());
    }

    @Test
    void findByName() {
        Duty duty = dutyService.findByName("facilities");
        assertNotNull(duty);
        assertNotNull(duty.getId());
        assertEquals("facilities", duty.getName());
    }

    @Test
    void findAll() {
        List<DutyResponse> dutyResponseList = dutyService.findAll();
        assertNotNull(dutyResponseList);
        assertFalse(dutyResponseList.isEmpty());
    }
}