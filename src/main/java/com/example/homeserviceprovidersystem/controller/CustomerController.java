package com.example.homeserviceprovidersystem.controller;

import com.example.homeserviceprovidersystem.customeException.CustomIoException;
import com.example.homeserviceprovidersystem.dto.cardInformation.CardInformationRequest;
import com.example.homeserviceprovidersystem.dto.comments.CommentRequest;
import com.example.homeserviceprovidersystem.dto.comments.CommentResponse;
import com.example.homeserviceprovidersystem.dto.customer.CustomerRequest;
import com.example.homeserviceprovidersystem.dto.customer.CustomerRequestWithEmail;
import com.example.homeserviceprovidersystem.dto.customer.CustomerSummaryResponse;
import com.example.homeserviceprovidersystem.dto.expert.ExpertSummaryResponse;
import com.example.homeserviceprovidersystem.dto.expertsuggestion.ExpertSuggestionsRequest;
import com.example.homeserviceprovidersystem.dto.expertsuggestion.ExpertSuggestionsRequestWithId;
import com.example.homeserviceprovidersystem.dto.expertsuggestion.ExpertSuggestionsResponse;
import com.example.homeserviceprovidersystem.dto.order.OrderRequest;
import com.example.homeserviceprovidersystem.dto.order.OrderSummaryRequest;
import com.example.homeserviceprovidersystem.dto.order.OrdersResponse;
import com.example.homeserviceprovidersystem.dto.password.ChangePasswordRequest;
import com.example.homeserviceprovidersystem.dto.person.PersonRequestWithEmail;
import com.example.homeserviceprovidersystem.dto.subduty.SubDutyResponse;
import com.example.homeserviceprovidersystem.dto.wallet.WalletResponse;
import com.example.homeserviceprovidersystem.security.AuthenticationRequest;
import com.example.homeserviceprovidersystem.security.AuthenticationResponse;
import com.example.homeserviceprovidersystem.security.CustomerDetails;
import com.example.homeserviceprovidersystem.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/customer")
public class CustomerController {
    final CustomerService customerService;
    final OrdersService ordersService;
    final SubDutyService subDutyService;
    final ExpertService expertService;
    final ExpertSuggestionsService expertSuggestionsService;
    final CommentsService commentsService;
    final CaptchaService captchaService;
    final WalletService walletService;

    @PostMapping("/addCustomer")
    public ResponseEntity<CustomerSummaryResponse> saveCustomer(@Valid @RequestBody CustomerRequest request) {
        return new ResponseEntity<>(customerService.save(request), HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return new ResponseEntity<>(customerService.authenticate(request), HttpStatus.OK);
    }

    @PostMapping("/saveOrders")
    public ResponseEntity<OrdersResponse> saveOrders(@Valid @RequestBody OrderRequest request) {
        return new ResponseEntity<>(ordersService.save(request), HttpStatus.CREATED);
    }

    @PostMapping(value = "/selectExpertSuggestion")
    public ResponseEntity<ExpertSuggestionsResponse> selectExpertSuggestion(@Valid @RequestBody ExpertSuggestionsRequestWithId request) {
        return new ResponseEntity<>(expertSuggestionsService.selectExpertSuggestion(request), HttpStatus.OK);
    }

    @PostMapping(value = "/addComment")
    public ResponseEntity<CommentResponse> saveComment(@Valid @RequestBody CommentRequest request) {
        return new ResponseEntity<>(commentsService.save(request), HttpStatus.CREATED);
    }

    @PostMapping(value = "/onlinePayment/{customerEmail}/{orderId}")
    public String onlinePayment(
            @Email @PathVariable String customerEmail,
            @NonNull @PathVariable Long orderId,
            HttpServletRequest request,
            @RequestParam("cardNumber1") String cardNumber1,
            @RequestParam("cardNumber2") String cardNumber2,
            @RequestParam("cardNumber3") String cardNumber3,
            @RequestParam("cardNumber4") String cardNumber4,
            @RequestParam("cvv2") String cvv2,
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("amount") String amount,
            @RequestParam("captcha") String captcha
    ) {
        return ordersService.onlinePayment(
                customerEmail,
                orderId,
                request,
                new CardInformationRequest(cardNumber1, cardNumber2, cardNumber3, cardNumber4, cvv2, year, month, amount, captcha)
        );
    }

    @PatchMapping(value = "/selectStartWork")
    public ResponseEntity<OrdersResponse> selectStartWork(@Valid @RequestBody OrderSummaryRequest request) {
        return new ResponseEntity<>(ordersService.selectStartWork(request), HttpStatus.OK);
    }

    @PatchMapping(value = "/endOfOrder")
    public ResponseEntity<OrdersResponse> endOfOrder(@Valid @RequestBody OrderSummaryRequest request) {
        return new ResponseEntity<>(ordersService.endOfOrder(request), HttpStatus.OK);
    }

    @PatchMapping(value = "/orderPayment")
    public ResponseEntity<OrdersResponse> orderPayment(@Valid @RequestBody OrderSummaryRequest request) {
        return new ResponseEntity<>(ordersService.orderPayment(request), HttpStatus.OK);
    }
    @PatchMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return new ResponseEntity<>(customerService.changePassword(request), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllSubDuty")
    public ResponseEntity<List<SubDutyResponse>> findAllSubDuty() {
        return new ResponseEntity<>(subDutyService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllExpert")
    public ResponseEntity<List<ExpertSummaryResponse>> findAllExpert() {
        return new ResponseEntity<>(expertService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllOrderSuggestions")
    public ResponseEntity<List<ExpertSuggestionsResponse>> findAllOrderSuggestions(@Valid @RequestBody ExpertSuggestionsRequest request) {
        return new ResponseEntity<>(expertSuggestionsService.findAllOrderSuggestions(request), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllOrderWaitingForSpecialistToWorkPlace")
    public ResponseEntity<List<OrdersResponse>> findAllOrderWaitingForSpecialistToWorkPlace(@Valid @RequestBody CustomerRequestWithEmail request) {
        return new ResponseEntity<>(ordersService.findAllOrderWaitingForSpecialistToWorkPlace(request), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllDoneOrders")
    public ResponseEntity<List<OrdersResponse>> findAllDoneOrders(@Valid @RequestBody CustomerRequestWithEmail request) {
        return new ResponseEntity<>(ordersService.findAllDoneOrders(request), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllStartedOrders")
    public ResponseEntity<List<OrdersResponse>> findAllStartedOrders(@Valid @RequestBody CustomerRequestWithEmail request) {
        return new ResponseEntity<>(ordersService.findAllStartedOrders(request), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllPaidOrders")
    public ResponseEntity<List<OrdersResponse>> findAllPaidOrders(@Valid @RequestBody CustomerRequestWithEmail request) {
        return new ResponseEntity<>(ordersService.findAllPaidOrders(request), HttpStatus.OK);
    }

    @GetMapping(value = "/historyOrders")
    public ResponseEntity<List<OrdersResponse>> findAllOrders(@Valid @RequestBody PersonRequestWithEmail request) {
        return new ResponseEntity<>(ordersService.findAllOrders(request, "customer"), HttpStatus.OK);
    }

    @GetMapping(value = "/showWallet")
    public ResponseEntity<WalletResponse> showWallet(@Valid @RequestBody PersonRequestWithEmail request) {
        return new ResponseEntity<>(walletService.findWallet(request, "customer"), HttpStatus.OK);
    }

    @GetMapping(value = "/onlinePaymentPortal/{customerEmail}/{orderId}")
    public String onlinePaymentPortal(
            @Email @PathVariable String customerEmail,
            @NonNull @PathVariable Long orderId,
            HttpServletRequest request
    ) {
        return ordersService.onlinePaymentPortal(customerEmail, orderId, request);
    }

    @GetMapping("/generateCaptcha")
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws CustomIoException {
        captchaService.generateCaptcha(request, response);
    }

    @GetMapping("/timeout")
    public String showTimeOutPage() {
        return "timeout";
    }

    @GetMapping("/successfullyPayment")
    public String showSuccessfullyPayment() {
        return "successfullyPayment";
    }

    @GetMapping("/verifyToken")
    public String verifyToken(@RequestParam("token") String token) {
        return customerService.verifyToken(token);
    }
}