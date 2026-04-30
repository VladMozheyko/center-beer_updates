![AppUpdater Banner](banner.jfif)
# AppUpdater

**AppUpdater** — это удобный REST сервис для управления и выдачи информации о доступности новых версий мобильных приложений для Android и iOS.  
Используется как центр распространения обновлений: мобильный клиент отправляет свою версию, сервис отвечает ссылками и рекомендациями по необходимости обновления.

---

## Возможности

- Обновление политики версий и ссылок на магазины (админ эндпоинт)
- Получение информации о необходимости/обязательности обновления для мобильного приложения
- **Swagger/OpenAPI** для удобного тестирования и документации
- Защита админских методов с помощью API Key
- Валидация всех входных данных
- Качественное автоматическое логирование контроллеров (AOP + Slf4j)
- Гибкая архитектура на Spring Boot + JPA MySQL

---

## Технологии

- Java 21
- Spring Boot 3+
- Spring Web / Data JPA
- Hibernate/MySQL
- Lombok
- Swagger (springdoc-openapi)
- AOP (AspectJ)
- Slf4j (логирование)
- Jakarta Validation (`@Valid`, `@NotBlank`, `@Pattern`)
- HandlerInterceptor

---

## Архитектура решения

- **Контроллеры** принимают входные параметры / запросы, валидируют их и возвращают строго типизированный DTO-ответ.
- **Сервисы** реализуют бизнес-логику сравнения версий, взаимодействуют с БД через JPA Repository.
- **Логирование**:
    - Входящие запросы/результаты контроллеров логируются через AOP-аспект (`RestControllerLoggingAspect`).
    - Ошибки/исключения логируются на уровне глобального обработчика.
- **API Key**:
    - Все POST-эндпоинты с изменением данных защищены — требуют заголовок `X-API-Key` (ключ хранится в настройках или базе).
    - Проверяется через HandlerInterceptor.
- **Валидация**:
    - Строгая — поля DTO c аннотациями `@NotBlank`, `@Pattern` и т.п., с возвратом подробных ошибок (400 Bad Request).
- **Swagger**:
    - Описаны все методы, параметры и схемы данных, настроена схема безопасности API Key.

---

## Быстрый старт

### 1. Склонируйте репозиторий

    git clone https://github.com/yourorg/appupdater.git
    cd appupdater

### 2. Настройте подключение к БД

Измените файл `application.properties` под ваши параметры MySQL:

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/app_updater?serverTimezone=Europe/Moscow
spring.datasource.username=YOUR_USER
spring.datasource.password=YOUR_PASSWORD
```

### 3. Сконфигурируйте API KeyВ
```properties
application.properties:app.x-api-key=YOUR-SUPER-SECRET-KEY
```

### 4. Соберите и запустите проект

    ./mvnw clean package
    java -jar target/appupdater-*.jar

Swagger-интерфейсSwagger UI автоматически доступен по адресу:
http://localhost:8080/swagger-ui/index.html

Для админских эндпоинтов (POST /update-config/create) кликните Authorize и введите X-API-Key.

Получить информацию о необходимости обновления (для приложения)

    GET /update-config/get/{platform}

Обновить или создать параметры обновления (админ):

    POST /update-config/create

Логирование
Контроллеры полностью логируются аспектом (RestControllerLoggingAspect):

Ошибки логируются глобальным Handler'ом

Валидация и обработка ошибок
Используется @Valid, @NotBlank, @Pattern и т.д. для DTO

Все ошибки валидации и constraint-исключения попадают в красивый структурированный ответ с code и читаемым описанием

Любые неожиданные ошибки дают 500 с сообщением

Ключ должен передаваться в заголовке X-API-Key
Доступ для обычной проверки обновлений доступен всем (GET не требует ключа).