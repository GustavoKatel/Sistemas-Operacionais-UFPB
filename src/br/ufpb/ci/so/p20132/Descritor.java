package br.ufpb.ci.so.p20132;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Descritor {

	private Socket socket;
	private String tipo;
	private String arquivo;
	
	public Descritor(Socket socket)
	{
		this.socket = socket;
		processaTipo();
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
		tipo = str.substring(0, 3);
		arquivo = str.substring(0, str.lastIndexOf("HTTP/1.0"));
		arquivo = arquivo.replaceAll(tipo, "");
		arquivo = arquivo.trim();
	}
	
	public Socket getSocket()
	{
		return this.socket;
	}
	
	public String getTipo()
	{
		return this.tipo;
	}
	
}
