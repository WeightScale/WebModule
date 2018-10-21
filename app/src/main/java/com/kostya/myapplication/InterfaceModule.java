package com.kostya.myapplication;

/**
 * Интерфей для комманд.
 * @author Kostya. */
public interface InterfaceModule {

    //String command(Commands commands);
    //void write(String command);
    ObjectCommand sendCommand(String commands);
}
