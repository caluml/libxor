package xor.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XoringInputStream extends InputStream {

	private final InputStream inputStream;
	private final InputStream xorData;
	private int xorRead;
	private ProgressListener progressListener;
	private int bytesRead;

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
	public int read() throws IOException {
		int read = inputStream.read();
		if (read == -1) {
			return -1;
		}
		bytesRead++;
		return read ^ readXor();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > b.length - off) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		int c = read();
		if (c == -1) {
			return -1;
		}
		b[off] = (byte) c;

		int i = 1;
		try {
			for (; i < len; i++) {
				c = read();
				if (c == -1) {
					break;
				}

				b[off + i] = (byte) c;
			}
		} catch (IOException ee) {
		}
		notifyProgress();
		return i;
	}

	private int readXor() throws IOException {
		int x = xorData.read();
		xorRead++;
		if (x == -1) {
			throw new InsufficientXorDataRuntimeException("Ran out of XOR data after reading " + xorRead + " bytes");
		}
		return x;
	}

	@Override
	public long skip(long n) throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
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

	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	private void notifyProgress() {
		if (progressListener != null) {
			progressListener.bytesProcessed(bytesRead);
		}
	}
}