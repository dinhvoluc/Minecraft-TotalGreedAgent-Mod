package tga.Str;

import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class Dir64 {
    public int PropValue;
    
    public static final int DIR_INT_NONE = 0;
    public static final int DIR_INT_UP = 1;
    public static final int DIR_INT_DOWN = 2;
    public static final int DIR_INT_NORTH = 4;
    public static final int DIR_INT_SOUTH = 8;
    public static final int DIR_INT_EAST = 16;
    public static final int DIR_INT_WEST = 32;

    public Dir64(boolean up, boolean down, boolean north, boolean south, boolean east, boolean west) {
        PropValue = up ? DIR_INT_UP : DIR_INT_NONE;
        if (down) PropValue += DIR_INT_DOWN;
        if (north) PropValue += DIR_INT_NORTH;
        if (south) PropValue += DIR_INT_SOUTH;
        if (east) PropValue += DIR_INT_EAST;
        if (west) PropValue += DIR_INT_WEST;
    }

    public Dir64(int dirs) {
        PropValue = dirs;
    }

    public static Dir64 AllDirection() {
        return new Dir64(63);
    }

    public List<Direction> GetAllHave() {
        List<Direction> rt = new ArrayList<>();
        if ((PropValue & DIR_INT_UP) != DIR_INT_NONE) rt.add(Direction.UP);
        if ((PropValue & DIR_INT_DOWN) != DIR_INT_NONE) rt.add(Direction.DOWN);
        if ((PropValue & DIR_INT_NORTH) != DIR_INT_NONE) rt.add(Direction.NORTH);
        if ((PropValue & DIR_INT_SOUTH) != DIR_INT_NONE) rt.add(Direction.SOUTH);
        if ((PropValue & DIR_INT_EAST) != DIR_INT_NONE) rt.add(Direction.EAST);
        if ((PropValue & DIR_INT_WEST) != DIR_INT_NONE) rt.add(Direction.WEST);
        return rt;
    }

    public boolean IsHave(Direction dir) {
        return switch (dir) {
            case Direction.UP -> (PropValue & DIR_INT_UP) != DIR_INT_NONE;
            case Direction.DOWN -> (PropValue & DIR_INT_DOWN) != DIR_INT_NONE;
            case Direction.NORTH -> (PropValue & DIR_INT_NORTH) != DIR_INT_NONE;
            case Direction.SOUTH -> (PropValue & DIR_INT_SOUTH) != DIR_INT_NONE;
            case Direction.EAST -> (PropValue & DIR_INT_EAST) != DIR_INT_NONE;
            case Direction.WEST -> (PropValue & DIR_INT_WEST) != DIR_INT_NONE;
        };
    }

    public void SetHave(Direction dir) {
        switch (dir) {
            case Direction.UP -> PropValue |= DIR_INT_UP;
            case Direction.DOWN -> PropValue |= DIR_INT_DOWN;
            case Direction.NORTH -> PropValue |= DIR_INT_NORTH;
            case Direction.SOUTH -> PropValue |= DIR_INT_SOUTH;
            case Direction.EAST -> PropValue |= DIR_INT_EAST;
            case Direction.WEST -> PropValue |= DIR_INT_WEST;
        }
    }

    public void SetNot(Direction dir) {
        switch (dir) {
            case Direction.UP -> PropValue &= ~DIR_INT_UP;
            case Direction.DOWN -> PropValue &= ~DIR_INT_DOWN;
            case Direction.NORTH -> PropValue &= ~DIR_INT_NORTH;
            case Direction.SOUTH -> PropValue &= ~DIR_INT_SOUTH;
            case Direction.EAST -> PropValue &= ~DIR_INT_EAST;
            case Direction.WEST -> PropValue &= ~DIR_INT_WEST;
        }
    }

    public boolean HaveUp() {
        return (PropValue & DIR_INT_UP) != DIR_INT_NONE;
    }

    public boolean HaveDown() {
        return (PropValue & DIR_INT_DOWN) != DIR_INT_NONE;
    }

    public boolean HaveNorth() {
        return (PropValue & DIR_INT_NORTH) != DIR_INT_NONE;
    }

    public boolean HaveSouth() {
        return (PropValue & DIR_INT_SOUTH) != DIR_INT_NONE;
    }

    public boolean HaveEast() {
        return (PropValue & DIR_INT_EAST) != DIR_INT_NONE;
    }

    public boolean HaveWest() {
        return (PropValue & DIR_INT_WEST) != DIR_INT_NONE;
    }
}