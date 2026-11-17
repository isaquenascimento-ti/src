package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Operacao {
    private String tipo;
    private Solicitacao solicitacao;
    // data e hora em que a operação foi feita (para o histórico de operações)
    private LocalDateTime dataHora;
    // usada para desfazer uma operação de atendimento e recuperar o estado anterior do rodízio
    private int altasConsecutivasAntes = 0;
    // usados para desfazer CANCELAMENTO e REMOCAO: como a solicitação estava antes
    private String statusAnterior;
    private int posicaoAnterior = 0; // posição na fila (CANCELAMENTO) ou no cadastro geral (REMOCAO), começando em 1

    public Operacao(String tipo, Solicitacao solicitacao) {
        this.tipo = tipo;
        this.solicitacao = solicitacao;
        this.dataHora = LocalDateTime.now();
    }

    // construtor com o atributo altasConsecutivasAntes, usado nas operações de ATENDIMENTO
    public Operacao(String tipo, Solicitacao solicitacao, int altasConsecutivasAntes) {
        this.tipo = tipo;
        this.solicitacao = solicitacao;
        this.altasConsecutivasAntes = altasConsecutivasAntes;
        this.dataHora = LocalDateTime.now();
    }

    public int getAltasConsecutivasAntes() {
        return altasConsecutivasAntes;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public Solicitacao getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(Solicitacao solicitacao) {
        this.solicitacao = solicitacao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    // data e hora no formato dia/mês/ano hora:minuto:segundo, para exibir no menu
    public String getDataHoraFormatada() {
        return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    public String getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(String statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public int getPosicaoAnterior() {
        return posicaoAnterior;
    }

    public void setPosicaoAnterior(int posicaoAnterior) {
        this.posicaoAnterior = posicaoAnterior;
    }

}
