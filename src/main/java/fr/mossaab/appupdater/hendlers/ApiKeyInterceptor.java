package fr.mossaab.appupdater.hendlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${app.x-api-key}")
    private String secretKey;

    private static final String HEADER = "X-API-Key";

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull Object handler) throws IOException {
        String apiKey = request.getHeader(HEADER);
        if (apiKey == null || !apiKey.equals(secretKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized. Ключ API неверен или отсутствует.\"}");
            return false;
        }
        return true;
    }
}
