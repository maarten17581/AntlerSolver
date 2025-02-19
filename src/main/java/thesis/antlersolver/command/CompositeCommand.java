package thesis.antlersolver.command;

import java.util.ArrayList;
import java.util.List;

public class CompositeCommand implements Command {
    public boolean executed = false;
    public List<Command> commands;

    public CompositeCommand() {
        this.commands = new ArrayList<>();
    }

    public CompositeCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute() {
        for(int i = 0; i < commands.size(); i++) {
            commands.get(i).execute();
        }
        executed = true;
    }

    @Override
    public void undo() {
        for(int i = commands.size()-1; i >= 0; i--) {
            commands.get(i).undo();
        }
        executed = false;
    }
}
