package Utilities;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class CreateJwtToken {

    public static String createJwtHMAC(String clientId, String key) {

        byte[] decodedKey = Base64.getMimeDecoder().decode(key);

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("client-id", clientId);
        String token = Jwts.builder().setIssuedAt(new Date()).addClaims(claims)
                .signWith(SignatureAlgorithm.HS512, decodedKey).compact();
        return token;
    }

    public static String createJwtHMACHS256(String clientId, String key) {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("clientId", clientId);
        String token = Jwts.builder().setIssuedAt(new Date()).addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key.getBytes(StandardCharsets.UTF_8)).compact();
        return token;
    }




    public static String createJwtHMAC_test(String clientId, String key) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        long millisCurrent = date.getTime();

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(date); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 2); // adds one hour
        date = cal.getTime();

        long millisLater = date.getTime();

        byte[] decodedKey = Base64.getMimeDecoder().decode(key);

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("client-id", clientId);

        String token = Jwts.builder().setIssuedAt(Date.from(Instant.ofEpochSecond(millisCurrent)))
                .setExpiration(Date.from(Instant.ofEpochSecond(millisLater))).addClaims(claims)
                .signWith(SignatureAlgorithm.HS512, decodedKey).compact();
        return token;
    }
}
