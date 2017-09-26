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

package com.sunny.unlimited;

import com.sunny.Input;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by lzx on 17/6/14.
 */
public class UnlimitedInput extends Input {


    private final DataInputStream din;
    private UnlimitedInputStream in;

    public UnlimitedInput(UnlimitedInputStream in)
    {
        this.in = in;
        this.din = new DataInputStream(in);
    }

    @Override
    public int read() throws IOException {
        return din.read();
    }

    @Override
    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rewind() {
        in.rewind();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        din.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        din.read(b,off,len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return din.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return din.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return din.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return din.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return din.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return din.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return din.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return din.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return din.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return din.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return din.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return din.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return din.readUTF();
    }
}
