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

import java.util.concurrent.TimeUnit;

/**
 * Created by lzx on 17/6/13.
 */
public class Test1 {

    public static void main(String[] args) throws Exception
    {
        MemoryPool heapPool = createNMapPool();
        MemoryPoolTest tester = new MemoryPoolTest(heapPool);
        tester.testMultiThread(1000, 2);

        /*
        heapPool = createDirectPool();
        tester = new MemoryPoolTest(heapPool);
        tester.test();

        heapPool = createNMapPool();
        tester = new MemoryPoolTest(heapPool);
        tester.test();
        */
    }

    private static MemoryPool createHeapPool() {
        return MemoryPool.builder().bufferCount(20 * 1024).bufferSize(1024 * 8).heap().build();
    }

    private static MemoryPool createDirectPool() {
        return MemoryPool.builder().bufferCount(64 * 1024).bufferSize(1024 * 8).directMemory().build();
    }

    private static MemoryPool createNMapPool() {
        return MemoryPool.builder().bufferCount(32 * 1024).bufferSize(1024 * 32).timeout(5, TimeUnit.MILLISECONDS).filename("abc.temp").nmap().build();
    }
}
