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

/**
 * Created by lzx on 17/6/13.
 */
public class ExhaustedException extends MemoryPoolException{
    public ExhaustedException() {
        super();
    }

    public ExhaustedException(String message) {
        super(message);
    }

    public ExhaustedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExhaustedException(Throwable cause) {
        super(cause);
    }

    protected ExhaustedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
