package app;
import java.util.NoSuchElementException;

import ui.Menu;


public class Main {
    public static void main(String[] args) throws Exception{
        Menu menu = new Menu();

        boolean fim = false;

        // loop principal: mantém o programa rodando até o usuário digitar 0
        while (fim==false) {
            try {
                fim=menu.exibir();
            } catch (NoSuchElementException e) {
                // a entrada de dados terminou (não há mais nada para ler): encerra o programa
                fim = true;
            } catch (Exception e) {
                // proteção geral: um erro não previsto mostra uma mensagem e volta para o menu
                System.out.println("\nErro inesperado: " + e.getMessage());
            }
        }

    }
}

    
