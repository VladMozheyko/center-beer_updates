package fr.mossaab.appupdater.service;

import fr.mossaab.appupdater.dto.UpdateConfigRequest;
import fr.mossaab.appupdater.dto.UpdateConfigResponse;
import fr.mossaab.appupdater.entity.UpdateConfig;
import fr.mossaab.appupdater.enums.Platform;
import fr.mossaab.appupdater.enums.UpdateType;
import fr.mossaab.appupdater.repository.UpdateConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateConfigService {
    private final UpdateConfigRepository repository;

    public UpdateConfig createOrUpdate(UpdateConfigRequest dto) {
        Optional<UpdateConfig> configFromDb = repository.findUpdateConfigByPlatform(dto.getPlatform());
        UpdateConfig updateConfig = configFromDb.orElse(new UpdateConfig());
        if (configFromDb.isEmpty()) {
            updateConfig.setPlatform(dto.getPlatform());
            updateConfig.setCreatedAt(LocalDateTime.now());
        }
        updateConfig.setForceUpdateVersion(dto.getForceUpdateVersion());
        updateConfig.setLatestVersion(dto.getLatestVersion());
        updateConfig.setGooglePlayUrl(dto.getGooglePlayUrl());
        updateConfig.setRuStoreUrl(dto.getRuStoreUrl());
        updateConfig.setAppStoreUrl(dto.getAppStoreUrl());
        updateConfig.setUpdatedAt(LocalDateTime.now());
        return repository.save(updateConfig);
    }

    public Optional<UpdateConfigResponse> get(Platform platform, String currentVersion) {
        Optional<UpdateConfig> config = repository.findUpdateConfigByPlatform(platform);
        if (config.isPresent()) {
            UpdateConfig c = config.get();
            UpdateType updateType = getUpdateTypeByVersions(c.getForceUpdateVersion(), c.getLatestVersion(), currentVersion);
            return Optional.of(UpdateConfigResponse.builder()
                    .platform(platform)
                    .latestVersion(c.getLatestVersion())
                    .forceUpdateVersion(c.getForceUpdateVersion())
                    .appStoreUrl(c.getAppStoreUrl())
                    .ruStoreUrl(c.getRuStoreUrl())
                    .googlePlayUrl(c.getGooglePlayUrl())
                    .updateType(updateType)
                    .message(updateType.getMessage())
                    .build());
        } else {
            return Optional.empty();
        }
    }

    public UpdateType getUpdateTypeByVersions(String forceUpdateVersion, String latestVersion, String userVersion) {
        int cmpForce = compareVersions(userVersion, forceUpdateVersion);
        if (cmpForce < 0) {
            return UpdateType.MANDATORY;
        }
        int cmpLatest = compareVersions(userVersion, latestVersion);
        if (cmpLatest < 0) {
            return UpdateType.OPTIONAL;
        }
        return UpdateType.UP_TO_DATE;
    }

    /**
     * Сравнивает две версии в формате "0.0.0".
     * @return 0 если версии равны равны, <0 если v1 < v2, >0 если v1 > v2
     */
    public int compareVersions(String v1, String v2) {
        String[] a1 = v1.split("\\.");
        String[] a2 = v2.split("\\.");
        int len = Math.max(a1.length, a2.length);
        for (int i = 0; i < len; i++) {
            int n1 = i < a1.length ? Integer.parseInt(a1[i]) : 0;
            int n2 = i < a2.length ? Integer.parseInt(a2[i]) : 0;
            if (n1 != n2)
                return n1 - n2;
        }
        return 0;
    }
}