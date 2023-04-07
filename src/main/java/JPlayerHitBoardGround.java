import io.github.personbelowrocks.minecraft.dndbv.boards.Board;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class JPlayerHitBoardGround extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Board board;
    private final Player player;
    private final Block block;

    private Boolean cancelled;

    public @NotNull Board getBoard() {
        return board;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Block getBlock() {
        return block;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    public JPlayerHitBoardGround(Block block, Board board, Player player) {
        this.block = block;
        this.board = board;
        this.player = player;

        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
