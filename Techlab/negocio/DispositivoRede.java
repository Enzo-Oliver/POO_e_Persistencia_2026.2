package Techlab.negocio;

import java.time.LocalDate;

public class DispositivoRede extends AtivoTI {
    private int numeroPortas;

    public DispositivoRede(int id, String codigoPatrimonio, String modelo, LocalDate dataAquisicao, StatusAtivo status,
                           int numeroPortas) {
        super(id, codigoPatrimonio, modelo, dataAquisicao, status);
        setNumeroPortas(numeroPortas);
    }

    public DispositivoRede(String codigoPatrimonio, String modelo, LocalDate dataAquisicao, int numeroPortas) {
        this(0, codigoPatrimonio, modelo, dataAquisicao, StatusAtivo.ATIVO, numeroPortas);
    }

    public int getNumeroPortas() { return numeroPortas; }

    public void setNumeroPortas(int numeroPortas) {
        this.numeroPortas = validarNumeroPortas(numeroPortas);
    }

    public static int validarNumeroPortas(int numeroPortas) {
        if (numeroPortas <= 0) {
            throw new IllegalArgumentException("Número de portas deve ser maior que zero.");
        }
        return numeroPortas;
    }

    @Override
    public String getCategoria() {
        return "DispositivoRede";
    }

    @Override
    public String getDetalheEspecifico() {
        return String.valueOf(numeroPortas);
    }

    @Override
    public double calcularCustoManutencaoMensal() {
        return numeroPortas * 20.0;
    }

    @Override
    public String toString() {
        return "[Rede] " + super.toString() + " | Portas: " + numeroPortas;
    }
}
