package com.mukunda.loremeta;

import java.util.UUID;

import org.bukkit.ChatColor;
 
 
//---------------------------------------------------------------------------------------------
public enum DataType {
	// types of data an entry can hold
	
	BYTE(1),SHORT(2),INT(3),LONG(4),UID(5),TEXT(6);
	
	int type;
	private DataType(int type) {
		this.type = type;
	}
	
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
		default:
			return null;				
		}
		
	}
	
	public int getInt() {
		return type;
	}
	
	public String getTag() {
		return "" + ChatColor.COLOR_CHAR + (char)(0x200+type);
	}
	
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
			}
		} catch( IllegalArgumentException e ) {
			return null; 
		}
		return null;
	}
}
