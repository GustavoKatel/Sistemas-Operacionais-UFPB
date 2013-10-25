package br.ufpb.ci.so.p20131.projeto2;
/**
 * Um fornecedor de cana de a��car
 * 
 */
public class Fornecedor implements Runnable {
	/** Quantidade de itera��es antes de finalizar a execu��o */
    private int iteracoes;

    /** Total entregue at� o momento */
    private Pedido entregue = new Pedido();

    /** Cria um novo fornecedor
     * @param iteracoes n�mero de itera��es antes de finalizar a execu��o
     */
    public Fornecedor(int iteracoes) {
        this.iteracoes = iteracoes;
    }

    /** Indica a quantidade de cada variedade de cana de a��car entregue
     * @return uma indica��o da quantidade de cada variedade de cana de a��car entregue at� o momento.
     */
    public synchronized Pedido getProducao() {
        return entregue;
    }

    /** Loop principal
     * Gera pedidos aleat�rios
     */
    public void run() {
        for (int i = 0; i < iteracoes; i++) {
            try {
                Thread.sleep(Projeto2.randInt(100));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Cana g = Cana.randChoice();
            int quant = Projeto2.randInt(1,10);
            entregue.troca(g, quant);
            Projeto2.debug("entregando %d %s de cana %s para o atravessador de cana %s ",
                quant, (quant == 1 ? "fardo" : "fardos"), g, g);
            Projeto2.especialista(g).entrega(quant);

        }
        Projeto2.debug("Fornecedor finalizando ...");
    } // run()
}
