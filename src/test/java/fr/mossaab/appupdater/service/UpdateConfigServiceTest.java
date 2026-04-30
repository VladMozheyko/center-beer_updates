package fr.mossaab.appupdater.service;

import fr.mossaab.appupdater.dto.UpdateConfigRequest;
import fr.mossaab.appupdater.dto.UpdateConfigResponse;
import fr.mossaab.appupdater.entity.UpdateConfig;
import fr.mossaab.appupdater.enums.Platform;
import fr.mossaab.appupdater.enums.UpdateType;
import fr.mossaab.appupdater.repository.UpdateConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
@DisplayName("Юнит-тесты для UpdateConfigService: обновление и выдача версий приложения")
class UpdateConfigServiceTest {

    @InjectMocks
    private UpdateConfigService updateConfigService;

    @Mock
    private UpdateConfigRepository repository;

    @Test
    @DisplayName("createOrUpdate: при существующей сущности — обновляет параметры и возвращает обновлённую запись")
    public void createOrUpdate_entityExists_updatesAndReturnsUpdatedConfig() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowWithHour = now.withHour(10);

        UpdateConfig updateConfigOld = UpdateConfig.builder()
                .platform(Platform.ANDROID)
                .latestVersion("0.0.25")
                .forceUpdateVersion("0.0.5")
                .createdAt(now)
                .updatedAt(nowWithHour)
                .build();

        UpdateConfig updateConfigNew = UpdateConfig.builder()
                .platform(Platform.ANDROID)
                .latestVersion("1.2.3")
                .forceUpdateVersion("1.0.0")
                .createdAt(now)
                .updatedAt(nowWithHour)
                .build();

        UpdateConfigRequest req = UpdateConfigRequest.builder()
                .platform(Platform.ANDROID)
                .latestVersion("1.2.3")
                .forceUpdateVersion("1.0.0")
                .build();

        Mockito.when(repository.findUpdateConfigByPlatform(Platform.ANDROID)).thenReturn(Optional.of(updateConfigOld));
        Mockito.when(repository.save(any(UpdateConfig.class))).thenReturn(updateConfigNew);

        UpdateConfig uc = updateConfigService.createOrUpdate(req);

        assertEquals(updateConfigNew.getLatestVersion(), uc.getLatestVersion());
        assertEquals(updateConfigNew.getForceUpdateVersion(), uc.getForceUpdateVersion());
        assertEquals(updateConfigNew.getPlatform().name(), uc.getPlatform().name());
    }

    @Test
    @DisplayName("createOrUpdate: если сущности нет — создает новую конфигурацию и возвращает её")
    public void createOrUpdate_entityNotExist_createsAndReturnsNewConfig() {
        LocalDateTime now = LocalDateTime.now();

        UpdateConfigRequest req = UpdateConfigRequest.builder()
                .platform(Platform.IOS)
                .latestVersion("2.5.0")
                .forceUpdateVersion("2.0.0")
                .googlePlayUrl(null)
                .ruStoreUrl(null)
                .appStoreUrl("https://apps.apple.com/app/test")
                .build();

        UpdateConfig newConfig = UpdateConfig.builder()
                .platform(Platform.IOS)
                .latestVersion("2.5.0")
                .forceUpdateVersion("2.0.0")
                .createdAt(now)
                .updatedAt(now)
                .appStoreUrl("https://apps.apple.com/app/test")
                .build();

        Mockito.when(repository.findUpdateConfigByPlatform(Platform.IOS)).thenReturn(Optional.empty());
        Mockito.when(repository.save(any(UpdateConfig.class))).thenReturn(newConfig);

        UpdateConfig created = updateConfigService.createOrUpdate(req);

        assertEquals("2.5.0", created.getLatestVersion());
        assertEquals("2.0.0", created.getForceUpdateVersion());
        assertEquals(Platform.IOS, created.getPlatform());
        assertEquals("https://apps.apple.com/app/test", created.getAppStoreUrl());
    }

    @Test
    @DisplayName("get: если конфиг отсутствует — возвращает Optional.empty()")
    public void get_configNotExists_returnsEmptyOptional() {
        Mockito.when(repository.findUpdateConfigByPlatform(Platform.IOS)).thenReturn(Optional.empty());
        Optional<UpdateConfigResponse> response = updateConfigService.get(Platform.IOS, "1.0.0");
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("get: при наличии конфига возвращает корректную информацию о необходимости обновления")
    public void get_configExists_returnsValidUpdateConfigResponse() {
        UpdateConfig config = UpdateConfig.builder()
                .platform(Platform.ANDROID)
                .forceUpdateVersion("1.5.0")
                .latestVersion("2.0.0")
                .googlePlayUrl("https://play.google.com/test")
                .build();

        Mockito.when(repository.findUpdateConfigByPlatform(Platform.ANDROID)).thenReturn(Optional.of(config));

        Optional<UpdateConfigResponse> response = updateConfigService.get(Platform.ANDROID, "1.0.0");

        assertTrue(response.isPresent());
        assertEquals(UpdateType.MANDATORY, response.get().getUpdateType());
        assertEquals("https://play.google.com/test", response.get().getGooglePlayUrl());
    }

    @Test
    @DisplayName("compareVersions: корректно сравнивает версии (равные, большие, меньшие)")
    public void compareVersions_varietyOfVersions_returnsIntComparison() {
        assertEquals(0, updateConfigService.compareVersions("1.0.0", "1.0.0"));
        assertTrue(updateConfigService.compareVersions("1.1.0", "1.0.9") > 0);
        assertTrue(updateConfigService.compareVersions("1.0.9", "1.1.0") < 0);
        assertTrue(updateConfigService.compareVersions("1.0.0", "1.0.1") < 0);
        assertTrue(updateConfigService.compareVersions("2.0.0", "1.9.9") > 0);
    }

    @Test
    @DisplayName("getUpdateTypeByVersions: возвращает корректный тип обновления в зависимости от порядка версий")
    public void getUpdateTypeByVersions_variousCases_returnsCorrectUpdateType() {
        // userVersion < forceUpdateVersion -> MANDATORY
        assertEquals(UpdateType.MANDATORY, updateConfigService.getUpdateTypeByVersions("2.0.0", "3.0.0", "1.5.0"));
        // userVersion < latestVersion -> OPTIONAL
        assertEquals(UpdateType.OPTIONAL, updateConfigService.getUpdateTypeByVersions("2.0.0", "3.0.0", "2.5.0"));
        // userVersion >= latestVersion -> UP_TO_DATE
        assertEquals(UpdateType.UP_TO_DATE, updateConfigService.getUpdateTypeByVersions("2.0.0", "3.0.0", "3.0.0"));
        assertEquals(UpdateType.UP_TO_DATE, updateConfigService.getUpdateTypeByVersions("2.0.0", "3.0.0", "3.1.0"));
    }
}