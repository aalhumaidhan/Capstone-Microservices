package Capstone.Users.controller;

import Capstone.Users.bo.*;
import Capstone.Users.bo.Register.Request.RegisterAssociateRequest;
import Capstone.Users.repository.BusinessRepository;
import Capstone.Users.service.BusinessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/business")
@RestController
public class BusinessController {
    private final BusinessService businessService;
    private final BusinessRepository businessRepository;

    public BusinessController(BusinessService businessService, BusinessRepository businessRepository) {
        this.businessService = businessService;
        this.businessRepository = businessRepository;
    }

    @GetMapping("/associate/{associateId}/business")
    public ResponseEntity<GetBusinessByAssociateIdResponse> getBusinesses(@PathVariable("associateId") Long associateId) {
        GetBusinessByAssociateIdResponse response = new GetBusinessByAssociateIdResponse();
        try {
            response.setBusiness(businessService.getBusiness(associateId));
        } catch (Exception e) {
            response.setMessage("Business not found");
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer/faceid")
    public ResponseEntity<Object> initiateBusinessTransactionWithFaceID(@RequestBody MakeFaceIdTransactionRequest request,
                                                                        @RequestHeader("Authorization") String token) {
        try {
            Object response = businessService.initiateBusinessTransactionWithFaceID(request, token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/profile/{businessId}")
    public ResponseEntity<BusinessProfileResponse> getBusinessProfile(@PathVariable("businessId") Long businessId) {
        try {
            businessRepository.findById(businessId).orElseThrow();
        } catch (Exception e) {
            BusinessProfileResponse response = new BusinessProfileResponse();
            response.setMessage("Business not found");
            return ResponseEntity.status(404).body(response);
        }
        BusinessProfileResponse response = new BusinessProfileResponse();
        response.setBusinessEntity(businessRepository.findById(businessId).orElseThrow());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/profile/{businessId}/transactions")
    public ResponseEntity<BusinessTransactionListResponse> getBusinessTransactions(
            @PathVariable("businessId") Long businessId,
            @RequestHeader("Authorization") String token) {
        try {
            businessRepository.findById(businessId).orElseThrow();
        } catch (Exception e) {
            BusinessTransactionListResponse response = new BusinessTransactionListResponse();
            response.setMessage("Business not found");
            return ResponseEntity.status(404).body(response);
        }

        List<TransactionDTO> transactions = businessService.getTransactions(businessId, token);
        BusinessTransactionListResponse response = new BusinessTransactionListResponse();
        response.setTransactionList(transactions);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{businessId}/associates")
    public ResponseEntity<BusinessAssociateListResponse> getBusinessAssociates(@PathVariable("businessId") Long businessId) {
        try {
            businessRepository.findById(businessId).orElseThrow();
        } catch (Exception e) {
            BusinessAssociateListResponse response = new BusinessAssociateListResponse();
            response.setMessage("Business not found");
            return ResponseEntity.status(404).body(response);
        }
        BusinessAssociateListResponse response = new BusinessAssociateListResponse();
        response.setAssociateList(businessService.getAssociates(businessId));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/{businessId}/associate")
    public ResponseEntity<BusinessAssociateListResponse> createAssociate(@PathVariable("businessId") Long businessId, @RequestBody RegisterAssociateRequest associate) {
        BusinessAssociateListResponse response = new BusinessAssociateListResponse();
        try {
            response.setAssociateList(businessService.createAssociate(businessId, associate));
            response.setMessage("Associate created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @GetMapping("/associate/{associateId}")
    public ResponseEntity<GetAssociateResponse> getAssociate(@PathVariable("associateId") Long associateId) {
        GetAssociateResponse response = new GetAssociateResponse();
        try {
            response.setAssociate(businessService.getAssociate(associateId));
        } catch (Exception e) {
            response.setMessage("Associate not found");
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
