package com.example.homeserviceprovidersystem.service.impl;

import com.example.homeserviceprovidersystem.customeException.CustomBadRequestException;
import com.example.homeserviceprovidersystem.customeException.CustomEntityNotFoundException;
import com.example.homeserviceprovidersystem.customeException.CustomResourceNotFoundException;
import com.example.homeserviceprovidersystem.dto.expert.*;
import com.example.homeserviceprovidersystem.dto.password.ChangePasswordRequest;
import com.example.homeserviceprovidersystem.entity.Customer;
import com.example.homeserviceprovidersystem.entity.Expert;
import com.example.homeserviceprovidersystem.entity.SubDuty;
import com.example.homeserviceprovidersystem.entity.Wallet;
import com.example.homeserviceprovidersystem.entity.enums.ExpertStatus;
import com.example.homeserviceprovidersystem.mapper.ExpertMapper;
import com.example.homeserviceprovidersystem.repositroy.ExpertRepository;
import com.example.homeserviceprovidersystem.security.*;
import com.example.homeserviceprovidersystem.service.EmailService;
import com.example.homeserviceprovidersystem.service.ExpertService;
import com.example.homeserviceprovidersystem.service.SubDutyService;
import com.example.homeserviceprovidersystem.service.WalletService;
import jakarta.persistence.criteria.Join;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ExpertServiceImpl implements ExpertService {
    private final SubDutyService subDutyService;
    private final ExpertRepository expertRepository;
    private final WalletService walletService;
    private final ExpertMapper expertMapper;
    private final Validator validator;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private String token;
    private Expert expert;

    @Autowired
    public ExpertServiceImpl(
            SubDutyService subDutyService,
            ExpertRepository expertRepository,
            WalletService walletService,
            ExpertMapper expertMapper,
            Validator validator,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.subDutyService = subDutyService;
        this.expertRepository = expertRepository;
        this.walletService = walletService;
        this.expertMapper = expertMapper;
        this.validator = validator;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public ExpertResponse save(MultipartFile multipartFile, ExpertRequest request) {
        validateRequest(request);
        validateMultiPartFile(multipartFile);
        expertRepository.findByEmail(request.getEmail()).ifPresent(existingExpert -> {
            throw new CustomBadRequestException("Email already exists");
        });
        Expert expert = createExpert(multipartFile, request);
        String token = UUID.randomUUID().toString();
        emailService.sendEmail(expert.getEmail(), "Welcome to our Service",
                "Thank you for registering! Please click the following link to verify your email: "
                        + "http://localhost:8080/expert/verifyToken?token=" + token);
        this.token = token;
        this.expert = expert;
        return expertMapper.expertToExpertResponse(expert);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UserDetailsServiceImpl.userType = "EXPERT";
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        Expert findExpert = findByEmail(request.getUsername());
        String token = jwtService.generateToken(new ExpertDetails(findExpert), "EXPERT");
        return AuthenticationResponse.builder().token(token).build();
    }

    @Override
    public Expert save(Expert expert) {
        return expertRepository.save(expert);
    }

    private void validateRequest(ExpertRequest request) {
        Set<ConstraintViolation<ExpertRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
            throw new CustomBadRequestException(String.join(",", errorMessages));
        }
    }

    private void validateMultiPartFile(MultipartFile multipartFile) {
        if (multipartFile.getSize() > 300 * 1024) {
            throw new CustomBadRequestException("File size exceeds the maximum limit of 300 KB");
        }
        String fileName = multipartFile.getOriginalFilename();
        if (fileName != null && !fileName.toLowerCase().endsWith(".jpg")) {
            throw new CustomBadRequestException("File format must be JPG");
        }
    }

    private Expert createExpert(MultipartFile multipartFile, ExpertRequest request) {
        SubDuty foundSubDuty = subDutyService.findByName(request.getNameSubDuty());
        Expert expert = new Expert();
        expert.setFirstName(request.getFirstName());
        expert.setLastName(request.getLastName());
        expert.setEmail(request.getEmail());
        expert.setPassword(passwordEncoder.encode(request.getPassword()));
        expert.setRegistrationDate(LocalDate.now());
        expert.setRegistrationTime(LocalTime.now());
        expert.setExpertStatus(ExpertStatus.DISABLE);
        expert.setPictureData(multipartFile.getOriginalFilename());
        expert.setScore(0);
        expert.setSubDuties(Collections.singleton(foundSubDuty));
        return expert;
    }

    @Override
    public ExpertSummaryResponse expertConfirmation(ExpertRequestWithEmail request) {
        return expertRepository.findByEmail(request.getEmail())
                .map(expert -> {
                    if (expert.getExpertStatus() == ExpertStatus.DISABLE) {
                        expert.setExpertStatus(ExpertStatus.ENABLE);
                        return expertMapper.expertToExpertSummaryResponse(expertRepository.save(expert));
                    } else {
                        throw new CustomBadRequestException("This expert is enable");
                    }
                })
                .orElseThrow(() -> new CustomEntityNotFoundException("no expert was found with this id"));
    }

    @Override
    public String changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ExpertDetails expertDetails = (ExpertDetails) authentication.getPrincipal();
        String  expertEmail = expertDetails.getExpert().getEmail();
        Expert foundExpert = findByEmail(expertEmail);
        if (!passwordEncoder.matches(request.getPreviousPassword(),foundExpert.getPassword())) {
            throw new CustomEntityNotFoundException("Expert with this password was not found");
        }
        foundExpert.setPassword(passwordEncoder.encode(request.getNewPassword()));
        expertRepository.save(foundExpert);
        return "password changed successfully";
    }

    @Override
    public Expert findById(Long id) {
        return expertRepository.findById(id)
                .orElseThrow(() -> new CustomEntityNotFoundException("expert with this id was not found"));
    }

    @Override
    public Expert findByEmail(String email) {
        return expertRepository.findByEmail(email)
                .orElseThrow(() -> new CustomEntityNotFoundException("Expert with this email was not found"));
    }

    @Override
    public List<ExpertSummaryResponse> findAllDisableExperts() {
        List<Expert> expertList = expertRepository.findAllByExpertStatus(ExpertStatus.DISABLE);
        if (expertList.isEmpty()) {
            throw new CustomResourceNotFoundException("There is no result");
        } else {
            return expertList.stream().map(expertMapper::expertToExpertSummaryResponse).toList();
        }
    }

    @Override
    public List<ExpertSummaryResponse> findAll() {
        List<Expert> expertList = expertRepository.findAll();
        if (expertList.isEmpty()) {
            throw new CustomResourceNotFoundException("There is no result");
        } else {
            return expertList.stream().map(expertMapper::expertToExpertSummaryResponse).toList();
        }
    }

    @Override
    public List<ExpertSummaryResponse> findExpertsByDynamicSearch(ExpertSummaryRequest request) {
        Specification<Expert> specification = Specification.where(null);
        boolean isRequestValid = false;

        if (isNotEmpty(request.getFirstName())) {
            specification = specification.and(hasFirstName(request.getFirstName()));
            isRequestValid = true;
        }
        if (isNotEmpty(request.getLastName())) {
            specification = specification.and(hasLastName(request.getLastName()));
            isRequestValid = true;
        }
        if (isNotEmpty(request.getEmail())) {
            specification = specification.and(hasEmail(request.getEmail()));
            isRequestValid = true;
        }
        if (isNotEmpty(request.getScore())) {
            specification = specification.and(hasScore(Integer.parseInt(request.getScore())));
            isRequestValid = true;
        }
        if (isNotEmpty(request.getSubDutyName())) {
            specification = specification.and(hasSubDutyName(request.getSubDutyName()));
            isRequestValid = true;
        }
        if (!isRequestValid) {
            throw new CustomBadRequestException("There is no result");
        }
        List<Expert> expertList = expertRepository.findAll(specification);
        if (expertList.isEmpty()) {
            throw new CustomBadRequestException("There is no result");
        } else {
            return expertList.stream().map(expertMapper::expertToExpertSummaryResponse).toList();
        }
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    private Specification<Expert> hasSubDutyName(String subDutyName) {
        return ((root, query, criteriaBuilder) -> {
            Join<Expert, SubDuty> expertSubDutyJoin = root.join("subDuties");
            return criteriaBuilder.like(expertSubDutyJoin.get("name"), "%" + subDutyName + "%");
        });
    }

    private Specification<Expert> hasFirstName(String firstName) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%"));
    }

    private Specification<Expert> hasLastName(String lastName) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%"));
    }

    private Specification<Expert> hasEmail(String email) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("email"), "%" + email + "%"));
    }

    private Specification<Expert> hasScore(int score) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("score"), score));
    }

    @Override
    public String verifyToken(String token) {
        if (token.equals(this.token)) {
            this.expert.setWallet(walletService.save(new Wallet(0.0)));
            expertRepository.save(this.expert);
            this.token = null;
            this.expert = null;
            return "successfulRegistration";
        } else {
            return "registrationFailed";
        }
    }
}
