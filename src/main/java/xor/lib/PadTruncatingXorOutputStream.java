package xor.lib;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.SecureRandom;

/**
 * An {@link OutputStream} that XORs the data being written through it with a pad, and truncates the pad as it is used
 */
public class PadTruncatingXorOutputStream extends OutputStream {

	private final SecureRandom random = new SecureRandom();

	private final OutputStream outputStream;
	private final RandomAccessFile padFile;
	private final boolean overwritePad;

	private ProgressListener progressListener;

	private long padPosition;
	private int padRead;
	private int bytesWritten;

	/**
	 * Creates a {@link PadTruncatingXorOutputStream}. This XORs the supplied {@link OutputStream} with the pad data in a
	 * file. It overwrites, and then truncates the XOR data file as it reads it, to encourage non-reuse of the pad data.
	 * The data in the pad file is read backwards, from the end of the file.
	 */
	public PadTruncatingXorOutputStream(final OutputStream outputStream,
																			final File xorData,
																			final int offset,
																			final boolean overwritePad) throws IOException {
		this.outputStream = outputStream;
		this.padFile = new RandomAccessFile(xorData, "rwd");
		this.padPosition = padFile.length() - offset;
		this.overwritePad = overwritePad;
	}

	@Override
	public void write(final int b) throws IOException {
		outputStream.write((byte) (b ^ readPad()));

		bytesWritten++;
	}

	public void setProgressListener(final ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	private void notifyProgress() {
		if (progressListener != null) {
			progressListener.bytesProcessed(bytesWritten);
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
