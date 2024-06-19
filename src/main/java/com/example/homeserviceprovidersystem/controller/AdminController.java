package com.example.homeserviceprovidersystem.controller;

import com.example.homeserviceprovidersystem.dto.customer.CustomerSummaryRequest;
import com.example.homeserviceprovidersystem.dto.customer.CustomerSummaryResponse;
import com.example.homeserviceprovidersystem.dto.duty.DutyRequest;
import com.example.homeserviceprovidersystem.dto.duty.DutyResponse;
import com.example.homeserviceprovidersystem.dto.expert.ExpertRequestWithEmail;
import com.example.homeserviceprovidersystem.dto.expert.ExpertSummaryRequest;
import com.example.homeserviceprovidersystem.dto.expert.ExpertSummaryResponse;
import com.example.homeserviceprovidersystem.dto.order.OrderSummaryRequestWithOrderStatus;
import com.example.homeserviceprovidersystem.dto.order.OrdersResponse;
import com.example.homeserviceprovidersystem.dto.password.ChangePasswordRequest;
import com.example.homeserviceprovidersystem.dto.subduty.SubDutyRequest;
import com.example.homeserviceprovidersystem.dto.subduty.SubDutyRequestWithBasePrice;
import com.example.homeserviceprovidersystem.dto.subduty.SubDutyRequestWithDescription;
import com.example.homeserviceprovidersystem.dto.subduty.SubDutyResponse;
import com.example.homeserviceprovidersystem.security.AuthenticationRequest;
import com.example.homeserviceprovidersystem.security.AuthenticationResponse;
import com.example.homeserviceprovidersystem.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin")
public class AdminController {
    final DutyService dutyService;
    final SubDutyService subDutyService;
    final ExpertService expertService;
    final CustomerService customerService;
    final OrdersService ordersService;
    final AdminService adminService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return new ResponseEntity<>(adminService.authenticate(request), HttpStatus.OK);
    }

    @PostMapping(value = "/addDuty")
    public ResponseEntity<DutyResponse> saveDuty(@Valid @RequestBody DutyRequest dutyRequest) {
        return new ResponseEntity<>(dutyService.save(dutyRequest), HttpStatus.CREATED);
    }

    @PostMapping(value = "/addSubDuty")
    public ResponseEntity<SubDutyResponse> saveSubDuty(@Valid @RequestBody SubDutyRequest subDutyRequest) {
        return new ResponseEntity<>(subDutyService.save(subDutyRequest), HttpStatus.CREATED);
    }

    @PostMapping(value = "/editDescriptionSubDuty")
    public ResponseEntity<SubDutyResponse> updateDescriptionSubDuty(@Valid @RequestBody SubDutyRequestWithDescription request) {
        return new ResponseEntity<>(subDutyService.updateDescription(request), HttpStatus.OK);
    }

    @PostMapping(value = "/editBasePriceSubDuty")
    public ResponseEntity<SubDutyResponse> updateBasePriceSubDuty(@Valid @RequestBody SubDutyRequestWithBasePrice request) {
        return new ResponseEntity<>(subDutyService.updateBasePrice(request), HttpStatus.OK);
    }

    @PatchMapping("/expertConfirmation")
    public ResponseEntity<ExpertSummaryResponse> expertConfirmation(@Valid @RequestBody ExpertRequestWithEmail request) {
        return new ResponseEntity<>(expertService.expertConfirmation(request), HttpStatus.OK);
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return new ResponseEntity<>(adminService.changePassword(request), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllDuty")
    public ResponseEntity<List<DutyResponse>> findAllDuty() {
        return new ResponseEntity<>(dutyService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllSubDuty")
    public ResponseEntity<List<SubDutyResponse>> findAllSubDuty() {
        return new ResponseEntity<>(subDutyService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/findAllDisableExperts")
    public ResponseEntity<List<ExpertSummaryResponse>> findAllDisableExperts() {
        return new ResponseEntity<>(expertService.findAllDisableExperts(), HttpStatus.OK);
    }

    @GetMapping("/searchCustomer")
    public ResponseEntity<List<CustomerSummaryResponse>> searchCustomer(@Valid @RequestBody CustomerSummaryRequest request) {
        return new ResponseEntity<>(customerService.findCustomersByDynamicSearch(request), HttpStatus.OK);
    }

    @GetMapping("/searchExpert")
    public ResponseEntity<List<ExpertSummaryResponse>> searchExpert(@Valid @RequestBody ExpertSummaryRequest request) {
        return new ResponseEntity<>(expertService.findExpertsByDynamicSearch(request), HttpStatus.OK);
    }

    @GetMapping("/searchOrders")
    public ResponseEntity<List<OrdersResponse>> searchOrders(@Valid @RequestBody OrderSummaryRequestWithOrderStatus request) {
        return new ResponseEntity<>(ordersService.findOrdersByDynamicSearch(request), HttpStatus.OK);
    }
}