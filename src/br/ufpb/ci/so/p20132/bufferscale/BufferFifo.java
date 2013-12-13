package br.ufpb.ci.so.p20132.bufferscale;

import br.ufpb.ci.so.p20132.Descritor;

public class BufferFifo extends Buffer {
	
	public BufferFifo(int tamanho) {
		super(tamanho);
	}

	@Override
	public synchronized Descritor getNext() throws Exception {
		
		while(total==0)
			wait();

		Descritor d = super.getNext();
		
		d = buff[0];
		
		for(int i=0;i<total-1;i++)
		{
			buff[i] = buff[i+1];
		}
		total--;
		
		notifyAll();
		
		return d;
	}

	@Override
	public int getTotalRequisicoesAgendadas(Descritor d) {
		return total;
	}
	
}
