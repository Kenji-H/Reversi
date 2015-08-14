package com.kenjih.reversi.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketWrapper {
	private Socket skt;
	private BufferedReader reader;
	private BufferedWriter writer;
	
	public SocketWrapper(Socket skt) throws IOException {
		this.skt = skt;
		reader = new BufferedReader(new InputStreamReader(this.skt.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(this.skt.getOutputStream()));			
	}
	
	public String readLine() throws IOException {
		return reader.readLine();
	}
	
	public void writeLine(String s) throws IOException {
		writer.write(s);
		writer.newLine();
		writer.flush();
	}
}
