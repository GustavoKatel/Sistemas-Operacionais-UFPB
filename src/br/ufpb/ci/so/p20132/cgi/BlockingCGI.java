package br.ufpb.ci.so.p20132.cgi;

public class BlockingCGI {

	public static void main(String a[]) throws InterruptedException
	{
		while(true)
			Thread.sleep(10000);
	}
	
}
