package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;
import bgu.spl.net.api.bidi.BGSProtocol;
import bgu.spl.net.srv.Reactor;

public class ReactorMain {
    public static void main(String[] args) {
        try (Reactor<String> server = new Reactor<>(Integer.parseInt(args[0]) , Integer.parseInt(args[1]), () -> new BGSProtocol(), () -> new BGSEncoderDecoder());) {
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
