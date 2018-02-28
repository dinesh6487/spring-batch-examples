package com.main.java.ngt.mts;

import java.util.ArrayList;
import java.util.List;

public class UserInput {
    
    public static class TextInput {
    	
    	private List<Character> userInput = new ArrayList<>();
    	
    	public boolean add(Character ch){
    		if(ch == null || Character.isWhitespace(ch)){
    			return false;
    		}
    		userInput.add(ch);
    		return true;
    	}
    	
    	public String getValue(){
    		return userInput.toString();
    	}
    }

    public static class NumericInput extends TextInput {
    	
    	public boolean add(Character ch){
    		if(ch == null || Character.isWhitespace(ch)){
    			return false;
    		}else if (Character.isDigit(ch)){
    			super.userInput.add(ch);
    			return true;
    		}else{
    			return false;
    		} 
    	}
    }

    public static void main(String[] args) {
        TextInput input = new NumericInput();
        input.add('1');
        input.add('a');
        input.add('0');
        System.out.println(input.getValue());
        TextInput input1 = new TextInput();
        input1.add('1');
        input1.add('a');
        input1.add('0');
        System.out.println(input1.getValue());
        
    }
}
