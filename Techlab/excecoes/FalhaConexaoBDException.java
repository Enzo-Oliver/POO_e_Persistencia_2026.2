package Techlab.excecoes;

import java.sql.SQLException;

public class FalhaConexaoBDException extends SQLException {
    public FalhaConexaoBDException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
