package com.vabrant.console.commandsections;

public class DoubleArgument extends BasicArgument<Double> {

	private double value = 0;

	@Override
	public void setArgument(Double argument) {
		
	}

	@Override
	public Class<Double> getArgumentType() {
		return double.class;
	}

	@Override
	public Double getArgument() {
		return value;
	}

	@Override
	public void reset() {
		super.reset();
		value = 0;
	}
	
}
