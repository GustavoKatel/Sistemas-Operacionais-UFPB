package br.ufpb.ci.so.p20132.bufferscale;

import br.ufpb.ci.so.p20132.Descritor;

public class BufferPCGI extends Buffer {

	public BufferPCGI(int tamanho) {
		super(tamanho);
	}

	@Override
	public synchronized Descritor getNext() throws Exception {
		while(total==0)
			wait();
		
		Descritor d = super.getNext();
		
		int i;
		
		// pega o primeiro CGI da fila
		for(i=0;i<total;i++)
		{
			if(buff[i].getTipo().equals("CGI"))
			{
				d = buff[i];
				break;
			}
		}
		if(i<total)
		{
			for(int j=0;j<i;j++)
				buff[j].addIdade();
		}
		
		// se não houver CGI, pega o primeiro GET
		// reorganiza a fila
		if(i>=total)
		{
			d = buff[0];
			i=0;
		}
		
		for(int j=i;j<total-1;j++)
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
		String tipo = d.getTipo();
		int contagem = 0;
		for(int i=0;i<total;i++)
		{
			if(buff[i].getTipo().equals(tipo))
				contagem++;
		}
		return contagem;
	}

}
