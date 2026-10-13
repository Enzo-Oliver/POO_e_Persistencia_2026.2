// //package techlab-inventory;

// public class Main {
//     public static void main(string[]args){
//         AtivoTI servidor = new AtivoTI("1","PAT-2026-001","DELL PowerEdge","Ativo");
    
//         system.out.println("=== Ativo cadastrado ===");
//         system.out.println("Patrimonio: " + servidor.getCodigoPatrimonio());
//         system.out.println("Modelo: " + servidor.getmodelo());
//         system.out.println("Status Inicial: " + servidor.getStatus());

//         servidor.setStatus("Em manutenção");
//         system.out.println("Novo status: " + servidor.getStatus());
//     }


// }

package techlab;
import java.util.scanner;

//public class main

public class main {
    public static void(String[] args){
        Scanner leitor = new Scanner(System.int);

        System.out.println("=== Cadastro de AtivoTI ===");
        System.out.println("Digite o ID do ativo: ");
        int id = leitor.nextLine();
        int id = leitor.nextLine(); //limpar o buffer

        System.out.println("Digite o codigo de Patrimonio: ");
        System.codigoPatrimonio = leitor.nextLine();

        System.out.print("Informe o status do ativo");
        string modelo = leitor.nextLine();

        System.out.print("informe o status do ativo: ");
        string status = leitor.newtLine();

        AtivoTI equipamento = new AtivoTI(idAtivo, codigoPatrimonio, modelo, status);

        System.out.println("\n=== Ativo Cadastro Chamada ===");
        System.out.print("ID do chamado de suporte: ");
        int idChamado = leitor.nextInt();
        leitor.nextLine(); // lIMPAR O BUFFER 

        System.out.print("Descrição do Chamado; ");
        String descricaoChamado = leitor.nextLine();

        ChamadoSuporte chamado = nww ChamadoSuporte(idChamado, descricaoChamado,equipamento);

        System.out.println("\n=== Status inicial do Equipamento ===");
        System.out.println("Status do Equipamento: + chamado.getAtivoRelacionado().getStatus()";)
        
    }
}