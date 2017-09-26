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

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Created by lzx on 17/6/13.
 */
public class HeapMemoryPool extends BufferPoolMemoryPool {


    FixedSizeBufferPool bufferPool = null;

    public HeapMemoryPool(PoolConfig config) {
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

        int bufferCount = getPoolConfig().getBufferCount();
        int bufferSize = getPoolConfig().getBufferSize();

        this.bufferPool = new FixedSizeBufferPool(bufferSize);

        ByteBuffer buffer;
        long position = 0;
        for(int i=0;i<bufferCount;i++)
        {
            buffer = ByteBuffer.allocate(bufferSize);

            bufferPool.initAddBuffer(buffer);
        }
    }

    @Override
    public void close() {

    }
}
