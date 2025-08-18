package tga.TicksMng;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.util.math.Direction;
import tga.Mechanic.IPipeType;
import tga.Str.PipeProperty;

import java.util.ArrayDeque;
import java.util.Queue;

public class PipeManager {
    public static final float ACTIVE_GAP = 0.0003f;
    public static PipeManager INTANCE;
    public Queue<IFMTarget> NeedUpdate = new ArrayDeque<>();
    private Queue<IFMTarget> SwapCache = new ArrayDeque<>();

    public int TurnUpdated;

    public void ServerTick() {
        TurnUpdated = 0;
        //Reset Queue
        Queue<IFMTarget> tmp = NeedUpdate;
        NeedUpdate = SwapCache;
        SwapCache = tmp;
        while (!tmp.isEmpty()) {
            TurnUpdated++;
            IFMTarget target = tmp.poll();
            target.PipeUpdate(this);
        }
    }

    public void ClearQueue() {
        NeedUpdate.clear();
        SwapCache.clear();
    }

    /**
     * @return calc transfer amont not do actual transfer, do not hanlde source null or  check variants
     */
    public static long TransferCalcHelper(PipeProperty source, PipeProperty target, Direction flow, long amountSource, long amountTarget) {
        //Target is full
        float pressureTarget = target.GetPressure(amountTarget);
        if (flow == Direction.DOWN) {
            if (pressureTarget < 1f + ACTIVE_GAP) {
                //push
                long maxSpeed = Math.min(target.MaxSpeed, source.MaxSpeed);
                long free = (long)(target.PressureLine * (1 + ACTIVE_GAP * 0.5f)) - amountTarget;
                long move = Math.min(Math.min(amountSource, free), maxSpeed);
                return Math.max(0, move);
            } else {
                float pressureSource = source.GetPressure(amountSource);
                float gapPressure = pressureSource + ACTIVE_GAP - pressureTarget;
                if (gapPressure < ACTIVE_GAP) return 0;
                long maxSpeed = Math.min(target.MaxSpeed, source.MaxSpeed);
                long free = target.PipeCap - amountTarget;
                long maxAmount = Math.min(maxSpeed, Math.min(free, amountSource));
                float movePre = Math.min(target.PressureLine, source.PressureLine) * gapPressure * 0.4f;
                long move = Math.min(maxAmount, (long) movePre);
                return Math.max(0, move);
            }
        } else {
            float pressureSource = source.GetPressure(amountSource);
            if (flow == Direction.UP && pressureSource < 1f) return 0;
            float cprPressure = pressureTarget + ACTIVE_GAP;
            float gapPressure = pressureSource - cprPressure;
            if (gapPressure < ACTIVE_GAP) return 0;
            long maxSpeed = Math.min(target.MaxSpeed, source.MaxSpeed);
            long free = target.PipeCap - amountTarget;
            long maxAmount = Math.min(maxSpeed, Math.min(free, amountSource));
            float movePre = Math.min(target.PressureLine, source.PressureLine) * gapPressure * 0.4f;
            long move = Math.min(maxAmount, (long) movePre);
            return Math.max(0, move);
        }
    }
}