package thesis.antlersolver.strategy.splitting;

import thesis.antlersolver.model.Graph;

public interface Splitter {
    public Graph next();
    public int graphNum();
}
