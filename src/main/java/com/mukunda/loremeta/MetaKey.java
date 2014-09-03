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

import org.bukkit.ChatColor; 

/******************************************************************************
 * A "key" used to identify embedded data in item lore.
 * 
 * @author mukunda
 *
 ******************************************************************************/
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
