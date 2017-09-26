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

package com.sunny.memorypool;

import com.sunny.MemoryPoolException;
import com.sunny.PoolConfig;
import com.sunny.bufferpool.BufferPool;
import com.sunny.bufferpool.FixedSizeBufferPool;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by lzx on 17/6/13.
 */
public class NMapMemoryPool extends BufferPoolMemoryPool {
    private File file;
    private RandomAccessFile raf;
    private FileChannel channel;
    private long maxSize;
    private int bufferSize;
    private int bufferCount;

    private ArrayList<ByteBuffer> bufferList;

    FixedSizeBufferPool bufferPool = null;

    public NMapMemoryPool(PoolConfig config, File file) {
        super(config);

        try {
            init(file);
        }catch(IOException e)
        {
            throw new MemoryPoolException("MemoryPool init failed.", e);
        }
    }

    @Override
    public BufferPool getBufferPool() {
        return bufferPool;
    }

    private void init(File file) throws IOException
    {
        if(file.exists() && file.isDirectory())
        {
            throw new MemoryPoolException("File already exists and is a directory");
        }
        
        boolean truncate = false;
        if(file.exists())
        {
            truncate = true;
        }
        else
            file.createNewFile();

        this.file = file;
        file.deleteOnExit();

        this.raf = new RandomAccessFile(file, "rw");
        this.channel = raf.getChannel();
        if (truncate) {
            try {
                channel.truncate(0);
            } catch (IOException e) {
                throw new MemoryPoolException("Can't truncate nmap file:" + file.getAbsolutePath());
            }
        }
        this.bufferSize = getPoolConfig().getBufferSize();
        this.bufferCount = getPoolConfig().getBufferCount();

        maxSize = 1L * bufferSize * bufferCount;

        try {
            ByteBuffer one = ByteBuffer.allocate(1);
            while (one.hasRemaining()) {
                channel.write(one, maxSize - 1);
            }
        } catch (IOException e) {
            throw new MemoryPoolException("IOException while attempting to extend file " + file.getAbsolutePath(), e);
        }



        initBufferPool();
    }

    private ReentrantLock lock = new ReentrantLock();
    protected ByteBuffer allocateNew()
    {
        try {
            lock.lock();

            return _allocate();

        }finally
        {
            lock.unlock();
        }
    }
    private Exception allocatedException;
    private long position;
    private MappedByteBuffer _allocate() {
        if(allocatedException!=null)
            return null;
        if(position<maxSize) {
            position += bufferSize;
            try {
                return channel.map(FileChannel.MapMode.READ_WRITE, position, bufferSize);
            } catch (IOException e) {
                allocatedException = e;
                e.printStackTrace();
            }

        }

        return null;
    }


    private void initBufferPool() throws IOException {

        this.bufferPool = new FixedSizeBufferPool(bufferSize);
        this.bufferList = new ArrayList(bufferCount);

        MappedByteBuffer buffer;
        long position = 0;
        for(int i=0;i<bufferCount;i++)
        {
            buffer = _allocate();
            if(allocatedException!=null)
                throw new MemoryPoolException("Nmap init pool failed. ", allocatedException);
            if(buffer==null)
                break;

            bufferList.add(buffer);

        }

        bufferPool.initAddBuffer(bufferList);
    }

    @Override
    public void close() {

        unmap();
        file.delete();
    }

    private void unmap() {
        bufferList.forEach(buffer->unmap((MappedByteBuffer)buffer));
    }

    private void unmap(MappedByteBuffer b) {
        try {
            if ((unmapHackSupported)) {
                Method cleanerMethod = b.getClass().getMethod("cleaner", new Class[0]);
                if (cleanerMethod != null) {
                    cleanerMethod.setAccessible(true);
                    Object cleaner = cleanerMethod.invoke(b, new Object[0]);
                    if (cleaner != null) {
                        Method clearMethod = cleaner.getClass().getMethod("clean", new Class[0]);
                        if (clearMethod != null)
                            clearMethod.invoke(cleaner, new Object[0]);
                    }
                }
            }
        } catch (Exception e) {
            unmapHackSupported = false;
        }
    }
    private static boolean unmapHackSupported = false;
    static {
        try {
            unmapHackSupported = Class.forName("sun.nio.ch.DirectBuffer") != null;
        } catch (Exception e) {
            unmapHackSupported = false;
        }

    }

}
