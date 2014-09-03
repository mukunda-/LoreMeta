/*
 * LoreMeta
 *
 * Copyright (c) 2014 Mukunda Johnson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.mukunda.loremeta;

import java.util.UUID;
 
/**
 * Types of data a LoreMeta entry can hold
 * @author bim
 *
 */
public enum DataType {
	
	/**
	 * 1 byte value
	 */
	BYTE(1),
	
	/**
	 * 2 byte value
	 */
	SHORT(2),
	
	/**
	 * 4 byte value
	 */
	INT(3),
	
	/**
	 * 8 byte value
	 */
	LONG(4),
	
	/**
	 * UUID value (16 bytes)
	 */
	UID(5),
	
	/**
	 * variable length value, ascii characters only.
	 */
	TEXT(6),
	
	/**
	 * flag only, no data associated with this type.
	 */
	FLAG(7);
	
	private static final char COLOR_CHAR = '\u00A7';
	
	int type;
	private DataType(int type) {
		this.type = type;
	}
	
	/**************************************************************************
	 * Convert a character value (from a data initializer) to a type.
	 * 
	 * @param t uppercase character value
	 * @return  type associated with the character, or null if invalid
	 **************************************************************************/
	public static DataType fromCharacter( char t ) {
		switch( t ) {
		case 'B':
			return BYTE;
		case 'S':
			return SHORT;
		case 'I':
			return INT;
		case 'L':
			return LONG;
		case 'U':
			return UID;
		case 'T':
			return TEXT;
		case 'F':
			return FLAG;
		default:
			return null;				
		}
		
	}
	
	/**************************************************************************
	 * Get the raw type index.
	 **************************************************************************/
	public int getInt() {
		return type;
	}
	
	/**************************************************************************
	 * Create a tag string, which is used to prefix the data entry name.
	 * 
	 * @return Tag string
	 **************************************************************************/
	public String getTag() {
		return "" + COLOR_CHAR + (char)(0x200+type);
	}
	
	/**************************************************************************
	 * Convert a string to data according to this type.
	 * 
	 * @param input String to convert
	 * @return      converted value or null if the string was invalid.
	 **************************************************************************/
	public Object convertString( String input ) {
		try {
			switch( this ) {
			case BYTE:
				return Byte.parseByte( input );
			case SHORT:
				return Short.parseShort( input );
			case INT:
				return Integer.parseInt( input );
			case LONG:
				return Long.parseLong( input );
			case UID:
				return UUID.fromString( input );
			case TEXT:
				return input;
			case FLAG:
				return null;
			}
		} catch( IllegalArgumentException e ) {
			return null; 
		}
		return null;
	}
}
