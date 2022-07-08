// Bedir Asici
// Casper Tyson

public class REcompile {
    public static void main(String[] args){
        // pre-prossesing
        parseBrackets pb = new parseBrackets();
        // pass through arg[0] and start process
        FSMV1 fsm = new FSMV1(pb.ReturnFormatted(args[0]));
        fsm.parse();
    }
}
