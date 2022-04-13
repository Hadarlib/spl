package bgu.spl.net.api.Commands;

import bgu.spl.net.api.bidi.DATA;


public abstract class CommandImpl {
    private short opcode;
    protected DATA data = DATA.getInstance();


    public CommandImpl(short opcode) {
        this.opcode = opcode;
    }
    public abstract String execute();
}
