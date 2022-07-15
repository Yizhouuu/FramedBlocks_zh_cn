package xfacthd.framedblocks.common.data.property;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Locale;
import java.util.function.Function;

public enum HorizontalRotation implements StringRepresentable
{
    UP(dir -> Direction.UP),
    DOWN(dir -> Direction.DOWN),
    RIGHT(Direction::getClockWise),
    LEFT(Direction::getCounterClockWise);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Function<Direction, Direction> facingMod;

    HorizontalRotation(Function<Direction, Direction> facingMod) { this.facingMod = facingMod; }

    public Direction withFacing(Direction dir) { return facingMod.apply(dir); }

    public HorizontalRotation getOpposite()
    {
        return switch (this)
        {
            case UP -> DOWN;
            case DOWN -> UP;
            case RIGHT -> LEFT;
            case LEFT -> RIGHT;
        };
    }

    public HorizontalRotation rotate(Rotation rot)
    {
        return switch (rot)
        {
            case NONE -> this;
            case CLOCKWISE_180 -> getOpposite();
            case CLOCKWISE_90 -> switch(this)
            {
                case UP -> RIGHT;
                case DOWN -> LEFT;
                case RIGHT -> DOWN;
                case LEFT -> UP;
            };
            case COUNTERCLOCKWISE_90 -> switch (this)
            {
                case UP -> LEFT;
                case DOWN -> RIGHT;
                case RIGHT -> UP;
                case LEFT -> DOWN;
            };
        };
    }

    public boolean isVertical() { return this == UP || this == DOWN; }

    @Override
    public String getSerializedName() { return name; }



    /**
     * @param facing The view direction from which the rotation is determined, must not be on the Y axis
     * @param dir The direction to rotate towards, must be perpendicular to facing
     */
    public static HorizontalRotation fromDirection(Direction facing, Direction dir)
    {
        Preconditions.checkArgument(!Utils.isY(facing), "View direction must not be on the Y axis");
        Preconditions.checkArgument(facing.getAxis() != dir.getAxis(), "Directions must be perpendicular");

        if (dir == Direction.UP) { return UP; }
        if (dir == Direction.DOWN) { return DOWN; }
        if (dir == facing.getClockWise()) { return RIGHT; }
        if (dir == facing.getCounterClockWise()) { return LEFT; }
        throw new IllegalStateException(String.format("How did we get here?! %s|%s", facing, dir));
    }

    public static HorizontalRotation fromWallCross(Vec3 hitVec, Direction hitFace)
    {
        hitVec = Utils.fraction(hitVec);

        double xz = (Utils.isX(hitFace) ? hitVec.z() : hitVec.x()) - .5;
        double y = hitVec.y() - .5;

        if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz))
        {
            if (Utils.isX(hitFace))
            {
                return (xz < 0) == Utils.isPositive(hitFace) ? LEFT : RIGHT;
            }
            else
            {
                return (xz < 0) == Utils.isPositive(hitFace) ? RIGHT : LEFT;
            }
        }
        else
        {
            return y < 0 ? HorizontalRotation.UP : HorizontalRotation.DOWN;
        }
    }
}