package com.blogboard.server.service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.io.IOException;
import java.util.HashMap;

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

    public static String createTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String timeStamp = dateFormat.format(calendar.getTime());
        return timeStamp;
    }

    public static HashMap<String, Integer> parseTimeStamp(String timeStamp) {
        String[] values = timeStamp.split(" ");
        String[] dateValues = values[0].split("/");
        String[] timeValues = values[1].split(":");

        HashMap<String, Integer> numericDateValues = new HashMap<String, Integer>();
        numericDateValues.put("years", new Integer(dateValues[0]));
        numericDateValues.put("months", new Integer(dateValues[1]));
        numericDateValues.put("days", new Integer(dateValues[2]));
        numericDateValues.put("hours", new Integer(timeValues[0]));
        numericDateValues.put("minutes", new Integer(timeValues[1]));
        numericDateValues.put("seconds", new Integer(timeValues[2]));

        return numericDateValues;
    }

    public static String decodeString(String encodedString) {
        String decodedString;
        try {
            decodedString = URLDecoder.decode(encodedString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");
        }
        return decodedString;
    }


    //checks time stamp against current time, if diff >= 30 returns true
    //currently only works for timestamps in same month
    public static boolean validateExpirationTime(String timeStamp, String currentTime) {

        HashMap<String,Integer> timeStampValues = AppServiceHelper.parseTimeStamp(timeStamp);
        HashMap<String,Integer> currentTimeValues = AppServiceHelper.parseTimeStamp(currentTime);

        Integer timeStampMinutes = (timeStampValues.get("hours")*60) + timeStampValues.get("minutes");
        Integer currentTimeMinutes = (currentTimeValues.get("hours")*60) + currentTimeValues.get("minutes");

        if (currentTimeMinutes - timeStampMinutes >= 30) {
            return true;
        } else if ((currentTimeValues.get("days") - timeStampValues.get("days") > 0)) {
            if ((currentTimeValues.get("days") - timeStampValues.get("days") == 1)) {
                if (!(timeStampValues.get("hours") == 23 && currentTimeValues.get("hours") == 0)) {
                    return true;
                }
            }
        }
        return false;
    }
}
