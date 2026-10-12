package service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Hash PBKDF2-HMAC-SHA256 com salt aleatório. Formato: iteracoes:salt:hash (Base64). */
public class Pbkdf2SenhaHasher implements SenhaHasher {

    private static final int ITERACOES = 120_000;
    private static final int TAMANHO_HASH_BITS = 256;

    @Override
    public String gerarHash(String senha) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] hash = derivar(senha, salt, ITERACOES);
        return ITERACOES + ":" + Base64.getEncoder().encodeToString(salt)
                + ":" + Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public boolean confere(String senha, String hashArmazenado) {
        String[] partes = hashArmazenado.split(":");
        if (partes.length != 3) {
            return false;
        }
        byte[] esperado = Base64.getDecoder().decode(partes[2]);
        byte[] calculado = derivar(senha, Base64.getDecoder().decode(partes[1]), Integer.parseInt(partes[0]));
        return MessageDigest.isEqual(esperado, calculado);
    }

    private byte[] derivar(String senha, byte[] salt, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, iteracoes, TAMANHO_HASH_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Algoritmo de hash indisponível", e);
        }
    }
}
