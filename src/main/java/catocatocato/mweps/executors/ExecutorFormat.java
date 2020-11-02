package catocatocato.mweps.executors;

import java.util.HashMap;

public abstract class ExecutorFormat {

    //parse data
    public abstract void parseData();

    //execute weapon
    public abstract void executeMwep(HashMap<String,Object> data);

}
