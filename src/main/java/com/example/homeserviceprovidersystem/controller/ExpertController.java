package com.example.homeserviceprovidersystem.controller;

import com.example.homeserviceprovidersystem.dto.comments.CommentSummaryRequest;
import com.example.homeserviceprovidersystem.dto.comments.CommentSummaryResponse;
import com.example.homeserviceprovidersystem.dto.expert.ExpertRequest;
import com.example.homeserviceprovidersystem.dto.expert.ExpertResponse;
import com.example.homeserviceprovidersystem.dto.expertsuggestion.ExpertSuggestionsResponse;
import com.example.homeserviceprovidersystem.dto.expertsuggestion.ExpertSuggestionsSummaryRequest;
import com.example.homeserviceprovidersystem.dto.order.OrdersResponse;
import com.example.homeserviceprovidersystem.dto.password.ChangePasswordRequest;
import com.example.homeserviceprovidersystem.dto.person.PersonRequestWithEmail;
import com.example.homeserviceprovidersystem.dto.subduty.SubDutyRequestWithName;
import com.example.homeserviceprovidersystem.dto.subduty.SubDutyResponse;
import com.example.homeserviceprovidersystem.dto.wallet.WalletResponse;
import com.example.homeserviceprovidersystem.security.AuthenticationRequest;
import com.example.homeserviceprovidersystem.security.AuthenticationResponse;
import com.example.homeserviceprovidersystem.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/expert")
public class ExpertController {
    final SubDutyService subDutyService;
    final ExpertService expertService;
    final OrdersService ordersService;
    final ExpertSuggestionsService expertSuggestionsService;
    final CommentsService commentsService;
    final WalletService walletService;

    @PostMapping("/addExpert")
    public ResponseEntity<ExpertResponse> saveExpert(
            @RequestParam("subDutyName") String subDutyName,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("picture") MultipartFile multipartFile
    ) {
        ExpertResponse savedExpert =
                expertService.save(multipartFile, new ExpertRequest(subDutyName, firstName, lastName, email, password));
        return new ResponseEntity<>(savedExpert, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return new ResponseEntity<>(expertService.authenticate(request), HttpStatus.OK);
    }

    @PostMapping(value = "/addExpertSuggestion")
    public ResponseEntity<ExpertSuggestionsResponse> saveExpertSuggestion(@Valid @RequestBody ExpertSuggestionsSummaryRequest request) {
        return new ResponseEntity<>(expertSuggestionsService.save(request), HttpStatus.CREATED);
    }
    @PatchMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return new ResponseEntity<>(expertService.changePassword(request), HttpStatus.OK);
    }
    @GetMapping(value = "/showScore")
    public ResponseEntity<CommentSummaryResponse> findScore(@Valid @RequestBody CommentSummaryRequest request) {
        return new ResponseEntity<>(commentsService.findScore(request), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllSubDuty")
    public ResponseEntity<List<SubDutyResponse>> findAllSubDuty() {
        return new ResponseEntity<>(subDutyService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/findAllOrders")
    public ResponseEntity<List<OrdersResponse>> findAllOrders(@Valid @RequestBody SubDutyRequestWithName request) {
        return new ResponseEntity<>(ordersService.findAllOrderWaitingForSpecialistSuggestion(request), HttpStatus.OK);
    }

    @GetMapping(value = "/historyOrders")
    public ResponseEntity<List<OrdersResponse>> findAllOrders(@Valid @RequestBody PersonRequestWithEmail request) {
        return new ResponseEntity<>(ordersService.findAllOrders(request, "expert"), HttpStatus.OK);
    }

    @GetMapping(value = "/showWallet")
    public ResponseEntity<WalletResponse> showWallet(@Valid @RequestBody PersonRequestWithEmail request) {
        return new ResponseEntity<>(walletService.findWallet(request, "expert"), HttpStatus.OK);
    }

    @GetMapping("/verifyToken")
    public String verifyToken(@RequestParam("token") String token) {
        return expertService.verifyToken(token);
    }
}
