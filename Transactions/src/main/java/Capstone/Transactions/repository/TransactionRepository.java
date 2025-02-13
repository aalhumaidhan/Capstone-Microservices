package Capstone.Transactions.repository;

import Capstone.Transactions.Enums.Methods;
import Capstone.Transactions.Enums.Status;
import Capstone.Transactions.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    TransactionEntity findByDateTime(String dateTime);
    List<TransactionEntity> findByStatus(Status status);
    List<TransactionEntity> findByMethod(Methods method);
    List<TransactionEntity> findBySenderId(Long senderId);
    List<TransactionEntity> findByReceiverId(Long receiverId);
    List<TransactionEntity> findByAssociateId(Long associateId);
//    List<TransactionEntity> findBySenderOrReceiverId(Long senderId, Long receiverId);

}
