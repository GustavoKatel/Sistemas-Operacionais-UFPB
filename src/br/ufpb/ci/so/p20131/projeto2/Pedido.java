package br.ufpb.ci.so.p20131.projeto2;
import java.util.*;

/** Um pedido
 * 
 */
public class Pedido {
		/** N�mero de vers�o */
    private Map<Cana,Integer> quant;

    /** Cria um novo pedido com todos os valores em 0. */
    public Pedido() {
        quant = new EnumMap<Cana,Integer>(Cana.class);
        for (Cana g : Cana.values()) {
            quant.put(g, 0);
        }
    } 

    /** 
     * @return retorna uma vers�o leg�vel deste pedido.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String sep = "[";
        for (Cana g : Cana.values()) {
            sb.append(String.format("%s%d %s", sep, quant.get(g), g));
            sep = ", ";
        }
        sb.append("]");
        return sb.toString();
    } // toString()

    /** Retorna a quantidade da cana g neste pedido.
     * @param g tipo de cana de a��car
     * @return quantidade de cana dessa variedade
     */
    public int get(Cana g) {
        return quant.get(g).intValue();
    } 

    /** Seta a quantidade desejada cana de variedade g para n
     * @param g uma variedade de cana
     * @param n a quantidade solicitada dessa variedade
     */
    public void set(Cana g, int n) {
        quant.put(g, n);
    }

    /** Modifica o valor de "g" pelo valor "diff".
     * @param g uma variedade de cana
     * @param diff a diferen�a na quantidade de g
     */
    public void troca(Cana g, int diff) {
        quant.put(g, quant.get(g) + diff);
    } 

    /** Retorna uma c�pia deste pedido.
     * @return uma c�pia deste pedido.
     */
    public Pedido copia() {
        Pedido result = new Pedido();
        for (Cana g : Cana.values()) {
            result.quant.put(g, quant.get(g));
        }
        return result;
    } 
        
}
