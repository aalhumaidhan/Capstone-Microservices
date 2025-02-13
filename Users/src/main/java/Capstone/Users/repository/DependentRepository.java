package Capstone.Users.repository;

import Capstone.Users.entity.DependentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DependentRepository extends JpaRepository<DependentEntity, Long> {
    Optional<DependentEntity> findByFaceId(String FaceId);

}
