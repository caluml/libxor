libxor
======

libxor is a Java library to help with XORing functionality.

It consists of XoringInputStream and XoringOutputStream.
These take an XOR data file, and can wrap other Streams (FileInputStream, network socket streams, etc).
When data is written/read from the stream, the content is XORed against the data in the XOR data file.

If you run out of pad data, an InsufficientXorDataRuntimeException is thrown.

This can be used to implement a <a href="https://en.wikipedia.org/wiki/One-time_pad">One-time Pad</a>.
Remember, if you use a one time pad, they are only secure if you never reuse a pad, if the pad data is perfectly random, and if the pad is securely destroyed after the pad is used.

Usage
=====

<code>
FileInputStream source = new FileInputStream("/etc/passwd");
InputStream xorData = new FileInputStream("/your/pad/file");

XoringInputStream xoringInputStream = new XoringInputStream(source, xorData, 0);

FileUtils.copyInputStreamToFile(xoringInputStream, new File("/tmp/output"));

// Remember to delete /your/pad/file now.
</code>
