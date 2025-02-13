package Capstone.Users.repository;

import Capstone.Users.entity.AssociateEntity;
import Capstone.Users.entity.BusinessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssociateRepository extends JpaRepository<AssociateEntity, Long> {
    AssociateEntity findByUsername(String username);
    Optional<AssociateEntity> findByBusiness(BusinessEntity business);

}
