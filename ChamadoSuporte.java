//package techlab;

public class ChamadoSuporte{
    private int id;
    private String descrição;
    private boolean aberto;
    private AtivoTI ativoRelacionado;

    //construtor
    public ChamadoSuporte(int id, String descrição,boolean aberto, AtivoTI ativoRelacionado){
        this.id = id
        this.descrição = descrição
        this.aberto = true
        this.ativoRelacionado = ativoRelacionado
    } 

    public void fecharChamado(){
        this.aberto = false;
    }
    
    public AtivoTI getAtivoRelacionado(){
        return this.ativoRelacionado;
    }
}