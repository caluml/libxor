package xor.cli;

import xor.lib.XoringOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileReceiver {

	/**
	 * 1. Pad 2. Offset 3. Output file 4. Port (optional)
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.err.println("Usage: FileReceiver <pad> <offset> <output file> <port>");
			System.exit(1);
		}
		File pad = new File(args[0]);
		if (!pad.canRead()) {
			throw new RuntimeException("Can't read from " + pad.getAbsolutePath());
		}
		int offset = Integer.parseInt(args[1]);
		System.out.println("Using " + pad.getAbsolutePath() + " as pad, starting at " + offset);
		File outputFile = new File(args[2]);
		if (outputFile.exists()) {
			throw new RuntimeException(outputFile.getAbsolutePath() + " already exists!");
		}
		OutputStream outputStream = new FileOutputStream(outputFile);
		System.out.println("Opened " + args[2] + " as output");
		XoringOutputStream outputStream2 = new XoringOutputStream(outputStream, pad, offset);

		ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[3]));
		System.out.println("Listening on " + serverSocket);
		Socket socket = serverSocket.accept();
		System.out.println("Accepted connection from " + socket.getRemoteSocketAddress());
		InputStream inputStream = socket.getInputStream();
		int read;
		int bytes = 0;
		while ((read = inputStream.read()) != -1) {
			outputStream2.write(read);
			bytes++;
		}
		outputStream2.close();
		outputStream.close();
		socket.close();
		outputStream2.close();
		System.out.println("Flushed and closed output stream after writing " + bytes + " bytes");
	}

}
