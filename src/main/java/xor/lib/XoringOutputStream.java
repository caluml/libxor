package xor.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class XoringOutputStream extends OutputStream {

	private final OutputStream outputStream;
	private final InputStream xorData;
	private int xorRead;

	/**
	 * Constructs an {@link XoringOutputStream}
	 * 
	 * @param outputStream
	 *            the {@link OutputStream} to wrap
	 * @param xorData
	 *            the data to XOR against
	 * @param offset
	 *            the offset into the XOR data to start with
	 * @throws IOException
	 *             if the stream does not support seek, or if some other I/O error occurs
	 */
	public XoringOutputStream(OutputStream outputStream, InputStream xorData, int offset) throws IOException {
		this.outputStream = outputStream;
		this.xorData = xorData;
		xorData.skip(offset);
	}

	public XoringOutputStream(OutputStream outputStream, File xorData, int offset) throws IOException {
		this.outputStream = outputStream;
		this.xorData = new FileInputStream(xorData);
		this.xorData.skip(offset);
	}

	@Override
	public void write(int b) throws IOException {
		outputStream.write((byte) (b ^ readXor()));
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		for (int i = 0; i < len; i++) {
			write(b[off + i]);
		}
	}

	@Override
	public void flush() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void close() throws IOException {
		outputStream.close();
	}

	private int readXor() throws IOException {
		int x = xorData.read();
		xorRead++;
		if (x == -1) {
			throw new InsufficientXorDataRuntimeException("Ran out of XOR data after reading " + xorRead + " bytes");
		}
		return x;
	}

}
