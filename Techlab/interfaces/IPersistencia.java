package Techlab.interfaces;

import Techlab.excecoes.RegistroNaoEncontradoException;

import java.sql.SQLException;
import java.util.List;

public interface IPersistencia<T> {
    void salvar(T objeto) throws SQLException;

    T buscarPorId(int id) throws SQLException, RegistroNaoEncontradoException;

    List<T> listarTodos() throws SQLException;

    void atualizar(T objeto) throws SQLException, RegistroNaoEncontradoException;

    void deletar(int id) throws SQLException, RegistroNaoEncontradoException;
}
