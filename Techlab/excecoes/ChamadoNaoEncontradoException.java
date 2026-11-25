package Techlab.excecoes;

public class ChamadoNaoEncontradoException extends RegistroNaoEncontradoException {
    public ChamadoNaoEncontradoException(int id) {
        super("Chamado com ID " + id + " não foi encontrado.");
    }
}
