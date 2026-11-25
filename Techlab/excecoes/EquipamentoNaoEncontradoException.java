package Techlab.excecoes;

public class EquipamentoNaoEncontradoException extends RegistroNaoEncontradoException {
    public EquipamentoNaoEncontradoException(int id) {
        super("Equipamento com ID " + id + " não foi encontrado.");
    }
}
