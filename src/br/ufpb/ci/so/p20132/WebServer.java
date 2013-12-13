package br.ufpb.ci.so.p20132;

import java.io.*; 
import java.net.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.ufpb.ci.so.p20132.bufferscale.Buffer;
import br.ufpb.ci.so.p20132.bufferscale.BufferFifo;
import br.ufpb.ci.so.p20132.bufferscale.BufferPCGI;
import br.ufpb.ci.so.p20132.bufferscale.BufferPGET;
import br.ufpb.ci.so.p20132.bufferscale.BufferRandom;
import br.ufpb.ci.so.p20132.bufferscale.BufferSJF;

/**
 * Um servidor Web simples porém totalmente funcional.
 * O servidor atende apenas requisições do tipo GET ou CGI.
 * @author Alexandre Duarte - alexandre@ci.ufpb.br
 * @author Gustavo Brito - gbritosampaio@gmail.com
 * @author Rafael Germano - rafael.germano321@gmail.com
 *
 */
public class WebServer {
	
	private ServerSocket socketEscuta;
	
	private ExecutorService pool;

	private int nthreads, porta;
	
	public static long start_time = 0;
	
	public WebServer( int porta, int nthreads) throws Exception {
			
		this.porta = porta;
		this.nthreads = nthreads;
		
		socketEscuta = new ServerSocket(this.porta);
		
		start_time = System.currentTimeMillis();
		
		System.out.println("[Servidor] Início em "+ new Date(start_time));
		
		pool = Executors.newFixedThreadPool(this.nthreads);
		
		for(int i=0; i<nthreads; i++)
			pool.submit(new WorkerThread(i));
		
	}
	
	public void run () throws IOException {
		
		while( true ) {			

			Socket reqSocket = socketEscuta.accept();
			processaRequisicao( reqSocket );
				
		}	
	}
	
	private void processaRequisicao( Socket reqSocket) throws IOException {
		
		Descritor d = new Descritor(reqSocket, // socket 
				System.currentTimeMillis()-start_time // delta a partir do início do servidor
				);
		d.setTotalRequisicoesAgendadas(Buffer.getInstance().getTotalRequisicoesAgendadas(d));
		try {
			Buffer.getInstance().addDescritor(d);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(final String argv[]) throws Exception {
		
		System.out.println( "Iniciando o servidor...");
		
		int porta = 0;
		int nthreads = 0;
		int bufferSize = 0;
		String algoritmo = "None";
		
		for(String s : argv)
			System.out.println(s);
		
		try {
			if(argv.length<4) throw new Exception();
			porta = Integer.parseInt(argv[0]);
			nthreads = Integer.parseInt(argv[1]);
			bufferSize = Integer.parseInt(argv[2]);
			algoritmo = argv[3];
		}catch (Exception e) {
			System.out.println("java br.ufpb.ci.so.p20132.WebServer porta numero-threads tamanho-buffer alg-escalonamento");
			System.exit(1);
		}
		
		switch(algoritmo)
		{
			case "fifo":
				Buffer.init(new BufferFifo(bufferSize));
				break;
			case "pget":
				Buffer.init(new BufferPGET(bufferSize));
				break;
			case "pcgi":
				Buffer.init(new BufferPCGI(bufferSize));
				break;
			case "random":
				Buffer.init(new BufferRandom(bufferSize));
				break;
			case "sjf":
				Buffer.init(new BufferSJF(bufferSize));
				break;
			default:
				System.out.println("Algorítimo não reconhecido!");
				System.exit(1);
		}
		
		WebServer servidor = new WebServer(porta, nthreads);		
		
		System.out.println( "Servidor no ar. Aguardando requisições.");
		
		servidor.run();
		
		System.out.println( "Servidor finalizando.");
		
	}
}
