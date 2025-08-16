package tga.Str;

import tga.Mechanic.ManMachineManager;

public interface IMMMTarget {
    void MachineUpdate(ManMachineManager mng);

    void QueQueNext(ManMachineManager mng);
}