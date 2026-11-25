package Techlab.dao;

import Techlab.excecoes.ChamadoNaoEncontradoException;
import Techlab.interfaces.IConexaoBD;
import Techlab.interfaces.IPersistencia;
import Techlab.negocio.AtivoTI;
import Techlab.negocio.ChamadoSuporte;
import Techlab.negocio.Prioridade;
import Techlab.negocio.StatusChamado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChamadoDAO implements IPersistencia<ChamadoSuporte> {

    private static final String SELECT_BASE =
            "SELECT c.id, c.titulo, c.descricao, c.prioridade, c.status, c.data_abertura, "
            + "a.id AS a_id, a.patrimonio AS a_patrimonio, a.modelo AS a_modelo, a.categoria AS a_categoria, "
            + "a.status AS a_status, a.data_aquisicao AS a_data_aquisicao, "
            + "a.detalhe_especifico AS a_detalhe_especifico "
            + "FROM chamados c JOIN ativos a ON a.id = c.ativo_id ";

    private final IConexaoBD conexaoBD;

    public ChamadoDAO(IConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(ChamadoSuporte chamado) throws SQLException {
        String sql = "INSERT INTO chamados (titulo, descricao, prioridade, status, data_abertura, ativo_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, chamado.getTitulo());
            stmt.setString(2, chamado.getDescricao());
            stmt.setString(3, chamado.getPrioridade().name());
            stmt.setString(4, chamado.getStatus().name());
            stmt.setString(5, chamado.getDataAbertura().toString());
            stmt.setInt(6, chamado.getAtivoRelacionado().getId());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    chamado.setId(chaves.getInt(1));
                }
            }
        }
    }

    @Override
    public ChamadoSuporte buscarPorId(int id) throws SQLException, ChamadoNaoEncontradoException {
        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BASE + "WHERE c.id = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarChamado(rs);
                }
            }
        }
        throw new ChamadoNaoEncontradoException(id);
    }

    @Override
    public List<ChamadoSuporte> listarTodos() throws SQLException {
        List<ChamadoSuporte> lista = new ArrayList<>();
        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BASE + "ORDER BY c.id");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montarChamado(rs));
            }
        }
        return lista;
    }

    @Override
    public void atualizar(ChamadoSuporte chamado) throws SQLException, ChamadoNaoEncontradoException {
        String sql = "UPDATE chamados SET titulo = ?, descricao = ?, prioridade = ?, status = ?, ativo_id = ? "
                + "WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, chamado.getTitulo());
            stmt.setString(2, chamado.getDescricao());
            stmt.setString(3, chamado.getPrioridade().name());
            stmt.setString(4, chamado.getStatus().name());
            stmt.setInt(5, chamado.getAtivoRelacionado().getId());
            stmt.setInt(6, chamado.getId());

            if (stmt.executeUpdate() == 0) {
                throw new ChamadoNaoEncontradoException(chamado.getId());
            }
        }
    }

    @Override
    public void deletar(int id) throws SQLException, ChamadoNaoEncontradoException {
        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement("DELETE FROM chamados WHERE id = ?")) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new ChamadoNaoEncontradoException(id);
            }
        }
    }

    private ChamadoSuporte montarChamado(ResultSet rs) throws SQLException {
        AtivoTI ativo = AtivoDAO.montarAtivo(rs, "a_");
        try {
            return new ChamadoSuporte(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("descricao"),
                    Prioridade.deTexto(rs.getString("prioridade")),
                    LocalDateTime.parse(rs.getString("data_abertura")),
                    StatusChamado.deTexto(rs.getString("status")),
                    ativo);
        } catch (RuntimeException e) {
            throw new SQLException("Registro corrompido na tabela chamados (id " + rs.getInt("id") + "): "
                    + e.getMessage(), e);
        }
    }
}
