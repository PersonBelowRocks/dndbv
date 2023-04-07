import io.github.personbelowrocks.minecraft.dndbv.boards.Board;
import io.github.personbelowrocks.minecraft.dndbv.boards.Figure;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class JPlayerRightClickBoardFigure extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Board board;
    private final Figure figure;
    private final Player player;

    public @NotNull Board getBoard() {
        return board;
    }

    public @NotNull Figure getFigure() {
        return figure;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    public JPlayerRightClickBoardFigure(Figure figure, Board board, Player player) {
        this.figure = figure;
        this.board = board;
        this.player = player;
    }
}
