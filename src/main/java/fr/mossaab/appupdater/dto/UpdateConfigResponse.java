package fr.mossaab.appupdater.dto;

import fr.mossaab.appupdater.enums.Platform;
import fr.mossaab.appupdater.enums.UpdateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateConfigResponse {
    @Schema(description = "платформа iOS или ANDROID")
    private Platform platform;

    @Schema(description = "Самая свежая версия (на нее рекомендуем обновиться)")
    private String latestVersion;

    @Schema(description = "Граница ниже которой обновление обязательно")
    private String forceUpdateVersion;

    @Schema(description = "Ссылки на магазин GOOGLE PLAY (может быть пустой, если платформа не поддерживает)")
    private String googlePlayUrl;

    @Schema(description = "Ссылки на магазин RU STORE (может быть пустой, если платформа не поддерживает)")
    private String ruStoreUrl;

    @Schema(description = "Ссылки на магазин APP STORE (может быть пустой, если платформа не поддерживает)")
    private String appStoreUrl;

    @Schema(description = "Тип рекомендации к обновлению")
    private UpdateType updateType;

    @Schema(description = "Сообщение с рекомендациями по обновлению")
    private String message;
}