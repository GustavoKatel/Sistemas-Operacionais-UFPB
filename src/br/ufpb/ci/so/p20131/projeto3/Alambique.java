package br.ufpb.ci.so.p20131.projeto3;


public class Alambique implements Runnable {
  
    /** Identificador deste consumidor */
    private int id;

    // Parametros para determinar o comportamento deste consumidor:

    /** Tempo m�dio de espera entre duas requisi��es */
    private int tempoEsperaMedio = 50;

    /** Quantidade m�xima de cada cana que pode ser requisitada. */
    private Pedido pedidoMaximo = new Pedido();

    // Estado atual do consumidor:

    /** Total consumido at� o momento. */
    private Pedido consumido  = new Pedido();

    /** N�mero de requisi��es conclu�das at� o momento. */
    private int numeroDeRequisicoes = 0;

    /** Soma do tempo de servi�o para todas as requisi�oes */
    private int tempoTotalDeServico = 0;

    // Construtores

    /** Cria um novo Alambique
     * @param identificador deste consumidor
     */
    public Alambique(int id) {
        this.id = id;

        // Todos os consumidors geram pedidos balanceados, mas o consumidor 0 
        // � mais guloso que os demais but Brewer 0 is

        for (Cana c : Cana.values()) {
            pedidoMaximo.set(c, id == 0 ? 50 : 10);
        }
    } // Brewer(int)

    // Methods

    /** Retorna o total consumido at� o momento.
     * @return o total consumido at� o momento.
     */
    public synchronized Pedido getConsumo() {
        return consumido;
    } 

    /** Retorna o total de requisicoes conclu�das
     *
     * @return n�mero de requsi��es concluidas
     */
    public synchronized int requisicoesConcluidas() {
        return numeroDeRequisicoes;
    } 

    /** Retorna o tempo em milisegundos que este consumidor passou esperando
     *
     * @return tempo de espera
     */
    public synchronized int tempoDeEspera() {
        return tempoTotalDeServico;
    } 

    /** Consome a quantidade indicada de recursos
     * @param quantidade cana de cada variedade a ser consumida.
     */
    private synchronized void consome(Pedido quantidade) {
        consumido.troca(quantidade);
    } // consume(Order)

    /** Main loop.
     * Repeatedly generates random requests to the warehouse.
     */
    public void run() {
        Pedido pedido = new Pedido();
        for (;;) {
            try {
                Thread.sleep(Projeto3.expo(tempoEsperaMedio));
            } catch (InterruptedException e) {
                Projeto3.setVerbose(true);
                Projeto3.debug("interrompido durante o sono");
                return;
            }

            // Gera uma requisi��o n�o nula
            int sum;
            do {
                sum = 0;
                for (Cana c : Cana.values()) {
                    int amt = Projeto3.randInt(pedidoMaximo.get(c) + 1);
                    pedido.set(c, amt);
                    sum += amt;
                }
            } while (sum == 0);
            
            Projeto3.debug("requisitando %s do deposito", pedido);
            int tempoRequisicao = Projeto3.tempo();
            try {
                Projeto3.getDeposito().get(id, pedido);
            } catch (InterruptedException e) {
                Projeto3.setVerbose(true);
                Projeto3.debug("interrompido enquanto pedia %s ao deposito",
                    pedido);
                return;
            }
            Projeto3.debug("recebeu %s do deposito", pedido);
            tempoTotalDeServico += Projeto3.tempo() - tempoRequisicao;
            numeroDeRequisicoes++;
            consome(pedido);
        }
    } 
} 
