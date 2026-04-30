package fr.mossaab.appupdater.dto;

import fr.mossaab.appupdater.enums.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateConfigRequest {
    @Schema(description = "платформа iOS или ANDROID")
    @NotNull(message = "Платформа не должна быть null")
    private Platform platform;

    @Schema(description = "Самая свежая версия (на нее рекомендуем обновиться)", example="1.2.3")
    @NotBlank(message = "lastVersion обязательна")
    @Pattern(
            regexp = "^[0-9]+(\\.[0-9]+){2}$",
            message = "latestVersion должна быть в формате 0.0.0"
    )
    private String latestVersion;

    @Schema(description = "Граница ниже которой обновление обязательно", example="1.0.0")
    @NotBlank(message = "forceUpdateVersion обязательно")
    @Pattern(
            regexp = "^[0-9]+(\\.[0-9]+){2}$",
            message = "forceUpdateVersion должна быть в формате 0.0.0"
    )
    private String forceUpdateVersion;

    @Schema(description = "Ссылка на Google Play (если есть)", example="https://play.google.com/store/apps/details?id=yourapp")
    private String googlePlayUrl;

    @Schema(description = "Ссылка на RuStore (если есть)", example="https://apps.rustore.ru/app/yourapp")
    private String ruStoreUrl;

    @Schema(description = "Ссылка на App Store (если есть)", example="https://apps.apple.com/app/idxxxxxxxx")
    private String appStoreUrl;
}