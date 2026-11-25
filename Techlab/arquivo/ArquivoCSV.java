package Techlab.arquivo;

import Techlab.dto.AtivoDTO;
import Techlab.dto.AtivoMapper;
import Techlab.negocio.AtivoTI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ArquivoCSV {

    private static final String CABECALHO = "categoria;patrimonio;modelo;status;data_aquisicao;detalhe_especifico";

    public void exportarAtivos(List<AtivoTI> ativos, Path destino) throws IOException {
        if (destino.getParent() != null) {
            Files.createDirectories(destino.getParent());
        }
        try (BufferedWriter out = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            out.write(CABECALHO);
            out.newLine();
            for (AtivoTI ativo : ativos) {
                AtivoDTO dto = AtivoMapper.paraDTO(ativo);
                out.write(String.join(";", dto.getCategoria(), dto.getPatrimonio(), dto.getModelo(),
                        dto.getStatus(), dto.getDataAquisicao(), dto.getDetalheEspecifico()));
                out.newLine();
            }
        }
    }

    public List<AtivoTI> importarAtivos(Path origem, List<String> erros) throws IOException {
        List<AtivoTI> ativos = new ArrayList<>();
        try (BufferedReader in = Files.newBufferedReader(origem, StandardCharsets.UTF_8)) {
            String linha;
            int numero = 0;
            while ((linha = in.readLine()) != null) {
                numero++;
                if (linha.isBlank() || (numero == 1 && linha.startsWith("categoria;"))) {
                    continue;
                }
                String[] c = linha.split(";", -1);
                if (c.length != 6) {
                    erros.add("Linha " + numero + ": esperava 6 colunas, encontrou " + c.length);
                    continue;
                }
                try {
                    AtivoDTO dto = new AtivoDTO(0, c[0].trim(), c[1], c[2], c[3].trim(), c[4].trim(), c[5]);
                    ativos.add(AtivoMapper.paraEntidade(dto));
                } catch (IllegalArgumentException e) {
                    erros.add("Linha " + numero + ": " + e.getMessage());
                }
            }
        }
        return ativos;
    }
}
