package service;

/** Estratégia de proteção de senhas (permite trocar o algoritmo sem mexer no login). */
public interface SenhaHasher {

    String gerarHash(String senha);

    boolean confere(String senha, String hashArmazenado);
}
