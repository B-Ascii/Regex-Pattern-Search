//Bedir Asici
//Casper Tyson

public class parseBrackets{

    //method returns s if no '[' are detected, or returns modified version of s
    //will err in case of no closing ']' bracket
    //can do several [] in a row
    String ReturnFormatted(String s){
        char[] regex = s.toCharArray();
        String changedRegex = "";
        for(int i = 0; i < regex.length; i++){
            // if '[' detected, modify 
            if(regex[i] == '['){
                int start = i;
                String middlePart = "(";
                i++;

               //what replacement [] looks like
                while(i < regex.length){
                    if(regex[i] == ']' && !middlePart.equals("(")){
                        middlePart = middlePart.substring(0, middlePart.length() - 1);
                        middlePart = middlePart + ')';
                        i++;
                        break;
                    }
                    if(i == regex.length - 1){
                        System.err.println("Error: no closing ] ");
                    }
                    middlePart = middlePart + '\\' + regex[i] + '|';
                    i++;
                }
                String newRegex = "";

                //start of regex (if regex not changed) important if more than one set of '[]'
                if(changedRegex == ""){
                    for(int x = 0; x < start; x++){
                        newRegex += regex[x];
                    }
                }
                //add middle part (changed part)
                newRegex += middlePart;
                for(int y = i; y < regex.length; y++){ // add end (unless another [] is detected)
                    if(regex[y] == '['){
                        break;
                    }
                    newRegex += regex[y];
                }
                changedRegex += newRegex;
                i--;
            }
        }
        if(changedRegex == ""){
            return s;
        }
        return changedRegex;
    }
}
