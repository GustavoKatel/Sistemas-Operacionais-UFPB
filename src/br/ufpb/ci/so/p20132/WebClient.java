package br.ufpb.ci.so.p20132;

import java.util.Random;

import com.sun.swing.internal.plaf.synth.resources.synth;

public class WebClient extends Thread {

	// Escopo estático
	private static int nfinalizados = 0;
	
	public static synchronized void addFinalizado()
	{
		nfinalizados++;
	}
	
	public static synchronized int getFinalizados()
	{
		return nfinalizados;
	}
	
	// Escopo dinâmico
	
	private String host;
	private int porta;
	private int max_req;
	private String algo;
	private WebClient anterior;

	private boolean requisicaoDone = false;
	
	public WebClient(String host, int porta, int max_req, String algo, WebClient anterior) {
		this.host = host;
		this.porta = porta;
		this.max_req = max_req;
		this.algo = algo;
		this.anterior = anterior;
	}
	
	public synchronized void waitRequisicao() throws InterruptedException
	{
		while(requisicaoDone==false)
			wait();
	}
	
	public synchronized void run() {

		WebClientAgent agent;
		String tipo;
		Random r = new Random();
		while(true) {
			
			try {
				
				for(int i=0;i<max_req;i++)
				{
					tipo = ( r.nextBoolean() ? "CGI" : "GET" );
					agent = new WebClientAgent(host, porta, tipo);
					agent.start();
					if(algo.equals("fifo"))
					{
						agent.waitEnviado();
					}
					if(anterior!=null)
						anterior.waitRequisicao();
					requisicaoDone=true;
					notifyAll();
				}				
				
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
			
		}
		
	}
	
	public static void main( String args[]) throws Exception {
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		int n = Integer.parseInt(args[2]);
		String algoritmo = args[3];
		
		WebClient list[] = new WebClient[n];
		for(int i=0;i<n;i++)
		{
			list[i] = new WebClient(host, port, n, algoritmo, (i==0 ? null : list[i-1]) );
			list[i].start();
		}
		for(int i=0;i<n;i++)
		{
			list[i].join();
		}		
		
	}
	
}
