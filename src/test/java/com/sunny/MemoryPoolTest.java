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

package com.sunny;


import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by lzx on 17/6/13.
 */
public class MemoryPoolTest {



    MemoryPool memoryPool;
    public MemoryPoolTest(MemoryPool memoryPool)
    {
        this.memoryPool = memoryPool;
    }

    public void test() throws Exception {

        stat("testWriteReadByte(10000)", () -> {
            try {
                testWriteReadByte(10000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        );

        stat("testWriteReadInt(10000)", () -> {
            try {
                testWriteReadInt(10000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stat("testWriteReadUTF(1000)", () -> {
            try {
                testWriteReadUTF(10000);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void stat(String name, Runnable func) throws Exception {
        long begin = System.currentTimeMillis();
        try {
            func.run();
        }finally {
            long end = System.currentTimeMillis();
            System.out.println(name + " cost " + (end-begin) + " ms.");
        }
    }

    public void testWriteReadInt(int n) throws IOException {
        Output output = memoryPool.malloc();

        for(int i=0;i<n;i++)
        {
            output.writeInt(i);
        }

        Input input = output.toInput();
        output.close();

        for(int i=0;i<n;i++)
        {
            int v = input.readInt();
            Preconditions.checkState(i == v);
        }

        input.close();

    }

    public void testWriteReadByte(int n) throws IOException {
        Output output = memoryPool.malloc();

        for(int i=0;i<n;i++)
        {
            output.writeByte(i % 128);
        }

        Input input = output.toInput();
        output.close();

        for(int i=0;i<n;i++)
        {
            int v = input.readByte();
            Preconditions.checkState(i % 128 == v);
        }

        input.close();

    }

    public void testWriteReadUTF(int n) throws IOException {
        Output output = memoryPool.malloc();
        String value = makeString(500);
        for(int i=0;i<n;i++)
        {

            output.writeUTF(value);
        }

        Input input = output.toInput();
        output.close();

        for(int i=0;i<n;i++)
        {
            //String value = makeString(i+1);
            String v = input.readUTF();
            Preconditions.checkState(v.equals(value));
        }

        input.close();

    }

    private String makeString(int n)
    {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<n;i++)
            sb.append("我");

        return sb.toString();
    }

    public void testMultiThread(int n, int threads)
    {

        ExecutorService pool = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threads);
        for(int i=0;i<threads;i++) {
            pool.submit(new MyThread(n, latch));//.start();
            //new MyThread(n, latch).start();
            /*
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.exit(0);

    }

    private List<Object> createValues(int n) {

        Random random = new Random(Long.MAX_VALUE);
        ArrayList<Object> list = new ArrayList<>();
        for(int i=0;i<n;i++)
        {
            int v = (int)random.nextLong()%100;
            Object value = null;
            switch(v % 3)
            {
                case 0:
                    value = (byte)v;
                    break;
                case 1:
                    value = (int)v;
                    break;
                case 2:
                    value = makeString(v);
                    break;
                default:

            }

            list.add(value);
        }

        return list;
    }

    private class MyThread extends Thread{

        //final List<Object> values;
        private int n;
        private CountDownLatch latch;

        public MyThread(int n, CountDownLatch latch) {
            this.latch = latch;

            //values = createValues(n);
            Output output = memoryPool.malloc();
            this.n = n;
        }

        public void run()
        {
            try {
                Random random = new Random(100000);
                int i = Math.abs(random.nextInt()%5) + 1;
                Thread.sleep(i*1000);
                stat("testWriteReadUTF()", () -> {
                    try {
                        testWriteReadUTF(n);
                        //testWriteReadInt(n);
                        latch.countDown();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
            }catch(Throwable e)
            {
                e.printStackTrace();
            }
        }
    }
}
