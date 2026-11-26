package Techlab;

import Techlab.arquivo.ArquivoCSV;
import Techlab.dao.AtivoDAO;
import Techlab.dao.ChamadoDAO;
import Techlab.db.ConexaoBanco;
import Techlab.excecoes.FalhaConexaoBDException;
import Techlab.excecoes.RegistroNaoEncontradoException;
import Techlab.interfaces.IConexaoBD;
import Techlab.negocio.AtivoTI;
import Techlab.negocio.ChamadoSuporte;
import Techlab.negocio.DispositivoRede;
import Techlab.negocio.EstacaoTrabalho;
import Techlab.negocio.Prioridade;
import Techlab.negocio.Servidor;
import Techlab.negocio.StatusAtivo;
import Techlab.negocio.StatusChamado;
import Techlab.servico.GerenciadorChamados;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;

public class Main {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final Path ARQUIVO_EXPORTACAO = Path.of("exportacao", "ativos.csv");

    private static final Scanner leitor = new Scanner(System.in);
    private static AtivoDAO ativoDAO;
    private static ChamadoDAO chamadoDAO;

    public static void main(String[] args) {
        IConexaoBD conexao = new ConexaoBanco();
        try {
            conexao.inicializarBanco();
        } catch (FalhaConexaoBDException e) {
            System.err.println("[ERRO CRÍTICO] " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Causa: " + e.getCause().getMessage());
            }
            return;
        }
        ativoDAO = new AtivoDAO(conexao);
        chamadoDAO = new ChamadoDAO(conexao);

        System.out.println("==================================================");
        System.out.println("      TECHLAB INVENTORY & SERVICE DESK v1.0       ");
        System.out.println("==================================================");

        try {
            boolean rodando = true;
            while (rodando) {
                exibirMenu();
                String opcao = lerLinha("Opção: ");
                try {
                    switch (opcao) {
                        case "1": cadastrarAtivo(); break;
                        case "2": listarAtivos(); break;
                        case "3": buscarAtivo(); break;
                        case "4": atualizarAtivo(); break;
                        case "5": removerAtivo(); break;
                        case "6": abrirChamado(); break;
                        case "7": listarChamados(); break;
                        case "8": painelChamados(); break;
                        case "9": fecharChamado(); break;
                        case "10": removerChamado(); break;
                        case "11": relatorioCustos(); break;
                        case "12": exportarCSV(); break;
                        case "13": importarCSV(); break;
                        case "0": rodando = false; break;
                        default: System.out.println("Opção inválida.");
                    }
                } catch (RegistroNaoEncontradoException e) {
                    System.out.println("[AVISO] " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("[DADO INVÁLIDO] " + e.getMessage());
                } catch (FalhaConexaoBDException e) {
                    System.out.println("[ERRO DE CONEXÃO] " + e.getMessage());
                } catch (SQLException e) {
                    System.out.println("[ERRO NO BANCO] " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("[ERRO DE ARQUIVO] " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
        }
        System.out.println("Encerrando o TechLab. Até logo!");
    }

    private static void exibirMenu() {
        System.out.println();
        System.out.println("---------------- ATIVOS ----------------");
        System.out.println(" 1 - Cadastrar ativo");
        System.out.println(" 2 - Listar ativos");
        System.out.println(" 3 - Buscar ativo por ID");
        System.out.println(" 4 - Atualizar ativo");
        System.out.println(" 5 - Remover ativo");
        System.out.println("--------------- CHAMADOS ---------------");
        System.out.println(" 6 - Abrir chamado");
        System.out.println(" 7 - Listar chamados (com filtro)");
        System.out.println(" 8 - Painel de chamados abertos por prioridade");
        System.out.println(" 9 - Fechar chamado");
        System.out.println("10 - Remover chamado");
        System.out.println("-------------- RELATÓRIOS --------------");
        System.out.println("11 - Relatório de custos de manutenção");
        System.out.println("12 - Exportar ativos para CSV");
        System.out.println("13 - Importar ativos de CSV");
        System.out.println(" 0 - Sair");
    }

    private static void cadastrarAtivo() throws SQLException {
        System.out.println("Tipo: 1 - Servidor | 2 - Estação de Trabalho | 3 - Dispositivo de Rede");
        String tipo = lerLinha("Escolha: ");
        if (!tipo.equals("1") && !tipo.equals("2") && !tipo.equals("3")) {
            System.out.println("Tipo inválido.");
            return;
        }

        String patrimonio = lerPatrimonioNovo();
        String modelo = lerValidado("Modelo: ", v -> AtivoTI.validarTexto(v, "Modelo"));
        LocalDate data = lerData("Data de aquisição (dd/mm/aaaa, vazio = hoje): ");

        AtivoTI ativo;
        switch (tipo) {
            case "1":
                String ip = lerValidado("IP estático: ", Servidor::validarIp);
                String so = lerValidado("Sistema operacional: ", v -> AtivoTI.validarTexto(v, "Sistema operacional"));
                int ram = lerValidado("Capacidade de RAM (GB): ",
                        v -> Servidor.validarCapacidadeRAM(converterInt(v)));
                ativo = new Servidor(patrimonio, modelo, data, ip, so, ram);
                break;
            case "2":
                String depto = lerValidado("Departamento: ", v -> AtivoTI.validarTexto(v, "Departamento"));
                String usuario = lerValidado("Usuário alocado: ", v -> AtivoTI.validarTexto(v, "Usuário alocado"));
                ativo = new EstacaoTrabalho(patrimonio, modelo, data, depto, usuario);
                break;
            default:
                int portas = lerValidado("Número de portas: ",
                        v -> DispositivoRede.validarNumeroPortas(converterInt(v)));
                ativo = new DispositivoRede(patrimonio, modelo, data, portas);
        }

        ativoDAO.salvar(ativo);
        System.out.println("[OK] Ativo cadastrado com ID " + ativo.getId() + ".");
    }

    private static void listarAtivos() throws SQLException {
        List<AtivoTI> ativos = ativoDAO.listarTodos();
        if (ativos.isEmpty()) {
            System.out.println("Nenhum ativo cadastrado.");
            return;
        }
        System.out.println("=== Inventário (" + ativos.size() + " ativos) ===");
        for (AtivoTI a : ativos) {
            System.out.println(a);
        }
    }

    private static void buscarAtivo() throws SQLException, RegistroNaoEncontradoException {
        AtivoTI ativo = ativoDAO.buscarPorId(lerInt("ID do ativo: "));
        System.out.println(ativo);
        System.out.printf("Custo mensal de manutenção: R$ %.2f%n", ativo.calcularCustoManutencaoMensal());
    }

    private static void atualizarAtivo() throws SQLException, RegistroNaoEncontradoException {
        AtivoTI ativo = ativoDAO.buscarPorId(lerInt("ID do ativo: "));
        System.out.println("Atual: " + ativo);
        System.out.println("(deixe em branco para manter o valor atual)");

        atualizarCampo("Modelo [" + ativo.getModelo() + "]: ", ativo::setModelo);

        System.out.println("Status: 1 - Ativo | 2 - Em Manutenção | 3 - Descartado");
        atualizarCampo("Novo status [" + ativo.getStatus() + "]: ", v -> ativo.setStatus(escolherStatus(v)));

        if (ativo instanceof Servidor) {
            Servidor s = (Servidor) ativo;
            atualizarCampo("IP [" + s.getIpEstatico() + "]: ", s::setIpEstatico);
            atualizarCampo("SO [" + s.getSistemaOperacional() + "]: ", s::setSistemaOperacional);
            atualizarCampo("RAM em GB [" + s.getCapacidadeRAM() + "]: ", v -> s.setCapacidadeRAM(converterInt(v)));
        } else if (ativo instanceof EstacaoTrabalho) {
            EstacaoTrabalho e = (EstacaoTrabalho) ativo;
            atualizarCampo("Departamento [" + e.getDepartamento() + "]: ", e::setDepartamento);
            atualizarCampo("Usuário [" + e.getUsuarioAlocado() + "]: ", e::setUsuarioAlocado);
        } else if (ativo instanceof DispositivoRede) {
            DispositivoRede d = (DispositivoRede) ativo;
            atualizarCampo("Portas [" + d.getNumeroPortas() + "]: ", v -> d.setNumeroPortas(converterInt(v)));
        }

        ativoDAO.atualizar(ativo);
        System.out.println("[OK] Ativo atualizado: " + ativo);
    }

    private static void removerAtivo() throws SQLException, RegistroNaoEncontradoException {
        AtivoTI ativo = ativoDAO.buscarPorId(lerInt("ID do ativo: "));
        System.out.println(ativo);
        String resp = lerLinha("Remover este ativo e todos os seus chamados? (s/n): ");
        if (resp.equalsIgnoreCase("s")) {
            ativoDAO.deletar(ativo.getId());
            System.out.println("[OK] Ativo removido.");
        } else {
            System.out.println("Remoção cancelada.");
        }
    }

    private static void abrirChamado() throws SQLException, RegistroNaoEncontradoException {
        AtivoTI ativo = ativoDAO.buscarPorId(lerInt("ID do ativo com problema: "));
        if (ativo.getStatus() == StatusAtivo.DESCARTADO) {
            System.out.println("[AVISO] Não é possível abrir chamado para um ativo descartado.");
            return;
        }
        String titulo = lerLinha("Título: ");
        String descricao = lerLinha("Descrição: ");
        Prioridade prioridade = escolherPrioridade();

        ChamadoSuporte chamado = new ChamadoSuporte(titulo, descricao, prioridade, ativo);
        chamadoDAO.salvar(chamado);
        System.out.println("[OK] Chamado #" + chamado.getId() + " aberto.");

        if (prioridade == Prioridade.ALTA) {
            ativo.setStatus(StatusAtivo.EM_MANUTENCAO);
            ativoDAO.atualizar(ativo);
            chamado.enviarAlerta("Ativo enviado para manutenção - " + titulo);
        }
    }

    private static void listarChamados() throws SQLException {
        GerenciadorChamados gerenciador = new GerenciadorChamados(chamadoDAO.listarTodos());
        System.out.println("Filtro: 0 - Todos | 1 - Abertos | 2 - Fechados | 3 - Por prioridade");
        String filtro = lerLinha("Escolha: ");

        List<ChamadoSuporte> lista;
        switch (filtro) {
            case "1": lista = gerenciador.filtrarPorStatus(StatusChamado.ABERTO); break;
            case "2": lista = gerenciador.filtrarPorStatus(StatusChamado.FECHADO); break;
            case "3": lista = gerenciador.filtrarPorPrioridade(escolherPrioridade()); break;
            default: lista = gerenciador.getTodos();
        }

        if (lista.isEmpty()) {
            System.out.println("Nenhum chamado encontrado.");
        }
        for (ChamadoSuporte c : lista) {
            System.out.println(c);
        }
    }

    private static void painelChamados() throws SQLException {
        GerenciadorChamados gerenciador = new GerenciadorChamados(chamadoDAO.listarTodos());
        Map<Prioridade, List<ChamadoSuporte>> painel = gerenciador.agruparAbertosPorPrioridade();

        System.out.println("=== Chamados abertos por prioridade ===");
        for (Map.Entry<Prioridade, List<ChamadoSuporte>> entrada : painel.entrySet()) {
            System.out.println(entrada.getKey() + " (" + entrada.getValue().size() + ")");
            for (ChamadoSuporte c : entrada.getValue()) {
                System.out.println("   #" + c.getId() + " " + c.getTitulo()
                        + " -> " + c.getAtivoRelacionado().getCodigoPatrimonio());
            }
        }
    }

    private static void fecharChamado() throws SQLException, RegistroNaoEncontradoException {
        ChamadoSuporte chamado = chamadoDAO.buscarPorId(lerInt("ID do chamado: "));
        if (!chamado.isAberto()) {
            System.out.println("[AVISO] Este chamado já está fechado.");
            return;
        }
        chamado.fecharChamado();
        chamadoDAO.atualizar(chamado);
        System.out.println("[OK] Chamado #" + chamado.getId() + " fechado.");

        AtivoTI ativo = chamado.getAtivoRelacionado();
        if (ativo.getStatus() == StatusAtivo.EM_MANUTENCAO) {
            boolean temAberto = false;
            for (ChamadoSuporte c : chamadoDAO.listarTodos()) {
                if (c.isAberto() && c.getAtivoRelacionado().getId() == ativo.getId()) {
                    temAberto = true;
                    break;
                }
            }
            if (!temAberto) {
                ativo.setStatus(StatusAtivo.ATIVO);
                ativoDAO.atualizar(ativo);
                System.out.println("[INFO] Ativo " + ativo.getCodigoPatrimonio() + " voltou para o status Ativo.");
            }
        }
    }

    private static void removerChamado() throws SQLException, RegistroNaoEncontradoException {
        int id = lerInt("ID do chamado: ");
        chamadoDAO.deletar(id);
        System.out.println("[OK] Chamado #" + id + " removido.");
    }

    private static void relatorioCustos() throws SQLException {
        List<AtivoTI> ativos = ativoDAO.listarTodos();
        double total = 0.0;
        System.out.println("=== Relatório de custos de manutenção mensal ===");
        for (AtivoTI a : ativos) {
            if (a.getStatus() == StatusAtivo.DESCARTADO) {
                continue;
            }
            double custo = a.calcularCustoManutencaoMensal();
            total += custo;
            System.out.printf("%-16s %-14s %-28s R$ %10.2f%n",
                    a.getCategoria(), a.getCodigoPatrimonio(), a.getModelo(), custo);
        }
        System.out.println("------------------------------------------------------------------------");
        System.out.printf("CUSTO TOTAL MENSAL: R$ %.2f%n", total);
    }

    private static void exportarCSV() throws SQLException, IOException {
        List<AtivoTI> ativos = ativoDAO.listarTodos();
        new ArquivoCSV().exportarAtivos(ativos, ARQUIVO_EXPORTACAO);
        System.out.println("[OK] " + ativos.size() + " ativo(s) exportado(s) para " + ARQUIVO_EXPORTACAO.toAbsolutePath());
    }

    private static void importarCSV() throws SQLException, IOException {
        String caminho = lerLinhaOpcional("Caminho do CSV [dados/ativos_exemplo.csv]: ");
        Path origem = Path.of(caminho.isEmpty() ? "dados/ativos_exemplo.csv" : caminho);

        List<String> erros = new ArrayList<>();
        List<AtivoTI> ativos = new ArquivoCSV().importarAtivos(origem, erros);

        int importados = 0;
        for (AtivoTI a : ativos) {
            if (ativoDAO.existePatrimonio(a.getCodigoPatrimonio())) {
                erros.add(a.getCodigoPatrimonio() + ": patrimônio já cadastrado, ignorado");
                continue;
            }
            ativoDAO.salvar(a);
            importados++;
        }
        System.out.println("[OK] " + importados + " ativo(s) importado(s).");
        for (String erro : erros) {
            System.out.println("  [IGNORADO] " + erro);
        }
    }

    private static String lerLinhaOpcional(String mensagem) {
        System.out.print(mensagem);
        return leitor.nextLine().trim();
    }

    private static String lerLinha(String mensagem) {
        while (true) {
            String valor = lerLinhaOpcional(mensagem);
            if (!valor.isEmpty()) {
                return valor;
            }
            System.out.println("Campo obrigatório.");
        }
    }

    private static int lerInt(String mensagem) {
        while (true) {
            try {
                return converterInt(lerLinha(mensagem));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static int converterInt(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Digite um número inteiro válido.");
        }
    }

    private static LocalDate lerData(String mensagem) {
        while (true) {
            String texto = lerLinhaOpcional(mensagem);
            if (texto.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return AtivoTI.validarDataAquisicao(LocalDate.parse(texto, FORMATO_DATA));
            } catch (DateTimeParseException e) {
                System.out.println("[DADO INVÁLIDO] Data inválida. Use o formato dd/mm/aaaa. Tente novamente.");
            } catch (IllegalArgumentException e) {
                System.out.println("[DADO INVÁLIDO] " + e.getMessage() + " Tente novamente.");
            }
        }
    }

    private static <T> T lerValidado(String mensagem, Function<String, T> validador) {
        while (true) {
            try {
                return validador.apply(lerLinha(mensagem));
            } catch (IllegalArgumentException e) {
                System.out.println("[DADO INVÁLIDO] " + e.getMessage() + " Tente novamente.");
            }
        }
    }

    private static void atualizarCampo(String mensagem, Consumer<String> setter) {
        while (true) {
            String valor = lerLinhaOpcional(mensagem);
            if (valor.isEmpty()) {
                return;
            }
            try {
                setter.accept(valor);
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("[DADO INVÁLIDO] " + e.getMessage() + " Tente novamente.");
            }
        }
    }

    private static String lerPatrimonioNovo() throws SQLException {
        while (true) {
            String patrimonio = lerValidado("Código de patrimônio: ", AtivoTI::validarPatrimonio);
            if (!ativoDAO.existePatrimonio(patrimonio)) {
                return patrimonio;
            }
            System.out.println("[AVISO] Já existe um ativo com o patrimônio " + patrimonio + ". Digite outro.");
        }
    }

    private static Prioridade escolherPrioridade() {
        while (true) {
            String p = lerLinha("Prioridade (1 - Baixa | 2 - Média | 3 - Alta): ");
            switch (p) {
                case "1": return Prioridade.BAIXA;
                case "2": return Prioridade.MEDIA;
                case "3": return Prioridade.ALTA;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private static StatusAtivo escolherStatus(String opcao) {
        switch (opcao) {
            case "1": return StatusAtivo.ATIVO;
            case "2": return StatusAtivo.EM_MANUTENCAO;
            case "3": return StatusAtivo.DESCARTADO;
            default: throw new IllegalArgumentException("Status inválido: " + opcao);
        }
    }
}
