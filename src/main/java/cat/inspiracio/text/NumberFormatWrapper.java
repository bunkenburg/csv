/*  Copyright 2011 Alexander Bunkenburg alex@inspiracio.cat

    This file is part of csv.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
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
