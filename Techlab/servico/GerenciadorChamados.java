package Techlab.servico;

import Techlab.negocio.ChamadoSuporte;
import Techlab.negocio.Prioridade;
import Techlab.negocio.StatusChamado;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GerenciadorChamados {

    private final List<ChamadoSuporte> chamados = new ArrayList<>();

    public GerenciadorChamados(List<ChamadoSuporte> iniciais) {
        chamados.addAll(iniciais);
    }

    public List<ChamadoSuporte> getTodos() {
        return new ArrayList<>(chamados);
    }

    public List<ChamadoSuporte> filtrarPorPrioridade(Prioridade prioridade) {
        List<ChamadoSuporte> resultado = new ArrayList<>();
        for (ChamadoSuporte c : chamados) {
            if (c.getPrioridade() == prioridade) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public List<ChamadoSuporte> filtrarPorStatus(StatusChamado status) {
        List<ChamadoSuporte> resultado = new ArrayList<>();
        Iterator<ChamadoSuporte> it = chamados.iterator();
        while (it.hasNext()) {
            ChamadoSuporte c = it.next();
            if (c.getStatus() == status) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public Map<Prioridade, List<ChamadoSuporte>> agruparAbertosPorPrioridade() {
        Map<Prioridade, List<ChamadoSuporte>> mapa = new LinkedHashMap<>();
        Prioridade[] ordem = {Prioridade.ALTA, Prioridade.MEDIA, Prioridade.BAIXA};
        for (Prioridade p : ordem) {
            mapa.put(p, new ArrayList<>());
        }
        for (ChamadoSuporte c : filtrarPorStatus(StatusChamado.ABERTO)) {
            mapa.get(c.getPrioridade()).add(c);
        }
        for (List<ChamadoSuporte> lista : mapa.values()) {
            lista.sort(Comparator.comparing(ChamadoSuporte::getDataAbertura));
        }
        return mapa;
    }
}
