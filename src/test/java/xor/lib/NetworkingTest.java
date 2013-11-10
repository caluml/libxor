package xor.lib;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import org.junit.Test;

public class NetworkingTest {

	private final Random random = new Random();

	@Test
	public void Xor_works_over_network_socket() throws Exception {
		byte[] original = "Test 客服中心 Фото".getBytes("UTF-8");
		byte[] xorData = getXorData(original.length);
		ServerSocket serverSocket = new ServerSocket(0);
		Socket socket = new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort());
		Socket listening = serverSocket.accept();

		@SuppressWarnings("resource")
		XoringOutputStream xoringOutputStream = new XoringOutputStream(socket.getOutputStream(),
				new ByteArrayInputStream(xorData), 0);

		@SuppressWarnings("resource")
		XoringInputStream xoringInputStream = new XoringInputStream(listening.getInputStream(),
				new ByteArrayInputStream(xorData), 0);


		xoringOutputStream.write(original);

		byte[] input = new byte[original.length];
		xoringInputStream.read(input);

		assertArrayEquals(original, input);

		socket.close();
		serverSocket.close();
	}

	@SuppressWarnings("unused")
	private byte[] copyArray(byte[] bytes) {
		byte[] result = new byte[bytes.length];
		System.arraycopy(bytes, 0, result, 0, bytes.length);
		return result;
	}

	private byte[] getXorData(int length) {
		byte[] xorData = new byte[length];
		random.nextBytes(xorData);
		return xorData;
	}

}
