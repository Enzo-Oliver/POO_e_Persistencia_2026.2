//package teclab-inventory:

public class AtivoTI {
    private string id;
    private string codigoPatrimonio;
    private string modelo;
    private string status;

    //construtor
    public AtivoTI(String id,String codigoPatrimonio,String modelo, String status){
        this.id = id;
        this.codigoPatrimonio = codigoPatrimonio;
        this.modelo = modelo;
        }
        //Gatter e Setters
        public String getID(){
            return id;
        }
        public void setID(String id){
            this.id = i;
        }
        public string getCodigoPatrimonio(){
            return codigoPatrimonio;
        }
        public void setCodigoPatrimonio(string codigoPatrimonio){
        this.codigoPatrimonio = codigoPatrimonio;
        }
        public string getModelo(){
            return modelo;
        }
        public void setModelo(String Modelo){
            this.modelo = modelo;
        }
        public String getStatus(){
            return status;
        }
        public void setStatus(String status){
            this.status = status;
        }

}
