package Techlab.db;

import Techlab.excecoes.FalhaConexaoBDException;
import Techlab.interfaces.IConexaoBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBanco implements IConexaoBD {

    private static final String URL_PADRAO = "jdbc:sqlite:techlab.db";

    private final String url;

    public ConexaoBanco() {
        this(URL_PADRAO);
    }

    public ConexaoBanco(String url) {
        this.url = url;
    }

    @Override
    public Connection obterConexao() throws FalhaConexaoBDException {
        try {
            Connection conn = DriverManager.getConnection(url);
            try (Statement s = conn.createStatement()) {
                s.execute("PRAGMA foreign_keys = ON");
            }
            return conn;
        } catch (SQLException e) {
            throw new FalhaConexaoBDException("Não foi possível conectar em " + url
                    + ". Verifique se o sqlite-jdbc está no classpath.", e);
        }
    }

    @Override
    public void inicializarBanco() throws FalhaConexaoBDException {
        String ddlAtivos = "CREATE TABLE IF NOT EXISTS ativos ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "patrimonio TEXT NOT NULL UNIQUE, "
                + "modelo TEXT NOT NULL, "
                + "categoria TEXT NOT NULL, "
                + "status TEXT NOT NULL, "
                + "data_aquisicao TEXT NOT NULL, "
                + "detalhe_especifico TEXT NOT NULL)";

        String ddlChamados = "CREATE TABLE IF NOT EXISTS chamados ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "titulo TEXT NOT NULL, "
                + "descricao TEXT NOT NULL, "
                + "prioridade TEXT NOT NULL, "
                + "status TEXT NOT NULL, "
                + "data_abertura TEXT NOT NULL, "
                + "ativo_id INTEGER NOT NULL, "
                + "FOREIGN KEY (ativo_id) REFERENCES ativos(id) ON DELETE CASCADE)";

        try (Connection conexao = obterConexao();
             Statement stmt = conexao.createStatement()) {
            stmt.execute(ddlAtivos);
            stmt.execute(ddlChamados);
        } catch (SQLException e) {
            throw new FalhaConexaoBDException("Falha ao criar as tabelas do banco.", e);
        }
    }
}
