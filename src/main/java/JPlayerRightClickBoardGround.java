import io.github.personbelowrocks.minecraft.dndbv.boards.Board;
import io.github.personbelowrocks.minecraft.dndbv.util.IntVec3D;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class JPlayerRightClickBoardGround extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final IntVec3D clickedFace;
    private final Board board;
    private final Player player;

    public @NotNull IntVec3D getClickedFace() {
        return this.clickedFace;
    }

    public @NotNull Board getBoard() {
        return this.board;
    }

    public @NotNull Player getPlayer() {
        return this.player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    public JPlayerRightClickBoardGround(IntVec3D clickedFace, Board board, Player player) {
        this.clickedFace = clickedFace;
        this.board = board;
        this.player = player;
    }

    public IntVec3D clickedFaceBoardPos() {
        return this.clickedFace.minus(this.board.getMin());
    }
}
