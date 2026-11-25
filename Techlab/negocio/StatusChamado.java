package Techlab.negocio;

public enum StatusChamado {
    ABERTO("Aberto"),
    FECHADO("Fechado");

    private final String descricao;

    StatusChamado(String descricao) {
        this.descricao = descricao;
    }

    public static StatusChamado deTexto(String texto) {
        for (StatusChamado s : values()) {
            if (s.name().equalsIgnoreCase(texto) || s.descricao.equalsIgnoreCase(texto)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de chamado inválido: " + texto);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
