package br.ufpb.ci.so.p20132.bufferscale;

import java.lang.reflect.InvocationTargetException;

import br.ufpb.ci.so.p20132.Descritor;
import br.ufpb.ci.so.p20132.WebServer;

public abstract class Buffer {

	/*
	 * Escopo estático
	 */
	private static Buffer buffer = null;
	
	public static void init(Buffer tempBuffer) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		buffer = tempBuffer;
	}
	
	public static Buffer getInstance()
	{
		return buffer;
	}
	
	/*
	 * Escopo de instância
	 */
	protected Descritor buff[];
	
	protected int total;
	
	protected int totalRequisicoesAtendidas;
	
	protected int totalConcluidas;
	
	public Buffer(int tamanho)
	{
		buff = new Descritor[tamanho];
		total = 0;
		totalRequisicoesAtendidas = 0;
		totalConcluidas = 0;
	}
	
	public synchronized Descritor getNext() throws Exception
	{
		totalRequisicoesAtendidas++;
		return null;
	}
	
	public synchronized void addDescritor(Descritor d) throws InterruptedException {
		
		while(total==buff.length)
			wait();
		
		buff[total++] = d;
		
		d.setTempoAgendamento( System.currentTimeMillis() - WebServer.start_time );
		
		notifyAll();
		
	}
	
	public abstract int getTotalRequisicoesAgendadas(Descritor d);
	
	public int getSize()
	{
		return total;
	}
	
	public int getTotalRequisicoesAtendidas()
	{
		return this.totalRequisicoesAtendidas;
	}
	
	public void addConcluida()
	{
		totalConcluidas++;
	}
	
	public int getTotalConcluida()
	{
		return this.totalConcluidas;
	}
	
}
