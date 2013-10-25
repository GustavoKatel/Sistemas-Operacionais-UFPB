package br.ufpb.ci.so.p20131.projeto2;
/**
 * Interface a ser implementada pela classe by AtravessadorImpl
 *
 */
public interface Atravessador {

    /** Informa a quantidade total de cana de a��car em posse deste atravessador
     * @return uma indica��o das quantidades de cada variedade de cana de a��car em posse deste atravessador
     */
    Pedido getEstoqueDisponivel();

    /** Um pedido de um Alambique
     * O objeto que invoca este m�todo � bloqueado at� que a requisi��o possa ser atendida por completo
     * @param pedido n�mero de fardos necess�rios de cada variedade de cana de a��car
     * @throws InterruptedException se o thread atual for interrompido enquanto
     *            espera que o pedido possa ser atendido por completo.
     */
    void get(Pedido pedido) throws InterruptedException;

    /** Responde a um pedido de troca de um outro atravessador.
     * Um outro atravessador invoca este m�todo para requisitar uma troca  
     * de uma quantidade de uma determinada variedade de cana de a��car pela variedade de cana 
     * de a��car que � a especialidade deste atravessador. 
     * Bloqueia o objeto que invoca o m�todo at� que este atravessador possa completar a troca.
     * @param variedade a variedade de cana de a��car que o outro atravessador deseja enviar para este atravessador.
     * @param quant n�mero de fardos a serem trocados
     * @throws InterruptedException se o thread atual for interrompido enquanto aguarda que a remessa
     * seja finalizada
     */
    void troca(Cana variedade, int quant) throws InterruptedException;

    /** Recebe uma entrega de um fornecedor.
     * O fornecedor invoca este m�todo para entregar uma quantidade de fardos da variedade de 
     * cana de a��car que � especialidade deste atravessador.
     * 
     * @param quant a quantidade de fardos de cana de a��car sendo entregues
     * 
     */
    void entrega(int quant);
} 
