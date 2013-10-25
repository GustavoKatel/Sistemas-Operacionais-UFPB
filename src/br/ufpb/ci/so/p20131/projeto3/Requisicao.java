package br.ufpb.ci.so.p20131.projeto3;
/**
 * Uma requisi��o de um Alambique para o Deposito
 * 
 * Uma Requisi��o registra as quantidades de cada cana requisitadas e atendidas at� o momento, 
 * e serve como um ponto de espera para o atendimento de um pedido.
 */
public class Requisicao {
   

    /** Raiz da sequ�ncia de n�meros */
    private static int proximoSeq= 0;

    /** A quantidade requisitada originalmente. */
    private Pedido requisitado;

    /** Id do consumidor */
    private int id;

    /** N�mero de sequ�ncia deste pedido  */
    public final int seq;

    /** Total alocado at� o momento */
    private Pedido alocado;

    /** Flag que indica se a requisi��o foi completamente atendida */
    private boolean concluida = false;

    /** Cria uma nova requisi��o
     * Nota: Este m�todo s� deve ser invocado dentre de um m�todo sincronizado
     * @param id do consumidor
     * @param quantidade requisitada de cana gr�o
     */
    public Requisicao(int id, Pedido requisicao) {
        this.id = id;
        this.requisitado = requisicao.copia();
        this.seq = ++proximoSeq;
        alocado = new Pedido();
    } 

    /** Checa se a quantidade n�o atendida nesta requisi��o � menor ou igual ao dispon�vel no
     * fornecedor
     * @param a quantidade dispon�vel no fornecedor
     * @return true se a requisi��o pode ser completamente atendida
     */
    public synchronized boolean menorOuIgual(Pedido estoque) {
        for (Cana c : Cana.values()) {
            if (requisitado.get(c) - alocado.get(c) > estoque.get(c)) {
                return false;
            }
        }
        return true;
    } 

    /** Aloca alguma quantidade de cana para esta requsi��o
     * A quantidade de cana cana entregue � o m�nimo entre a quantidade em estoque
     * e a quantidade necess�ria para completar o pedido. No entanto, se o limite for positivo
     * n�o mais do que o limite ser� entregue
     * @param estoque de cana
     * @param se o limite for maior que zero, n�o mais do que esta quantidade de cada cana
     * ser� entegue
     * @return a quantidade total de cana entregue
     */
    public synchronized int entrega(Pedido estoque, int limite) {
        int resultado = 0;
        for (Cana c : Cana.values()) {
            int quant = requisitado.get(c) - alocado.get(c);
            if (quant > estoque.get(c)) {
                quant = estoque.get(c);
            }
            if (limite > 0 && quant > limite) {
                quant = limite;
            }
            alocado.troca(c, quant);
            estoque.troca(c, -quant);
            resultado += quant;
        }
        return resultado;
    } 

    /** Checa se esta requisi��o foi completamente atendida
     * @return true se a requisi��o foi atendida.
     */
    public synchronized boolean satisfeita() {
        for (Cana c : Cana.values()) {
            if (alocado.get(c) != requisitado.get(c)) {
                return false;
            }
        }
        return true;
    } 

    /** Retorna o total requisitado
     * @return soma de todas as quantidades
     */
    public synchronized int total() {
        int soma = 0;
        for (Cana c : Cana.values()) {
            soma += requisitado.get(c);
        }
        return soma;
    } 

    /** Retorna as quantidades requisitadas restantes
     * @return soma do que foi solicitado e n�o atendido
     */
    public synchronized int restante() {
        int soma = 0;
        for (Cana c : Cana.values()) {
            soma += requisitado.get(c) - alocado.get(c);
        }
        return soma;
    } 

    /** Sinaliza a conclus�o da requisi��o
     */
    public synchronized void completa() {
        concluida = true;
        notify();
    } 

    /** Aguarda a conclus�o desta requisi��o
     * @throws InterruptedException se o thread for interrompido enquanto aguarda 
     * o atendimento da requisi��o.
     */
    public synchronized void await() throws InterruptedException {
        while (!concluida) {
            wait();
        }
    } 

    /** Retorna a quantidade j� alocada desta requisi��o
     *
     * @return a quantidade alocada
     */
    public synchronized Pedido getAlocado() {
        return alocado;
    } // getAlloc()

    /** toString
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(seq + ":" + id);
        char sep = '['; 
        
        for (Cana c : Cana.values()) {
            sb.append(String.format(
                "%c%d/%d %s", sep, alocado.get(c), requisitado.get(c), c));
            sep = ',';
        }
        sb.append(']');
        return sb.toString();
    }
} 
