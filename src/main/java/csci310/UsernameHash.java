package csci310;

public class UsernameHash {
    public String hashUsername(String input){
        String hashedString = "";
        for(char c : input.toCharArray()){
            if(c == 'z'){
                hashedString += 'a';
            }else if(c == 'Z'){
                hashedString += 'A';
            }else if(c =='0'){
                hashedString += '1';
            }else{
                hashedString += (char)(c+1);
            }
        }
        return hashedString;
    }
    public String unHashUsername(String input){
        String unHashedString = "";
        for(char c : input.toCharArray()){
            if(c == 'a'){
                unHashedString += 'z';
            }else if(c == 'A'){
                unHashedString += 'Z';
            }else if(c == '1'){
                unHashedString += '0';
            }else{
                unHashedString += (char)(c-1);
            }
        }
        return unHashedString;
    }

}
