package xor.lib;

import java.io.InputStream;

/**
 * Implementation of {@link InputStream} which just keeps supplying a loop of the sample data.
 * <p>
 * Used for testing.
 */
public class TestingInputStream extends InputStream {

	private final byte[] bytes;

	private int i = 0;

	public TestingInputStream(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public int read() {
		if (i >= bytes.length) {
			i = 0;
		}
		byte b = bytes[i];
		i++;
		return b;
	}

}
