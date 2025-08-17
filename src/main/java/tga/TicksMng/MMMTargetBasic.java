package tga.TicksMng;

import tga.TotalGreedyAgent;

public class MMMTargetBasic implements IMMMTarget {
    public interface ITarget{
        void MachineUpdate(ManMachineManager mng);
    }

    public ITarget TARGET;
    public int Updated = -1;
    public int Queuered = -2;

    public MMMTargetBasic(ITarget target){
        TARGET = target;
    }

    @Override
    public void MachineUpdate(ManMachineManager mng) {
        if (Updated == TotalGreedyAgent.TGA_SERVER_UPDATE_GLOBAL_TICK) return;
        Updated = TotalGreedyAgent.TGA_SERVER_UPDATE_GLOBAL_TICK;
        TARGET.MachineUpdate(mng);
    }

    @Override
    public void QueQueNext(ManMachineManager mng) {
        if (Queuered == Updated) return;
        Queuered = Updated;
        mng.NeedUpdate.add(this);
    }
}
