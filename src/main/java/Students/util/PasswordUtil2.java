package Students.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Password hashing with PBKDF2-HMAC-SHA256 (preferred) and a transparent
 * migration path from the old SHA-256 unsalted scheme.
 * <p>
 * Stored format: a single string of the form {@code "PBKDF2$<iterations>$<saltBase64>$<hashBase64>"}
 * or, for legacy rows, the bare Base64 SHA-256 hash. The {@code algo} column on
 * {@code user} tells us which verifier to call.
 */
public final class PasswordUtil2 {

    private static final String ALGO_PBKDF2 = "PBKDF2";
    private static final int SALT_LEN = 16;
    private static final int HASH_LEN = 32;          // 256 bits
    private static final int ITERATIONS = 120_000;    // ~100ms on a modern CPU
    private static final String PBKDF2_ALG = "PBKDF2WithHmacSHA256";

    private static final SecureRandom RNG = new SecureRandom();

    private PasswordUtil2() {
    }

    /** Hash a fresh password. Returns a self-describing string. */
    public static String hash(String password) {
        byte[] salt = new byte[SALT_LEN];
        RNG.nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, HASH_LEN);
        return ALGO_PBKDF2 + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verify a password against a stored value. The {@code algo} parameter
     * tells us which hash scheme the stored value uses:
     * <ul>
     *   <li>{@code "PBKDF2"} (or null) - parse the self-describing format.</li>
     *   <li>{@code "SHA256"} (legacy) - compare against the bare Base64 SHA-256 hash.</li>
     * </ul>
     * @return {@code true} if the password matches.
     */
    public static boolean verify(String password, String stored, String algo) {
        if (stored == null || stored.isEmpty()) {
            return false;
        }
        if ("SHA256".equalsIgnoreCase(algo) || !stored.contains("$")) {
            return verifyLegacySha256(password, stored);
        }
        return verifyPbkdf2(password, stored);
    }

    private static boolean verifyPbkdf2(String password, String stored) {
        String[] parts = stored.split("\\$");
        if (parts.length != 4 || !"PBKDF2".equals(parts[0])) {
            return false;
        }
        int iters;
        byte[] salt, expected;
        try {
            iters = Integer.parseInt(parts[1]);
            salt = Base64.getDecoder().decode(parts[2]);
            expected = Base64.getDecoder().decode(parts[3]);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        byte[] actual = pbkdf2(password.toCharArray(), salt, iters, expected.length);
        return constantTimeEquals(expected, actual);
    }

    private static boolean verifyLegacySha256(String password, String stored) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            String encoded = Base64.getEncoder().encodeToString(digest);
            return constantTimeEquals(stored.getBytes(), encoded.getBytes());
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    /**
     * Convenience used by the login flow: if the password matched a legacy
     * SHA-256 hash, return a freshly-hashed PBKDF2 string so the caller can
     * upgrade the row in the same transaction. Returns {@code null} when no
     * upgrade is needed.
     */
    public static String upgradeFromLegacyIfMatched(String password, String stored, String algo) {
        if ("SHA256".equalsIgnoreCase(algo) || (stored != null && !stored.contains("$"))) {
            if (verify(password, stored, algo)) {
                return hash(password);
            }
        }
        return null;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iters, int len) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iters, len * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALG);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 unavailable", e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null || a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
