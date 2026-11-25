package Techlab.dao;

import Techlab.dto.AtivoDTO;
import Techlab.dto.AtivoMapper;
import Techlab.excecoes.EquipamentoNaoEncontradoException;
import Techlab.interfaces.IConexaoBD;
import Techlab.interfaces.IPersistencia;
import Techlab.negocio.AtivoTI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AtivoDAO implements IPersistencia<AtivoTI> {

    private static final String COLUNAS = "id, patrimonio, modelo, categoria, status, data_aquisicao, detalhe_especifico";

    private final IConexaoBD conexaoBD;

    public AtivoDAO(IConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    @Override
    public void salvar(AtivoTI ativo) throws SQLException {
        String sql = "INSERT INTO ativos (patrimonio, modelo, categoria, status, data_aquisicao, detalhe_especifico) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            AtivoDTO dto = AtivoMapper.paraDTO(ativo);
            stmt.setString(1, dto.getPatrimonio());
            stmt.setString(2, dto.getModelo());
            stmt.setString(3, dto.getCategoria());
            stmt.setString(4, dto.getStatus());
            stmt.setString(5, dto.getDataAquisicao());
            stmt.setString(6, dto.getDetalheEspecifico());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    ativo.setId(chaves.getInt(1));
                }
            }
        }
    }

    @Override
    public AtivoTI buscarPorId(int id) throws SQLException, EquipamentoNaoEncontradoException {
        String sql = "SELECT " + COLUNAS + " FROM ativos WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarAtivo(rs, "");
                }
            }
        }
        throw new EquipamentoNaoEncontradoException(id);
    }

    @Override
    public List<AtivoTI> listarTodos() throws SQLException {
        List<AtivoTI> lista = new ArrayList<>();
        String sql = "SELECT " + COLUNAS + " FROM ativos ORDER BY id";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(montarAtivo(rs, ""));
            }
        }
        return lista;
    }

    public boolean existePatrimonio(String patrimonio) throws SQLException {
        String sql = "SELECT 1 FROM ativos WHERE patrimonio = ?";
        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, patrimonio.trim().toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void atualizar(AtivoTI ativo) throws SQLException, EquipamentoNaoEncontradoException {
        String sql = "UPDATE ativos SET patrimonio = ?, modelo = ?, categoria = ?, status = ?, "
                + "data_aquisicao = ?, detalhe_especifico = ? WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            AtivoDTO dto = AtivoMapper.paraDTO(ativo);
            stmt.setString(1, dto.getPatrimonio());
            stmt.setString(2, dto.getModelo());
            stmt.setString(3, dto.getCategoria());
            stmt.setString(4, dto.getStatus());
            stmt.setString(5, dto.getDataAquisicao());
            stmt.setString(6, dto.getDetalheEspecifico());
            stmt.setInt(7, dto.getId());

            if (stmt.executeUpdate() == 0) {
                throw new EquipamentoNaoEncontradoException(ativo.getId());
            }
        }
    }

    @Override
    public void deletar(int id) throws SQLException, EquipamentoNaoEncontradoException {
        String sql = "DELETE FROM ativos WHERE id = ?";

        try (Connection conexao = conexaoBD.obterConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new EquipamentoNaoEncontradoException(id);
            }
        }
    }

    static AtivoTI montarAtivo(ResultSet rs, String prefixo) throws SQLException {
        AtivoDTO dto = new AtivoDTO(
                rs.getInt(prefixo + "id"),
                rs.getString(prefixo + "categoria"),
                rs.getString(prefixo + "patrimonio"),
                rs.getString(prefixo + "modelo"),
                rs.getString(prefixo + "status"),
                rs.getString(prefixo + "data_aquisicao"),
                rs.getString(prefixo + "detalhe_especifico"));
        try {
            return AtivoMapper.paraEntidade(dto);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Registro corrompido na tabela ativos (id " + dto.getId() + "): "
                    + e.getMessage(), e);
        }
    }
}
