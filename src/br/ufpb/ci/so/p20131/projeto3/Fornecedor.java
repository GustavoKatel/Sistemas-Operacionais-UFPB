package br.ufpb.ci.so.p20131.projeto3;

import static java.lang.System.out;

/**
 * Um fornecedor de cana de a��car
 * 
 */
public class Fornecedor implements Runnable {
    
	// Parametros quie definem o comportamento do fornecedor

    /** N�mero de intera��es antes de finalizar */
    private int iteracoes;

    /** Tempo de espera m�dio entre itera��es */
    private int esperaMedio;

    /** Quantidade m�xima a ser fornecida em cada itera��o */
    private Pedido entregaMaxima = new Pedido();

    /** total entregue at� o momento*/
    private Pedido entregue = new Pedido();

    // Construtores

    /** Cria um novo fornecedor
     * @param n�mero de itera��es antes da finaliza��o
     */
    public Fornecedor(int iteracoes) {
        this.iteracoes = iteracoes;

        // Configura os v�rios par�metros da simula��o
        // NOTA: Estes n�meros foram escolhidas para que a taxa m�dia de produ��o
        // case com a taxa m�dia de consumo
        //
        // O Alambique 0 gera em m�dia um pedido a cada 50 ms por uma quantidade m�dia de 
        // 25 �nidades de cada cana por pedido, o que gera um consumo m�dio de 25/50 = 0.50 unidades/ms.\
        // De forma similar, os demais consumidores consomem em uma taxa de 5/50 = 0.10 unidades/ms.
        // Portanto, a taxa m�dia total de consiumo � de 0.50 + (N-1)*0.10, onde N � o n�mero de consumidores
        // 
        //
        // Configuramo o fornecedor para fornecer 4 unidades por itera��o
        // portanto se S � o tempo m�dio de espera e A = entregaMaxima/2 � quantidade
        // fornecida, temos a equa��o
        //     A / S = 0.50 + (N-1)*0.10
        // ou
        //     S = A / (0.50 + (N-1)*0.10)

        for (Cana c : Cana.values()) {
            entregaMaxima.set(c, 10);
        }

        esperaMedio
            = (int) Math.round(5 / (0.50 + (Projeto3.getAlambiqueN() - 1) * 0.10));

        Projeto3.debug(
            "Fornecendo uma m�dia de 5 unidades e cana a cada " + esperaMedio + " ms");
    } 

    // M�todos

    /** Indica a quantidade de cana entregue
     */
    public synchronized Pedido getProducao() {
        return entregue;
    } 

    /** Loop principal
     */
    public void run() {
        Pedido quant = new Pedido();
        for (int i = 0; i < iteracoes; i++) {
            try {
                int slp = Projeto3.expo(esperaMedio);
                Thread.sleep(slp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Cana g : Cana.values()) {
                quant.set(g, Projeto3.randInt(entregaMaxima.get(g) + 1));
            }
            Projeto3.getDeposito().entrega(quant);
            entregue.troca(quant);
        }
        out.printf("Fornecedor%n");
        out.printf("   Itera��es:       %d%n", iteracoes);
        out.printf("   Total fornecido: %s%n", entregue);
    } 
} 