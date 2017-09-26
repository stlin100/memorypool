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

import com.sunny.MemoryPoolException;

import java.io.*;

/**
 * Created by lzx on 17/6/13.
 */
public class FileUnlimitedOutputStream extends UnlimitedOutputStream {
    private final File file;

    public FileUnlimitedOutputStream(File file) {
        super(createFileOutputStream(file));
        this.file = file;
        init();
    }

    private static OutputStream createFileOutputStream(File file) {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new MemoryPoolException(e);
        }
    }

    private void init()
    {
        file.deleteOnExit();
    }

    @Override
    public long dataSize() {
        try {
            out.flush();

            return file.length();

        }catch(IOException e)
        {
            throw new MemoryPoolException(e);
        }

    }

    @Override
    public UnlimitedInputStream toInputStream() {
        close();

        return new FileUnlimitedInputStream(file);
    }

    @Override
    public void close() {
        try {
            out.flush();
            out.close();
        }catch(IOException e)
        {

        }
    }
}
