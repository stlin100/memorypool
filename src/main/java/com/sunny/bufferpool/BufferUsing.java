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

import java.nio.ByteBuffer;

/**
 * Created by lzx on 17/6/12.
 */
public class BufferUsing {
    private BufferPool pool;
    private ByteBuffer buffer;

    public BufferUsing(ByteBuffer buffer, BufferPool pool) {
        this.buffer = buffer;
        this.pool = pool;
    }

    public BufferPool getPool() {
        return pool;
    }


    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void free()
    {
        pool.free(buffer);
    }
}