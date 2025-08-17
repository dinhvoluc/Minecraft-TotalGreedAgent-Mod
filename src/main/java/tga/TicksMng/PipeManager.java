package tga.TicksMng;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.util.math.Direction;
import tga.Mechanic.IPipeType;
import tga.Str.PipeProperty;

import java.util.ArrayDeque;
import java.util.Queue;

public class PipeManager {
    public static final float ACTIVE_PRESSURE_GAP = 0.00009f;
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
    public static long TransferCalcHelper(PipeProperty source, PipeProperty target, Direction dirFromSource, long amountSource, long amountTarget) {
        //Target is full
        float pressureTarget = target.GetPressure(amountTarget);
        if (pressureTarget >= target.PressureCap) return 0;
        if (dirFromSource == Direction.DOWN) {
            //drop to lower height
            if (pressureTarget < 1f + ACTIVE_PRESSURE_GAP) {
                long maxMove = Math.min(amountSource, Math.min(source.MaxSpeed, target.MaxSpeed));
                long gapToFull = Math.max( (long)(target.PressureLine * ( 1f + ACTIVE_PRESSURE_GAP)) - amountTarget, 5);
                //can apcept full
                if (gapToFull >= maxMove) return maxMove;
                long leftAmount = maxMove - gapToFull;
                //update after
                long newSourceAmount = amountSource - gapToFull;
                long newTargetAmount = amountTarget + gapToFull;
                float pressureSource = source.GetPressure(newSourceAmount);
                float targetNewPressure = target.GetPressure(newTargetAmount);
                float presureGap = pressureSource - targetNewPressure;
                //not enoght pressure
                if (presureGap < ACTIVE_PRESSURE_GAP) return gapToFull;
                float blancedPressure = Math.min(target.PressureCap - targetNewPressure, presureGap * 0.8f);
                float moveAmount = Math.min(source.PressureLine, target.PressureLine) * blancedPressure;
                long maxSpeed = Math.min((int) moveAmount, Math.min(source.MaxSpeed, target.MaxSpeed));
                return Math.min(leftAmount, maxSpeed < 0 ? gapToFull : (maxSpeed + gapToFull));
            }
            else {
                float pressureSource = source.GetPressure(amountSource);
                float presureGap = pressureSource + ACTIVE_PRESSURE_GAP * 16 - pressureTarget;
                //not enoght pressure
                if (presureGap < ACTIVE_PRESSURE_GAP) return 0;
                float blancedPressure = Math.min(target.PressureCap - pressureTarget, presureGap * 0.8f);
                float moveAmount = Math.min(source.PressureLine, target.PressureLine) * blancedPressure;
                long maxSpeed = Math.min((int) moveAmount, Math.min(source.MaxSpeed, target.MaxSpeed));
                return maxSpeed <= 0 ? 5 : maxSpeed;
            }
        } else if (dirFromSource == Direction.UP) {
            //only push
            float pressureSource = source.GetPressure(amountSource);
            //Not enough pressure
            if (pressureSource <= 1f + ACTIVE_PRESSURE_GAP * 3f) return 0;
            //normal dir
            float presureGap = pressureSource - pressureTarget;
            //not enoght pressure
            if (presureGap < ACTIVE_PRESSURE_GAP * 3f) return 0;
            float blancedPressure = Math.min(target.PressureCap - pressureTarget, presureGap * 0.5f);
            float moveAmount = Math.min(source.PressureLine, target.PressureLine) * blancedPressure;
            long maxSpeed = Math.min((int) moveAmount, Math.min(source.MaxSpeed, target.MaxSpeed));
            return maxSpeed <= 0 ? 5 : maxSpeed;
        }
        else {
            //normal dir
            float pressureSource = source.GetPressure(amountSource);
            float presureGap = pressureSource - pressureTarget;
            //not enoght pressure
            if (presureGap < ACTIVE_PRESSURE_GAP) return 0;
            float blancedPressure = Math.min(target.PressureCap - pressureTarget, presureGap * 0.5f);
            float moveAmount = Math.min(source.PressureLine, target.PressureLine) * blancedPressure;
            long maxSpeed = Math.min((int) moveAmount, Math.min(source.MaxSpeed, target.MaxSpeed));
            return maxSpeed <= 0 ? 1 : maxSpeed;
        }
    }
}