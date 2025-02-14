package xor.lib;

import java.io.IOException;
import java.io.InputStream;

public class SimpleXorStream extends InputStream {

	private final InputStream input;
	private final InputStream pad;

	public SimpleXorStream(InputStream input,
												 InputStream pad) {
		this.input = input;
		this.pad = pad;
	}

	@Override
	public int read() throws IOException {
		return input.read() ^ pad.read();
	}
}
