package xor.lib;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.SecureRandom;

public class PadTruncatingXorOutputStream extends OutputStream {

	private final SecureRandom	   random	= new SecureRandom();

	private final OutputStream	   outputStream;
	private final RandomAccessFile	file;
	private long	               pos;
	private int	                   xorRead;

	public PadTruncatingXorOutputStream(final OutputStream outputStream, final File xorData, final int offset)
	        throws IOException {
		this.outputStream = outputStream;
		file = new RandomAccessFile(xorData, "rwd");
		pos = file.length() - offset;
	}

	@Override
	public void write(final int b) throws IOException {
		outputStream.write((byte) (b ^ readXor()));
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
		for (int i = 0; i < 10; i++) {
			file.write(random.nextInt());
		}
		file.setLength(pos);

		xorRead++;
		return x;
	}
}
