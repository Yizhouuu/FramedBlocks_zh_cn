package xfacthd.framedblocks.common.data.property;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum NullableDirection implements StringRepresentable
{
    NONE(null),
    DOWN(Direction.DOWN),
    UP(Direction.UP),
    NORTH(Direction.NORTH),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST),
    EAST(Direction.EAST);

    private final Direction dir;

    NullableDirection(Direction dir) { this.dir = dir; }

    public Direction toDirection() { return dir; }

    @Override
    public String getSerializedName() { return toString().toLowerCase(Locale.ROOT); }



    public static NullableDirection fromDirection(Direction dir)
    {
        if (dir == null) { return NONE; }

        return switch (dir)
        {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }
}