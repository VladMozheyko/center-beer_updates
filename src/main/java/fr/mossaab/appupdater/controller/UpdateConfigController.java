package fr.mossaab.appupdater.controller;

import fr.mossaab.appupdater.dto.UpdateConfigRequest;
import fr.mossaab.appupdater.dto.UpdateConfigResponse;
import fr.mossaab.appupdater.enums.Platform;
import fr.mossaab.appupdater.service.UpdateConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/update-config")
@RequiredArgsConstructor
@Tag(name = "Управление версиями приложений")
public class UpdateConfigController {
    private final UpdateConfigService service;

    @Operation(summary = "Создать или обновить конфигурацию обновления")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфигурация создана/обновлена",
                    content = @Content(schema = @Schema(implementation = UpdateConfigResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации запроса",
                    content = @Content(schema = @Schema(example = "{\"error\": \"Validation failed\", \"details\": \"...\"}")))
    })
    @PostMapping("/create")
    public ResponseEntity<UpdateConfigResponse> createUpdate(@Valid @RequestBody UpdateConfigRequest dto) {
        System.out.println("тут========================");
        var saved = service.createOrUpdate(dto);
        var response = UpdateConfigResponse.builder()
                .platform(saved.getPlatform())
                .latestVersion(saved.getLatestVersion())
                .forceUpdateVersion(saved.getForceUpdateVersion())
                .googlePlayUrl(saved.getGooglePlayUrl())
                .ruStoreUrl(saved.getRuStoreUrl())
                .appStoreUrl(saved.getAppStoreUrl())
                .updateType(null)
                .message("Конфигурация сохранена")
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получить описание обновления для клиента",
            parameters = {
                    @Parameter(name = "platform", description = "ANDROID или IOS", required = true),
                    @Parameter(name = "version", description = "Текущая версия клиента, строка в формате 0.0.0", example = "1.0.0", required = true)
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о необходимости обновления/ссылки",
                    content = @Content(schema = @Schema(implementation = UpdateConfigResponse.class))),
            @ApiResponse(responseCode = "404", description = "Конфигурация платформы не найдена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации версии")
    })
    @GetMapping("/get/{platform}")
    public ResponseEntity<?> getUpdateConfig(
            @PathVariable Platform platform,
            @RequestParam @Pattern(regexp = "^[0-9]+(\\.[0-9]+){2}$", message = "Версия должна соответствовать строке 0.0.0") String version
    ) {
        Optional<UpdateConfigResponse> dto = service.get(platform, version);
        return dto.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ErrorResponse("NOT_FOUND", "Не найдена конфигурация обновлений для платформы: " + platform)
                ));
    }

    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private String code;
        private String message;
    }
}
