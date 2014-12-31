package cat.inspiracio.text;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class NumberFormatWrapper extends NumberFormat{

	private NumberFormat wrapped;
	
	public NumberFormatWrapper(NumberFormat f){this.wrapped=f;}
	
	@Override public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos){return wrapped.format(number, toAppendTo, pos);}

	@Override public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos){return wrapped.format(number, toAppendTo, pos);}

	@Override public Number parse(String source, ParsePosition parsePosition){return wrapped.parse(source, parsePosition);}

}
