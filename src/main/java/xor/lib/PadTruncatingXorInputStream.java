package xor.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.SecureRandom;

/**
 * An {@link InputStream} that XORs the data being read through it with a pad, and truncates the pad as it is used
 */
public class PadTruncatingXorInputStream extends InputStream {

	private final SecureRandom random = new SecureRandom();

	private final InputStream inputStream;
	private final RandomAccessFile padFile;
	private final boolean overwritePad;

	private ProgressListener progressListener;

	private long padPosition;
	private int padRead;
	private int bytesRead;


	/**
	 * Creates a {@link PadTruncatingXorInputStream}. This XORs the supplied {@link InputStream} with the pad data in a
	 * file. It overwrites, and then truncates the pad data file as it reads it, to encourage non-reuse of the pad data.
	 * The data in the pad file is read backwards, from the end of the file.
	 */
	public PadTruncatingXorInputStream(final InputStream inputStream,
																		 final File xorData,
																		 final int offset,
																		 final boolean overwritePad) throws IOException {
		this.inputStream = inputStream;
		this.padFile = new RandomAccessFile(xorData, "rwd");
		this.padPosition = padFile.length() - offset;
		this.overwritePad = overwritePad;
	}

	@Override
	public int read() throws IOException {
		final int read = inputStream.read();
		if (read == -1) {
			return -1;
		}
		bytesRead++;

		return read ^ readPad();
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
	 * Reads a byte from the pad file, optionally overwrites it with a random value, and truncates the file.
	 */
	private int readPad() throws IOException {
		if (padPosition <= 0) {
			throw new InsufficientPadDataRuntimeException("Ran out of pad data after reading " + padRead + " bytes");
		}

		padPosition--;
		padFile.seek(padPosition);
		final int x = padFile.read();

		if (overwritePad) padFile.write(random.nextInt()); // This is a slow operation
		padFile.setLength(padPosition);

		padRead++;
		return x;
	}
}
