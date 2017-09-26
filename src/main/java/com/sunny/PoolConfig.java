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
public class PoolConfig {

    private int timeout;
    private TimeUnit timeUnit;
    private int bufferSize;
    private int bufferCount;

    private int maxOutputSize = Integer.MAX_VALUE;

    private boolean useUnlimited;
    private String unlimitedProvider;

    public PoolConfig()
    {

    }

    public PoolConfig timeout(int timeout, TimeUnit timeUnit)
    {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public PoolConfig bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    public PoolConfig bufferCount(int bufferCount) {
        this.bufferCount = bufferCount;
        return this;
    }

    public PoolConfig maxOutputSize(int maxOutputSize) {
        this.maxOutputSize = maxOutputSize;
        return this;
    }

    public PoolConfig useUnlimited(boolean useUnlimited)
    {
        this.useUnlimited = true;
        return this;
    }

    public PoolConfig unlimitedProvider(String unlimitedProvider)
    {
        this.unlimitedProvider = unlimitedProvider;
        return this;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public int getBufferCount() {
        return bufferCount;
    }

    public int getMaxOutputSize() {
        return maxOutputSize;
    }

    public boolean isUseUnlimited() {
        return useUnlimited;
    }

    public String getUnlimitedProvider() {
        return unlimitedProvider;
    }
}
