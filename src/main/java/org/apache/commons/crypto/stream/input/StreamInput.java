 /*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.crypto.stream.input;

import static org.apache.commons.crypto.stream.CryptoInputStream.EOS;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.commons.crypto.stream.CryptoInputStream;

 /**
 * The StreamInput class takes a {@link InputStream} object and wraps it as
 * {@link Input} object acceptable by {@link CryptoInputStream}.
 */
public class StreamInput implements Input {

    private final byte[] buf;
    private final int bufferSize;
    final InputStream in;

    /**
     * Constructs a {@link org.apache.commons.crypto.stream.input.StreamInput}.
     *
     * @param inputStream the InputStream object.
     * @param bufferSize the buffer size.
     */
    public StreamInput(final InputStream inputStream, final int bufferSize) {
        this.in = inputStream;
        this.bufferSize = bufferSize;
        this.buf = new byte[bufferSize];
    }

    /**
     * Overrides the {@link Input#available()}. Returns an estimate of the
     * number of bytes that can be read (or skipped over) from this input stream
     * without blocking by the next invocation of a method for this input
     * stream. The next invocation might be the same thread or another thread. A
     * single read or skip of this many bytes will not block, but may read or
     * skip fewer bytes.
     *
     * @return an estimate of the number of bytes that can be read (or skipped
     *         over) from this input stream without blocking or {@code 0} when
     *         it reaches the end of the input stream.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int available() throws IOException {
        return in.available();
    }

    /**
     * Overrides the
     * {@link org.apache.commons.crypto.stream.input.Input#seek(long)}. Closes
     * this input and releases any system resources associated with the under
     * layer input.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        in.close();
    }

    /**
     * Overrides the
     * {@link org.apache.commons.crypto.stream.input.Input#read(ByteBuffer)}.
     * Reads a sequence of bytes from input into the given buffer.
     *
     * @param dst The buffer into which bytes are to be transferred.
     *
     * @return the total number of bytes read into the buffer, or
     *         {@code EOS (-1)} if there is no more data because the end of the
     *         stream has been reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(final ByteBuffer dst) throws IOException {
        int remaining = dst.remaining();
        int read = 0;
        while (remaining > 0) {
            final int n = in.read(buf, 0, Math.min(remaining, bufferSize));
            if (n == EOS) {
                if (read == 0) {
                    read = EOS;
                }
                break;
            }
            if (n > 0) {
                dst.put(buf, 0, n);
                read += n;
                remaining -= n;
            }
        }
        return read;
    }

    /**
     * Overrides the
     * {@link org.apache.commons.crypto.stream.input.Input#read(long, byte[], int, int)}
     * . Reads up to {@code len} bytes of data from the input stream into
     * an array of bytes. An attempt is made to read as many as {@code len}
     * bytes, but a smaller number may be read. The number of bytes actually
     * read is returned as an integer.
     *
     * @param position the given position within a stream.
     * @param buffer the buffer into which the data is read.
     * @param offset the start offset in array buffer.
     * @param length the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or
     *         {@code EOS (-1)} if there is no more data because the end of the
     *         stream has been reached.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int read(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
        throw new UnsupportedOperationException("Positioned read is not supported by this implementation");
    }

    /**
     * Overrides the
     * {@link org.apache.commons.crypto.stream.input.Input#seek(long)}. Seeks to
     * the given offset from the start of the stream. The next read() will be
     * from that location.
     *
     * @param position the offset from the start of the stream.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void seek(final long position) throws IOException {
        throw new UnsupportedOperationException("Seek is not supported by this implementation");
    }

    /**
     * Overrides the
     * {@link org.apache.commons.crypto.stream.input.Input#skip(long)}. Skips
     * over and discards {@code n} bytes of data from this input stream.
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public long skip(final long n) throws IOException {
        return in.skip(n);
    }
}
