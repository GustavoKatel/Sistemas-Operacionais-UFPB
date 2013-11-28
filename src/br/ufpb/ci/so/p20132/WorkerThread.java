package br.ufpb.ci.so.p20132;

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
			// se buffer vazio
			// wait();
			//
			processaRequisicao();
			//
			contador_thread++;
		}
		
	}
	
	private void processaRequisicao()
	{
		
	}
	
}
