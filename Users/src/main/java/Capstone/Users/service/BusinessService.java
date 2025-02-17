package Capstone.Users.service;

import Capstone.Users.bo.BusinessDTO;
import Capstone.Users.bo.MakeFaceIdTransactionRequest;
import Capstone.Users.bo.Register.Request.RegisterAssociateRequest;
import Capstone.Users.bo.TransactionDTO;
import Capstone.Users.entity.AssociateEntity;
import Capstone.Users.entity.BusinessEntity;
import Capstone.Users.entity.DependentEntity;
import Capstone.Users.entity.PersonalEntity;
import Capstone.Users.repository.AssociateRepository;
import Capstone.Users.repository.BusinessRepository;
import Capstone.Users.repository.DependentRepository;
import Capstone.Users.repository.PersonalRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;


@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final AuthenticationService authenticationService;
    private final PersonalRepository personalRepository;
    private final RestTemplate restTemplate;
    private final DependentRepository dependentRepository;
    private final AssociateRepository associateRepository;

//    private static final String BUSINESS_API = "http://localhost:8082/transactions/business/profile/";
    private static final String TRANSACTION_API = "http://localhost:8082/transactions/";


    public BusinessService(BusinessRepository businessRepository, AuthenticationService authenticationService, PersonalRepository personalRepository, RestTemplate restTemplate, DependentRepository dependentRepository, AssociateRepository associateRepository) {
        this.businessRepository = businessRepository;
        this.authenticationService = authenticationService;
        this.personalRepository = personalRepository;
        this.restTemplate = restTemplate;
        this.dependentRepository = dependentRepository;
        this.associateRepository = associateRepository;
    }

    public Object initiateBusinessTransactionWithFaceID(MakeFaceIdTransactionRequest request, String token) throws Exception {
        Optional<PersonalEntity> personalEntity = personalRepository.findByFaceID(request.getFaceId());
        Optional<DependentEntity> dependentEntity = dependentRepository.findByFaceId(request.getFaceId());

        if (personalEntity.isEmpty() && dependentEntity.isEmpty()) {
            throw new Exception("Face ID not found in either repository");
        }

        String url = "http://localhost:8082/transactions/business/transfer/faceid";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);

        HttpEntity<MakeFaceIdTransactionRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);

        return response.getBody();
    }


//    public Object makeBusinessTransaction(MakeBusinessTransactionRequest request, String token) {
//        if (request.getSenderId().equals(request.getReceiverId())) {
//            throw new IllegalArgumentException("Sender and receiver cannot be the same.");
//        }
//
//        BusinessEntity business = businessRepository.findById(request.getSenderId())
//                .orElseThrow(() -> new IllegalArgumentException("Business not found"));
//
//        if (request.getAssociateId() != null) {
//            AssociateEntity associate = (AssociateEntity) businessRepository.findById(request.getAssociateId())
//                    .orElseThrow(() -> new IllegalArgumentException("Associate not found"));
//        }
//
//        PersonalEntity receiver = personalRepository.findById(request.getReceiverId())
//                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));
//
//
//        // ✅ 6. Call Transactions Microservice
//        String url = TRANSACTION_API + "/business/transfer";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", token);
//
//        HttpEntity<MakeBusinessTransactionRequest> requestEntity = new HttpEntity<>(request, headers);
//        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            business.setWalletBalance(business.getWalletBalance() - request.getAmount());
//            businessRepository.save(business);
//        } else {
//            throw new RuntimeException("Transaction failed in Transactions Microservice.");
//        }
//
//        return response.getBody();
//    }


    public List<TransactionDTO> getTransactions(Long businessId, String token) {
        String url = TRANSACTION_API + "business/" + businessId + "/transactions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<TransactionDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    public List<AssociateEntity> getAssociates(Long businessId) {
        BusinessEntity business = businessRepository.findById(businessId).orElseThrow();
        return business.getAssociates();
    }

    public List<AssociateEntity> createAssociate(Long businessId, RegisterAssociateRequest associate) {
        BusinessEntity business = businessRepository.findById(businessId).orElseThrow();
        AssociateEntity newAssociate = (AssociateEntity) authenticationService.Register(associate, associate.getClass().getSimpleName());
        business.addAssociate(newAssociate);
        business = businessRepository.save(business);
        return business.getAssociates();
    }

    public BusinessDTO getBusiness(Long associateId) {
        AssociateEntity associate = associateRepository.findById(associateId).orElseThrow();
        System.out.println(associate.getBusiness());
        return fillBusinessDTO(associate.getBusiness());
    }

    public BusinessDTO fillBusinessDTO(BusinessEntity businessEntity) {
        BusinessDTO businessDTO = new BusinessDTO();
        businessDTO.setId(businessEntity.getId().toString());
        businessDTO.setName(businessEntity.getName());
        businessDTO.setAddress(businessEntity.getAddress());
        return businessDTO;
    }

    public AssociateEntity getAssociate(Long associateId) {
        return associateRepository.findById(associateId).orElseThrow();
    }
}
