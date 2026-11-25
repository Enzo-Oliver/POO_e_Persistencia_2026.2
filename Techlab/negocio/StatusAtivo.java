package Techlab.negocio;

public enum StatusAtivo {
    ATIVO("Ativo"),
    EM_MANUTENCAO("Em Manutenção"),
    DESCARTADO("Descartado");

    private final String descricao;

    StatusAtivo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusAtivo deTexto(String texto) {
        for (StatusAtivo s : values()) {
            if (s.name().equalsIgnoreCase(texto) || s.descricao.equalsIgnoreCase(texto)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de ativo inválido: " + texto);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
