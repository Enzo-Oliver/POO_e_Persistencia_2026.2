package Techlab.negocio;

import Techlab.interfaces.INotificavel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChamadoSuporte implements INotificavel {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private String titulo;
    private String descricao;
    private Prioridade prioridade;
    private LocalDateTime dataAbertura;
    private StatusChamado status;
    private AtivoTI ativoRelacionado;

    public ChamadoSuporte(int id, String titulo, String descricao, Prioridade prioridade,
                          LocalDateTime dataAbertura, StatusChamado status, AtivoTI ativoRelacionado) {
        this.id = id;
        setTitulo(titulo);
        setDescricao(descricao);
        setPrioridade(prioridade);
        this.dataAbertura = dataAbertura;
        this.status = status;
        setAtivoRelacionado(ativoRelacionado);
    }

    public ChamadoSuporte(String titulo, String descricao, Prioridade prioridade, AtivoTI ativoRelacionado) {
        this(0, titulo, descricao, prioridade, LocalDateTime.now().withNano(0), StatusChamado.ABERTO, ativoRelacionado);
    }

    public void fecharChamado() {
        this.status = StatusChamado.FECHADO;
    }

    public boolean isAberto() {
        return status == StatusChamado.ABERTO;
    }

    @Override
    public void enviarAlerta(String mensagem) {
        System.out.println("[ALERTA - " + prioridade.getDescricao().toUpperCase() + "] Chamado #" + id
                + " (" + ativoRelacionado.getCodigoPatrimonio() + "): " + mensagem);
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public Prioridade getPrioridade() { return prioridade; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public StatusChamado getStatus() { return status; }
    public AtivoTI getAtivoRelacionado() { return ativoRelacionado; }

    public void setId(int id) { this.id = id; }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título do chamado não pode ser vazio.");
        }
        this.titulo = titulo.trim();
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição do chamado não pode ser vazia.");
        }
        this.descricao = descricao.trim();
    }

    public void setPrioridade(Prioridade prioridade) {
        if (prioridade == null) {
            throw new IllegalArgumentException("Prioridade é obrigatória.");
        }
        this.prioridade = prioridade;
    }

    public void setAtivoRelacionado(AtivoTI ativoRelacionado) {
        if (ativoRelacionado == null) {
            throw new IllegalArgumentException("Todo chamado precisa de um ativo relacionado.");
        }
        this.ativoRelacionado = ativoRelacionado;
    }

    @Override
    public String toString() {
        return "------------------------------------\n"
                + "Chamado #" + id + " [" + status + "]\n"
                + "Título: " + titulo + "\n"
                + "Descrição: " + descricao + "\n"
                + "Prioridade: " + prioridade + "\n"
                + "Aberto em: " + dataAbertura.format(FORMATO) + "\n"
                + "Equipamento: " + ativoRelacionado + "\n"
                + "------------------------------------";
    }
}
