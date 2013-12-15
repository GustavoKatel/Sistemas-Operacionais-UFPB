package br.ufpb.ci.so.p20132;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Descritor {

	private Socket socket;
	private String requisicao;
	private String tipo;
	private String arquivo;
	private long tempo_chegada;
	private int totalRequisicoesAgendadas;
	private long tempo_agendamento;
	private int idade;
	
	private long arquivo_tamanho;
	
	public Descritor(Socket socket, long tempo_chegada)
	{
		this.socket = socket;
		this.tempo_chegada = tempo_chegada;
		processaTipo();
		//
		this.idade = 0;
		//
		File f = new File(arquivo);
		arquivo_tamanho = f.length();
	}
	
	private void processaTipo()
	{
		InputStream is = null;
		try {
			is = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader reader=new BufferedReader(new InputStreamReader(is));
		String str="";
		try {
			str = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		requisicao = str;
		tipo = str.substring(0, 3);
		arquivo = str.substring(4, str.lastIndexOf("HTTP/1.0"));
//		arquivo = arquivo.replaceAll(tipo, "");
		arquivo = arquivo.trim();
	}
	
	public Socket getSocket()
	{
		return this.socket;
	}
	
	public String getRequisicao()
	{
		return requisicao;
	}
	
	public String getTipo()
	{
		return this.tipo;
	}
	
	public String getArquivo()
	{
		return this.arquivo;
	}
	
	public long getArquivoTamanho()
	{
		return this.arquivo_tamanho;
	}
	
	public long getTempoChegada()
	{
		return this.tempo_chegada;
	}
	
	public void setTotalRequisicoesAgendadas(int t)
	{
		this.totalRequisicoesAgendadas = t;
	}
	
	public int getTotalRequisicoesAgendadas()
	{
		return this.totalRequisicoesAgendadas;
	}
	
	public void setTempoAgendamento(long t)
	{
		this.tempo_agendamento = t;
	}
	
	public long getTempoAgendamento()
	{
		return this.tempo_agendamento;
	}
	
	public void addIdade()
	{
		this.idade++;
	}
	
	public int getIdade()
	{
		return this.idade;
	}
	
}
