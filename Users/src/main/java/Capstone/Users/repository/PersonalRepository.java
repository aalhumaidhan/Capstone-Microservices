package Capstone.Users.repository;

import Capstone.Users.entity.PersonalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalRepository extends JpaRepository<PersonalEntity, Long> {
    Optional<PersonalEntity> findByFaceID(String faceId);
}
