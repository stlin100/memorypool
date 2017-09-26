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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by lzx on 17/6/13.
 */
public class FileUnlimited implements UnlimitedProvider {

    public static FileUnlimited instance = new FileUnlimited();

    @Override
    public UnlimitedOutputStream createOutputStream() {
        try {
            File file = File.createTempFile("MemoryPool-Unlimited-", ".temp");
            System.out.println("Using unlimited temp file: " + file.getAbsolutePath());
            return new FileUnlimitedOutputStream(file);
        } catch (IOException e) {
            throw new MemoryPoolException("Can't create temp file for Unlimited.");
        }
    }

}
