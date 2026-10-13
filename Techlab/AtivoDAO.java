package Techlab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AtivoDAO {

    public void salvar(AtivoTI ativo) throws SQLException {
        String sql = "INSERT INTO ativos (patrimonio, nome, categoria, detalhe_especifico) VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, ativo.getPatrimonio());
            stmt.setString(2, ativo.getModelo());
            stmt.setString(3, ativo.getCategoria());
            stmt.setString(4, ativo.getDetalheEspecifico());
            stmt.executeUpdate();

            System.out.println("[LOG] Ativo cadastrado com sucesso: " + ativo.getModelo());

        } catch (SQLException e) {
            System.err.println("[ERRO DAO] Falha ao inserir ativo (" + ativo.getPatrimonio() + "): " + e.getMessage());
            throw e;
        }
    }

    public List<AtivoTI> listarTodos() throws SQLException {
        List<AtivoTI> lista = new ArrayList<>();
        String sql = "SELECT id, patrimonio, nome, categoria, detalhe_especifico FROM ativos ORDER BY id ASC";

        try (Connection conexao = ConexaoBanco.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(montarAtivo(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ERRO DAO] Falha ao listar ativos: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    // Reconstrói o objeto da subclasse certa a partir da linha do banco
    private AtivoTI montarAtivo(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String patrimonio = rs.getString("patrimonio");
        String modelo = rs.getString("nome");
        String categoria = rs.getString("categoria");
        String detalhe = rs.getString("detalhe_especifico");

        if ("Servidor".equals(categoria)) {
            String[] p = detalhe.split(";");
            return new Servidor(id, patrimonio, modelo, "Ativo", p[0], p[1], Integer.parseInt(p[2]));
        }
        throw new SQLException("Categoria desconhecida: " + categoria);
    }
}