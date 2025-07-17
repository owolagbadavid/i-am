package me.oreos.iam.services.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wakanda.framework.exception.BaseException;
import org.wakanda.framework.response.dto.ResponseDTO;
import org.wakanda.framework.response.enums.ResponseType;
import org.wakanda.framework.response.helper.ResponseHelper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Helper {
    public static String getClientIp(javax.servlet.http.HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            // In case of multiple IPs in header, take the first one
            ip = ip.split(",")[0];
        }
        return ip;
    }

    public static JSONObject getGeoLocation(String ip) throws IOException {
        String urlString = "https://ipapi.co/" + ip + "/json/";
        URI uri = URI.create(urlString);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return new JSONObject(response.toString());
    }

    // String userAgentString = request.getHeader("User-Agent");
    // UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
    // OperatingSystem os = userAgent.getOperatingSystem();
    // Browser browser = userAgent.getBrowser();

    public static <T> ResponseEntity<ResponseDTO<T>> errorHandler(Exception e) {
        return errorHandler(e, false);
    }

    public static <T> ResponseEntity<ResponseDTO<T>> errorHandler(Exception e, boolean showStackTrace) {
        var responseHelper = new ResponseHelper<T>();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ResponseType responseType = ResponseType.UNKNOWN_ERROR;
        String message = e.getMessage();

        if (e instanceof BaseException) {
            BaseException baseException = (BaseException) e;
            switch (baseException.getCode()) {
                case 400:
                    status = HttpStatus.BAD_REQUEST;
                    responseType = ResponseType.BAD_REQUEST;
                    break;
                case 401:
                    status = HttpStatus.UNAUTHORIZED;
                    responseType = ResponseType.FORBIDDEN;
                    break;
                case 403:
                    status = HttpStatus.FORBIDDEN;
                    responseType = ResponseType.FORBIDDEN;
                    break;
                case 404:
                    status = HttpStatus.NOT_FOUND;
                    responseType = ResponseType.NOT_FOUND;
                    break;
                case 409:
                    status = HttpStatus.CONFLICT;
                    responseType = ResponseType.CONFLICT;
                    break;
                case 500:
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    responseType = ResponseType.UNKNOWN_ERROR;
                    message = "An unexpected error occurred" + (showStackTrace ? ": " + e.getMessage() : "");
                    break;
                default:
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    responseType = ResponseType.UNKNOWN_ERROR;
                    message = "An unexpected error occurred" + (showStackTrace ? ": " + e.getMessage() : "");
                    break;
            }
        }
        return responseHelper.error(
                status,
                responseType,
                message);
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    // For non-generic classes
    public static <T> T mapToModel(Map<String, Object> map, Class<T> clazz) {
        return mapper.convertValue(map, clazz);
    }

    // For generic types like List<T>, Map<K, V>, etc.
    public static <T> T mapToModel(Object value, TypeReference<T> typeRef) {
        return mapper.convertValue(value, typeRef);
    }

}
