package com.mukunda.loremeta;

import org.bukkit.ChatColor; 

//---------------------------------------------------------------------------------------------
public class MetaKey {
	private String formatted;
	private String name;
	private final DataType type;
	
	public MetaKey( String key, DataType type ) {
		name = key;
		this.type = type;
		StringBuilder builder = new StringBuilder();
		builder.append( type.getTag() );
		for( int i = 0; i < key.length(); i++ ) {
			builder.append( "" + ChatColor.COLOR_CHAR + key.charAt(i) );
		}
		formatted = builder.toString() ;
	}
	
	public String getFormattedKey() {
		return formatted;
	}
	
	public String getName() {
		return name;
	}
	
	public DataType getDataType() {
		return type;
		
	}
	
	@Override
	public int hashCode() {
		return formatted.hashCode();
	}
	
	@Override
	public boolean equals( Object obj ) {
		if( this == obj ) return true;
		if( obj == null ) return false;
		if( !(obj instanceof MetaKey) ) return false;
		MetaKey key = (MetaKey)obj;
		return formatted.equals(key.formatted);
	}
}
