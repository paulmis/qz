package client.utils.communication;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to encrypt and decrypt messages.
 */
@Slf4j
public class EncryptionUtils {
    public static final String ENCRYPTION_KEY = "HJphLjJMDeGmuRHY";

    /**
     * Get the key spec (secret key) for the encryption.
     *
     * @param key key to use
     * @return key spec
     * @throws NoSuchAlgorithmException if algorithm is not found (should never happen)
     */
    private static SecretKeySpec getKey(final String key) throws NoSuchAlgorithmException {
        // Get key bytes
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        // Calculate SHA-1 hash (it's weak, but good enough for this purpose)
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        keyBytes = sha.digest(keyBytes);
        // Get first 16 bytes (AES key size)
        keyBytes = Arrays.copyOf(keyBytes, 16);

        // Return SecretKeySpec
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Encrypt a string using AES (ECB) encryption.
     * Horribly insecure, but... good enough.
     *
     * @param strToEncrypt string to encrypt
     * @param key key to encrypt with
     * @return encrypted string (Base64 encoded)
     */
    public static String encrypt(final String strToEncrypt, final String key) {
        try {
            SecretKeySpec keySpec = getKey(key);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("Error while encrypting: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Decrypt a string using AES (ECB) encryption.
     * Horribly insecure, but... good enough.
     *
     * @param strToDecrypt string to decrypt (Base64 encoded)
     * @param key key to decrypt with
     * @return decrypted string
     */
    public static String decrypt(final String strToDecrypt, final String key) {
        try {
            SecretKeySpec keySpec = getKey(key);

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            log.error("Error while decrypting: {}", e.getMessage());
            return null;
        }
    }
}
