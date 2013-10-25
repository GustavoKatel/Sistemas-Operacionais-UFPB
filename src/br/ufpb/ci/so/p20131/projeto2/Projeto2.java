package br.ufpb.ci.so.p20131.projeto2;
import java.util.*;
import static java.lang.System.*;

/** A classe principal do projeto 2
 * 
 */
public class Projeto2 {
	/** N�mero de Alambiques. */
    private static int alambiqueCount;

    /** Atravessadores  traders.get(g) � um atravessador especialista na variedade de cana de a��car g */
    private static EnumMap<Cana, Atravessador> atravessadores;

    /** Alambiques */
    private static Alambique[] alambiques;

    /** Threads dos Alambiques. */
    private static Thread[] alambiqueThreads;

    /** O �nico fornecedor de cana de a��car */
    private static Fornecedor fornecedor;

    /** Flag para controlar o n�vel de sa�da de depura��o */
    private static boolean verbose = false;

    /** Liga ou desliga a depura��o
     * @param on se for true, liga a depura��o; desliga caso contr�rio
     */
    public static void setVerbose(boolean on) {
        verbose = on;
    } 

    /** Retorna o atravessador especialista em uma determinada variedade de cana de a��car
     * @param g a variedade de cana
     * @return o atravessador especialista na variedade de cana de a��car g
     */
    public static Atravessador especialista(Cana g) {
        return atravessadores.get(g);
    } 

    /** Se a depura��o estiver ativada imprime na sa�da padr�o o nome do thread atual
     * e uma mensagem.
     * @param message mensagem a ser impressa
     */
    public static void debug(Object message) {
        if (verbose) {
            out.printf("%s: %s%n", Thread.currentThread().getName(), message);
        }
    }

    /** Se a depura��o estiver ativada imprime na sa�da padr�o o nome do thread atual
     * e uma mensagem.
     * @param format uma mensagem em formato printf-like
     * @param args argumentos do printf
     */
    public static void debug(String format, Object... args) {
        if (verbose) {
            String message = String.format(format, args);
            out.printf("%s: %s%n", Thread.currentThread().getName(), message);
        }
    } 

    /** Gerador de n�meros aleat�rios */
    private static Random rand;
        
    /** Fun��o utilit�ria para gerar um n�mero aleat�rio n�o negativo < max
     * @param max teto para os n�meros aleat�rios a serem gerados
     * @return um inteiro n�o negativo menor do que max
     */
    public static int randInt(int max) {
        if (0 >= max) {
            throw new IllegalArgumentException("randInt");
        }
        return (rand.nextInt(max));
    }

    /** Fun��o utilit�ria para gerar um n�mero aleat�rio entre min e max (inclusive)
     * @param min o menor resultado poss�vel
     * @param max o maior resultado poss�vel
     * @return um inteiro entre min e max.
     */
    public static int randInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("randInt");
        }
        return min + rand.nextInt(max - min + 1);
    } 

    /** Imprime uma mensagem e finaliza.*/
    private static void usage() {
        err.println(
            "uso: Projeto2 [-v][-r] alambiqueCount itera��es");
        exit(1);
    }

    /** Programa principal para o projeto 2.
     * @param args argumentos da linha de comando.
     */
    public static void main(String[] args) {
        // Faz o parsing dos argumentos da linha de comando.
        GetOpt options = new GetOpt("Projeto2", args, "vr");
        int opt;
        while ((opt = options.nextOpt()) != -1) {
            switch (opt) {
            default:
                usage();
                break;
            case 'v':
                verbose = true;
                break;
            case 'r':
                rand = new Random(0);
                break;
            }
        }
        if (rand == null) {
            rand = new Random();
        }
        if (options.optind != args.length - 2) {
            usage();
        }
        alambiqueCount = Integer.parseInt(args[options.optind]);
        int iteracoes = Integer.parseInt(args[options.optind + 1]);

        // Cria os atravessadores
        atravessadores = new EnumMap<Cana, Atravessador>(Cana.class);
        for (Cana g : Cana.values()) {
        	
        	//Voc� precisa implementar a classe AtravessadorImpl para o programa funcionar.
            atravessadores.put(g, new AtravessadorImpl(g));
        }

        // Criar o �nico fornecedor.
        fornecedor = new Fornecedor(iteracoes);

        Thread threadFornecedor = new Thread(fornecedor, "Fornecedor");

        alambiques = new Alambique[alambiqueCount];
        alambiqueThreads = new Thread[alambiqueCount];
        for (int i = 0; i < alambiqueCount; i++) {
            alambiques[i] = new Alambique();
            alambiqueThreads[i] = new Thread(alambiques[i], "Alambique " + i);
        }

        // Inicia os threads
        // Todos eles tem prioridade mais baixa que o thread pricniapl ent�o nenhum deles 
       // come�ar� a rodar antes que todos tenham sidos inicializados.
        threadFornecedor.setPriority(Thread.NORM_PRIORITY - 1);
        threadFornecedor.start();
        for (Thread t : alambiqueThreads) {
            t.setPriority(Thread.NORM_PRIORITY - 1);
            t.start();
        }

        // Espera que todos os threads sejam finalizados
        try {
            // O thread do fornecedor ser� finalizado quando ele concluir todas as itera��es especificadas.
            threadFornecedor.join();

            // Espera 3 segundos para dar uma chance para os threads do alambique terminarem 
            // depois disso, todos que n�o finalizarem ser�o interrompidos.
            Thread.sleep(3000);

            for (Thread t : alambiqueThreads) {
                t.interrupt();
                t.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // imprime o estado final
        out.printf("**** Finalizando%n");

        Pedido quant;
        Pedido balanco = new Pedido();
        int produzido = 0;
        int nosAtravessadores = 0;
        int consumido = 0;

        quant = fornecedor.getProducao();
        out.printf("Produzido %s%n", quant);

        for (Cana g : Cana.values()) {
            int n = quant.get(g);
            balanco.troca(g, n);
            produzido += n;
        }

        for (Cana g : Cana.values()) {
            quant = atravessadores.get(g).getEstoqueDisponivel();
            for (Cana c1 : Cana.values()) {
                int n = quant.get(c1);
                balanco.troca(c1, -n);
                nosAtravessadores += n;
            }
        }

        for (int i = 0; i < alambiqueCount; i++) {
            quant = alambiques[i].getConsumo();
            out.printf("O Alambique %d consumiu %s%n", i, quant);
            for (Cana g : Cana.values()) {
                int n = quant.get(g);
                balanco.troca(g, -n);
                consumido += n;
            }
        }
        out.printf("O balan�o final � %s%n", balanco);
        out.printf(
            "Total: produzido = %d, consumido = %d,"
                    + " sobrando nos atravessadores = %d, liquido = %d%n",
            produzido, consumido, nosAtravessadores, (produzido - consumido - nosAtravessadores));
    } // main(String[])
} // Projeto 2
