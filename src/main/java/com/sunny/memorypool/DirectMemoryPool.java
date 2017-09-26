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
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Created by lzx on 17/6/13.
 */
public class DirectMemoryPool extends BufferPoolMemoryPool {

    private long size;
    private int bufferSize;
    private int bufferCount;

    FixedSizeBufferPool bufferPool = null;

    public DirectMemoryPool(PoolConfig config) {
        super(config);

        try {
            initBufferPool();
        }catch(IOException e)
        {
            throw new MemoryPoolException("MemoryPool init failed.", e);
        }
    }

    @Override
    public BufferPool getBufferPool() {
        return bufferPool;
    }

    private void initBufferPool() throws IOException {

        bufferCount = getPoolConfig().getBufferCount();
        bufferSize = getPoolConfig().getBufferSize();

        this.bufferPool = new FixedSizeBufferPool(bufferSize);

        ByteBuffer buffer;
        long position = 0;
        for(int i=0;i<bufferCount;i++)
        {
            buffer = ByteBuffer.allocateDirect(bufferSize);

            bufferPool.initAddBuffer(buffer);
        }
    }

    @Override
    public void close() {

    }
}