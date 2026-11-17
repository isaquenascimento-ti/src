package ui;

import java.util.Scanner;

import model.Operacao;
import model.Solicitacao;
import service.CentralDeAtendimento;



public class Menu{//inicio da classe Menu

    // a central cuida das filas, da pilha, das listas e das regras do sistema
    private CentralDeAtendimento centralAtendimento = new CentralDeAtendimento();

    // scanner para ler a entrada do usuário
    private Scanner scanner = new Scanner(System.in);


    // lê um texto que não pode ficar em branco: enquanto vier vazio, pede de novo
    private String lerTextoObrigatorio(String mensagem) {
        String texto = "";
        while (texto.isEmpty()) {
            System.out.print(mensagem);
            texto = scanner.nextLine().trim();
            if (texto.isEmpty()) {
                System.out.println("Erro! Este campo não pode ficar em branco.");
            }
        }
        return texto;
    }


    // mostra o menu e executa a opção escolhida; chamado pelo Main
    public boolean exibir() throws Exception{//inicio exibir

        //menu
        System.out.println(//inicio menu
            "\n=================================================\n" +
            "        CENTRAL DE ATENDIMENTO E SERVIÇOS        \n" +
            "=================================================\n" +
            "ATENDIMENTO\n" +
            "1 - Cadastrar nova solicitação\n" +
            "2 - Consultar próxima solicitação\n" +
            "3 - Atender próxima solicitação\n" +
            "4 - Exibir fila de espera\n" +
            "5 - Quantidade de solicitações aguardando\n" +
            "21 - Cancelar solicitação\n\n" +

            "OPERAÇÕES\n" +
            "6 - Consultar última operação\n" +
            "7 - Concluir atendimento\n" +
            "8 - Exibir histórico recente\n" +
            "9 - Desfazer última operação\n\n" +

            "CADASTRO GERAL\n" +
            "10 - Exibir todas as solicitações\n" +
            "11 - Pesquisar solicitação por código\n" +
            "12 - Remover solicitação (somente CANCELADAS)\n" +
            "13 - Exibir solicitações por status\n" +
            "14 - Exibir solicitações por categoria\n\n" +

            "HISTÓRICO DE ATENDIMENTOS\n" +
            "15 - Exibir do primeiro ao último\n" +
            "16 - Exibir do último ao primeiro\n" +
            "17 - Navegar pelo histórico\n" +
            "18 - Pesquisar atendimento\n\n" +

            "RELATÓRIOS E SISTEMA\n" +
            "19 - Exibir resumo do sistema\n" +
            "20 - Inserir dados sintéticos\n\n" + // funcionalidade extra, para testes

            "0 - Encerrar\n" +
            "================================================="
        );//fim menu

        // solicita ao usuário que escolha uma opção do menu
        System.out.print("\nEscolha uma opção: ");
        int opcao;

        try{
            opcao = scanner.nextInt();
        } catch (java.util.InputMismatchException e) {
            System.out.println("\nErro! Digite apenas números.");
            scanner.nextLine(); // limpa o buffer do teclado
            return false; // continua o loop do Main
        }

        switch (opcao) {
            case 1:{ // CADASTRAR NOVA SOLICITAÇÃO
                System.out.println("\nOpção 1 selecionada: Cadastrar nova solicitação\n");

                scanner.nextLine(); // limpa o buffer do teclado

                // pede as informações necessárias para criar uma nova solicitação
                String descricao = lerTextoObrigatorio("Digite a descrição: ");

                String categoria = lerTextoObrigatorio("Digite a categoria: ");

                System.out.println("Prioridades: 1 = Alta | 2 = Baixa");

                // pede a prioridade; se a entrada for inválida, pede de novo
                int prioridade = -1;
                boolean prioridadeValida = false;
                while (!prioridadeValida) {
                    System.out.print("Digite a prioridade: ");
                    try {
                        prioridade = scanner.nextInt();
                        prioridadeValida = true;
                        // só aceita prioridade 1 ou 2
                        if (prioridade != 1 && prioridade != 2) {
                            System.out.println("Erro! A prioridade deve ser 1 (Alta) ou 2 (Baixa).");
                            prioridadeValida = false;
                        }
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("Erro! Digite apenas números.");
                        scanner.nextLine(); // limpa o buffer do teclado
                    }
                }
                scanner.nextLine(); // limpa o buffer do teclado

                String nome = lerTextoObrigatorio("Digite o nome do solicitante: ");

                Solicitacao nova = centralAtendimento.cadastrar(nome, descricao, categoria, prioridade);

                System.out.println("\nSolicitação cadastrada com sucesso!\n");
                System.out.print(nova);

                break;
            }

            case 2:{ // CONSULTAR PRÓXIMA SOLICITAÇÃO
                System.out.println("\nOpção 2 selecionada: Consultar próxima solicitação");
                if(centralAtendimento.filaVazia()){
                    System.out.println("\nA fila está vazia.");
                }
                else{
                    System.out.println("\n--- PRÓXIMA SOLICITAÇÃO A SER ATENDIDA ---");
                    System.out.print(centralAtendimento.primeiraDaFila());
                }
                break;
            }

            case 3:{ // ATENDER PRÓXIMA SOLICITAÇÃO
                System.out.println("\nOpção 3 selecionada: Atender próxima solicitação");
                if(centralAtendimento.filaVazia()){
                    System.out.println("\nA fila está vazia. Não há solicitação para atender.");
                }
                else{
                    scanner.nextLine(); // limpa o buffer do teclado

                    String responsavelAtendimento = lerTextoObrigatorio("\nDigite o nome do responsável pelo atendimento: ");

                    // a central tira da fila, muda o status e empilha a operação
                    Solicitacao seguraSolicitacao = centralAtendimento.atender(responsavelAtendimento);

                    System.out.println("\nAtendimento iniciado com sucesso!");
                    System.out.println("Código: " + seguraSolicitacao.getCodigo() + " | Solicitante: " + seguraSolicitacao.getSolicitante() + " | Prioridade: " + seguraSolicitacao.getPrioridade());
                }
                break;
            }

            case 4:{ // EXIBIR FILA DE ESPERA
                System.out.println("\nOpção 4 selecionada: Exibir fila de espera");
                if(centralAtendimento.filaVazia()){
                    System.out.println("\nA fila está vazia.");
                }

                else{
                    System.out.println("\n--- ORDEM GLOBAL DE ATENDIMENTO ---");
                    System.out.println(centralAtendimento.filaGlobal());

                    System.out.println("--- FILA DE PRIORIDADE 1 (ALTA) ---");
                    System.out.println(centralAtendimento.filaPorPrioridade(1));

                    System.out.println("--- FILA DE PRIORIDADE 2 (BAIXA) ---");
                    System.out.print(centralAtendimento.filaPorPrioridade(2));
                }
                break;
            }

            case 5:{ // QUANTIDADE DE SOLICITAÇÕES AGUARDANDO
                System.out.println("\nOpção 5 selecionada: Quantidade de solicitações aguardando");
                System.out.printf("\nSolicitações aguardando: %d\n", centralAtendimento.tamanhoFila());
                System.out.printf(" - Prioridade 1 (Alta): %d\n", centralAtendimento.tamanhoPrioridade(1));
                System.out.printf(" - Prioridade 2 (Baixa): %d\n", centralAtendimento.tamanhoPrioridade(2));
                break;
            }

            case 6:{ // CONSULTAR ÚLTIMA OPERAÇÃO
                System.out.println("\nOpção 6 selecionada: Consultar última operação");
                if(centralAtendimento.pilhaVazia()){
                    System.out.println("\nNão há operações registradas.");
                }

                else{
                    Operacao seguraOperacao = centralAtendimento.ultimaOperacao();
                    System.out.println("\n--- ÚLTIMA OPERAÇÃO REALIZADA ---");
                    System.out.println("Tipo: " + seguraOperacao.getTipo());
                    System.out.println("Data/hora: " + seguraOperacao.getDataHoraFormatada());
                    System.out.println("Código da solicitação: " + seguraOperacao.getSolicitacao().getCodigo());
                    System.out.println("Solicitante: " + seguraOperacao.getSolicitacao().getSolicitante());
                    System.out.println("Prioridade: " + seguraOperacao.getSolicitacao().getPrioridade());
                }

                break;
            }

            case 7:{ // CONCLUIR ATENDIMENTO
                System.out.println("\nOpção 7 selecionada: Concluir atendimento");

                if (!centralAtendimento.existeEmAtendimento()) {
                    System.out.println("\nNão há solicitações em atendimento para concluir.");
                    break;
                }

                // usa o buscaStatus para listar as solicitações em atendimento
                System.out.println("\n--- SOLICITAÇÕES EM ATENDIMENTO ---");
                System.out.print(centralAtendimento.buscaStatus("EM_ATENDIMENTO"));

                System.out.print("Digite o código da solicitação a concluir: ");
                scanner.nextLine(); // limpa o buffer do teclado

                int codigoConcluir;
                try {
                    codigoConcluir = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("\nErro! Digite apenas números.");
                    break;
                }

                System.out.println("\n" + centralAtendimento.concluirAtendimento(codigoConcluir));
                break;
            }

            case 8:{ // EXIBIR HISTÓRICO RECENTE
                System.out.println("\nOpção 8 selecionada: Exibir histórico recente");
                if (centralAtendimento.pilhaVazia()) {
                    System.out.println("\nNão há operações registradas.");
                }
                else{
                    System.out.println("\n--- HISTÓRICO DE OPERAÇÕES (DA MAIS RECENTE PARA A MAIS ANTIGA) ---");
                    // a central desempilha, monta o texto e devolve a pilha como estava
                    System.out.print(centralAtendimento.historico());
                }
                break;
            }

            case 9:{ // DESFAZER ÚLTIMA OPERAÇÃO
                System.out.println("\nOpção 9 selecionada: Desfazer última operação");
                if (centralAtendimento.pilhaVazia()) {
                    System.out.println("\nNão há operações para desfazer.");
                } else {
                    // a central desfaz e devolve a mensagem com o resultado
                    System.out.println("\n" + centralAtendimento.desfazer());
                }
                break;
            }




            case 10:{ // EXIBIR TODAS AS SOLICITAÇÕES (percorre a lista simples)
                System.out.println("\nOpção 10 selecionada: Exibir todas as solicitações");

                if(centralAtendimento.listaSimplesVazia()){
                    System.out.println("\nNão há solicitações cadastradas.");
                }
                else{
                    System.out.println("\n--- CADASTRO GERAL DE SOLICITAÇÕES ---");
                    System.out.print(centralAtendimento.listar());
                }
                break;
            }



            case 11:{ // PESQUISAR SOLICITAÇÃO POR CÓDIGO
                System.out.println("\nOpção 11 selecionada: Pesquisar solicitação por código");

                if(centralAtendimento.listaSimplesVazia()){
                    System.out.println("\nNão há solicitações cadastradas.");
                    break;
                }

                System.out.print("\nDigite o código da solicitação: ");
                scanner.nextLine(); // limpa o buffer do teclado
                int codigoDaBusca;

                // lê o código, com tratamento de erro para letras
                try {
                    codigoDaBusca = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("\nErro! Digite apenas números.");
                    break;
                }

                Solicitacao achei = centralAtendimento.buscaCodigo(codigoDaBusca);

                if(achei==null){
                    System.out.println("\nNão existe solicitação com o código " + codigoDaBusca + ".");
                    break;
                }

                System.out.println("\n--- SOLICITAÇÃO ENCONTRADA ---");
                System.out.print(achei);

                break;
            }



            case 12:{ // REMOVER SOLICITAÇÃO
                System.out.println("\nOpção 12 selecionada: Remover solicitação");
                if(centralAtendimento.listaSimplesVazia()){
                    System.out.println("\nNão há solicitações cadastradas.");
                    break;
                }


                System.out.print("\nDigite o código da solicitação a remover: ");
                scanner.nextLine(); // limpa o buffer do teclado
                int codigoDaBusca;

                // lê o código, com tratamento de erro para letras
                try {
                    codigoDaBusca = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("\nErro! Digite apenas números.");
                    break;
                }


                // a central só remove CANCELADAS e devolve a mensagem com o resultado
                System.out.println("\n" + centralAtendimento.remover(codigoDaBusca));
                break;
            }




            case 13:{ // EXIBIR SOLICITAÇÕES POR STATUS
                System.out.println("\nOpção 13 selecionada: Exibir solicitações por status");
                if(centralAtendimento.listaSimplesVazia()){
                    System.out.println("\nNão há solicitações cadastradas.");
                    break;
                }

                else{
                    System.out.print("\nDigite o status (AGUARDANDO, EM_ATENDIMENTO, CONCLUIDA ou CANCELADA): ");
                    scanner.nextLine(); // limpa o buffer do teclado
                    String statusDaBusca;

                    statusDaBusca = scanner.nextLine().trim();

                    if (!statusDaBusca.equalsIgnoreCase("AGUARDANDO") && !statusDaBusca.equalsIgnoreCase("EM_ATENDIMENTO") && !statusDaBusca.equalsIgnoreCase("CONCLUIDA") && !statusDaBusca.equalsIgnoreCase("CANCELADA")) {
                        System.out.println("\nErro! Status inválido.");
                        break;
                    }
                    System.out.println("\n--- SOLICITAÇÕES COM STATUS " + statusDaBusca.toUpperCase() + " ---");
                    String achei = centralAtendimento.buscaStatus(statusDaBusca);
                    System.out.println(achei);
                }
                break;
            }

            case 14:{ // EXIBIR SOLICITAÇÕES POR CATEGORIA
                System.out.println("\nOpção 14 selecionada: Exibir solicitações por categoria");
                if(centralAtendimento.listaSimplesVazia()){
                    System.out.println("\nNão há solicitações cadastradas.");
                    break;
                }

                else{
                    System.out.print("\nDigite a categoria: ");
                    scanner.nextLine(); // limpa o buffer do teclado
                    String categoriaDaBusca;

                    categoriaDaBusca = scanner.nextLine().trim();

                    // a categoria é livre (o usuário digita), só não pode ficar em branco
                    if (categoriaDaBusca.isEmpty()) {
                        System.out.println("\nErro! A categoria não pode ficar em branco.");
                        break;
                    }
                    System.out.println("\n--- SOLICITAÇÕES DA CATEGORIA " + categoriaDaBusca.toUpperCase() + " ---");
                    String achei = centralAtendimento.buscaCategoria(categoriaDaBusca);
                    System.out.println(achei);
                }
                break;
            }





            case 15:{ // HISTÓRICO DE ATENDIMENTOS, DO PRIMEIRO AO ÚLTIMO
                System.out.println("\nOpção 15 selecionada: Exibir histórico de atendimentos do primeiro ao último");

                if (centralAtendimento.listaDuplaVazia()) {
                    System.out.println("\nNão há atendimentos concluídos.");
                    break;
                }

                System.out.println("\n--- HISTÓRICO DE ATENDIMENTOS (DO PRIMEIRO AO ÚLTIMO) ---");
                System.out.print(centralAtendimento.atendimentoPU());
                break;
            }


            case 16:{ // HISTÓRICO DE ATENDIMENTOS, DO ÚLTIMO AO PRIMEIRO
                System.out.println("\nOpção 16 selecionada: Exibir histórico de atendimentos do último ao primeiro");

                if (centralAtendimento.listaDuplaVazia()) {
                    System.out.println("\nNão há atendimentos concluídos.");
                    break;
                }

                System.out.println("\n--- HISTÓRICO DE ATENDIMENTOS (DO ÚLTIMO AO PRIMEIRO) ---");
                System.out.print(centralAtendimento.atendimentoUP());
                break;
            }



            case 17:{ // NAVEGAR PELO HISTÓRICO
                System.out.println("\nOpção 17 selecionada: Navegar pelo histórico");

                if (centralAtendimento.listaDuplaVazia()) {
                    System.out.println("\nNão há atendimentos concluídos.");
                    break;
                }

                scanner.nextLine(); // limpa o buffer do teclado

                centralAtendimento.inicio(); // começa no primeiro atendimento

                String opcaoN= "";
                while (!opcaoN.equalsIgnoreCase("S")) {   // navega pelo histórico até o usuário digitar S (sair)
                    System.out.println("\n--- ATENDIMENTO ---");
                    System.out.print(centralAtendimento.atendimento());   // mostra o atendimento em que a navegação está
                    System.out.println("\n[A] Anterior | [P] Próximo | [S] Sair");
                    System.out.print("Escolha uma opção: ");

                    opcaoN = scanner.nextLine().trim();

                    if (opcaoN.equalsIgnoreCase("A")) {
                        if (!centralAtendimento.anterior()) {
                            System.out.println("\nEste é o primeiro atendimento.");
                        }
                    }
                    else if (opcaoN.equalsIgnoreCase("P")) {
                        if (!centralAtendimento.proximo()) {
                            System.out.println("\nEste é o último atendimento.");
                        }
                    }
                    else if (!opcaoN.equalsIgnoreCase("S")) {
                        System.out.println("\nErro! Opção inválida. Digite A, P ou S.");
                    }
                }
                break;
            }




            case 18:{ // PESQUISAR ATENDIMENTO (pelo código da solicitação)
                System.out.println("\nOpção 18 selecionada: Pesquisar atendimento");

                if (centralAtendimento.listaDuplaVazia()) {
                    System.out.println("\nNão há atendimentos concluídos.");
                    break;
                }

                scanner.nextLine(); // limpa o buffer do teclado

                System.out.print("\nDigite o código da solicitação: ");
                int codigoAtendimento;
                try {
                    codigoAtendimento = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("\nErro! Digite apenas números.");
                    break;
                }

                Solicitacao atendimentoAchado = centralAtendimento.buscaAtendimento(codigoAtendimento);

                if (atendimentoAchado == null) {
                    System.out.println("\nNão existe atendimento concluído com o código " + codigoAtendimento + ".");
                } else {
                    System.out.println("\n--- ATENDIMENTO ENCONTRADO ---");
                    System.out.print(atendimentoAchado);
                }
                break;
            }



            case 19:{ // EXIBIR RESUMO DO SISTEMA
                System.out.println("\nOpção 19 selecionada: Exibir resumo do sistema");
                System.out.print(
                    "\n=================================================\n" +
                    "               RESUMO DO SISTEMA                 \n" +
                    "=================================================\n" +
                    "Solicitações no cadastro geral: " + centralAtendimento.total() + "\n" +
                    " - Aguardando prioridade 1 (Alta): " + centralAtendimento.aguardandoP1() + "\n" +
                    " - Aguardando prioridade 2 (Baixa): " + centralAtendimento.aguardandoP2() + "\n" +
                    " - Em atendimento: " + centralAtendimento.emAtendimento() + "\n" +
                    " - Concluídas: " + centralAtendimento.concluidos() + "\n" +
                    " - Canceladas: " + centralAtendimento.canceladas() + "\n" +
                    "Operações registradas na pilha: " + centralAtendimento.pilhaDeOperacoes() + "\n" +
                    "=================================================\n"
                );
                break;
            }


            case 20:{ // INSERIR DADOS SINTÉTICOS
                System.out.println("\nOpção 20 selecionada: Inserir dados sintéticos");

                // a central devolve false se alguma solicitação de teste ainda está na fila aguardando
                if (!centralAtendimento.inserirDadosSinteticos()) {
                    System.out.println("\nErro! Os dados sintéticos já foram inseridos e ainda estão na fila.");
                    System.out.println("Atenda ou cancele essas solicitações antes de inserir novamente.");
                    break; // volta para o menu
                }
                System.out.println("\nDados sintéticos inseridos com sucesso! 6 solicitações (4 de prioridade 1 e 2 de prioridade 2).");

                break;
            }

            case 21:{ // CANCELAR SOLICITAÇÃO
                System.out.println("\nOpção 21 selecionada: Cancelar solicitação");
                if(centralAtendimento.listaSimplesVazia()){
                    System.out.println("\nNão há solicitações cadastradas.");
                    break;
                }

                System.out.print("\nDigite o código da solicitação a cancelar: ");
                scanner.nextLine(); // limpa o buffer do teclado
                int codigoCancelar;

                // lê o código, com tratamento de erro para letras
                try {
                    codigoCancelar = Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("\nErro! Digite apenas números.");
                    break;
                }

                // a central só cancela AGUARDANDO ou EM_ATENDIMENTO e devolve a mensagem com o resultado
                System.out.println("\n" + centralAtendimento.cancelar(codigoCancelar));
                break;
            }

            case 0:{
                System.out.println("\nEncerrando o programa...");
                return true; // sai do método exibir, encerrando o programa
            }
            default:{
                System.out.println("\nErro! Essa opção não existe.");
            }
        }
        return false; // continua o loop do Main
    }//fim exibir
}//fim menu
