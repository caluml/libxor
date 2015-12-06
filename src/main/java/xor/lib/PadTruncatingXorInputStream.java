package xor.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.SecureRandom;

public class PadTruncatingXorInputStream extends InputStream {

	private final SecureRandom	   random	= new SecureRandom();

	private final InputStream	   inputStream;
	private final RandomAccessFile	file;
	private long	               pos;
	private int	                   xorRead;
	private int	                   bytesRead;

	private ProgressListener	   progressListener;

	/**
	 * Creates a {@link PadTruncatingXorInputStream}. This XORs an {@link InputStream} with the XOR data in a file. It
	 * overwrites, and then truncates the XOR data file as it reads it, to encourage non-reuse of the XOR data. The data
	 * in the XOR file is read backwards, from the end of the file.
	 *
	 * @param inputStream
	 * @param xorData
	 * @param offset
	 * @throws IOException
	 */
	public PadTruncatingXorInputStream(final InputStream inputStream, final File xorData, final int offset)
	        throws IOException {
		this.inputStream = inputStream;
		file = new RandomAccessFile(xorData, "rwd");
		pos = file.length() - offset;
	}

	@Override
	public int read() throws IOException {
		final int read = inputStream.read();
		if (read == -1) {
			return -1;
		}
		bytesRead++;

		return read ^ readXor();
	}

	public void setProgressListener(final ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	private void notifyProgress() {
		if (progressListener != null) {
			progressListener.bytesProcessed(bytesRead);
		}
	}

	/**
	 * Reads a byte of XOR from the file, overwrites it with a random value, and truncates the file.
	 *
	 * @return
	 * @throws IOException
	 */
	private int readXor() throws IOException {
		if (pos <= 0) {
			throw new InsufficientXorDataRuntimeException("Ran out of XOR data after reading " + xorRead + " bytes");
		}
		pos--;
		file.seek(pos);
        final int x = file.read();
        file.write(random.nextInt());
        file.setLength(pos);

		xorRead++;
		return x;
	}
}
