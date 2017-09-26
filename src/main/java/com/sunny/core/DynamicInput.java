/*
 *  
 *  * Copyright 2012-2112 Sunny, Inc..
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  
 */

package com.sunny.core;

import com.google.common.base.Preconditions;
import com.sunny.Input;
import com.sunny.bufferpool.BufferUsing;
import com.sunny.unlimited.UnlimitedInputStream;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

/**
 * Created by lzx on 17/6/13.
 */
public class DynamicInput extends Input {

    private final List<BufferUsing> usingList;
    private final UnlimitedInputStream unlimitedInputStream;
    private DataInputStream unlimitedDataInput;
    private int currentIndex = 0;
    private BufferUsing current = null;
    private ByteBuffer currentBuffer = null;

    private boolean closed;

    private boolean unlimited;

    private DataInputStream $in = new DataInputStream(this);

    private boolean releaseAfterConsume = false;

    public DynamicInput(List<BufferUsing> list, UnlimitedInputStream unlimitedInputStream) {
        this(list, unlimitedInputStream, false);
    }

    public DynamicInput(List<BufferUsing> list, UnlimitedInputStream unlimitedInputStream, boolean releaseAfterConsume) {
        super();
        this.usingList = Objects.requireNonNull(list, "bufferUsing list can't be null.");
        //Preconditions.checkState(!list.isEmpty(), "bufferUsing list can't be empty.");
        this.unlimitedInputStream = unlimitedInputStream;
        this.releaseAfterConsume = releaseAfterConsume;

        current = usingList.get(currentIndex);
        currentBuffer = current.getBuffer();
    }

    private void checkState()
    {
        Preconditions.checkState(!closed, "Output has been closed.");

    }

    private void ensureByteBuffer(int needSize) throws EOFException {
        if(unlimited)
            return;

        if(!currentBuffer.hasRemaining())
        {
            closeAndMoveNext(true);
        }
        Preconditions.checkState(current.getBuffer().remaining()>=needSize,
                "Illegal read, need %d, but only %d.", needSize, currentBuffer.remaining());

    }

    private void closeAndMoveNext(boolean throwException) throws EOFException {
        if(releaseAfterConsume)
            current.free();
        else
            currentBuffer.rewind();

        if(usingList.size()==currentIndex+1)
        {
            if(unlimitedInputStream==null)
                throw new EOFException();
            else
            {
                unlimited = true;
                unlimitedDataInput = new DataInputStream(unlimitedInputStream);
            }
        }
        else {

            current = usingList.get(++currentIndex);

            if (current == null) {
                if (throwException)
                    throw new EOFException();
                else
                    return;
            }
            currentBuffer = current.getBuffer();
        }
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        checkState();
        $in.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        checkState();
        $in.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        checkState();

        if(unlimited)
        {
            return unlimitedDataInput.skipBytes(n);
        }
        if(n<currentBuffer.remaining())
        {
            currentBuffer.position(currentBuffer.position()+n);
            return n;
        }
        else
        {
            int skipped = 0;

            while(currentBuffer.remaining()<n-skipped)
            {
                skipped += currentBuffer.remaining();
                currentBuffer.position(currentBuffer.limit());


                closeAndMoveNext(false);

                if(current==null)
                {
                    if(unlimited)
                    {
                        return skipped + unlimitedDataInput.skipBytes(n-skipped);
                    }
                    else
                        return skipped;
                }
            }

            currentBuffer.position(currentBuffer.position()+n-skipped);
            skipped = n;

            return skipped;
        }

    }

    @Override
    public boolean readBoolean() throws IOException {
        checkState();
        return readByte()!=0;
    }

    @Override
    public byte readByte() throws IOException {
        checkState();
        ensureByteBuffer(Byte.BYTES);
        if(unlimited)
        {
            return unlimitedDataInput.readByte();
        }
        else {

            return currentBuffer.get();
        }
    }

    @Override
    public int readUnsignedByte() throws IOException {
        checkState();
        ensureByteBuffer(Byte.BYTES);
        if(unlimited)
        {
            return unlimitedDataInput.readUnsignedByte();
        }
        else {

            return currentBuffer.get() & 0XFF;
        }
    }

    @Override
    public short readShort() throws IOException {
        checkState();
        ensureByteBuffer(Short.BYTES);
        if(unlimited)
        {
            return unlimitedDataInput.readShort();
        }
        else {

            return currentBuffer.getShort();
        }
    }

    @Override
    public int readUnsignedShort() throws IOException {
        checkState();
        ensureByteBuffer(Short.BYTES);
        if(unlimited)
        {
            return unlimitedDataInput.readUnsignedShort();
        }
        else {

            return currentBuffer.getShort() & 0XFFFF;
        }
    }

    @Override
    public char readChar() throws IOException {
        return (char)readShort();
    }

    @Override
    public int readInt() throws IOException {
        checkState();
        ensureByteBuffer(Integer.BYTES);
        if(unlimited)
        {
            return unlimitedDataInput.readInt();
        }
        else {

            return currentBuffer.getInt();
        }
    }

    @Override
    public long readLong() throws IOException {
        checkState();
        ensureByteBuffer(Long.BYTES);
        if(unlimited)
        {
            return unlimitedDataInput.readLong();
        }
        else {

            return currentBuffer.getLong();
        }
    }

    @Override
    public float readFloat() throws IOException {
        checkState();
        ensureByteBuffer(Float.BYTES);
        if(unlimited)
        {
            return unlimitedDataInput.readFloat();
        }
        else {

            return currentBuffer.getFloat();
        }
    }

    @Override
    public double readDouble() throws IOException {
        checkState();
        ensureByteBuffer(Double.BYTES);
        if(unlimited)
        {
            return unlimitedDataInput.readDouble();
        }
        else {

            return currentBuffer.getDouble();
        }
    }

    @Deprecated
    @Override
    public String readLine() throws IOException {
        checkState();
        return $in.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        checkState();
        return $in.readUTF();
    }

    @Override
    public int read() throws IOException {
        return readByte() & 0XFF;
    }

    @Override
    public void close() {
        this.usingList.forEach(bufferUsing -> {bufferUsing.free();});
        /*
        if(current!=null)
            current.free();
        */

        closed = true;
        if(unlimitedInputStream!=null)
            try {
                unlimitedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void rewind() {
        this.usingList.forEach(bufferUsing -> {bufferUsing.getBuffer().rewind();});
        if(unlimitedInputStream!=null)
            unlimitedInputStream.rewind();

        current = usingList.get(currentIndex);
        currentBuffer = current.getBuffer();
    }

    public void reset() {
        throw new IllegalStateException("Not supported 'reset'.");
    }
}
