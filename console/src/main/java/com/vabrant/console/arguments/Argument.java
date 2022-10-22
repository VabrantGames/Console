package com.vabrant.console.arguments;

public abstract class Argument<T> {

    private ArgumentLogic logic;

    public Argument(ArgumentLogic logic) {
       this.logic = logic;
    }
    
    public interface ArgumentLogic {
        boolean isType();
    }
}
