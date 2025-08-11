package tga.Str;

import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class Dir32 {
    private int PropValue;

    public Dir32(boolean up, boolean down, boolean north, boolean south, boolean east, boolean west) {
        PropValue = up ? 1 : 0;
        if (down) PropValue += 2;
        if (north) PropValue += 4;
        if (south) PropValue += 8;
        if (east) PropValue += 16;
        if (west) PropValue += 32;
    }

    public List<Direction> GetAllHave() {
        List<Direction> rt = new ArrayList<>();
        if ((PropValue & 1) != 0) rt.add(Direction.UP);
        if ((PropValue & 2) != 0) rt.add(Direction.DOWN);
        if ((PropValue & 4) != 0) rt.add(Direction.NORTH);
        if ((PropValue & 8) != 0) rt.add(Direction.SOUTH);
        if ((PropValue & 16) != 0) rt.add(Direction.EAST);
        if ((PropValue & 32) != 0) rt.add(Direction.WEST);
        return rt;
    }

    public boolean IsHave(Direction dir) {
        return switch (dir) {
            case Direction.UP -> (PropValue & 1) != 0;
            case Direction.DOWN -> (PropValue & 2) != 0;
            case Direction.NORTH -> (PropValue & 4) != 0;
            case Direction.SOUTH -> (PropValue & 8) != 0;
            case Direction.EAST -> (PropValue & 16) != 0;
            case Direction.WEST -> (PropValue & 32) != 0;
        };
    }

    public void SetHave(Direction dir) {
        switch (dir) {
            case Direction.UP -> PropValue |= 1;
            case Direction.DOWN -> PropValue |= 2;
            case Direction.NORTH -> PropValue |= 4;
            case Direction.SOUTH -> PropValue |= 8;
            case Direction.EAST -> PropValue |= 16;
            case Direction.WEST -> PropValue |= 32;
        }
    }

    public void SetNot(Direction dir) {
        switch (dir) {
            case Direction.UP -> PropValue &= ~1;
            case Direction.DOWN -> PropValue &= ~2;
            case Direction.NORTH -> PropValue &= ~4;
            case Direction.SOUTH -> PropValue &= ~8;
            case Direction.EAST -> PropValue &= ~16;
            case Direction.WEST -> PropValue &= ~32;
        }
    }
}