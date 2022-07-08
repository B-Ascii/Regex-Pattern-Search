// Bedir Asici
// Casper Tyson


public class FSMV1{

    char[] ch = new char[100];
    int[] next1 = new int[100];
    int[] next2 = new int[100];

    String p;
    int point = 0;
    int state = 1;

    //collect string from main program
    public FSMV1(String x){
        p = x;
    }
    // parse string, and print output
    void parse(){
        expression();
        if(point != (p.length())){
            System.err.println("error: not valid regex");
        }else{
            set_state(state, 'µ', 0, 0);
            for(int i = 0; i < ch.length; i++){
                System.out.println(i + " " + ch[i] + " " + next1[i] + " " + next2[i]);
                if(i != 0 && next1[i] == 0){
                    break;
                }
            }
        }
    }
    int expression(){
        int r, t1, t2, f;
        f = state - 1;

        //save first factor pos in r and t1
        t1 = r = term();
        // checks still more char
        if((p.length() - 1) >= point){
            if((p.length() - 1) >= point){
                // If "or" attribute
                if(p.charAt(point) == '|'){
                    //points starting index to last | operator
                    if(next1[f] == next2[f]){
                        next2[f] = state;
                    }
                    next1[f] = state;
                    f = state - 1; 
                    point++; r = state; state++; // increase state before calling expression
                    if((p.length() - 1) >= point){
                        // after '|' char, find at least one expression
                        if(isVocab(p.charAt(point)) || p.charAt(point) == '.' || p.charAt(point) == '(' || p.charAt(point) == '\\'){
                            t2 = expression();
                            set_state(r, 'µ', t1, t2); // point to start of both T and E in (T | E)
                            if(next1[f] == next2[f]){
                                next2[f] = state;
                            }
                            next1[f] = state;
                            // to prevent two machines from pointing at curr branch
                            for(int i = 1; i <= r; i++){
                                if(next1[0] == next1[i]){
                                    next1[i] = state;
                                    if(next2[0] == next2[i]){
                                        next2[i] = state;
                                    }
                                }
                            }
                        }else{ System.err.println("Error: | must be followed by E"); } // if not valid expression 
                    }else{ System.err.println("Error: | must be followed by E"); } // if not enough chars
                }
            }
        }
        // return pos of first T
        return r;
    }
    int term(){
        int r, t1, t2, f;
        f = state - 1; 
        r = t1 = factor();
        if((p.length() - 1) >= point){
            if(p.charAt(point) == '*'){ // 0 or many times
                if(next1[f] == next2[f]){ // if not a branching state, change both to point to this branching state
                    next2[f] = state;
                }
                next1[f] = state;
                set_state(state, 'µ', state + 1, t1);
                point++; 
                r = state; 
                state++;
            }
            else if(p.charAt(point) == '+'){ // 1 or many times
                if(next1[f] == next2[f]){
                    next2[f] = t1;
                }
                next1[f] = t1;
                set_state(state, 'µ', state + 1, t1);
                point++; 
                //r = state; 
                state++;
            }
            else if(p.charAt(point) == '?'){ // 0 or 1 times
                if(next1[f] == next2[f]){
                    next2[f] = state;
                }
                next1[f] = state;
                set_state(state, 'µ', state + 1, t1);
                next1[state - 1] = state + 1;   //point term at same as branch (so can only be used once)
                next2[state - 1] = state + 1;
                point++; r = state; state++;
            }
        }
        if((p.length() - 1) >= point){ // conjunction, call term again if valid input
            if(isVocab(p.charAt(point))|| p.charAt(point) == '.' || p.charAt(point) == '(' || p.charAt(point) == '\\'){
                term();
            }
        }
        return r;
    }
    int factor(){
        int r = 0;

        if(isVocab(p.charAt(point))){ // if valid vocab, point to next state
            set_state(state, p.charAt(point), state + 1, state + 1);
            point++;
            r = state;
            state++;
        }
        else if(p.charAt(point) == '.'){ // same here, but set to π to signify wildcard
            set_state(state, 'π', state + 1, state + 1);
            point++; r = state; state++;
        }
        else{
            if(p.charAt(point) == '\\'){
                point++;
                if((p.length() - 1) >= point){ // if '\' immediatly set char as symbol in machine
                    set_state(state, p.charAt(point), state + 1, state + 1);
                    point++; r = state; state++;
                }else{System.err.println("error: '\\' must be followed by somethign"); }
            }
            else if(p.charAt(point) == '('){ 
                point++;
                if((p.length() - 1) >= point){
                    r = expression();           // if '(' call expression then check for ')'
                }
                if((p.length() - 1) >= point){
                    if(p.charAt(point) == ')'){
                        point++;
                    }else{ System.err.println("error: no closing bracket"); }
                }else{ System.err.println("error: no closing bracket"); }
            }else{ System.err.println("error: not vocab, (, or \\"); }
        }
        return r;
    }
    void set_state(int s, char c, int n1, int n2){
        ch[s] = c; next1[s] = n1; next2[s] = n2;
    }
    boolean isVocab(char c){ 
        char[] listSymbols = {'+', '*', '?', '|', '\\', '.', '(', ')', 'π', 'µ'};
        for(char s: listSymbols){
            if(s == c){
                return false;
            }
        }
        return true; // if not a registered symbol, then is valid vocab
    }
}