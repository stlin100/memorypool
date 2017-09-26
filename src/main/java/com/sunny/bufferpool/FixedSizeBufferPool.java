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

package com.sunny.bufferpool;

import com.google.common.collect.Sets;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lzx on 17/6/12.
 */
public class FixedSizeBufferPool implements BufferPool {

    private final int bufferSize;

    private BlockingQueue<ByteBuffer> freeList = new LinkedBlockingQueue<ByteBuffer>();

    //private Set<ByteBuffer> usedSet = Sets.newConcurrentHashSet();//Sets.newIdentityHashSet();

    private AtomicInteger usedBufferCount = new AtomicInteger(0);

    public FixedSizeBufferPool(int bufferSize)
    {
        this.bufferSize = bufferSize;

    }

    public void initAddBuffer(Collection<ByteBuffer> buffers)
    {
        freeList.addAll(buffers);
    }

    public void initAddBuffer(ByteBuffer buffer)
    {
        freeList.offer(buffer);
    }

    protected void innerAdd(ByteBuffer buffer) {
        freeList.offer(buffer);
    }

    @Override
    public int totalBufferCount()
    {
        return freeList.size() + usedBufferCount.get();
    }

    @Override
    public int freeBufferCount()
    {
        return freeList.size();
    }

    @Override
    public int usedbufferCount()
    {
        return usedBufferCount.get();
    }

    @Override
    public BufferUsing acquire()
    {
        ByteBuffer buffer = freeList.poll();
        if(buffer==null)
            return null;
        else
        {
            return using(buffer, this);
        }
    }

    @Override
    public BufferUsing acquire(long timeout, TimeUnit unit)
            throws InterruptedException
    {
        ByteBuffer buffer = freeList.poll(timeout, unit);
        if(buffer==null)
            return null;
        else
            return using(buffer, this);
    }

    @Override
    public void free(ByteBuffer buffer) {
        clear(buffer);
        innerAdd(buffer);
        //usedSet.remove(buffer);
        usedBufferCount.decrementAndGet();
    }

    private BufferUsing using(ByteBuffer buffer, FixedSizeBufferPool bufferPool) {
        //usedSet.add(buffer);
        usedBufferCount.incrementAndGet();

        return new BufferUsing(buffer, this);
    }

    private void clear(ByteBuffer buffer) {
        buffer.clear();
    }
}
