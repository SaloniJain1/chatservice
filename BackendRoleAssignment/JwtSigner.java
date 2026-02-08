import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class JwtSigner {
    public static void main(String[] args) throws Exception {
        String privateKeyContent = new String(Files.readAllBytes(Paths.get("private.pem")))
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        // Note: java.security expects PKCS#8. OpenSSL genrsa gives PKCS#1.
        // I already converted it to private.der (PKCS#8 DER) in Step 187.
        byte[] keyBytes = Files.readAllBytes(Paths.get("private.der"));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        String header = base64UrlEncode("{\"alg\":\"RS256\",\"typ\":\"JWT\"}");
        String payload = base64UrlEncode("{\"sub\":\"test-user\",\"exp\":1801944000}"); // Feb 7 2027
        String data = header + "." + payload;

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        byte[] signed = signature.sign();

        String jwt = data + "." + base64UrlEncode(signed);
        System.out.println(jwt);
    }

    private static String base64UrlEncode(String input) {
        return base64UrlEncode(input.getBytes());
    }

    private static String base64UrlEncode(byte[] input) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(input);
    }
}
