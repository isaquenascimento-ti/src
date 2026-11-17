package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Solicitacao {//inicio class solicitação
    private int codigo;
    private String solicitante;
    private String descricao;
    private String categoria;
    private int prioridade;
    private LocalDateTime dataHora;
    private String status;
    private String responsavelAtendimento;

    public Solicitacao(int codigo, String solicitante, String descricao, String categoria, int prioridade, LocalDateTime dataHora, String status, String responsavelAtendimento) {//inicio metodo solicitacao
        this.codigo = codigo;
        this.solicitante = solicitante;
        this.descricao = descricao;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.dataHora = dataHora;
        this.status = status;
        this.responsavelAtendimento=responsavelAtendimento;
    }//fim metodo solicitacao



    // getters e setters

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
    public String getResponsavelAtendimento() {
        return responsavelAtendimento;
    }

    public void setResponsavelAtendimento(String responsavelAtendimento) {
        this.responsavelAtendimento = responsavelAtendimento;
    }


    @Override
    public String toString() {
        return "Código: " + this.codigo + 
               "\nSolicitante: " + this.solicitante + 
               "\nDescrição: " + this.descricao + 
               "\nCategoria: " + this.categoria + 
               "\nPrioridade: " + this.prioridade + 
               "\nData/hora: " + this.dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
               "\nStatus: " + this.status +
               "\nResponsável pelo atendimento: " + (this.responsavelAtendimento == null ? "-" : this.responsavelAtendimento) + "\n";
    }
}//fim class solicitação




