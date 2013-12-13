package br.ufpb.ci.so.p20132.bufferscale;

import java.util.Random;

import br.ufpb.ci.so.p20132.Descritor;

public class BufferRandom extends Buffer {

	public BufferRandom(int tamanho) {
		super(tamanho);
	}

	@Override
	public Descritor getNext() throws Exception {
		while(total==0)
			wait();
		
		Descritor d = super.getNext();
		
		int i = (new Random()).nextInt(total);
		d = buff[i];
		
		for(int j=0;j<total;j++)
		{
			if(j==i) continue;
			buff[j].addIdade();
		}
		
		for(int j=i;j<total-1;j++)
		{
			buff[j] = buff[j+1];
		}
		
		total--;
		notifyAll();
		
		return d;
	}

	@Override
	public int getTotalRequisicoesAgendadas(Descritor d) {
		return -1;
	}

}
