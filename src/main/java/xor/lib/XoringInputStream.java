package xor.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XoringInputStream extends InputStream {

	private final InputStream inputStream;
	private final InputStream xorData;
	private int xorRead;

	/**
	 * Constructs an {@link XoringInputStream}
	 * 
	 * @param inputStream
	 *            the {@link InputStream} to wrap
	 * @param xorData
	 *            the data to XOR against
	 * @param offset
	 *            the offset into the XOR data to start with
	 * @throws IOException
	 *             if the stream does not support seek, or if some other I/O error occurs
	 */
	public XoringInputStream(InputStream inputStream, InputStream xorData, int offset) throws IOException {
		this.inputStream = inputStream;
		this.xorData = xorData;
		xorData.skip(offset);
	}

	public XoringInputStream(InputStream inputStream, File xorData, int offset) throws IOException {
		this.inputStream = inputStream;
		this.xorData = new FileInputStream(xorData);
		this.xorData.skip(offset);
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		int count = 0;
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) read();
			count++;
		}
		return count;
	}

	@Override
	public int read() throws IOException {
		int i = inputStream.read();
		int x = xorData.read();
		if (x == -1) {
			throw new InsufficientXorDataRuntimeException("Ran out of XOR data after reading " + xorRead + " bytes");
		}
		xorRead++;
		return x ^ i;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public long skip(long n) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int available() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void close() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public synchronized void mark(int readlimit) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean markSupported() {
		throw new RuntimeException("Not implemented");
	}

}