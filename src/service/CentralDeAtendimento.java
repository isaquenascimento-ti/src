package service;

import java.time.LocalDateTime;
import estrutura.LinkedList;
import estrutura.TADFila;
import estrutura.ListaDupla;
import estrutura.NoDuplo;
import estrutura.Fila;
import estrutura.Pilha;
import estrutura.Node;
import model.Operacao;
import model.Solicitacao;

public class CentralDeAtendimento {//inicio da classe CentralDeAtendimento

    // contador de solicitações cadastradas; também é usado como código da solicitação
    private int contador=0;

    // ESTRUTURAS DE DADOS
    private Pilha<Operacao> pilhaDeOperacoes = new Pilha<>();
    private Fila<Solicitacao> filaDeSolicitacoes = new Fila<>();
    private Fila<Solicitacao> filaDeSolicitacoesP2 = new Fila<>();
    private LinkedList<Solicitacao> listaDeSolicitacoes = new LinkedList<>();
    private ListaDupla<Solicitacao> historicoAtendimentos = new ListaDupla<>();



    // rodízio: 3 atendimentos de prioridade 1 (alta) para 1 de prioridade 2 (baixa), para a prioridade 2 não esperar para sempre
    public static final int LIMITE_ALTA_CONSECUTIVA = 3;

    // quantos atendimentos seguidos de prioridade 1 já ocorreram enquanto havia prioridade 2 esperando
    private int altasConsecutivas = 0;

    // solicitações de teste da opção 20 (dados sintéticos)
    private Solicitacao joao = null;
    private Solicitacao maria = null;
    private Solicitacao pedro = null;
    private Solicitacao andre = null;
    private Solicitacao carlos = null;
    private Solicitacao ana = null;


// =-=-=-=-=-=-=-= MÉTODOS DA PRIORIDADE (ALTA E BAIXA) PARA SABER QUAL FILA ATENDER =-=-=-=-=-=-=-=

    // decide qual fila atende: se só uma fila tem solicitações, é ela que atende;
    // se as duas têm, atende a prioridade 1 (alta) até completar LIMITE_ALTA_CONSECUTIVA
    private Fila<Solicitacao> filaDaVez() throws Exception {
        boolean p1Vazia = filaDeSolicitacoes.qIsEmpty();
        boolean p2Vazia = filaDeSolicitacoesP2.qIsEmpty();

        if (p1Vazia && !p2Vazia) {
            return filaDeSolicitacoesP2;
        }
        if (p2Vazia) {
            return filaDeSolicitacoes;
        }
        return (altasConsecutivas >= LIMITE_ALTA_CONSECUTIVA) ? filaDeSolicitacoesP2 : filaDeSolicitacoes;
    }

    // usado na opção 4
    // monta o texto da fila global (com as 2 prioridades), sem criar uma nova lista com todas as solicitações
    public String filaGlobal() throws Exception {
        TADFila<Solicitacao> aux1 = new Fila<>();
        TADFila<Solicitacao> aux2 = new Fila<>();
        String texto = "";
        int simulaAltas = altasConsecutivas;  // cópia, para a exibição não mexer no rodízio de verdade
        int posicao = 0;

        while (!filaDeSolicitacoes.qIsEmpty() || !filaDeSolicitacoesP2.qIsEmpty()) {
            boolean p1Vazia = filaDeSolicitacoes.qIsEmpty();
            boolean p2Vazia = filaDeSolicitacoesP2.qIsEmpty();
            boolean daP2 = (p1Vazia && !p2Vazia) || (!p1Vazia && !p2Vazia && simulaAltas >= LIMITE_ALTA_CONSECUTIVA);

            Solicitacao s;
            if (daP2) {
                s = filaDeSolicitacoesP2.dequeue();
                aux2.enqueue(s);
                simulaAltas = 0;
            }
            else {
                s = filaDeSolicitacoes.dequeue();
                aux1.enqueue(s);
                if (!p2Vazia) {
                    simulaAltas++;
                }
            }

            posicao++;
            texto += posicao + "º | Prioridade " + s.getPrioridade() + " | Código: " + s.getCodigo() + " | Solicitante: " + s.getSolicitante() + " | Descrição: " + s.getDescricao() + "\n";
        }

        // devolve cada solicitação para a sua fila, na ordem original
        while (!aux1.qIsEmpty()) {
            filaDeSolicitacoes.enqueue(aux1.dequeue());
        }
        while (!aux2.qIsEmpty()) {
            filaDeSolicitacoesP2.enqueue(aux2.dequeue());
        }

        return "(Rodízio: " + LIMITE_ALTA_CONSECUTIVA + " de prioridade 1 para cada 1 de prioridade 2)\n" + texto;
    }

    // usado na opção 5
    // quantidade aguardando em cada prioridade
    public int tamanhoPrioridade(int prioridade) throws Exception {
        if (prioridade==1){
            return filaDeSolicitacoes.size();
        }


        return filaDeSolicitacoesP2.size();

    }

    // usado na opção 7
    // verifica se existe alguma solicitação EM_ATENDIMENTO no cadastro geral
    public boolean existeEmAtendimento() throws Exception {
        Node<Solicitacao> atual = listaDeSolicitacoes.getHead();

        while (atual != null) {
            if (atual.getDado().getStatus().equals("EM_ATENDIMENTO")) {
                return true;
            }
            atual = atual.getProx();
        }
        return false;
    }


// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- MÉTODOS AUXILIARES =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    public boolean filaVazia() throws Exception{
        return filaDeSolicitacoes.qIsEmpty() && filaDeSolicitacoesP2.qIsEmpty();
    }

    public boolean pilhaVazia() throws Exception{
        return pilhaDeOperacoes.isEmpty();
    }

    public boolean listaSimplesVazia() throws Exception{
        return listaDeSolicitacoes.isEmpty();
    }

    public boolean listaDuplaVazia() throws Exception{
        return historicoAtendimentos.isEmpty();
    }


// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=- MÉTODOS PRINCIPAIS (DE CADA CASE) =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    // ===================== CASE 1 = Cadastrar nova solicitação ======================
    public Solicitacao cadastrar(String nome, String descricao, String categoria, int prioridade) throws Exception {
        if (prioridade != 1 && prioridade != 2) {
            throw new IllegalArgumentException("Prioridade inválida: " + prioridade + ". Use 1 (Alta) ou 2 (Baixa).");
        }

        // data e hora do cadastro, guardadas na solicitação
        LocalDateTime dataHora = LocalDateTime.now();

        contador++; // soma 1 a cada nova solicitação, mesmo que ela seja desfeita depois,
                    // assim o código é único e nunca se repete

        // cria a solicitação com as informações fornecidas pelo usuário; sempre começa como AGUARDANDO
        Solicitacao novaSolicitacao = new Solicitacao(contador, nome, descricao, categoria, prioridade, dataHora, "AGUARDANDO", null);

        if (prioridade == 2) {
            filaDeSolicitacoesP2.enqueue(novaSolicitacao);
        } else {
            filaDeSolicitacoes.enqueue(novaSolicitacao);
        }

        // registra a operação na pilha
        Operacao operacao = new Operacao("CADASTRO", novaSolicitacao);
        pilhaDeOperacoes.push(operacao);

        // adiciona no cadastro geral (lista simples)
        listaDeSolicitacoes.addLast(novaSolicitacao);

        return novaSolicitacao;
    }


    // ==================== CASE 2 = Consultar próxima solicitação ====================
    // devolve a próxima solicitação a ser atendida, sem tirá-la da fila
    public Solicitacao primeiraDaFila() throws Exception  {
        if (filaDaVez() == filaDeSolicitacoesP2) {
            return filaDeSolicitacoesP2.front();
        }
        return filaDeSolicitacoes.front();
    }


    // ===================== CASE 3 = Atender próxima solicitação =====================
    public Solicitacao atender(String responsavelAtendimento) throws Exception {

        Fila<Solicitacao> filaEscolhida = filaDaVez(); // descobre qual fila atende agora: prioridade alta ou baixa
        int altasAntes = altasConsecutivas; // guarda o rodízio de antes, para o desfazer

        Solicitacao seguraSolicitacao = filaEscolhida.dequeue(); // tira a primeira solicitação da fila escolhida

        seguraSolicitacao.setStatus("EM_ATENDIMENTO");
        seguraSolicitacao.setResponsavelAtendimento(responsavelAtendimento);

        // atualiza o rodízio: se atendeu prioridade 2, zera; se atendeu prioridade 1 com prioridade 2 esperando, soma 1
        if (filaEscolhida == filaDeSolicitacoesP2) {
            altasConsecutivas = 0;
        } else if (!filaDeSolicitacoesP2.qIsEmpty()) {
            altasConsecutivas++;
        }

        // empilha a operação de atendimento, guardando o rodízio de antes para o desfazer
        Operacao operacao = new Operacao("ATENDIMENTO", seguraSolicitacao, altasAntes);
        pilhaDeOperacoes.push(operacao);

        return seguraSolicitacao;
    }


    // ===================== CASE 4 = Exibir fila de solicitações =====================
    // a ordem global está no método filaGlobal(), mais acima

    // monta o texto de uma das filas (1 ou 2) separadamente, na ordem de chegada
    // tira cada solicitação da frente e coloca de volta no fim; depois de dar a volta inteira, a fila fica igual
    public String filaPorPrioridade(int prioridade) throws Exception {
        Fila<Solicitacao> fila = (prioridade == 2) ? filaDeSolicitacoesP2 : filaDeSolicitacoes;
        String texto = "";
        int qtd = fila.size();

        for (int i = 0; i < qtd; i++) {
            Solicitacao s = fila.dequeue();
            texto += (i + 1) + "º | Código: " + s.getCodigo() + " | Solicitante: " + s.getSolicitante() + " | Descrição: " + s.getDescricao() + "\n";
            fila.enqueue(s);
        }

        if (texto.isEmpty()) {
            return "A fila está vazia.\n";
        }
        return texto;
    }


    // ================== CASE 5 = Exibir quantidade de solicitações ==================
    public int tamanhoFila() throws Exception{
        return filaDeSolicitacoes.size() + filaDeSolicitacoesP2.size(); // soma as 2 filas de prioridade
    }



    // ================= CASE 6 = Consultar última operação realizada =================
    // devolve o topo da pilha, sem tirá-lo
    public Operacao ultimaOperacao() throws Exception{
        return pilhaDeOperacoes.top();
    }



    // ======================== CASE 7 = Concluir atendimento ========================
    // só conclui solicitação EM_ATENDIMENTO e registra a operação CONCLUSAO na pilha
    // devolve a mensagem que o menu vai mostrar
    public String concluirAtendimento(int codigo) throws Exception {
        Solicitacao s = buscaCodigo(codigo);

        if (s == null) {
            return "Erro! Não existe solicitação com o código " + codigo + ".";
        }
        if (!s.getStatus().equals("EM_ATENDIMENTO")) {
            return "Erro! A solicitação " + codigo + " está " + s.getStatus() + ". Só é possível concluir solicitações EM_ATENDIMENTO.";
        }

        s.setStatus("CONCLUIDA");
        pilhaDeOperacoes.push(new Operacao("CONCLUSAO", s));

        historicoAtendimentos.insertTail(s);


        return "Atendimento da solicitação " + codigo + " concluído com sucesso!";
    }



    // ==================== CASE 8 = Exibir histórico de operações ====================
    // monta o texto do histórico, da operação mais recente para a mais antiga, e devolve para o menu imprimir
    public String historico() throws Exception {
        Pilha<Operacao> pilhaAuxiliar = new Pilha<>(); // guarda o que sai da pilha, para não perder as operações
        String caixinha_operacao="";

        // desempilha todas as operações, montando o texto
        while (!pilhaDeOperacoes.isEmpty()) {
            Operacao operacao = pilhaDeOperacoes.pop();
            caixinha_operacao += "[" + operacao.getDataHoraFormatada() + "] " + operacao.getTipo() + " | Solicitação " + operacao.getSolicitacao().getCodigo() + " | Prioridade " + operacao.getSolicitacao().getPrioridade() + "\n";
            pilhaAuxiliar.push(operacao);
        }

        // devolve as operações para a pilha original, na mesma ordem
        while (!pilhaAuxiliar.isEmpty()) {
            pilhaDeOperacoes.push(pilhaAuxiliar.pop());
        }
        return caixinha_operacao;
    }



    // ====================== CASE 9 = Desfazer última operação =======================
    // devolve a mensagem que o menu vai mostrar
    public String desfazer() throws Exception{
        String mensagem_tipo = "";

        // tira a última operação realizada (topo da pilha)
        Operacao ultimaOperacao = pilhaDeOperacoes.pop();

        // segurança: se a solicitação desse cadastro não está mais AGUARDANDO, ela não está no fim da fila
        // e o cadastro não pode ser desfeito; a operação é descartada e o usuário é avisado
        // (como cancelamento e remoção ficam registrados na pilha, isso não deve acontecer)
        if (ultimaOperacao.getTipo().equals("CADASTRO") && !ultimaOperacao.getSolicitacao().getStatus().equals("AGUARDANDO")) {
            return "Erro! Não foi possível desfazer o cadastro da solicitação " + ultimaOperacao.getSolicitacao().getCodigo()
                 + ", pois ela não está mais na fila. A operação foi descartada do histórico.";
        }

        // se for atendimento, devolve o rodízio ao estado de antes dele
        if (ultimaOperacao.getTipo().equals("ATENDIMENTO")) {
            altasConsecutivas = ultimaOperacao.getAltasConsecutivasAntes();
        }

        // CADASTRO de prioridade 2: mesma lógica do cadastro mais abaixo, mas na fila de prioridade 2
        if (ultimaOperacao.getTipo().equals("CADASTRO") && ultimaOperacao.getSolicitacao().getPrioridade() == 2) {
            int qtdParaMover = filaDeSolicitacoesP2.size() - 1;
            TADFila<Solicitacao> filaAux = new Fila<>();

            for (int i = 0; i < qtdParaMover; i++) {
                filaAux.enqueue(filaDeSolicitacoesP2.dequeue());
            }

            filaDeSolicitacoesP2.dequeue(); // remove o último da fila, que é o cadastro desfeito
            ultimaOperacao.getSolicitacao().setStatus("CANCELADA");

            while (!filaAux.qIsEmpty()) {
                filaDeSolicitacoesP2.enqueue(filaAux.dequeue());
            }

            return "Cadastro desfeito! A solicitação " + ultimaOperacao.getSolicitacao().getCodigo() + " saiu da fila e ficou CANCELADA.";
        }

        // ATENDIMENTO de prioridade 2: mesma lógica do atendimento mais abaixo, mas na fila de prioridade 2
        if (ultimaOperacao.getTipo().equals("ATENDIMENTO") && ultimaOperacao.getSolicitacao().getPrioridade() == 2) {
            ultimaOperacao.getSolicitacao().setStatus("AGUARDANDO");
            ultimaOperacao.getSolicitacao().setResponsavelAtendimento(null); // voltou para a fila, então não tem mais responsável

            TADFila<Solicitacao> filaRecolocacao = new Fila<>();
            filaRecolocacao.enqueue(ultimaOperacao.getSolicitacao());

            while(!filaDeSolicitacoesP2.qIsEmpty()){
                filaRecolocacao.enqueue(filaDeSolicitacoesP2.dequeue());
            }
            while(!filaRecolocacao.qIsEmpty()){
                filaDeSolicitacoesP2.enqueue(filaRecolocacao.dequeue());
            }

            return "Atendimento desfeito! A solicitação " + ultimaOperacao.getSolicitacao().getCodigo() + " voltou para a fila na posição original.";
        }

        // CONCLUSAO (opção 7): a solicitação volta para EM_ATENDIMENTO e sai do histórico de atendimentos
        if (ultimaOperacao.getTipo().equals("CONCLUSAO")) {
            ultimaOperacao.getSolicitacao().setStatus("EM_ATENDIMENTO");

            historicoAtendimentos.remove(ultimaOperacao.getSolicitacao());
            return "Conclusão desfeita! A solicitação " + ultimaOperacao.getSolicitacao().getCodigo() + " voltou para EM_ATENDIMENTO.";
        }

        // CANCELAMENTO (opção 21): volta o status de antes e, se estava aguardando, volta para a mesma posição da fila
        if (ultimaOperacao.getTipo().equals("CANCELAMENTO")) {
            Solicitacao s = ultimaOperacao.getSolicitacao();
            s.setStatus(ultimaOperacao.getStatusAnterior());

            if (s.getStatus().equals("AGUARDANDO")) {
                if (s.getPrioridade() == 2) {
                    inserirNaFila(filaDeSolicitacoesP2, s, ultimaOperacao.getPosicaoAnterior());
                } else {
                    inserirNaFila(filaDeSolicitacoes, s, ultimaOperacao.getPosicaoAnterior());
                }
                return "Cancelamento desfeito! A solicitação " + s.getCodigo() + " voltou para a fila na posição original.";
            }
            return "Cancelamento desfeito! A solicitação " + s.getCodigo() + " voltou para " + s.getStatus() + ".";
        }

        // REMOCAO (opção 12): a solicitação volta para o cadastro geral, na mesma posição (continua CANCELADA)
        if (ultimaOperacao.getTipo().equals("REMOCAO")) {
            listaDeSolicitacoes.insert(ultimaOperacao.getSolicitacao(), ultimaOperacao.getPosicaoAnterior());
            return "Remoção desfeita! A solicitação " + ultimaOperacao.getSolicitacao().getCodigo() + " voltou para o cadastro geral.";
        }

        // CADASTRO de prioridade 1: tira a solicitação do fim da fila
        if (ultimaOperacao.getTipo().equals("CADASTRO")) { // inicio if cadastro
            int qtdParaMover = filaDeSolicitacoes.size() - 1;
            TADFila<Solicitacao> filaAux = new Fila<>();

            for (int i = 0; i < qtdParaMover; i++) {
                filaAux.enqueue(filaDeSolicitacoes.dequeue());
            }

            filaDeSolicitacoes.dequeue(); // remove o último da fila, que é o cadastro desfeito
            // altera o status para não ficar mais AGUARDANDO
            ultimaOperacao.getSolicitacao().setStatus("CANCELADA");

            // devolve as outras solicitações para a fila original
            while (!filaAux.qIsEmpty()) {
                filaDeSolicitacoes.enqueue(filaAux.dequeue());
            }

            mensagem_tipo = "Cadastro desfeito! A solicitação " + ultimaOperacao.getSolicitacao().getCodigo() + " saiu da fila e ficou CANCELADA.";
        } // fim if cadastro

        // ATENDIMENTO de prioridade 1: a solicitação volta a ser AGUARDANDO e retorna para a frente da fila
        else if (ultimaOperacao.getTipo().equals("ATENDIMENTO")) { // inicio if atendimento
            ultimaOperacao.getSolicitacao().setStatus("AGUARDANDO");
            ultimaOperacao.getSolicitacao().setResponsavelAtendimento(null); // voltou para a fila, então não tem mais responsável

            // fila auxiliar para recolocar a solicitação na posição original
            TADFila<Solicitacao> filaRecolocacao = new Fila<>();

            // a solicitação do atendimento desfeito entra primeiro na auxiliar
            filaRecolocacao.enqueue(ultimaOperacao.getSolicitacao());

            // move todas as solicitações da fila original para a auxiliar
            while(!filaDeSolicitacoes.qIsEmpty()){
                filaRecolocacao.enqueue(filaDeSolicitacoes.dequeue());
            }
            // devolve tudo para a fila original, agora com a solicitação do atendimento desfeito na frente
            while(!filaRecolocacao.qIsEmpty()){
                filaDeSolicitacoes.enqueue(filaRecolocacao.dequeue());
            }

            mensagem_tipo = "Atendimento desfeito! A solicitação " + ultimaOperacao.getSolicitacao().getCodigo() + " voltou para a fila na posição original.";
        } // fim if atendimento

        return mensagem_tipo;
    }



    // ==================== CASE 10 = Exibir todas as solicitações ====================
    // percorre a lista simples e monta o texto com todas as solicitações
    public String listar() throws Exception {
        String resultado = "";
        Node<Solicitacao> atual = listaDeSolicitacoes.getHead();

        while (atual != null) {
            resultado = resultado + atual.getDado() + "\n";
            atual = atual.getProx();
        }
        return resultado + "Total de solicitações: " + listaDeSolicitacoes.getSize() + "\n";
    }




    // ================== CASE 11 = Pesquisar solicitação por código ==================
    public Solicitacao buscaCodigo (int codigoDaBusca) throws Exception{
        Node<Solicitacao> atual=listaDeSolicitacoes.getHead();

        while(atual!=null){
            // pega a solicitação do nó atual e compara o código dela
            if (atual.getDado().getCodigo()==codigoDaBusca){
                return atual.getDado();
            }
            atual=atual.getProx();
        }
        return null;

    }


    // ===================== MÉTODOS AUXILIARES DO CANCELAR E REMOVER =====================

    // tira a solicitação "alvo" da fila, mantendo a ordem das outras (usa uma fila auxiliar)
    // devolve a posição em que ela estava (começando em 1), ou 0 se não estava na fila
    private int removerDaFila(Fila<Solicitacao> fila, Solicitacao alvo) throws Exception {
        Fila<Solicitacao> aux = new Fila<>();
        int posicao = 0;
        int contador = 0;

        while (!fila.qIsEmpty()) {
            Solicitacao atual = fila.dequeue();
            contador++;
            if (posicao == 0 && atual == alvo) {
                posicao = contador;        // achou: guarda a posição e não vai para a auxiliar
            } else {
                aux.enqueue(atual);
            }
        }
        while (!aux.qIsEmpty()) {
            fila.enqueue(aux.dequeue());
        }
        return posicao;
    }

    // coloca a solicitação "s" de volta na fila, na posição informada (começando em 1)
    // passa todos para uma fila auxiliar, encaixando "s" quando chega na posição, e depois devolve tudo
    private void inserirNaFila(Fila<Solicitacao> fila, Solicitacao s, int posicao) throws Exception {
        Fila<Solicitacao> aux = new Fila<>();
        int contador = 1;
        boolean inserido = false;

        while (!fila.qIsEmpty()) {
            if (contador == posicao) {
                aux.enqueue(s);
                inserido = true;
            }
            aux.enqueue(fila.dequeue());
            contador++;
        }
        if (!inserido) {      // a posição era a última
            aux.enqueue(s);
        }
        while (!aux.qIsEmpty()) {
            fila.enqueue(aux.dequeue());
        }
    }

    // posição da solicitação no cadastro geral (começando em 1), ou 0 se não estiver lá
    private int posicaoNaLista(Solicitacao alvo) {
        Node<Solicitacao> atual = listaDeSolicitacoes.getHead();
        int posicao = 1;
        while (atual != null) {
            if (atual.getDado() == alvo) {
                return posicao;
            }
            atual = atual.getProx();
            posicao++;
        }
        return 0;
    }


    // ========================= CASE 21 = Cancelar solicitação =========================
    // só cancela AGUARDANDO ou EM_ATENDIMENTO; a solicitação continua no cadastro geral
    public String cancelar(int codigo) throws Exception {
        Solicitacao alvo = buscaCodigo(codigo);

        if (alvo == null) {
            return "Erro! Não existe solicitação com o código " + codigo + ".";
        }
        if (alvo.getStatus().equals("CANCELADA")) {
            return "Erro! A solicitação " + codigo + " já está CANCELADA.";
        }
        if (alvo.getStatus().equals("CONCLUIDA")) {
            return "Erro! A solicitação " + codigo + " já está CONCLUIDA e não pode ser cancelada.";
        }

        Operacao operacao = new Operacao("CANCELAMENTO", alvo);
        operacao.setStatusAnterior(alvo.getStatus());   // guarda o status de antes, para o desfazer

        // se estava aguardando, sai da fila da sua prioridade (e a posição é guardada para o desfazer)
        if (alvo.getStatus().equals("AGUARDANDO")) {
            if (alvo.getPrioridade() == 2) {
                operacao.setPosicaoAnterior(removerDaFila(filaDeSolicitacoesP2, alvo));
            } else {
                operacao.setPosicaoAnterior(removerDaFila(filaDeSolicitacoes, alvo));
            }
        }

        alvo.setStatus("CANCELADA");
        pilhaDeOperacoes.push(operacao);

        return "Solicitação " + codigo + " cancelada com sucesso!";
    }


    // ========================= CASE 12 = Remover solicitação =========================
    // só remove solicitações CANCELADAS: apaga do cadastro geral (lista simples)
    public String remover(int codigo) throws Exception {
        Solicitacao alvo = buscaCodigo(codigo);

        if (alvo == null) {
            return "Erro! Não existe solicitação com o código " + codigo + ".";
        }
        if (!alvo.getStatus().equals("CANCELADA")) {
            return "Erro! A solicitação " + codigo + " está " + alvo.getStatus() + ". Só é possível remover solicitações CANCELADAS (cancele primeiro pela opção 21).";
        }

        Operacao operacao = new Operacao("REMOCAO", alvo);
        operacao.setPosicaoAnterior(posicaoNaLista(alvo));   // guarda a posição no cadastro, para o desfazer

        listaDeSolicitacoes.remove(alvo);
        pilhaDeOperacoes.push(operacao);

        return "Solicitação " + codigo + " removida do cadastro geral com sucesso!";
    }




    // =================== CASE 13 = Exibir solicitações por status ===================
    public String buscaStatus (String statusDaBusca) throws Exception{
        String resultado = "";   // começa vazio
        Node<Solicitacao> atual=listaDeSolicitacoes.getHead();

        while(atual!=null){
            // pega a solicitação do nó atual e compara o status dela
            if (atual.getDado().getStatus().equalsIgnoreCase(statusDaBusca)){
                // achou: guarda no texto (o + chama o toString() da Solicitacao)
                resultado = resultado + atual.getDado() + "\n";
            }
            atual=atual.getProx();
        }

        if(resultado.isEmpty()){
            return "Não há solicitações com o status " + statusDaBusca.toUpperCase() + ".";
        }
        return resultado;
    }



    // ================= CASE 14 = Exibir solicitações por categoria ==================
    public String buscaCategoria (String categoriaDaBusca) throws Exception{
        String resultado = "";   // começa vazio
        Node<Solicitacao> atual=listaDeSolicitacoes.getHead();

        while(atual!=null){
            // pega a solicitação do nó atual e compara a categoria dela
            if (atual.getDado().getCategoria().equalsIgnoreCase(categoriaDaBusca)){
                // achou: guarda no texto (o + chama o toString() da Solicitacao)
                resultado = resultado + atual.getDado() + "\n";
            }
            atual=atual.getProx();
        }

        if(resultado.isEmpty()){
            return "Não há solicitações na categoria " + categoriaDaBusca + ".";
        }
        return resultado;
    }



    // ============== CASE 15 = Histórico de atendimentos, do primeiro ao último ==============
    // percorre a lista dupla da cabeça para a direita
    public String atendimentoPU() {
        String resultado = "";
        NoDuplo<Solicitacao> atual = historicoAtendimentos.getHead();

        while (atual != null) {
            resultado = resultado + atual.getData() + "\n";
            atual = atual.getRight();
        }
        return resultado;
    }



    // ============== CASE 16 = Histórico de atendimentos, do último ao primeiro ==============
    // vai até o último nó da lista dupla e volta para a esquerda
    public String atendimentoUP() {
        String resultado = "";
        NoDuplo<Solicitacao> atual = historicoAtendimentos.getHead();

        if (atual == null) {
            return resultado;
        }
        while (atual.getRight() != null) {
            atual = atual.getRight();
        }
        while (atual != null) {
            resultado = resultado + atual.getData() + "\n";
            atual = atual.getLeft();
        }
        return resultado;
    }


    // ======================== CASE 17 = Navegar pelo histórico ========================
    private NoDuplo<Solicitacao> atendimento = null;
    public void inicio() {
        atendimento=historicoAtendimentos.getHead();
    }


    public String atendimento() {
        return atendimento.getData().toString();
    }

    public boolean anterior() {
        if (atendimento.getLeft() == null) {
            return false;
        }
        atendimento = atendimento.getLeft();
        return true;
    }

    public boolean proximo() {
        if (atendimento.getRight() == null) {
            return false;
        }
        atendimento = atendimento.getRight();
        return true;
    }



    // ========================= CASE 18 = Pesquisar atendimento =========================
    // procura no histórico de atendimentos pelo código da solicitação
    public Solicitacao buscaAtendimento(int codigoDaBusca) {
        NoDuplo<Solicitacao> atual = historicoAtendimentos.getHead();

        while (atual != null) {
            if (atual.getData().getCodigo() == codigoDaBusca) {
                return atual.getData();
            }
            atual = atual.getRight();
        }
        return null;
    }



    // ======================= CASE 19 = Exibir resumo do sistema ========================
    public int total() throws Exception {
        return listaDeSolicitacoes.getSize();
    }

    public int aguardandoP1() throws Exception {
        return filaDeSolicitacoes.size();
    }

    public int aguardandoP2() throws Exception {
        return filaDeSolicitacoesP2.size();
    }

    public int emAtendimento() throws Exception {  // percorre a lista e conta as solicitações EM_ATENDIMENTO
        int contador= 0;
        Node<Solicitacao> atual = listaDeSolicitacoes.getHead();
        while (atual != null) {
            if (atual.getDado().getStatus().equals("EM_ATENDIMENTO")) {
                contador++;
            }
            atual = atual.getProx();
        }
        return contador;
    }

    public int concluidos() throws Exception {
        return historicoAtendimentos.getSize();
    }

    public int canceladas() throws Exception {      // percorre a lista e conta as solicitações CANCELADAS
        int contadorCanceladas = 0;
        Node<Solicitacao> atual = listaDeSolicitacoes.getHead();
        while (atual != null) {
            if (atual.getDado().getStatus().equals("CANCELADA")) {
                contadorCanceladas++;
            }
            atual = atual.getProx();
        }
        return contadorCanceladas;
    }

    public int pilhaDeOperacoes() throws Exception {
        return pilhaDeOperacoes.sizeElements();
    }


    // ====================== CASE 20 = Inserir dados sintéticos =======================
    // devolve false se os dados já estão na fila (o menu avisa o usuário)
    public boolean inserirDadosSinteticos() throws Exception {

        // verifica se alguma solicitação de teste ainda está na fila aguardando
        if (       joao != null && joao.getStatus().equals("AGUARDANDO")
                || maria != null && maria.getStatus().equals("AGUARDANDO")
                || pedro != null && pedro.getStatus().equals("AGUARDANDO")
                || andre != null && andre.getStatus().equals("AGUARDANDO")
                || carlos != null && carlos.getStatus().equals("AGUARDANDO")
                || ana != null && ana.getStatus().equals("AGUARDANDO")
            ) {
            return false;
        }

        LocalDateTime dataHora = LocalDateTime.now();

        joao = new Solicitacao(contador+=1, "João", "Problema no sistema", "TI", 1, dataHora, "AGUARDANDO",null);
        maria = new Solicitacao(contador+=1, "Maria", "Problema com impressora", "TI", 2, dataHora,"AGUARDANDO",null);
        pedro = new Solicitacao(contador+=1, "Pedro", "Problema com computador", "TI", 1, dataHora,"AGUARDANDO",null);
        andre = new Solicitacao(contador+=1, "André", "Problema com celular", "TI", 1, dataHora,"AGUARDANDO",null);
        carlos = new Solicitacao(contador+=1, "Carlos", "Problema com notebook", "TI", 2, dataHora,"AGUARDANDO",null);
        ana = new Solicitacao(contador+=1, "Ana", "Problema com monitor", "TI", 1, dataHora,"AGUARDANDO",null);

        filaDeSolicitacoes.enqueue(joao);
        filaDeSolicitacoesP2.enqueue(maria);
        filaDeSolicitacoes.enqueue(pedro);
        filaDeSolicitacoes.enqueue(andre);
        filaDeSolicitacoesP2.enqueue(carlos);
        filaDeSolicitacoes.enqueue(ana);

        pilhaDeOperacoes.push(new Operacao("CADASTRO", joao));
        pilhaDeOperacoes.push(new Operacao("CADASTRO", maria));
        pilhaDeOperacoes.push(new Operacao("CADASTRO", pedro));
        pilhaDeOperacoes.push(new Operacao("CADASTRO", andre));
        pilhaDeOperacoes.push(new Operacao("CADASTRO", carlos));
        pilhaDeOperacoes.push(new Operacao("CADASTRO", ana));


        listaDeSolicitacoes.addLast(joao);
        listaDeSolicitacoes.addLast(maria);
        listaDeSolicitacoes.addLast(pedro);
        listaDeSolicitacoes.addLast(andre);
        listaDeSolicitacoes.addLast(carlos);
        listaDeSolicitacoes.addLast(ana);


        return true;
    }

}//fim CentralDeAtendimento
