package Techlab.dto;

import Techlab.negocio.AtivoTI;
import Techlab.negocio.DispositivoRede;
import Techlab.negocio.EstacaoTrabalho;
import Techlab.negocio.Servidor;
import Techlab.negocio.StatusAtivo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public final class AtivoMapper {

    private AtivoMapper() {}

    public static AtivoDTO paraDTO(AtivoTI ativo) {
        return new AtivoDTO(
                ativo.getId(),
                ativo.getCategoria(),
                ativo.getCodigoPatrimonio(),
                ativo.getModelo(),
                ativo.getStatus().name(),
                ativo.getDataAquisicao().toString(),
                ativo.getDetalheEspecifico());
    }

    public static AtivoTI paraEntidade(AtivoDTO dto) {
        StatusAtivo status = StatusAtivo.deTexto(dto.getStatus());
        LocalDate data;
        try {
            data = LocalDate.parse(dto.getDataAquisicao());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida: " + dto.getDataAquisicao());
        }

        String[] p = dto.getDetalheEspecifico().split(Pattern.quote(AtivoTI.SEPARADOR_DETALHE), -1);

        try {
            switch (dto.getCategoria()) {
                case "Servidor":
                    exigirPartes(p, 3, dto);
                    return new Servidor(dto.getId(), dto.getPatrimonio(), dto.getModelo(), data, status,
                            p[0], p[1], Integer.parseInt(p[2].trim()));
                case "EstacaoTrabalho":
                    exigirPartes(p, 2, dto);
                    return new EstacaoTrabalho(dto.getId(), dto.getPatrimonio(), dto.getModelo(), data, status,
                            p[0], p[1]);
                case "DispositivoRede":
                    exigirPartes(p, 1, dto);
                    return new DispositivoRede(dto.getId(), dto.getPatrimonio(), dto.getModelo(), data, status,
                            Integer.parseInt(p[0].trim()));
                default:
                    throw new IllegalArgumentException("Categoria desconhecida: " + dto.getCategoria());
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor numérico inválido no ativo " + dto.getPatrimonio()
                    + ": " + dto.getDetalheEspecifico());
        }
    }

    private static void exigirPartes(String[] partes, int esperado, AtivoDTO dto) {
        if (partes.length != esperado) {
            throw new IllegalArgumentException("Detalhe do ativo " + dto.getPatrimonio()
                    + " deveria ter " + esperado + " campo(s): " + dto.getDetalheEspecifico());
        }
    }
}
