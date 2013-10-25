package br.ufpb.ci.so.p20131.projeto3;


import java.util.*;
import static java.lang.System.out;

/** A classe principal do projeto 3
 * 
 */
public class Projeto3 {
   
    /** N�mero de Alambiques */
    private static int alambiqueN;

    /** O dep�sito �nico */
    private static Deposito deposito;

    /** Alambiques */
    private static Alambique[] alambiques;

    /**Threads dos Alambiques */
    private static Thread[] alambiqueThreads;

    /** O fornecedor �nico */
    private static Fornecedor fornecedor;

    /** Tempo de inicio */
    static private long startTime = System.currentTimeMillis();

    /** Controle de debug */
    private static boolean verbose = false;

    /** Liga ou desliga o debug
     * @param true para ligar, false para desligar
     */
    public static void setVerbose(boolean on) {
        verbose = on;
    } 

    /** Retorna o dep�sito
     *
     * @return deposito
     */
    public static Deposito getDeposito() {
        return deposito;
    }

    /** Imprime informa��o de depura��o
     * @param mensagem para impress�o
     */
    public static void debug(Object mensagem) {
        if (verbose) {
            out.printf("%6.3f %s: %s%n",
                tempo() / 1E3, Thread.currentThread().getName(), mensagem);
        }
    } // debug(Object)

    /** Imprime informa��o de depura��o
     * @param formato
     * @param mensagem para impress�o
     */
    public static void debug(String format, Object... args) {
        if (verbose) {
            String mensagem = String.format(format, args);
            out.printf("%6.3f %s: %s%n",
                tempo() / 1E3, Thread.currentThread().getName(), mensagem);
        }
    } 

    /** Calculo do tempo de execu��o
     * @return tempo desde o in�cio da execu��o
     */
    static public int tempo() {
        return (int)(System.currentTimeMillis() - startTime);
    } 

    /** N�mero de alambiques ativos
     * @return n�mero de alambiques
     */
    public static int getAlambiqueN() {
        return alambiqueN;
    } 

    /** gerador de n�meros aleat�rios */
    private static Random rand;
        
    /** fun��o para gera��o de n�meros aleat�rios
     * @param maior valor
     * @return um n�mero n�o negativo
     */
    public static int randInt(int max) {
        if (0 >= max) {
            throw new IllegalArgumentException("randInt");
        }
        return (rand.nextInt(max));
    } 

    /** fun��o para gera��o de n�meros aleat�rios
     * @param maior valor
     * @param menor valor
     * @return um n�mero n�o negativo entre maior e menor inclusive.
     */
    public static int randInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("randInt");
        }
        return min + rand.nextInt(max - min + 1);
    } 

    /** gera um n�mero aleat�rio distribu�do exponencialmente
     * @param media - a m�dia da distribui��o
     * @return o pr�ximo valor da s�rie
     */
    public static int expo(int media) {
        return (int) Math.round(-Math.log(rand.nextDouble()) * media);
    } 

    /** Imprime a forma de uso e encerra */
    private static void uso() {
        System.err.println(
            "uso: Projeto3 [-v][-r] algoritmo numeroDeAlambiques itera��es");
        System.exit(1);
    } 

    /** M�todo principal
     * 
     */
    public static void main(String[] args) {
        // faz o parsing dos argumentos da linha de comnaod
        GetOpt options = new GetOpt("Projeto3", args, "vr");
        int opt;
        while ((opt = options.nextOpt()) != -1) {
            switch (opt) {
            default:
                uso();
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
        if (options.optind != args.length - 3) {
            uso();
        }
        int algoritmo = Integer.parseInt(args[options.optind + 0]);
        alambiqueN = Integer.parseInt(args[options.optind + 1]);
        int iteracoes = Integer.parseInt(args[options.optind + 2]);

        // Cria o dep�sito
        deposito = new Deposito(algoritmo);
        Thread dthread = new Thread(deposito, "Deposito");

        // Cria o fornecedor
        fornecedor = new Fornecedor(iteracoes);
        Thread fThread = new Thread(fornecedor, "Fornecedor");

        // Cria os alambiques
        alambiques = new Alambique[alambiqueN];
        alambiqueThreads = new Thread[alambiqueN];
        for (int i = 0; i < alambiqueN; i++) {
            alambiques[i] = new Alambique(i);
            alambiqueThreads[i] = new Thread(alambiques[i], "Alambique" + i);
        }

        // Inicia os threads
       
        dthread.setPriority(Thread.NORM_PRIORITY - 1);
        dthread.start();
        fThread.setPriority(Thread.NORM_PRIORITY - 1);
        fThread.start();
        for (Thread t : alambiqueThreads) {
            t.setPriority(Thread.NORM_PRIORITY - 1);
            t.start();
        }

        //Espera at� que todos os threads sejam encerrados
        try {
            // O thread do fornecedor se encerra quando ele completa todas as itera��es
            fThread.join();

            // Aguarda 3 segundos para dar chance a todos de terminarem o que est�o fazendo,
            // em seguida todos s�o interrompidos
            Thread.sleep(3000);

            // Mata o thread do dep�sito
            dthread.interrupt();
            dthread.join();

            // Mata os alambiques
            for (Thread t : alambiqueThreads) {
                t.interrupt();
                t.join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // imprime o estado final
        out.printf("**** Programa terminando%n");

        Pedido quant;
        Pedido balanco = new Pedido();
        int produzido = 0;
        int noDeposito = 0;
        int consumido = 0;

        quant = fornecedor.getProducao();
        out.printf("Produzido %s%n", quant);

        balanco.troca(quant);
        produzido += quant.total();

        quant = deposito.getDisponivel();
        for (Cana c1 : Cana.values()) {
            int n = quant.get(c1);
            balanco.troca(c1, -n);
            noDeposito += n;
        }

        Pedido totalConsumido = new Pedido();
        int totalRequisitado = 0;
        int totalEspera = 0;
        for (int i = 0; i < alambiqueN; i++) {
            quant = alambiques[i].getConsumo();
            int requisitado = alambiques[i].requisicoesConcluidas();
            int espera = alambiques[i].tempoDeEspera();
            totalConsumido.troca(quant);
            for (Cana g : Cana.values()) {
                int n = quant.get(g);
                balanco.troca(g, -n);
                consumido += n;
            }
            out.printf("Alabique %d%n", i);
            out.printf("   Cana consumida:         %s%n", quant);
            out.printf("   Requisi��es atendidas:  %d%n", requisitado);
            out.printf("   Tempo total de espera:  %d ms%n", espera);
            if (requisitado > 0) {
                out.printf("   Tempo de espera m�dio:      %.2f ms%n",
                                espera / (double)requisitado);
            }
            totalRequisitado += requisitado;
            totalEspera += espera;
        }
        out.printf("Balan�o (deficit) � %s%n", balanco);
        out.printf(
            "Total: produzido = %d, consumido = %d,"
                    + " restando no dep�sito = %d, l�quido = %d%n",
            produzido, consumido, noDeposito,
            (produzido - consumido - noDeposito));
        out.printf(
            "Requisi��es conclu�das: %d, tempo de espera m�dio %.2fms%n",
            totalRequisitado, totalEspera / (double) totalRequisitado);
    } 
} 
