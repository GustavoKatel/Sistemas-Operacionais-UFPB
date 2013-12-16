package br.ufpb.ci.so.p20132;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;

public class WebClientAgent extends Thread {

	private String host;
	private int porta;
	private String tipo;

	private boolean enviado;
	
	public WebClientAgent(String host, int porta, String tipo) {
		this.host = host;
		this.porta = porta;
		this.tipo = tipo;
		
		this.enviado = false;
	}
	
	/**
	 * Método para testar a funcionalidade do servidor
	 * Envia repetidamente um conjunto alternado de requisições GET e CGI e imprime o resultado na saída padrão.
	 */
	public synchronized void run()
	{
		try {
			if(tipo.equals("GET"))
				sendGET();
			else
				sendCGI();
		} catch (Exception e) {
			// e.printStackTrace();
			enviado=true;
			notifyAll();
		}
	}
	
	private synchronized void sendGET() throws Exception {
		
		Socket servidor = new Socket(host,porta);
		
		int id = (new Random()).nextInt(4)+4;
			
		PrintStream ps = new PrintStream(servidor.getOutputStream());
		InputStream is = servidor.getInputStream();
		BufferedReader reader=new BufferedReader(new InputStreamReader(is));
		String str="";
		
		
		//Enviando requisição do tipo GET
		ps.println("GET resources/2013-2/test" + id + ".txt HTTP/1.0\r\n");
		
		enviado=true;
		notify();
		while((str=reader.readLine())!=null) {
			System.out.println(str);
		}
		
		servidor.close();
	}
	
	private synchronized void sendCGI() throws Exception {

		Socket servidor = new Socket(host,porta);
			
		PrintStream ps = new PrintStream(servidor.getOutputStream());
		InputStream is = servidor.getInputStream();
		BufferedReader reader=new BufferedReader(new InputStreamReader(is));
		String str="";	
		
		//Enviando requisição do tipo CGI
		// ps.println("CGI br.ufpb.ci.so.p20132.cgi.HelloWorld HTTP/1.0\r\n");
		// ps.println("CGI br.ufpb.ci.so.p20132.cgi.SimpleSort HTTP/1.0\r\n");
		ps.println("CGI br.ufpb.ci.so.p20132.cgi.LongHelloWorld HTTP/1.0\r\n");
		// ps.println("CGI br.ufpb.ci.so.p20132.cgi.BlockingCGI HTTP/1.0\r\n");
		
		enviado=true;
		notify();
		while((str=reader.readLine())!=null) {
			System.out.println(str);
		}
		
		servidor.close();
	}

	public synchronized void waitEnviado() throws InterruptedException
	{
		while(enviado==false)
		{
			System.out.println("esperando...");
			wait();
		}
	}
	
}
