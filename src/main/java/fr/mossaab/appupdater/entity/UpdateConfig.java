package fr.mossaab.appupdater.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "update_config")
public class UpdateConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // iOS или ANDROID для разделения записей
    @Column(name = "platform", nullable = false)
    @Enumerated(EnumType.STRING)
    private Platform platform;

    // Самая свежая версия (на нее рекомендуем обновиться)
    @Column(name = "latest_version", nullable = false)
    private String latestVersion;

    // Граница ниже которой обновление обязательно
    @Column(name = "force_update_version", nullable = false)
    private String forceUpdateVersion;

    // Ссылки на магазины (null для чужой платформы)
    @Column(name = "google_play_url")
    private String googlePlayUrl;

    @Column(name = "rustore_url")
    private String ruStoreUrl;

    @Column(name = "app_store_url")
    private String appStoreUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Platform {
        ANDROID,
        IOS
    }

    public UpdateConfig() {}

    public UpdateConfig(Platform platform, String latestVersion, String forceUpdateVersion,
                        String googlePlayUrl, String ruStoreUrl, String appStoreUrl) {
        this.platform = platform;
        this.latestVersion = latestVersion;
        this.forceUpdateVersion = forceUpdateVersion;
        this.googlePlayUrl = googlePlayUrl;
        this.ruStoreUrl = ruStoreUrl;
        this.appStoreUrl = appStoreUrl;
        this.createdAt = LocalDateTime.now();
    }

    // getters and setters...
}
