package br.ufpb.ci.so.p20132.bufferscale;

import br.ufpb.ci.so.p20132.Descritor;

public class BufferSJF extends Buffer {

	public BufferSJF(int tamanho) {
		super(tamanho);
	}

	@Override
	public synchronized Descritor getNext() throws Exception {
		while(total==0)
			wait();
		
		Descritor d = super.getNext();
		
		int i=0, min_index=0;
		
		for(i=1;i<total;i++)
		{
			if( buff[i].getArquivoTamanho() < buff[min_index].getArquivoTamanho() )
			{
				min_index = i;
			}
		}

		d = buff[min_index];
		
		for(int j=0;j<min_index;j++)
		{
			buff[j].addIdade();
		}
		
		for(int j=min_index;j<total-1;j++)
		{
			buff[j] = buff[j+1];
		}
		
		total--;
		notifyAll();
		
		showBuffer("rem");
		
		return d;
	}

	@Override
	public synchronized int getTotalRequisicoesAgendadas(Descritor d) {
		long tamanho = d.getArquivoTamanho();
		int contagem=0;
		
		for(int i=0;i<this.total;i++)
		{
			if( buff[i].getArquivoTamanho() <= tamanho )
				contagem++;
		}
		return contagem;
	}

}
