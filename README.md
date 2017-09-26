# memorypool
A java memory pool to support dynamic OutputStream/DataOutput, with heap, direct memory, unsafe memory, Nmap memory

Examples:

MemoryPool pool = MemoryPool.builder().nmap().build();

Output output = pool.malloc();

--Output inherit OutputStream and DataOutput

output.write(bytes);

Input input = output.toInput();

--Input inherit InputStream and DataInput

input.read(bytes);

output.close();

input.close();

