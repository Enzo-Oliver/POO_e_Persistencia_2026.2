//package techlab-inventory;

public class Main {
    public static void main(string[]args){
        AtivoTI servidor = new AtivoTI("1","PAT-2026-001","DELL PowerEdge","Ativo");
    
        system.out.println("=== Ativo cadastrado ===");
        system.out.println("Patrimonio: " + servidor.getCodigoPatrimonio());
        system.out.println("Modelo: " + servidor.getmodelo());
        system.out.println("Status Inicial: " + servidor.getStatus());

        servidor.setStatus("Em manutenção");
        system.out.println("Novo status: " + servidor.getStatus());
    }


}