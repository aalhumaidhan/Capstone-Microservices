package Capstone.Transactions.service;

import Capstone.Transactions.Enums.Methods;
import Capstone.Transactions.Enums.Status;
import Capstone.Transactions.bo.*;
import Capstone.Transactions.entity.*;
import Capstone.Transactions.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionService(TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    public TransactionDTO fillTransactionDto(TransactionEntity transaction){
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setTransactionDate(transaction.getDateTime());
        transactionDTO.setMethod(transaction.getMethod());
        transactionDTO.setReceiverId(transaction.getReceiverId());
        transactionDTO.setSenderId(transaction.getSenderId());
        transactionDTO.setStatus(transaction.getStatus());
        transactionDTO.setTransactionId(transaction.getId());
        if (transaction.getAssociateId() == null){
            transactionDTO.setAssociateId(0L);
        }
        else {
            transactionDTO.setAssociateId(transaction.getAssociateId());
        }
        return transactionDTO;
    }


//    @Cacheable(value = "userTransactions", key = "#userId")
//    public List<TransactionDTO> getTransactionsByUserId(Long userId) {
//        List<TransactionEntity> transactions = transactionRepository.findBySenderIdOrReceiverId(userId, userId);
//
//        return transactions.stream()
//                .map(this::convertToDTO)
//                .toList();
//    }

//    public List<TransactionDTO> getTransactionsByBusinessId(Long businessId) {
//        List<TransactionEntity> transactions = transactionRepository.findBySenderIdOrReceiverId(businessId, businessId);
//
//        return transactions.stream()
//                .map(this::convertToDTO)
//                .toList();
//    }

    public List<TransactionDTO> getTransactionsByBusinessId(Long businessId) {
        // Fetch transactions where the business ID is either sender or receiver
        List<TransactionEntity> transactions = transactionRepository.findBySenderIdOrReceiverId(businessId, businessId);

        // Convert TransactionEntity to TransactionDTO
        return transactions.stream()
                .map(this::fillTransactionDto)
                .toList();
    }


    private TransactionDTO convertToDTO(TransactionEntity transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(transaction.getId());
        dto.setSenderId(transaction.getSenderId());
        dto.setReceiverId(transaction.getReceiverId());
        dto.setAmount(transaction.getAmount());
        dto.setMethod(transaction.getMethod());
        dto.setTransactionDate(transaction.getDateTime());
        dto.setStatus(transaction.getStatus());
        return dto;
    }


    public TransactionEntity getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow();
    }

    public List<TransactionEntity> getTransactionsBySender(Long senderId) {
        return transactionRepository.findBySenderId(senderId);
    }

    public List<TransactionEntity> getTransactionsByReceiver(Long receiverId) {
        return transactionRepository.findByReceiverId(receiverId);
    }

    public List<TransactionEntity> getTransactionsByAssociate(Long associateId) {
        return transactionRepository.findByAssociateId(associateId);
    }

    public List<TransactionEntity> getTransactionsByStatus(Status status) {
        return transactionRepository.findByStatus(status);
    }

    public List<TransactionEntity> getTransactionsByMethod(Methods method) {
        return transactionRepository.findByMethod(method);
    }

    public TransactionEntity addDeposit(MakeDepositRequest request, Long userId) {
        LocalDateTime dateTime = LocalDateTime.now();
        TransactionEntity deposit = new TransactionEntity();

        deposit.setAmount(request.getAmount());
        deposit.setDateTime(dateTime.toString());
        deposit.setMethod(Methods.DEPOSIT);
        deposit.setReceiverId(userId);
        deposit.setSenderId(userId);
        deposit.setStatus(Status.PENDING);

        return transactionRepository.save(deposit);
    }

    public TransactionEntity makeBusinessTransactionWithFaceID(MakeFaceIdTransactionRequest request) {
        LocalDateTime dateTime = LocalDateTime.now();
        TransactionEntity transaction = new TransactionEntity();

        String userUrl = "http://localhost:8081/personal/faceid/" + request.getFaceId();
        ResponseEntity<Long> faceIdResponse = restTemplate.getForEntity(userUrl, Long.class);
        Long senderId = faceIdResponse.getBody();

        if (senderId == null) {
            throw new IllegalArgumentException("User with given Face ID not found.");
        }

        transaction.setAmount(request.getAmount());
        transaction.setAssociateId(request.getAssociateId());
        transaction.setSenderId(senderId);
        transaction.setReceiverId(request.getReceiverId());

        transaction.setMethod(Methods.FACEID);
        transaction.setStatus(Status.PENDING);
        transaction.setDateTime(dateTime.toString());

        transactionRepository.save(transaction);

        String balanceUrl = "http://localhost:8081/personal/wallet/" + senderId;
        ResponseEntity<Map> balanceResponse = restTemplate.getForEntity(balanceUrl, Map.class);

        if (!balanceResponse.getStatusCode().is2xxSuccessful() || balanceResponse.getBody() == null) {
            throw new IllegalArgumentException("Failed to fetch wallet balance.");
        }

        Double walletBalance = Double.valueOf(balanceResponse.getBody().get("walletBalance").toString());

        if (walletBalance < request.getAmount()) {
            transaction.setStatus(Status.REJECTED);
            return transactionRepository.save(transaction);
        }

        String deductUrl = "http://localhost:8081/personal/wallet/deduct/" + senderId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Double> deductRequest = new HashMap<>();
        deductRequest.put("amount", request.getAmount());

        HttpEntity<Map<String, Double>> deductEntity = new HttpEntity<>(deductRequest, headers);
        restTemplate.exchange(deductUrl, HttpMethod.PUT, deductEntity, String.class);

        transaction.setStatus(Status.APPROVED);
        return transactionRepository.save(transaction);
    }

    public TransactionEntity makePersonalTransactionWithQRCode(StartQrCodeTransactionRequest request) {
        LocalDateTime datetime = LocalDateTime.now();
        TransactionEntity transaction = new TransactionEntity();

        String userUrl = "http://localhost:8081/personal/wallet/" + request.getSenderId();
        ResponseEntity<Map> walletResponse = restTemplate.getForEntity(userUrl, Map.class);

        if (!walletResponse.getStatusCode().is2xxSuccessful() || walletResponse.getBody() == null) {
            throw new IllegalArgumentException("Failed to fetch wallet balance.");
        }

        Double walletBalance = Double.valueOf(walletResponse.getBody().get("walletBalance").toString());

        if (walletBalance < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        transaction.setAmount(request.getAmount());
        transaction.setSenderId(request.getSenderId());
        transaction.setMethod(Methods.QR);
        transaction.setStatus(Status.PENDING);
        transaction.setDateTime(datetime.toString());

        transactionRepository.save(transaction);

        String tempTransactionUrl = "http://localhost:8081/personal/createTempTransaction/" + request.getSenderId();

        Map<String, Object> tempTransactionRequest = new HashMap<>();
        tempTransactionRequest.put("id", transaction.getId());
        tempTransactionRequest.put("amount", transaction.getAmount());
        tempTransactionRequest.put("dateTime", transaction.getDateTime());
        tempTransactionRequest.put("status", transaction.getStatus().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> tempTransactionEntity = new HttpEntity<>(tempTransactionRequest, headers);

        ResponseEntity<String> tempTransactionResponse = restTemplate.postForEntity(tempTransactionUrl, tempTransactionEntity, String.class);

        if (!tempTransactionResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException("Failed to create temp transaction.");
        }

        return transaction;
    }

    public void deleteTransaction(Long transactionId) {
        transactionRepository.deleteById(transactionId);
    }


    @Transactional
    public TransactionEntity makeBusinessTransactionWithQRCode(MakeQRCodeTransactionRequest request) throws Exception {
        LocalDateTime datetime = LocalDateTime.now();
        TransactionEntity transaction = new TransactionEntity();

        String tempTransactionUrl = "http://localhost:8081/personal/tempTransactions/" + request.getSenderId();
        ResponseEntity<List<TempTransactionDTO>> response = restTemplate.exchange(
                tempTransactionUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        List<TempTransactionDTO> tempTransactions = response.getBody();
        if (tempTransactions == null || tempTransactions.isEmpty()) {
            throw new Exception("No temporary transactions found.");
        }

        TempTransactionDTO matchedTransaction = null;
        for (TempTransactionDTO tempTransaction : tempTransactions) {
            if (tempTransaction.getAmount().equals(request.getAmount())) {
                matchedTransaction = tempTransaction;
                break;
            }
        }

        if (matchedTransaction == null) {
            throw new Exception("Transaction not found in temp transactions.");
        }

        String balanceUrl = "http://localhost:8081/personal/wallet/" + request.getSenderId();
        ResponseEntity<Map<String, Double>> balanceResponse = restTemplate.exchange(
                balanceUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        Double walletBalance = balanceResponse.getBody().get("walletBalance");

        if (walletBalance < request.getAmount()) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        String deductBalanceUrl = "http://localhost:8081/personal/wallet/deduct/" + request.getSenderId();
        Map<String, Double> deductRequest = new HashMap<>();
        deductRequest.put("amount", request.getAmount());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Double>> deductEntity = new HttpEntity<>(deductRequest, headers);

        restTemplate.exchange(deductBalanceUrl, HttpMethod.PUT, deductEntity, String.class);

        transaction.setAmount(request.getAmount());
        transaction.setAssociateId(Long.valueOf(request.getAssociateId()));
        transaction.setSenderId(Long.valueOf(request.getSenderId()));
        transaction.setReceiverId(Long.valueOf(request.getReceiverId()));
        transaction.setMethod(Methods.QR);
        transaction.setStatus(Status.APPROVED);
        transaction.setDateTime(datetime.toString());

        transactionRepository.save(transaction);

        String deleteTempTransactionUrl = "http://localhost:8081/personal/tempTransactions/delete/" + request.getSenderId();
        HttpEntity<TempTransactionDTO> deleteRequest = new HttpEntity<>(matchedTransaction, headers);

        restTemplate.exchange(deleteTempTransactionUrl, HttpMethod.POST, deleteRequest, String.class);

        transactionRepository.findById(matchedTransaction.getId()).ifPresent(transactionRepository::delete);

        return transaction;
    }

}

