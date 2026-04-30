package fr.mossaab.appupdater.repository;

import fr.mossaab.appupdater.entity.UpdateConfig;
import fr.mossaab.appupdater.enums.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpdateConfigRepository extends JpaRepository<UpdateConfig, Long> {
    Optional<UpdateConfig> findUpdateConfigByPlatform(Platform platform);
}
