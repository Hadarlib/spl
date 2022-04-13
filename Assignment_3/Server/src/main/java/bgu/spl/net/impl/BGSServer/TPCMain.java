package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.api.bidi.BGSEncoderDecoder;
import bgu.spl.net.api.bidi.BGSProtocol;
import bgu.spl.net.srv.TPCServer;


public class TPCMain {
    public static void main(String[] args) {
        try (TPCServer<String> server = new TPCServer(Integer.parseInt(args[0]), () -> new BGSProtocol(), () -> new BGSEncoderDecoder());) {
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
