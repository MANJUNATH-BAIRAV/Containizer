package com.containizerapp.model;

public class ContainerRunOptions {
    public String cpu;      // e.g. "0.5", "1"
    public String memory;   // e.g. "512m", "1g"
    public String volume;   // e.g. "/host:/container"

    public boolean hasCpu()    { return cpu != null && !cpu.isBlank(); }
    public boolean hasMemory() { return memory != null && !memory.isBlank(); }
    public boolean hasVolume() { return volume != null && !volume.isBlank(); }
}
