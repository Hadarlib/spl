package bgu.spl.net.api.Commands;


public class ACK extends CommandImpl{
    private short MessageOpcode;
    private String Info;

    public ACK(Short opcode, short messageOpcode, String info) {
        super(opcode);
        MessageOpcode = messageOpcode;
        Info = info;
    }

    public String execute() {
        return null;
    }
}
