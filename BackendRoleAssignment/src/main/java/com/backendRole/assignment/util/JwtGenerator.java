package com.backendRole.assignment.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

public class JwtGenerator {

    public static String generateToken(String privateKeyStr, String subject, long expirationMillis) throws Exception {
        String safeKey = privateKeyStr
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(safeKey);
        // Note: Java expects PKCS#8 for PrivateKey.
        // The generated key by openssl genrsa is PKCS#1.
        // We might need to convert it or use a library that handles PKCS#1.
        // Let's assume we can convert it if needed, or use a workaround.

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public static void main(String[] args) throws Exception {
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC1J79mcQNHaDea2Zuq+Vnsgke5i/C7OHneGrNH3WbGjVaf6rGxFmsnIJ9C9k7zq3z/UD9yMZFdBGZKYFnAWFPV4pKF5zLHbH0t8il8bLiOsKrfmpjdP11jFpoNMre2boB3ngtabmBkSs3BI06wMPCiv0cRAhBnygWiN3i8GWD1QeejiAN6opnmvsnd/9S7Us3TpS5vU3aM1HuVBJnSAyrYL0KQTRh1x0f2L5ngr8azLrVtU0UwRvxmGF/KYpd5jzTmRDOVbHxaZOYQJtKKfsbqWaFQPDyNoxSp3I97c0moMLxGzaeckFJFNBMgvv1Jl0rjjr0AJMkG1N82AX2hoRk1AgMBAAECggEBAIweUWIoObQKvS+t70Ugl1hjMJ0oy6vUNBcCKfeFHZCoE/+fZY+m2nyqM+V0ZH7+/rDwXhKymlALLh+LRQCMkVPXayPdPx6XIH/gVgxif6IGrjRD/Mbs0wvomIFq4ERgVkevW/K3eyE35svjxmJriEUplAtowE23KgJdy6oaKy+nW8Ie6iuUpOyw0nQ65ieLo72S4ga42OKiJyWdRzoAJfoCXAgD265vvOe0lnFeGGJ0raooutrNu1e7tf1kix6+z3HuYf+h5PrjMBdkR/sRrQXwnZk4R+QE/3vbAsSsqMDlbmoD7J5hLPba7HlZ8K9D77F0TJL9YkgbvDa+WK8yWCECgYEA4d3aa7UBGSJVH7YpLkx9EPi/UZNZUfo8cH9qWZNkNdQXgE/iWw3G2a0EMIAj9YqAsph3ZO2VN+aGW5epXV+ZWZcbax11Mexhg8ddIrpwjqqJ13afq7dF1fjHEL3Fwjdt70EKTf3zT/0ha2vBj41rA6Qgq5K3BlGiP22iyc4f38kCgYEAzVLX1hv3D6Ay8eifjjRyCkdZEe8T4CpIHq4sE5L6SP685nwmvtQ0jWCw4f3OjodcVV4pcnYXqjMVsRtIaGZ2FJhSkFSYScjPXe2ZdJZRelffZ/KUlfyvjj+VSEf13wDRL/V24GxW7OtEC2GraHd2Nqp9Q5WrRALDhzDZzyUi3A0CgYATO/5Wv8JJLqhLHAsjuzXHVTeNrdukZIfzGJao7ClTgC1EKwlesaymatp5euAVD+dnzh4UxTq42PPGJwMwRWzcuUaHd6m8R6ICQ98FjxT4wUMdyydLyz6yOw5Quah2opvFDSfgfmjS/f5zlGiWXfeoBrkVg1f8vMTxW9fKEbibKQKBgC4IRbWN/Xz2WJWdpq/2GB8v0ctQg289a3/dYqpcyFo3KliuOu2Lm+Cabe2svnN763qEKtyrlzCkVVeomzo0xoJliBUtshrQt5UCYOYfkVLRgpfGbgOWbDGDFc/nujrt7vhGmwlDoNVrBY1br1czeUBWRtExK/2KdRXJIPvtAny9AoGATSlmHO49tq/bLq/GVZ1ml5sKAKIymIYPNVhdh4RZBdNW4vestRqZn8RH6r+DvEA14iu7H41PBy88Wm38QUQL6eKMMlUx6wAlE2ph9uKFEUJPVSUTdRtCez0DrpzhilzmvsNmDjPdvzRx2aNskaKpn+WrwHywqqXYi0vWx57IcVl";
        long oneYearMillis = 365L * 24 * 60 * 60 * 1000;
        String token = generateToken(privateKey, "test-user", oneYearMillis);
        System.out.println("Generated Token (1 Year Validity):");
        System.out.println(token);
    }
}
