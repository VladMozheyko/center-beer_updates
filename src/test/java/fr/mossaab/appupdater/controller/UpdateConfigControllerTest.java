package fr.mossaab.appupdater.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mossaab.appupdater.dto.UpdateConfigRequest;
import fr.mossaab.appupdater.dto.UpdateConfigResponse;
import fr.mossaab.appupdater.entity.UpdateConfig;
import fr.mossaab.appupdater.enums.Platform;
import fr.mossaab.appupdater.enums.UpdateType;
import fr.mossaab.appupdater.hendlers.ApiKeyInterceptor;
import fr.mossaab.appupdater.service.UpdateConfigService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UpdateConfigController.class)
@DisplayName("UpdateConfigController – Тесты REST эндпоинтов")
class UpdateConfigControllerTest {
    @Autowired MockMvc mockMvc;

    @MockitoBean UpdateConfigService service;
    ObjectMapper objectMapper = new ObjectMapper();


    @Nested
    @DisplayName("POST /update-config/create")
    class CreateUpdateEndpoint {
        @MockitoBean ApiKeyInterceptor apiKeyInterceptor;

        @Test
        @DisplayName("Должен вернуть 200 и тело ответа, если параметры запроса валидны")
        void createUpdate_validRequest_returns200() throws Exception {
            when(apiKeyInterceptor.preHandle(any(), any(), any())).thenReturn(true);
            UpdateConfigRequest req = UpdateConfigRequest.builder()
                    .platform(Platform.IOS)
                    .latestVersion("2.1.0")
                    .forceUpdateVersion("2.0.0")
                    .appStoreUrl("https://apps.apple.com/app/test")
                    .build();

            when(service.createOrUpdate(any(UpdateConfigRequest.class))).thenReturn(
                    UpdateConfig.builder()
                            .platform(Platform.IOS)
                            .latestVersion("2.1.0")
                            .forceUpdateVersion("2.0.0")
                            .appStoreUrl("https://apps.apple.com/app/test")
                            .build()
            );

            mockMvc.perform(post("/update-config/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.platform").value("IOS"))
                    .andExpect(jsonPath("$.latestVersion").value("2.1.0"))
                    .andExpect(jsonPath("$.message").value(containsString("Конфигурация сохранена")));
        }

        @Test
        @DisplayName("Должен вернуть 400 BadRequest, если параметры некорректны")
        void createUpdate_invalidRequest_returns400() throws Exception {
            when(apiKeyInterceptor.preHandle(any(), any(), any())).thenReturn(true);
            UpdateConfigRequest req = UpdateConfigRequest.builder()
                    .platform(null) // Не задана платформа — обязательное поле
                    .latestVersion("invalid_version")
                    .forceUpdateVersion("")
                    .build();

            mockMvc.perform(post("/update-config/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", containsString("VALIDATION_ERROR")));
        }
    }


    @Nested
    @DisplayName("GET /update-config/get/{platform}")
    class GetUpdateConfigEndpoint {

        @Test
        @DisplayName("Должен вернуть 200 и корректный ответ, если конфиг существует")
        void getUpdateConfig_found_returns200() throws Exception {
            when(service.get(eq(Platform.ANDROID), eq("1.2.3")))
                    .thenReturn(Optional.of(UpdateConfigResponse.builder()
                            .platform(Platform.ANDROID)
                            .latestVersion("2.5.0")
                            .forceUpdateVersion("2.2.0")
                            .googlePlayUrl("https://play.google.com/store/apps/details?id=test")
                            .updateType(UpdateType.OPTIONAL)
                            .message("Рекомендуем обновиться")
                            .build()));

            mockMvc.perform(get("/update-config/get/ANDROID?version=1.2.3"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.platform").value("ANDROID"))
                    .andExpect(jsonPath("$.updateType").value("OPTIONAL"))
                    .andExpect(jsonPath("$.message").value("Рекомендуем обновиться"));
        }

        @Test
        @DisplayName("Должен вернуть 404 NOT_FOUND, если конфиг для платформы не найден")
        void getUpdateConfig_notFound_returns404() throws Exception {
            when(service.get(eq(Platform.IOS), eq("1.0.0"))).thenReturn(Optional.empty());

            mockMvc.perform(get("/update-config/get/IOS?version=1.0.0"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message", containsString("Не найдена конфигурация")));
        }

        @Test
        @DisplayName("Должен вернуть 400 BadRequest, если версия не по паттерну")
        void getUpdateConfig_invalidVersion_returns400() throws Exception {
            mockMvc.perform(get("/update-config/get/ANDROID?version=not-a-version"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", containsString("CONSTRAINT_VIOLATION")));
        }
    }
}
