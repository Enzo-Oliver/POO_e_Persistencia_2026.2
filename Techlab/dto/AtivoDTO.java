package Techlab.dto;

public class AtivoDTO {
    private final int id;
    private final String categoria;
    private final String patrimonio;
    private final String modelo;
    private final String status;
    private final String dataAquisicao;
    private final String detalheEspecifico;

    public AtivoDTO(int id, String categoria, String patrimonio, String modelo,
                    String status, String dataAquisicao, String detalheEspecifico) {
        this.id = id;
        this.categoria = categoria;
        this.patrimonio = patrimonio;
        this.modelo = modelo;
        this.status = status;
        this.dataAquisicao = dataAquisicao;
        this.detalheEspecifico = detalheEspecifico;
    }

    public int getId() { return id; }
    public String getCategoria() { return categoria; }
    public String getPatrimonio() { return patrimonio; }
    public String getModelo() { return modelo; }
    public String getStatus() { return status; }
    public String getDataAquisicao() { return dataAquisicao; }
    public String getDetalheEspecifico() { return detalheEspecifico; }
}
