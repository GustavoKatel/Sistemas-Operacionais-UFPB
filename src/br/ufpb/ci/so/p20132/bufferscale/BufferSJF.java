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
		
		int i, min_index = 0;
		long min = buff[0].getArquivoTamanho();
		d = buff[0];
		// pega o arquivo menor
		for(i=1;i<total;i++)
		{
			if(d.getArquivoTamanho() < min)
			{
				min = d.getArquivoTamanho();
				min_index = i;
			}
		}
		
		d = buff[min_index];
		
		for(int j=0;j<total;j++)
		{
			if(j==min_index) continue;
			buff[j].addIdade();
		}
		
		for(int j=i;j<total-1;j++)
		{
			buff[j] = buff[j+1];
		}
		
		total--;
		notifyAll();
		
		showBuffer();
		
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
