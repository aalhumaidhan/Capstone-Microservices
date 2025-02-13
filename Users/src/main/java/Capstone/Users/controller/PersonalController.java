package Capstone.Users.controller;


import Capstone.Users.bo.*;
import Capstone.Users.entity.DependentEntity;
import Capstone.Users.entity.PersonalEntity;
import Capstone.Users.repository.DependentRepository;
import Capstone.Users.repository.PersonalRepository;
import Capstone.Users.service.PersonalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/personal")
@RestController
public class PersonalController {
    private final PersonalService personalService;
    private final PersonalRepository personalRepository;
    private final DependentRepository dependentRepository;

    public PersonalController(PersonalService personalService, PersonalRepository personalRepository, DependentRepository dependentRepository) {
        this.personalService = personalService;
        this.personalRepository = personalRepository;
        this.dependentRepository = dependentRepository;
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<Object> getTransactions(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(personalService.getPersonalTransactions(userId, token));
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<Object> getTransactionById(@PathVariable Long transactionId, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(personalService.getTransactionById(transactionId, token));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<GetProfileResponse> getProfile(@PathVariable("userId") Long userId) {
        GetProfileResponse response = new GetProfileResponse();
        try{
            System.out.println(personalRepository.findById(userId));

            personalRepository.findById(userId);
        } catch (Exception e) {
            response.setMessage("User does not exist");
            return ResponseEntity.badRequest().body(response);
        }
        response.setUser(personalRepository.findById(userId).get());
        response.setMessage("User retrieved successfully");
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<Object> addDeposit(@PathVariable Long userId,
                                             @RequestBody MakeDepositRequest request,
                                             @RequestHeader("Authorization") String token) {
        try {
            Object response = personalService.addDeposit(request, userId, token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deposit failed: " + e.getMessage());
        }
    }

    @PostMapping("/addFamilyMember/{userId}")
    public ResponseEntity<FamilyMemberResponse> addFamilyMember(@PathVariable("userId") Long userId, @RequestBody CreateFamilyAccountRequest request) {
        FamilyMemberResponse response = new FamilyMemberResponse();
        try {
            DependentEntity dependent = personalService.addFamilyMember(userId, request);
            response.setFamilyMembers(List.of(dependent));
            response.setMessage("Family member added successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/getFamilyMembers/{userId}")
    public ResponseEntity<FamilyMemberResponse> getFamilyMembers(@PathVariable("userId") Long userId) {
        FamilyMemberResponse response = new FamilyMemberResponse();
        try{
            personalRepository.findById(userId).orElseThrow();
        } catch (Exception e) {
            response.setMessage("User does not exist");
            return ResponseEntity.badRequest().body(response);
        }
        response.setFamilyMembers(personalService.getFamilyMembers(userId));
        response.setMessage("Family members retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/removeFamilyMember/{userId}/{familyMemberId}")
    public ResponseEntity<FamilyMemberResponse> removeFamilyMember(@PathVariable("userId") Long userId, @PathVariable("familyMemberId") Long familyMemberId) {
        FamilyMemberResponse response = new FamilyMemberResponse();
        try {
            personalRepository.findById(userId).orElseThrow();
            dependentRepository.findById(familyMemberId).orElseThrow();
        } catch (Exception e) {
            response.setMessage("User or family member does not exist");
            return ResponseEntity.badRequest().body(response);
        }
        response.setFamilyMembers(personalService.removeFamilyMember(userId, familyMemberId));
        response.setMessage("Family member removed successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/setLimit/{userId}/{memberId}/{limit}")
    public ResponseEntity<SetLimitResponse> setLimit(@PathVariable("userId") Long userId, @PathVariable("memberId") Long memberId, @PathVariable("limit") Double limit) {
        SetLimitResponse response = new SetLimitResponse();
        try{
            personalRepository.findById(userId).orElseThrow();
            dependentRepository.findById(memberId).orElseThrow();
        } catch (Exception e) {
            response.setMessage("User or family member does not exist");
            return ResponseEntity.badRequest().body(response);
        }
        response.setFamilyMember(personalService.setLimit(userId, memberId, limit));
        response.setMessage("Limit set successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/wallet/{userId}")
    public ResponseEntity<Map<String, Double>> getWalletBalance(@PathVariable Long userId) {
        PersonalEntity user = personalRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Map<String, Double> response = new HashMap<>();
        response.put("walletBalance", user.getWalletBalance());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/wallet/deduct/{userId}")
    public ResponseEntity<Object> deductWalletBalance(@PathVariable Long userId, @RequestBody Map<String, Double> request) {
        try {
            if (!request.containsKey("amount")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing 'amount' field");
            }
            Double amount = request.get("amount");
            String response = personalService.deductWalletBalance(userId, amount);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/faceid/{faceId}")
    public ResponseEntity<Long> getUserIdByFaceId(@PathVariable String faceId) {
        try {
            PersonalEntity user = personalRepository.findByFaceID(faceId)
                    .orElseThrow(() -> new IllegalArgumentException("User with Face ID not found"));

            return ResponseEntity.ok(user.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/createTempTransaction/{userId}")
    public ResponseEntity<String> addTempTransaction(@PathVariable Long userId, @RequestBody TempTransactionDTO tempTransactionDTO) {
        PersonalEntity user = personalRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.addTempTransaction(tempTransactionDTO);
        personalRepository.save(user);

        return ResponseEntity.ok("Temporary transaction added successfully.");
    }

    @GetMapping("/tempTransactions/{userId}")
    public ResponseEntity<List<TempTransactionDTO>> getTempTransactions(@PathVariable Long userId) {
        PersonalEntity user = personalRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<TempTransactionDTO> tempTransactions = user.getTempTransactions().stream()
                .map(transaction -> new TempTransactionDTO(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getDateTime(),
                        transaction.getStatus()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(tempTransactions);
    }

}

