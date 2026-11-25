package Techlab.negocio;

import java.time.LocalDate;

public class Servidor extends AtivoTI {
    private String ipEstatico;
    private String sistemaOperacional;
    private int capacidadeRAM;

    public Servidor(int id, String codigoPatrimonio, String modelo, LocalDate dataAquisicao, StatusAtivo status,
                    String ipEstatico, String sistemaOperacional, int capacidadeRAM) {
        super(id, codigoPatrimonio, modelo, dataAquisicao, status);
        setIpEstatico(ipEstatico);
        setSistemaOperacional(sistemaOperacional);
        setCapacidadeRAM(capacidadeRAM);
    }

    public Servidor(String codigoPatrimonio, String modelo, LocalDate dataAquisicao,
                    String ipEstatico, String sistemaOperacional, int capacidadeRAM) {
        this(0, codigoPatrimonio, modelo, dataAquisicao, StatusAtivo.ATIVO,
                ipEstatico, sistemaOperacional, capacidadeRAM);
    }

    public String getIpEstatico() { return ipEstatico; }
    public String getSistemaOperacional() { return sistemaOperacional; }
    public int getCapacidadeRAM() { return capacidadeRAM; }

    public void setIpEstatico(String ipEstatico) {
        String ip = validarTexto(ipEstatico, "IP estático");
        if (!ip.matches("\\d{1,3}(\\.\\d{1,3}){3}")) {
            throw new IllegalArgumentException("IP estático inválido: " + ip);
        }
        for (String parte : ip.split("\\.")) {
            if (Integer.parseInt(parte) > 255) {
                throw new IllegalArgumentException("IP estático inválido: " + ip);
            }
        }
        this.ipEstatico = ip;
    }

    public void setSistemaOperacional(String sistemaOperacional) {
        this.sistemaOperacional = validarTexto(sistemaOperacional, "Sistema operacional");
    }

    public void setCapacidadeRAM(int capacidadeRAM) {
        if (capacidadeRAM <= 0) {
            throw new IllegalArgumentException("Capacidade de RAM deve ser maior que zero.");
        }
        this.capacidadeRAM = capacidadeRAM;
    }

    @Override
    public String getCategoria() {
        return "Servidor";
    }

    @Override
    public String getDetalheEspecifico() {
        return ipEstatico + SEPARADOR_DETALHE + sistemaOperacional + SEPARADOR_DETALHE + capacidadeRAM;
    }

    @Override
    public double calcularCustoManutencaoMensal() {
        return 200.0 + (capacidadeRAM * 50.0);
    }

    @Override
    public String toString() {
        return "[Servidor] " + super.toString() + " | IP: " + ipEstatico
                + " | SO: " + sistemaOperacional + " | RAM: " + capacidadeRAM + "GB";
    }
}
