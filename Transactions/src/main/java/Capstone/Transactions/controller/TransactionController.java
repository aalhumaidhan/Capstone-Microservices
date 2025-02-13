package Capstone.Transactions.controller;

import Capstone.Transactions.bo.*;
import Capstone.Transactions.entity.TransactionEntity;
import Capstone.Transactions.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/transactions")
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByBusinessId(@PathVariable Long businessId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByBusinessId(businessId);
        return ResponseEntity.ok(transactions);
    }


    @GetMapping("/{transactionId}")
    public ResponseEntity<GetTransactionByIdResponse> getTransactionById(@PathVariable("transactionId") Long transactionId){
        GetTransactionByIdResponse response = new GetTransactionByIdResponse();
        try {
            TransactionDTO dto = transactionService.fillTransactionDto(transactionService.getTransactionById(transactionId));
            response.setTransaction(dto);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit/{userId}")
    public ResponseEntity<Object> addDeposit(@PathVariable("userId") Long userId, @RequestBody MakeDepositRequest request) {
        try {
            TransactionEntity deposit = transactionService.addDeposit(request, userId);
            return ResponseEntity.ok(deposit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deposit failed: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GetTransactionsBySenderResponse> getTransactionsByUserId(@PathVariable("userId") Long userId){
        GetTransactionsBySenderResponse response = new GetTransactionsBySenderResponse();
        try {
            List<TransactionDTO> transactions = new ArrayList<>();
            for (TransactionEntity transaction : transactionService.getTransactionsBySender(userId)){
                transactions.add(transactionService.fillTransactionDto(transaction));
            }

            for (TransactionEntity transaction : transactionService.getTransactionsByReceiver(userId)){
                transactions.add(transactionService.fillTransactionDto(transaction));
            }

            response.setTransactions(transactions);
            response.setMessage("Successfully retrieved all transactions made by UserId " + userId );
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }


//    @GetMapping("/associate/{associateId}")
//    public ResponseEntity<GetTransactionsByAssociateResponse> getTransactionsByAssociate(@PathVariable("associateId") Long associateId){
//        GetTransactionsByAssociateResponse response = new GetTransactionsByAssociateResponse();
//        try{
//            List<TransactionDTO> transactions = new ArrayList<>();
//            for (TransactionEntity transaction : transactionService.getTransactionsByAssociate(associateId)){
//                transactions.add(transactionService.fillTransactionDto(transaction));
//            }
//            response.setTransactions(transactions);
//        } catch (Exception e) {
//            response.setMessage(e.getMessage());
//            return ResponseEntity.status(404).body(response);
//        }
//        return ResponseEntity.ok(response);
//    }


    @PostMapping("/business/transfer/faceid")
    public ResponseEntity<Object> createBusinessTransactionWithFaceID(@RequestBody MakeFaceIdTransactionRequest request) {
        try {
            TransactionEntity transaction = transactionService.makeBusinessTransactionWithFaceID(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction failed: " + e.getMessage());
        }
    }

//    @PutMapping("/{transactionId}/finalize")
//    public ResponseEntity<Object> finalizeTransaction(@PathVariable Long transactionId) {
//        try {
//            TransactionEntity transaction = transactionService.finalizeTransaction(transactionId);
//            return ResponseEntity.ok(transaction);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction finalization failed: " + e.getMessage());
//        }
//    }

    @PostMapping("/qr-code")
    public ResponseEntity<Object> startQrCodeTransaction(@RequestBody StartQrCodeTransactionRequest request) {
        try {
            TransactionEntity transaction = transactionService.makePersonalTransactionWithQRCode(request);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/transactions/qr-code")
    public ResponseEntity<Object> makeBusinessTransactionWithQRCode(@RequestBody MakeQRCodeTransactionRequest request) {
        try {
            TransactionEntity transaction = transactionService.makeBusinessTransactionWithQRCode(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}

