package Techlab.negocio;

public enum Prioridade {
    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta");

    private final String descricao;

    Prioridade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Prioridade deTexto(String texto) {
        for (Prioridade p : values()) {
            if (p.name().equalsIgnoreCase(texto) || p.descricao.equalsIgnoreCase(texto)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Prioridade inválida: " + texto);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
