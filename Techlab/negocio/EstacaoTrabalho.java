package Techlab.negocio;

import java.time.LocalDate;

public class EstacaoTrabalho extends AtivoTI {
    private String departamento;
    private String usuarioAlocado;

    public EstacaoTrabalho(int id, String codigoPatrimonio, String modelo, LocalDate dataAquisicao, StatusAtivo status,
                           String departamento, String usuarioAlocado) {
        super(id, codigoPatrimonio, modelo, dataAquisicao, status);
        setDepartamento(departamento);
        setUsuarioAlocado(usuarioAlocado);
    }

    public EstacaoTrabalho(String codigoPatrimonio, String modelo, LocalDate dataAquisicao,
                           String departamento, String usuarioAlocado) {
        this(0, codigoPatrimonio, modelo, dataAquisicao, StatusAtivo.ATIVO, departamento, usuarioAlocado);
    }

    public String getDepartamento() { return departamento; }
    public String getUsuarioAlocado() { return usuarioAlocado; }

    public void setDepartamento(String departamento) {
        this.departamento = validarTexto(departamento, "Departamento");
    }

    public void setUsuarioAlocado(String usuarioAlocado) {
        this.usuarioAlocado = validarTexto(usuarioAlocado, "Usuário alocado");
    }

    @Override
    public String getCategoria() {
        return "EstacaoTrabalho";
    }

    @Override
    public String getDetalheEspecifico() {
        return departamento + SEPARADOR_DETALHE + usuarioAlocado;
    }

    @Override
    public double calcularCustoManutencaoMensal() {
        return 150.0;
    }

    @Override
    public String toString() {
        return "[Estação] " + super.toString() + " | Depto: " + departamento + " | Usuário: " + usuarioAlocado;
    }
}
