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
public class FileUnlimitedInputStream extends UnlimitedInputStream {
    private final File file;

    public FileUnlimitedInputStream(File file) {
        super(createInputStream(file));
        this.file = file;

        init();
    }

    private static InputStream createInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new MemoryPoolException(e);
        }
    }

    private void init()
    {
        file.deleteOnExit();
    }

    @Override
    public void close() throws IOException {
        try {
            in.close();
        }catch(IOException e){}
        file.delete();
    }

    @Override
    public void rewind() {
        try {
            in.close();
            in = createInputStream(file);
        }
        catch (IOException e) {
            throw new MemoryPoolException("not support rewind.", e);
        }
    }
}
