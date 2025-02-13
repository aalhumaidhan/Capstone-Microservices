package Capstone.Users.repository;

import Capstone.Users.entity.BusinessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<BusinessEntity, Long> {
    BusinessEntity findByUsername(String username);
    BusinessEntity findByName(String businessName);
    BusinessEntity findByBusinessLicenseId(String businessLicenseId);
}