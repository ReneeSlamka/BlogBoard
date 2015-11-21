package com.blogboard.server.service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.io.IOException;

public class AppServiceHelper {

    //TODO: is it safe for these security methods to be public?
    public static String generateSessionID() {
        Random randomNumberGenerator = new Random();
        int randomInt = randomNumberGenerator.nextInt(100);
        String sessionId = "ABC" + String.valueOf(randomInt);

        return sessionId;
    }

    public static String hashString(String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            //TODO: need to somehow return an error to client?
            System.out.println("Hashing function failed to hash password");
        }
        return password;
    }

    public static void configureCookie(Cookie cookie, int maxAge, String path, boolean httpOnly, boolean isSecure) {
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(isSecure);
    }

    public static void configureHttpError(HttpServletResponse httpResponse,  int status, String message) {
        try {
            httpResponse.sendError(status, message);
        } catch (IOException ex) {
            System.out.println (ex.toString());
            System.out.println("Internet connection was closed before error message could be sent");
        }
    }

    public static String createTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String timeStamp = dateFormat.format(calendar.getTime());
        return timeStamp;
    }

    public static void parseTimeStamp(String timeStamp) {
        String[] timeValues = timeStamp.split("/");
        int numMinutes;
        if (timeValues[5] != null) {
            numMinutes = Integer.parseInt(timeValues[5]);
        }

    }
}
