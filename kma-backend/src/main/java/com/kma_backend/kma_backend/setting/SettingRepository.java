package com.kma_backend.kma_backend.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findTopByOrderByIdAsc();

    @Query("SELECT s FROM Setting s ORDER BY s.id ASC LIMIT 1")
    Optional<Setting> findFirst();

}
