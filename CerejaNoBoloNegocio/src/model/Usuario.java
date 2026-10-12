package model;

/** Usuário do sistema. Guarda apenas o HASH da senha, nunca a senha em texto. */
public class Usuario {

    private int id;
    private String login;
    private String senhaHash;

    public Usuario() {
    }

    public Usuario(int id, String login, String senhaHash) {
        this.id = id;
        this.login = login;
        this.senhaHash = senhaHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }
}
