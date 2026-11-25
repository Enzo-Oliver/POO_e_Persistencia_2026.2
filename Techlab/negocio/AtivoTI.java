package Techlab.negocio;

import java.time.LocalDate;
import java.util.Objects;

public abstract class AtivoTI {

    public static final String SEPARADOR_DETALHE = "|";

    private int id;
    private String codigoPatrimonio;
    private String modelo;
    private LocalDate dataAquisicao;
    private StatusAtivo status;

    public AtivoTI(int id, String codigoPatrimonio, String modelo, LocalDate dataAquisicao, StatusAtivo status) {
        this.id = id;
        setCodigoPatrimonio(codigoPatrimonio);
        setModelo(modelo);
        setDataAquisicao(dataAquisicao);
        setStatus(status);
    }

    public AtivoTI(String codigoPatrimonio, String modelo, LocalDate dataAquisicao) {
        this(0, codigoPatrimonio, modelo, dataAquisicao, StatusAtivo.ATIVO);
    }

    public abstract String getCategoria();

    public abstract String getDetalheEspecifico();

    public abstract double calcularCustoManutencaoMensal();

    public int getId() { return id; }
    public String getCodigoPatrimonio() { return codigoPatrimonio; }
    public String getModelo() { return modelo; }
    public LocalDate getDataAquisicao() { return dataAquisicao; }
    public StatusAtivo getStatus() { return status; }

    public void setId(int id) { this.id = id; }

    public void setCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = validarTexto(codigoPatrimonio, "Código de patrimônio").toUpperCase();
    }

    public void setModelo(String modelo) {
        this.modelo = validarTexto(modelo, "Modelo");
    }

    public void setDataAquisicao(LocalDate dataAquisicao) {
        if (dataAquisicao == null) {
            throw new IllegalArgumentException("Data de aquisição é obrigatória.");
        }
        if (dataAquisicao.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de aquisição não pode estar no futuro.");
        }
        this.dataAquisicao = dataAquisicao;
    }

    public void setStatus(StatusAtivo status) {
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório.");
        }
        this.status = status;
    }

    protected static String validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio.");
        }
        if (valor.contains(SEPARADOR_DETALHE) || valor.contains(";")) {
            throw new IllegalArgumentException(campo + " não pode conter os caracteres '|' ou ';'.");
        }
        return valor.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AtivoTI)) return false;
        AtivoTI outro = (AtivoTI) obj;
        return codigoPatrimonio.equals(outro.codigoPatrimonio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoPatrimonio);
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Pat: " + codigoPatrimonio + " | Modelo: " + modelo
                + " | Aquisição: " + dataAquisicao + " | Status: " + status;
    }
}
