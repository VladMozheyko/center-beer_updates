package fr.mossaab.appupdater.entity;

import fr.mossaab.appupdater.enums.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "update_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "платформа iOS или ANDROID")
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private Platform platform;

    @Schema(description = "Самая свежая версия (на нее рекомендуем обновиться)")
    @Column(name = "latest_version", nullable = false)
    private String latestVersion;

    @Schema(description = "Граница ниже которой обновление обязательно")
    @Column(name = "force_update_version", nullable = false)
    private String forceUpdateVersion;

    @Schema(description = "Ссылки на магазин GOOGLE PLAY (может быть пустой, если платформа не поддерживает)")
    @Column(name = "google_play_url")
    private String googlePlayUrl;

    @Schema(description = "Ссылки на магазин RU STORE (может быть пустой, если платформа не поддерживает)")
    @Column(name = "rustore_url")
    private String ruStoreUrl;

    @Schema(description = "Ссылки на магазин APP STORE (может быть пустой, если платформа не поддерживает)")
    @Column(name = "app_store_url")
    private String appStoreUrl;

    @Schema(description = "Дата создания")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(description = "Дата выхода обновления")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
