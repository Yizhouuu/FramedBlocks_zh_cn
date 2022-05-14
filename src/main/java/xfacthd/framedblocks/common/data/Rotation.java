package xfacthd.framedblocks.common.data;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.Utils;

import java.util.Locale;
import java.util.function.Function;

public enum Rotation implements StringRepresentable
{
    UP(dir -> Direction.UP),
    DOWN(dir -> Direction.DOWN),
    RIGHT(Direction::getClockWise),
    LEFT(Direction::getCounterClockWise);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Function<Direction, Direction> facingMod;

    Rotation(Function<Direction, Direction> facingMod) { this.facingMod = facingMod; }

    public Direction withFacing(Direction dir) { return facingMod.apply(dir); }

    public Rotation getOpposite()
    {
        return switch (this)
        {
            case UP -> DOWN;
            case DOWN -> UP;
            case RIGHT -> LEFT;
            case LEFT -> RIGHT;
        };
    }

    public boolean isVertical() { return this == UP || this == DOWN; }

    @Override
    public String getSerializedName() { return name; }



    /**
     * @param facing The view direction from which the rotation is determined, must not be on the Y axis
     * @param dir The direction to rotate towards, must be perpendicular to facing
     */
    public static Rotation fromDirection(Direction facing, Direction dir)
    {
        Preconditions.checkArgument(!Utils.isY(facing), "View direction must not be on the Y axis");
        Preconditions.checkArgument(facing.getAxis() != dir.getAxis(), "Directions must be perpendicular");

        if (dir == Direction.UP) { return UP; }
        if (dir == Direction.DOWN) { return DOWN; }
        if (dir == facing.getClockWise()) { return RIGHT; }
        if (dir == facing.getCounterClockWise()) { return LEFT; }
        throw new IllegalStateException(String.format("How did we get here?! %s|%s", facing, dir));
    }

    public static Rotation fromWallCross(Vec3 hitVec, Direction hitFace)
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
            return y < 0 ? Rotation.UP : Rotation.DOWN;
        }
    }
}
