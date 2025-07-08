package me.oreos.iam.services.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

import org.json.JSONObject;

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

}
