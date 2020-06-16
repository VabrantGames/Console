package com.vabrant.console.commandsections;

public class IntArgument extends BasicArgument<Integer> {

	private int value = 0;

	@Override
	public void setArgument(Integer argument) {
		value = argument;
	}

	@Override
	protected void parse(String sectionString) throws RuntimeException {
		char firstChar = sectionString.charAt(0);
		
		if(Character.isDigit(firstChar)) {
			//Binary
			if(sectionString.length() > 1 && sectionString.charAt(1) == 'b') {
				value = Integer.parseInt(sectionString.substring(2), 2);
			}
			else {//Normal int
				value = Integer.parseInt(sectionString);
			}
		}
		else if(firstChar == '#') { //Hexadecimal 
			value = Integer.parseInt(sectionString.substring(1), 16);
		}
		else {
			throw new RuntimeException("Error parsing int.");
		}
	}
	
	public int get() {
		return value;
	}
	
	@Override
	public Class<Integer> getArgumentType() {
		return int.class;
	}

	@Override
	public Integer getArgument() {
		return value;
	}
	
	@Override
	public void reset() {
		value = 0;
	}

}
