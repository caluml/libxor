package xor.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class XoringOutputStream extends OutputStream {

	private final OutputStream outputStream;
	private final InputStream xorData;
	private int xorRead;

	public XoringOutputStream(OutputStream outputStream, InputStream xorData, int offset) throws IOException {
		this.outputStream = outputStream;
		this.xorData = xorData;
		xorData.skip(offset);
	}

	@Override
	public void write(int b) throws IOException {
		final int xor = xorData.read();
		if (xor == -1) {
			throw new InsufficientXorDataRuntimeException("Ran out of XOR data after reading " + xorRead + " bytes");
		}
		xorRead++;
		outputStream.write((byte) (b ^ xor));
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		for (byte x : bytes) {
			write(x);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void flush() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void close() throws IOException {
		throw new RuntimeException("Not implemented");
	}

}
