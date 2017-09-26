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

import com.sunny.Input;

import java.io.EOFException;
import java.io.IOException;

/**
 * Created by lzx on 17/6/14.
 */
public class EmptyInput extends Input {
    
    @Override
    public int read() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public void close() {
        
    }

    @Override
    public void rewind() {

    }

    @Override
    public void readFully(byte[] b) throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public int skipBytes(int n) throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public boolean readBoolean() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public byte readByte() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public int readUnsignedByte() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public short readShort() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public int readUnsignedShort() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public char readChar() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public int readInt() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public long readLong() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public float readFloat() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public double readDouble() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public String readLine() throws IOException {
        throw new EOFException("Input is empty.");
    }

    @Override
    public String readUTF() throws IOException {
        throw new EOFException("Input is empty.");
    }
}
