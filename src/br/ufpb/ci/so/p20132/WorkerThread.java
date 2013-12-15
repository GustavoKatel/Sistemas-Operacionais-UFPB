package br.ufpb.ci.so.p20132;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
//import java.util.StringTokenizer;

import br.ufpb.ci.so.p20132.bufferscale.Buffer;

public class WorkerThread extends Thread {

	
	private int id_thread;
	private int contador_thread;
	
	public WorkerThread(int id_thread)
	{
		super();
		this.id_thread = id_thread;
		this.contador_thread=0;
	}
	
	public void run()
	{
		
		while(true)
		{
			try {
				processaRequisicao();
			} catch (IOException e) {
				System.out.println("Erro ao processar requisição!");
			}
			//
			contador_thread++;
		}
		
	}
	
	private void processaRequisicao() throws IOException
	{
		Descritor d = null;
		try {
			d = Buffer.getInstance().getNext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Socket reqSocket = d.getSocket();
		
//		BufferedReader doCliente = new BufferedReader(new InputStreamReader( reqSocket.getInputStream()));
		DataOutputStream paraCliente = new DataOutputStream( reqSocket.getOutputStream());
//		String requisicao = doCliente.readLine();
		
		
		String req = new String("[th: " + id_thread + "] Requisicao numero [" + Buffer.getInstance().getTotalRequisicoesAtendidas() + "] = " + "\"" + d.getRequisicao() + "\"");
		System.out.println( req );
		
//		StringTokenizer st = new StringTokenizer(requisicao);
		String tipo = d.getTipo(); // st.nextToken();
		byte[] bytes = null;
		
		
		if(tipo.equals("GET")) {
			
			try {
			
				File arquivo = new File(d.getArquivo()); // File(st.nextToken().substring(1));
	
				FileInputStream leitor = new FileInputStream (arquivo);
				bytes = new byte[(int)arquivo.length()];
				leitor.read(bytes);
				leitor.close();
			} catch( IOException e ) {
				bytes = e.getMessage().getBytes();	
			}
		} else if( tipo.equals("CGI")) {
			
			Process p = Runtime.getRuntime().exec("java -classpath bin " + d.getArquivo());
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
		paraCliente.writeBytes("id-requisicao " + Buffer.getInstance().getTotalRequisicoesAtendidas() + "\r\n");
		paraCliente.writeBytes("tempo-chegada-requisicao " + d.getTempoChegada() + "\r\n");
		paraCliente.writeBytes("cont-requisicao-agendada " + d.getTotalRequisicoesAgendadas() + "\r\n");
		paraCliente.writeBytes("tempo-agendamento-requisicao " + d.getTempoAgendamento()  + "\r\n");
		paraCliente.writeBytes("cont-requisicao-concluida " + Buffer.getInstance().getTotalConcluida()  + "\r\n");
		paraCliente.writeBytes("tempo-requisicao-concluida " + String.valueOf(System.currentTimeMillis() - WebServer.start_time)  + "\r\n");
		paraCliente.writeBytes("idade-requisicao " + d.getIdade() + "\r\n");
		paraCliente.writeBytes("tipo-requisicao " + d.getTipo()  + "\r\n");
			
		//Retorno das estatísticas do thread
		paraCliente.writeBytes("ida-thread " + id_thread + "\r\n");
		paraCliente.writeBytes("cont-thread " + contador_thread + "\r\n");
			
		paraCliente.writeBytes("\r\n\n");
		
		paraCliente.write(bytes, 0, bytes.length);
		
		reqSocket.close();
		
		Buffer.getInstance().addConcluida();
	}
	
}
