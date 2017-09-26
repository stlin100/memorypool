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
import com.sunny.*;
import com.sunny.bufferpool.BufferPool;
import com.sunny.bufferpool.BufferUsing;
import com.sunny.bufferpool.BufferUsingList;
import com.sunny.unlimited.UnlimitedInputStream;
import com.sunny.unlimited.UnlimitedOutputStream;
import com.sunny.unlimited.UnlimitedProvider;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * not thread-safe, only used in single thread
 * Created by lzx on 17/6/12.
 */
public class DynamicOutput extends Output {

    private final BufferPool bufferPool;
    private final PoolConfig poolConfig;

    private BufferUsingList usingList;
    private BufferUsing current = null;
    private ByteBuffer currentBuffer = null;

    private boolean closed;

    private DataOutputStream utf8out = new DataOutputStream(this);

    private long dataSize = 0;

    private boolean unlimited;
    private UnlimitedOutputStream unlimitedOutputStream;
    private DataOutputStream unlimitedDataOutput;
    private UnlimitedInputStream unlimitedInputStream;

    public DynamicOutput(BufferPool bufferPool, PoolConfig poolConfig)
    {
        this.bufferPool = bufferPool;
        this.poolConfig = poolConfig;

        this.usingList = new BufferUsingList();
    }

    private void ensureByteBuffer(int needSize)
    {
        if(unlimited)
        {
            return;
        }

        if(current==null)
        {
            if(dataSize+needSize>poolConfig.getMaxOutputSize())
                throw new MemoryPoolException("exceed maxOutputSize:" + poolConfig.getMaxOutputSize());
            acquireNewByteBuffer();
        }
        else {
            if (dataSize + current.getBuffer().position() + needSize > poolConfig.getMaxOutputSize())
                throw new MemoryPoolException("exceed maxOutputSize:" + poolConfig.getMaxOutputSize());

            if ((currentBuffer.remaining()) < needSize) {
                dataSize += current.getBuffer().position();

                acquireNewByteBuffer();
            }
        }
    }

    private void acquireNewByteBuffer()
    {
        current = acquireTimeout();
        if(current!=null)
        {
            usingList.append(current);
            currentBuffer = current.getBuffer();
        }
        else
        {
            if(!poolConfig.isUseUnlimited())
                throw new ExhaustedException();

            initUnlimited();
        }

    }

    private void initUnlimited() {
        UnlimitedProvider provider = UnlimitedProvider.fileUnlimited();
        unlimitedOutputStream = provider.createOutputStream();
        unlimitedDataOutput = new DataOutputStream(unlimitedOutputStream);
        unlimited = true;
    }


    public long dataSize()
    {
        long size = dataSize + (current!=null?current.getBuffer().position():0);
        if(unlimited)
            size += unlimitedOutputStream.dataSize();

        return size;
    }

    private void checkState()
    {
        Preconditions.checkState(!closed, "Output has been closed.");

    }

    private BufferUsing acquireTimeout()
    {
        if(poolConfig.getTimeout()>0)
        {
            try {
                BufferUsing buffer = bufferPool.acquire(poolConfig.getTimeout(), poolConfig.getTimeUnit());
                return buffer;
            } catch (InterruptedException e) {
                return null;
            }
        }
        else
        {
            BufferUsing bufferUsing = null;
            //while(bufferUsing==null)
                bufferUsing = bufferPool.acquire();
            return bufferUsing;
        }
    }

    @Override
    public void write(int b) throws IOException {

        writeByte(b & 0XFF);
    }

    /**
     * if getInput before, close will not free usingList
     */
    @Override
    public void close() {
        if(closed)
            return;

        closed = true;
        if(unlimitedOutputStream!=null)
        {
            unlimitedOutputStream.close();
        }
        usingList.free();
    }



    @Override
    public void writeBoolean(boolean v) throws IOException {
        writeByte(v ? 1 : 0);
    }

    @Override
    public void writeByte(int v) throws IOException {
        checkState();
        ensureByteBuffer(Byte.BYTES);
        if(unlimited)
        {
            unlimitedDataOutput.writeByte(v);
        }
        else {

            current.getBuffer().put((byte) v);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        checkState();

        write0(b, off, len);

    }

    private void write0(byte[] b, int off, int len) throws IOException {
        if(unlimited)
        {
            unlimitedDataOutput.write(b,off,len);
            return;
        }
        if(current==null)
        {
            ensureByteBuffer(len);
        }

        if(current!=null)
        {
            int step = 0;
            while(len>0 && current!=null) {
                step = Math.min(currentBuffer.remaining(), len);
                currentBuffer.put(b, off, step);
                off += step;
                len -= step;

                if(len>0)
                    acquireNewByteBuffer();

            }
        }
        if(len>0)
        {
            Preconditions.checkState(unlimited, "no where to write?!");
            unlimitedDataOutput.write(b,off,len);
        }
    }

    @Override
    public void writeShort(int v) throws IOException {
        checkState();
        ensureByteBuffer(Short.BYTES);
        if(unlimited)
        {
            unlimitedDataOutput.writeShort(v);
        }
        else {

            current.getBuffer().putShort((short)v);
        }

    }

    @Override
    public void writeChar(int v) throws IOException {
        writeShort(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        checkState();
        ensureByteBuffer(Integer.BYTES);
        if(unlimited)
        {
            unlimitedDataOutput.writeInt(v);
        }
        else {

            current.getBuffer().putInt(v);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        checkState();
        ensureByteBuffer(Long.BYTES);

        if(unlimited)
        {
            unlimitedDataOutput.writeLong(v);
        }
        else {

            current.getBuffer().putLong(v);
        }
    }

    @Override
    public void writeFloat(float v) throws IOException {
        checkState();
        ensureByteBuffer(Float.BYTES);
        if(unlimited)
        {
            unlimitedDataOutput.writeFloat(v);
        }
        else {

            current.getBuffer().putFloat(v);
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        checkState();
        ensureByteBuffer(Double.BYTES);
        if(unlimited)
        {
            unlimitedDataOutput.writeDouble(v);
        }
        else {

            current.getBuffer().putDouble(v);
        }
    }

    @Override
    public void writeBytes(String s) throws IOException {
        checkState();
        int len = s.length();
        for(int i=0;i<len;i++)
        {
            writeByte(s.charAt(i));
        }
    }

    @Override
    public void writeChars(String s) throws IOException {
        checkState();
        int len = s.length();
        for(int i=0;i<len;i++)
        {
            writeChar(s.charAt(i));
        }
    }

    @Override
    public void writeUTF(String s) throws IOException {
        checkState();
        utf8out.writeUTF(s);
        //utf8out.flush();
    }

    @Override
    public Input toInput() {
        UnlimitedInputStream unlimitedInputStream = unlimited?unlimitedOutputStream.toInputStream():null;
        if(usingList.isEmpty())
        {
            if(unlimitedInputStream==null)
                return Input.empty();

            return unlimitedInputStream.toInput();
        }

        List<BufferUsing> list = usingList.toList();
        list.forEach(u->{u.getBuffer().flip();});
        closed = true;



        return new DynamicInput(list, unlimitedInputStream);
    }


}
