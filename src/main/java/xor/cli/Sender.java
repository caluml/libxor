package xor.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import xor.lib.XoringInputStream;

public class Sender {

	/**
	 * 1. Pad 2. Offset 3. File 4. Remote address. 5 Port
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		File pad = new File(args[0]);
		if (!pad.canRead()) {
			throw new RuntimeException("Can't read from " + pad.getAbsolutePath());
		}
		int offset = Integer.parseInt(args[1]);
		System.out.println("Using " + pad.getAbsolutePath() + " as pad, starting at " + offset);
		InputStream inputStream = new FileInputStream(args[2]);
		System.out.println("Opened " + args[2] + " as input");
		XoringInputStream inputStream2 = new XoringInputStream(inputStream, pad, offset);

		Socket socket = new Socket(args[3], Integer.parseInt(args[4]));
		System.out.println("Connected to " + socket.getRemoteSocketAddress());
		OutputStream outputStream = socket.getOutputStream();
		int read;
		int bytes = 0;
		while ((read = inputStream2.read()) != -1) {
			outputStream.write(read);
			bytes++;
		}
		outputStream.flush();
		outputStream.close();
		socket.close();
		inputStream2.close();
		System.out.println("Flushed and closed output stream after writing " + bytes + " bytes");
	}

}
