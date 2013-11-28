package br.ufpb.ci.so.p20132;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	
	private static int numeroRequisicao = 0;
	
	private ExecutorService pool;

	private int nthreads, porta, bufferSize;
	private String algoritmo;
	
	public WebServer( int porta, int nthreads, int bufferSize, String algoritmo) throws Exception {
			
		this.porta = porta;
		this.nthreads = nthreads;
		this.bufferSize = bufferSize;
		this.algoritmo = algoritmo;
		
		socketEscuta = new ServerSocket(this.porta);
		
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
		
		Descritor d = new Descritor(reqSocket);
		// adiciona d ao buffer
			
		BufferedReader doCliente = new BufferedReader(new InputStreamReader( reqSocket.getInputStream()));
		DataOutputStream paraCliente = new DataOutputStream( reqSocket.getOutputStream());
		String requisicao = doCliente.readLine();
		
		
		String req = new String("Requisicao numero [" + numeroRequisicao++ + "] = " + "\"" + requisicao + "\"");
		System.out.println( req );
		
		StringTokenizer st = new StringTokenizer(requisicao);
		String tipo = st.nextToken();
		byte[] bytes = null;
		
		
		if(tipo.equals("GET")) {
			
			try {
			
				File arquivo = new File(st.nextToken().substring(1));
	
				FileInputStream leitor = new FileInputStream (arquivo);
				bytes = new byte[(int)arquivo.length()];
				leitor.read(bytes);
				leitor.close();
			} catch( IOException e ) {
				bytes = e.getMessage().getBytes();	
			}
		} else if( tipo.equals("CGI")) {
			
			Process p = Runtime.getRuntime().exec("java -classpath bin " + st.nextToken());
			BufferedReader b = new BufferedReader( new InputStreamReader( p.getInputStream()));
			
			StringBuffer sb = new StringBuffer();
			String l;
			while((l = b.readLine())!= null) {
					sb.append(l);
					sb.append("\n");
			}
			
			bytes = sb.toString().getBytes();
			b.close();
		}
		
			
		paraCliente.writeBytes("HTTP/1.0 200 Document Follows\r\n");
		paraCliente.writeBytes("Content-Length " + bytes.length + "\r\n");
			
			
		paraCliente.writeBytes("Content-Length " + bytes.length + "\r\n");
			
		//Retorno das estatísticas da requisição
		paraCliente.writeBytes("id-requisicao " + 1 + "\r\n");
		paraCliente.writeBytes("tempo-chegada-requisicao " + 2 + "\r\n");
		paraCliente.writeBytes("cont-requisicao-agendada " + 3 + "\r\n");
		paraCliente.writeBytes("tempo-agendamento-requisicao " + 4  + "\r\n");
		paraCliente.writeBytes("cont-requisicao-concluida " + 5  + "\r\n");
		paraCliente.writeBytes("tempo-requisicao-concluida " + 6  + "\r\n");
		paraCliente.writeBytes("idade-requisicao " + 7 + "\r\n");
		paraCliente.writeBytes("tipo-requisicao " + tipo  + "\r\n");
			
		//Retorno das estatísticas do thread
		paraCliente.writeBytes("ida-thread " + 8 + "\r\n");
		paraCliente.writeBytes("cont-thread " + 9 + "\r\n");
			
		paraCliente.writeBytes("\r\n\n");
		
		paraCliente.write(bytes, 0, bytes.length);
		
		reqSocket.close();
		
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
		
		WebServer servidor = new WebServer(porta, nthreads, bufferSize, algoritmo);		
		
		System.out.println( "Servidor no ar. Aguardando requisições.");
		
		servidor.run();
		
		System.out.println( "Servidor finalizando.");
		
	}
}
