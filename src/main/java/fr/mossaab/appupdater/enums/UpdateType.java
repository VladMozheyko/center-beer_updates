package fr.mossaab.appupdater.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип рекомендации к обновлению")
public enum UpdateType {
    @Schema(description = "Рекомендация к обновлению приложения, текущая версия устарела")
    OPTIONAL("Рекомендация к обновлению приложения, текущая версия устарела"),

    @Schema(description = "Обязательное обновление, в ближайшее время возможно будет приостановлено поддержание текущей версии")
    MANDATORY("Обязательное обновление, в ближайшее время возможно будет приостановлено поддержание текущей версии"),

    @Schema(description = "Версия актуальна, обновление не требуется")
    UP_TO_DATE("Версия актуальна, обновление не требуется");

    private final String message;

    UpdateType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
