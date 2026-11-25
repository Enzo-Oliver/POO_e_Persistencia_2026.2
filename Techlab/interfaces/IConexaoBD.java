package Techlab.interfaces;

import Techlab.excecoes.FalhaConexaoBDException;

import java.sql.Connection;

public interface IConexaoBD {
    Connection obterConexao() throws FalhaConexaoBDException;

    void inicializarBanco() throws FalhaConexaoBDException;
}
