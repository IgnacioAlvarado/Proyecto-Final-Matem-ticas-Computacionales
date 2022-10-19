/**
 * Validate string processing through given NFA-Lambda from external files
 * <p>
 * @author      Ignacio Alvarado Reyes, A01656149
 * @author      Aaron Axhel Rosas Reyes, A01335324
 * @since 2.0
 * @see java.io.File#toPath()
 * @see java.io.IOException
 * @see java.nio.charset.Charset#defaultCharset()
 * @see java.nio.file.Files#readAllLines(Path, Charset)
 * @see java.util.Hashtable
 * @see java.util.Hashtable#put(String, String)
 * @see java.util.Hashtable#get(Object)
 * @see java.util.List
 * @see java.util.List#add(Object)
 * @see java.util.List#clear()
 * @see java.util.Scanner#
 * @see java.util.Scanner#nextLine()
 * @see java.util.Scanner#nextInt()
 * 
 */
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;



/**
 * Represents a full package for processing strings NFA-Lambda taking into
 * account the inputs of the user.
 * 
 */
public class Main {
    public static Hashtable<String, Hashtable<String,String>> transitionTable= new Hashtable<String,Hashtable<String,String>>();
    public static Hashtable<String,String> transitions = new Hashtable<String,String>();
    public static ArrayList<String> finalS= new ArrayList<String>();
    public static ArrayList<String> lineCheck= new ArrayList<String>();
    public static boolean first= true;
    public static int unitMax=0;
/**
 * The main method for the IntegrativePracticeOne program.
 *
 * @param args Not used
 * @throws IOException if the file is NULL
 * @see #extendedTransition(String, String)
 */

    public static void main (String args[]) throws IOException{
        Scanner sc= new Scanner(System.in);
        //We give the user the option of choosing what file he/she wants to use
        System.out.println("Give me the name of the text automata you want to use without the .txt extension:");
        String txtC = sc.nextLine();
        //sc.nextLine();

        //We take the line out of the file
        Path filePath = new File(txtC+".txt").toPath();
        Charset charset = Charset.defaultCharset();        
        List<String> lineList;
        lineList = Files.readAllLines(filePath, charset);
        String[] lineArray = lineList.toArray(new String[]{});

        //We parse the first 4 lines
        String[] states= lineArray[0].split(",");
        unitMax=states[states.length-1].length();
        String[] alphabet= lineArray[1].split(",");
        String initialState= lineArray[2];
        String[] finalStates= lineArray[3].split(",");
        for(int i=0; i<finalStates.length;i++){
            finalS.add(finalStates[i]);
        }

        //We parse the rest of the lines that apply to the transition table
        
        for(int i=4;i<lineArray.length;i++){
            String[] splitLine= lineArray[i].split("=>");
            String comb= splitLine[0];
            String result= splitLine[1];
            transitions.put(comb, result);
            //System.out.println("Combination: "+comb+", Result: "+transitions.get(comb));
        }

        //We build the proper transition table
        for(int i=0; i<states.length;i++){
            Hashtable<String,String> resultTable = new Hashtable<String,String>();
            for(int j=0;j<alphabet.length;j++){
                if(transitions.get(states[i]+","+alphabet[j])!=null){
                    resultTable.put(alphabet[j], transitions.get(states[i]+","+alphabet[j]));
                }else{
                    resultTable.put(alphabet[j], "SinkState");
                }
            }
            if(transitions.get(states[i]+",lambda")!=null){
                resultTable.put("lambda", transitions.get(states[i]+",lambda"));
            }
            transitionTable.put(states[i], resultTable);
        }

        lineCheck.add("q0");

        boolean run = true;
        while(run){
            System.out.println("Give me a String that you want to validate or type # to exit");
            String input = sc.nextLine();
            if(input.compareTo("#")!=0){
                String prueba= extendedTransition(initialState, input);
                System.out.println("Result: "+prueba);
                String[] results= prueba.split(",");
                boolean ok=false;
                for(int i=0;i<results.length;i++){
                    if(finalS.contains(results[i])){
                        ok=true;
                    }
                }
                if(ok){
                    System.out.println("The String "+input+" is valid");
                }else{
                    System.out.println("The String "+input+" is NOT valid");
                }
            }else{
                sc.close();
                run=false;
            }
        }
    }//Close Method

/**
* Return a set of the resulting states of evaluating the given state with the
* character.
* The state argument is the state where you want to evaluate
* The car argument is the character that you want to evaluate with the state
* <p>
* This method always returns the states evaluated with the character
* it can be one state, a series of states or a Sink State which
* indicates that that combination of state and character doesn´t lead
* to any other states
*
* @param  state  the state that the function recieves to evaluate
* @param  car the character of the string that dictates the next transition
* @return     the result of the evaluation of the state 
*/

    public static String transitionFunction(String state, String car){
        System.out.println("Transition state: "+state+" with: "+car+ " gives= "+transitionTable.get(state).get(car));
        return transitionTable.get(state).get(car);
        
    }//Close Method

/**
* Return a concatenation of the evaluted states or the recursive call that evaluates them
* through a loop of lambda transitions.
* The state argument is the state or set of states where you want to evaluate
* <p>
* This method always returns the states, whether or not the 
* the exisitng in the transition table using lambda transitions. When this
* function attempts to evaluate the states it will use a while loop
* that returns the result for the state or states that is needed to evaluate, attempting
* to follow the mathematical definition of the lambda closure function
*
*
* @param  state  the state that the function recieves to evaluatee
* @return   the result of the evaluation of the state or states
*/

    public static String lambdaClosure(String state){
        System.out.println("Evaluating: "+state+" in Lambda Closure");
        if(state.compareTo("SinkState")!=0){
            lineCheck.add(state);
            boolean run=true;
            String result=state;
            String[] prep= result.split(",");
            for(int i=0; i<prep.length;i++){
                lineCheck.add(prep[i]);
            }
            while(run){
                boolean transition= false;
                String[] s= result.split(",");
                for(int j=0;j<s.length;j++){
                    if(transitionTable.get(s[j]).get("lambda")!=null && lineCheck.contains(transitionTable.get(s[j]).get("lambda"))==false){
                        String a= transitionTable.get(s[j]).get("lambda");
                        System.out.println("This state: "+s[j]+" with lambda gives: "+ a);
                        String[] t= a.split(",");

                        for(int k=0; k<t.length;k++){
                            if(lineCheck.contains(t[k])==false){
                                lineCheck.add(t[k]);
                                result= result+","+t[k];
                                transition=true;
                            }
                        }
                    }
                }
                System.out.println("Resulting states with lambda: "+result);
                if(!transition){
                    run=false;
                }
            }
            
            return result;
        }
        System.out.println(state+" with lambda "+state);
        return state;
        
    }//Close Method

/**
* Return a concatenation of the evaluated states
* The state argument is the state where you want to evaluate
* The word argument is the input string that the user wants to evaluate
* <p>
* This method always returns the complete set of states,  When this
* function attempts to evaluate the states it will first recursively separate 
* the input string character by character and then will procede to evaluate the
* states by calling the transitionFunction() and lambdaClosure() methods 
*
*
* @param  state  the state that the function recieves to evaluate
* @param word the string that the function recieves to validate 
* @return   it returns a recursive call to the method and the lambdaClosure() method that 
* in the end will return the set of the states that result from the overall evaluation
* @see #transitionFunction(String, String)
* @see #lambdaClosure(String)
*/

    public static String extendedTransition(String state, String word){
        System.out.println("Extended Transition state: "+state+" processed word: "+word);
        if(word.length()==0){
            System.out.println("Length 0");
            return lambdaClosure(state);
            //lambdaClosure()
        }else{
                
                String firstPart= word.substring(0,word.length()-1);
                System.out.println("First Part cut of the string: "+firstPart);
                String lastChar= word.charAt(word.length()-1)+"";
                System.out.println("Last Char: "+lastChar);
                System.out.println("state: "+state);
                String aux= extendedTransition(state, firstPart);
                System.out.println(aux);
                String aux2="SinkState";
                String[] states= aux.split(",");
                if(states.length==1){
                    if(states[0].compareTo("SinkState")!=0){
                        aux2= transitionFunction(aux, lastChar);
                    }
                }else{
                    int count=0;
                    for(int i=0;i<states.length;i++){
                        String test= transitionFunction(states[i], lastChar);
                        if(test.compareTo("SinkState")!=0){
                            if(count==0){
                                aux2=test;
                                count++;
                            }else{
                                aux2=aux2+","+test;
                            }
                        }
                    }
                }
                System.out.println(aux2);
                lineCheck.clear();
                return lambdaClosure(aux2);
            }
        
    } //Close Method

}