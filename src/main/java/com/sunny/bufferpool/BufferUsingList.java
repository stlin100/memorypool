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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by lzx on 17/6/12.
 */
public class BufferUsingList {

    private final List<BufferUsing> usingList;

    public BufferUsingList(BufferUsing... usingList)
    {
        this.usingList = requireNonNull(Lists.newArrayList(usingList));
    }

    public BufferUsingList(List<BufferUsing> usingList)
    {
        this.usingList = requireNonNull(Lists.newArrayList(usingList));
    }

    public void append(BufferUsing buffer)
    {
        usingList.add(buffer);
    }

    public void free()
    {
        usingList.forEach(BufferUsing::free);
    }

    public List<BufferUsing> toList()
    {
        return ImmutableList.copyOf(usingList);
    }


    public boolean isEmpty() {
        return usingList.isEmpty();
    }
}
