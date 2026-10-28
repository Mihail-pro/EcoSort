package md.ecosort.ecosort.security;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

// Hash Argon2id în format PHC: $argon2id$v=19$m=<KiB>,t=<iterații>,p=<paralelism>$<sare>$<hash>
// Parametrii sunt salvați în hash, deci pot fi crescuți ulterior fără a invalida parolele existente.
public final class PasswordHasher {

    // Recomandarea OWASP pentru Argon2id: m = 19 MiB, t = 2, p = 1.
    private static final int MEMORIE_KIB = 19 * 1024;
    private static final int ITERATII = 2;
    private static final int PARALELISM = 1;
    private static final int LUNGIME_SARE = 16;
    private static final int LUNGIME_HASH = 32;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder B64 = Base64.getEncoder().withoutPadding();
    private static final Base64.Decoder B64D = Base64.getDecoder();

    private PasswordHasher() { }

    public static String hash(char[] parola) {
        byte[] sare = new byte[LUNGIME_SARE];
        RANDOM.nextBytes(sare);
        byte[] h = calculeaza(parola, sare, MEMORIE_KIB, ITERATII, PARALELISM, LUNGIME_HASH);
        return "$argon2id$v=19$m=" + MEMORIE_KIB + ",t=" + ITERATII + ",p=" + PARALELISM
                + "$" + B64.encodeToString(sare) + "$" + B64.encodeToString(h);
    }

    // Întoarce false (nu aruncă excepție) pentru orice hash invalid sau într-un alt format.
    public static boolean verifica(char[] parola, String hashStocat) {
        if (hashStocat == null) return false;
        String[] parti = hashStocat.split("\\$");
        // ["", "argon2id", "v=19", "m=..,t=..,p=..", sare, hash]
        if (parti.length != 6 || !parti[1].equals("argon2id") || !parti[2].equals("v=19")) return false;
        try {
            int m = 0, t = 0, p = 0;
            for (String kv : parti[3].split(",")) {
                int v = Integer.parseInt(kv.substring(2));
                switch (kv.substring(0, 2)) {
                    case "m=" -> m = v;
                    case "t=" -> t = v;
                    case "p=" -> p = v;
                    default -> { return false; }
                }
            }
            byte[] sare = B64D.decode(parti[4]);
            byte[] asteptat = B64D.decode(parti[5]);
            // Limite rezonabile, ca un hash alterat să nu poată bloca aplicația.
            if (m < 8 || m > 1 << 20 || t < 1 || t > 20 || p < 1 || p > 16
                    || sare.length < 8 || asteptat.length < 16 || asteptat.length > 64) {
                return false;
            }
            return MessageDigest.isEqual(asteptat, calculeaza(parola, sare, m, t, p, asteptat.length));
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            return false;
        }
    }

    private static byte[] calculeaza(char[] parola, byte[] sare, int m, int t, int p, int lungime) {
        Argon2Parameters parametri = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withMemoryAsKB(m)
                .withIterations(t)
                .withParallelism(p)
                .withSalt(sare)
                .build();
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(parametri);

        ByteBuffer buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(parola));
        byte[] octeti = Arrays.copyOf(buffer.array(), buffer.limit());
        byte[] rezultat = new byte[lungime];
        try {
            generator.generateBytes(octeti, rezultat);
        } finally {
            Arrays.fill(octeti, (byte) 0);
            Arrays.fill(buffer.array(), (byte) 0);
        }
        return rezultat;
    }
}
