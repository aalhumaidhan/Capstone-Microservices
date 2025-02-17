package Capstone.Users.service;

import Capstone.Users.bo.CreateFamilyAccountRequest;
import Capstone.Users.bo.MakeDepositRequest;
import Capstone.Users.bo.TransactionDTO;
import Capstone.Users.entity.DependentEntity;
import Capstone.Users.entity.PersonalEntity;
import Capstone.Users.repository.DependentRepository;
import Capstone.Users.repository.PersonalRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonalService {

    private final RestTemplate restTemplate;
    private final PersonalRepository personalRepository;
    private final DependentRepository dependentRepository;
    private static final String TRANSACTION_API = "http://transactions:8082/transactions/";


    public PersonalService(PersonalRepository personalRepository, DependentRepository dependentRepository, RestTemplate restTemplate) {
        this.personalRepository = personalRepository;
        this.dependentRepository = dependentRepository;
        this.restTemplate = restTemplate;
    }

    public DependentEntity addFamilyMember(Long userId, CreateFamilyAccountRequest request) {
        PersonalEntity user = personalRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));
        DependentEntity dependent = new DependentEntity();
        dependent.setFullName(request.getFullName());
        dependent.setFaceId(request.getFaceId());
        dependent.setWalletBalance(0.0);
        dependent.setGuardian(user);
        dependent.setTransactionHistory(new ArrayList<>());

        DependentEntity savedDependent = dependentRepository.save(dependent);

        user.addFamilyMember(savedDependent);
        personalRepository.save(user);

        return savedDependent;
    }

    public Object getPersonalTransactions(Long userId, String token) {
        String url = TRANSACTION_API + "user/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<TransactionDTO>> response = getExchange(url, requestEntity);
//        System.out.println("Trying to work here, here is your user ID: " + userId);
//        System.out.println(response);
        System.out.println(response.getBody());
        return response.getBody();
    }

    private ResponseEntity getExchange(String url, HttpEntity<Void> requestEntity) {
        return restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                }
        );
    }

    public Object getTransactionById(Long transactionId, String token) {
        String url = TRANSACTION_API + transactionId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<TransactionDTO> response = getExchange(url, requestEntity);

        System.out.println("Fetching transaction, here is the transaction ID: " + transactionId);
        System.out.println(response);
        System.out.println(response.getBody());

        return response.getBody();
    }

    public Object addDeposit(MakeDepositRequest request, Long userId, String token) {
        PersonalEntity user = personalRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String url = TRANSACTION_API + "/deposit/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<MakeDepositRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            user.setWalletBalance(user.getWalletBalance() + request.getAmount());
            personalRepository.save(user);
        } else {
            throw new RuntimeException("Deposit failed in Transactions Microservice.");
        }

        return response.getBody();
    }

    public String deductWalletBalance(Long userId, Double amount) {
        PersonalEntity user = personalRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getWalletBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        user.setWalletBalance(user.getWalletBalance() - amount);
        personalRepository.save(user);

        return "Wallet updated successfully";
    }

    public Long getUserIdByFaceId(String faceId) {
        PersonalEntity user = personalRepository.findByFaceID(faceId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for Face ID: " + faceId));
        return user.getId();
    }

    public Double getWalletBalance(Long userId) {
        PersonalEntity user = personalRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getWalletBalance();
    }

    public List<DependentEntity> getFamilyMembers(Long userId) {
        PersonalEntity user = personalRepository.findById(userId).orElseThrow();
        return user.getFamilyMembers();
    }

    public List<DependentEntity> removeFamilyMember(Long userId, Long familyMemberId) {
        PersonalEntity user = personalRepository.findById(userId).orElseThrow();
        DependentEntity member = dependentRepository.findById(familyMemberId).orElseThrow();
        user.removeFamilyMember(member);
        dependentRepository.delete(member);
        user = personalRepository.save(user);
        return user.getFamilyMembers();
    }

    public DependentEntity setLimit(Long userId, Long memberId, Double limit) {
        PersonalEntity user = personalRepository.findById(userId).orElseThrow();
        DependentEntity member = dependentRepository.findById(memberId).orElseThrow();
        if (user.getFamilyMembers().contains(member)) {
            member.setWalletBalance(limit);
            return dependentRepository.save(member);
        }
        else {
            throw new IllegalArgumentException("Member is not a family member");
        }
    }

}

