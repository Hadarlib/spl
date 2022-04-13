package bgu.spl.net.api.Commands;

public class Error extends CommandImpl{
    Short MessageOpcode;

    public Error(Short opcode, short messageOpcode) {
        super(opcode);
        MessageOpcode = messageOpcode;
    }



    public String execute() {
        return null;
    }
}
