package tga.TicksMng;

import tga.TotalGreedyAgent;

public class FMTargetBasic implements IFMTarget {
    public interface ITarget{
        void PipeUpdate(PipeManager mng);
    }

    public ITarget TARGET;
    public int Updated = -1;
    public int Queuered = -2;

    public FMTargetBasic(ITarget target){
        TARGET = target;
    }

    @Override
    public void PipeUpdate(PipeManager mng) {
        if (Updated == TotalGreedyAgent.TGA_SERVER_UPDATE_GLOBAL_TICK) return;
        Updated = TotalGreedyAgent.TGA_SERVER_UPDATE_GLOBAL_TICK;
        TARGET.PipeUpdate(mng);
    }

    @Override
    public void QueQueNext(PipeManager mng) {
        if (Queuered == Updated) return;
        Queuered = Updated;
        mng.NeedUpdate.add(this);
    }
}