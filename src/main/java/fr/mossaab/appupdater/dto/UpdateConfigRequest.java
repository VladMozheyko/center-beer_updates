package fr.mossaab.appupdater.dto;

import fr.mossaab.appupdater.enums.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateConfigDto {

    @Schema(description = "платформа iOS или ANDROID")
    @Column(name = "platform", nullable = false)
    @Enumerated(EnumType.STRING)
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
}
