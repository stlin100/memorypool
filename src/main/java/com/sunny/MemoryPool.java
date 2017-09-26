package com.sunny;

import com.google.common.base.Preconditions;
import com.sunny.memorypool.DirectMemoryPool;
import com.sunny.memorypool.HeapMemoryPool;
import com.sunny.memorypool.NMapMemoryPool;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by lzx on 17/6/12.
 */
public abstract class MemoryPool {

    public abstract Output malloc();

    public abstract void close();

    public static Builder builder()
    {
        return new Builder();
    }

    enum Level
    {
        HEAP,
        DIRECTMEMORY,
        NMAP,
        FILE
    }

    public static class Builder
    {
        private String filename;
        private File file;
        private Level level = Level.DIRECTMEMORY;
        private int bufferSize = 0;
        private int bufferCount = 0;
        private int timeout;
        private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        private boolean useUnlimited;
        //private boolean closeOnJvmShutdown;


        public Builder filename(String filename)
        {
            this.filename = filename;
            this.file = new File(filename);
            file.deleteOnExit();
            return this;
        }

        public Builder file(File file)
        {
            this.file = file;
            file.deleteOnExit();
            return this;
        }

        public Builder tempfile()
        {
            try {
                this.file = File.createTempFile("memorypool", ".data");
                file.deleteOnExit();
            } catch (IOException e) {
                throw new MemoryPoolException(e);
            }
            return this;
        }

        public Builder level(Level level)
        {
            this.level = level;
            return this;
        }

        public Builder nmap()
        {
            this.level = Level.NMAP;
            return this;
        }

        public Builder heap()
        {
            this.level = Level.HEAP;
            return this;
        }

        public Builder directMemory()
        {
            this.level = Level.DIRECTMEMORY;
            return this;
        }

        public Builder bufferSize(int bufferSize)
        {
            this.bufferSize = bufferSize;
            return this;
        }

        public Builder bufferCount(int bufferCount)
        {
            this.bufferCount = bufferCount;
            return this;
        }

        public Builder timeout(int timeout)
        {
            this.timeout = timeout;
            return this;
        }

        public Builder timeout(int timeout, TimeUnit timeUnit)
        {
            this.timeout = timeout;
            this.timeUnit = timeUnit;
            return this;
        }

        public Builder useUnlimited()
        {
            this.useUnlimited = true;
            return this;
        }

        public MemoryPool build()
        {

            switch(level)
            {
                case NMAP:
                    return buildNmap();
                case DIRECTMEMORY:
                    return buildDirectMemory();
                case HEAP:
                    return buildHeap();
                case FILE:
                    return buildFile();
            }

            throw new MemoryPoolException("Unsupported level: " + level);
        }

        private MemoryPool buildFile() {
            throw new MemoryPoolException("Not implemented.");
        }

        private MemoryPool buildDirectMemory() {

            Preconditions.checkArgument(bufferSize!=0, "bufferSize can't be null.");
            Preconditions.checkArgument(bufferCount!=0, "bufferCount can't be null.");

            PoolConfig config = new PoolConfig();
            config.timeout(timeout, timeUnit);
            config.bufferSize(bufferSize);
            config.bufferCount(bufferCount);
            config.useUnlimited(true);

            return new DirectMemoryPool(config);
        }

        private MemoryPool buildHeap() {

            Preconditions.checkArgument(bufferSize!=0, "bufferSize can't be null.");
            Preconditions.checkArgument(bufferCount!=0, "bufferCount can't be null.");

            PoolConfig config = new PoolConfig();
            config.timeout(timeout, timeUnit);
            config.bufferSize(bufferSize);
            config.bufferCount(bufferCount);
            config.useUnlimited(true);

            return new HeapMemoryPool(config);
        }

        private MemoryPool buildNmap() {
            Preconditions.checkNotNull(file, "file can't be null.");
            Preconditions.checkArgument(bufferSize!=0, "bufferSize can't be null.");
            Preconditions.checkArgument(bufferCount!=0, "bufferCount can't be null.");

            PoolConfig config = new PoolConfig();
            config.timeout(timeout, timeUnit);
            config.bufferSize(bufferSize);
            config.bufferCount(bufferCount);
            config.useUnlimited(true);

            return new NMapMemoryPool(config, file);
        }
    }
}
