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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/******************************************************************************
 * LoreMeta interface
 * 
 * @author mukunda
 *
 ******************************************************************************/
public final class LoreMeta {
	
	/**************************************************************************
	 * Item initialization function
	 * 
	 * This function scans the lore of an ItemStack, and replaces certain 
	 * markers with embedded data.
	 * 
	 * ## marks a field
	 * @@[...] marks a data initializer
	 * 
	 * For example, an item lore can look like this:
	 *   Spellbook
	 *   
	 *   ##Charges: 3
	 *   @@[B:POWER:3]
	 * 
	 * After processing, the item lore will look like this:
	 *   Spellbook
	 *   
	 *   Charges: 3
	 *   
	 * If ## is found at the start of a line, it is hidden and that line
	 * is marked as a field. Fields are visible data that can be modified
	 * later. With this item, you can do setField( "Charges", x ) to change
	 * the field value, which will be reflected in the item lore. Fields
	 * are always stored as string values.
	 * 
	 * Fields cannot appear on the first line of the lore, as that would
	 * collide with the other storage mechanism.
	 * 
	 * The @@ marker creates a data initializer. It's format is as follows:
	 * 
	 * @@[t:name:value]
	 * 
	 * t may be "B", "S", "I", "L", "Q", "T", or "F", which means BYTE, SHORT
	 * INT, LONG, UUID, TEXT, or FLAG respectively.
	 * 
	 * Only one data initializer can be on a line at a time, and it must be the
	 * only text on the line. The marker @@[B:POWER:3] will create a BYTE data
	 * entry called POWER. After initialization of the example item, 
	 * getData( item, new MetaKeyByte(POWER) ) will return 3
	 * 
	 * For flags, define them as [F:name:].
	 * 
	 * Data initializer lines are removed from the lore after initialization.
	 * 
	 * If there is an error when processing a line, the line will remain unchanged
	 * in the lore.
	 * 
	 * This function preserves any LoreMeta data already present.
	 * 
	 * @param item Item to initialize.
	 **************************************************************************/
	public static void initialize( ItemStack item ) {
		ItemMeta meta = item.getItemMeta();
		if( !meta.hasLore() ) return; // no initialization needed.
		
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
		
		HashMap<MetaKey,String> dataEntries = new HashMap<MetaKey,String>();
		
		Pattern dataEntryPattern = Pattern.compile( "^@@\\[([A-Z]):(.+):(.*)\\]$" );
		boolean changed = false;
		for( int i = 0; i < lore.size(); i++ ) {
			
			String entry = lore.get(i);
			
			if( entry.startsWith( "##" ) ) {
				if( i == 0 ) continue; // field cannot be on first line.
				// this is a field
				entry = TAG_FIELD + entry.substring( 2 );
				lore.set( i, entry );
				changed = true;
			} else if( entry.startsWith( "@@[" ) ) {
				Matcher matcher = dataEntryPattern.matcher( entry );
				
				if( matcher.matches() ) {
					DataType type = DataType.fromCharacter( matcher.group(1).charAt(0) );
					if( type == null ) continue; // bad type
					String value = matcher.group(3);
					if( type != DataType.FLAG ) {
						if( type.convertString( value ) == null ) continue;
					} else {
						value = "";
					}
					
					String name = matcher.group(2);
					MetaKey key = new MetaKey( name, type );
					 
					dataEntries.put( key, value ); 
					changed = true;
					
					lore.remove(i);
					i--;
				} 
			}
		}
		
		if( !changed ) return;
		
		StringBuilder builder = new StringBuilder( lore.get(0) );
		for( Map.Entry<MetaKey, String> entry : dataEntries.entrySet()) {
			
			Object value = entry.getKey().getDataType().convertString( entry.getValue() );
			if( value == null ) continue; // bad string
			
			removeEntry( builder, entry.getKey() );
			setDataAppendValue( 
					builder, 
					entry.getKey(), 
					entry.getKey().getDataType().convertString( entry.getValue() ) );
			
		}
		
		lore.set( 0, builder.toString() );
		meta.setLore( lore );
		item.setItemMeta( meta );
	}
	
	/**************************************************************************
	 * Read a byte from an item.
	 * 
	 * @param item Item to read from
	 * @param key  Byte key to access
	 * @return     Byte value, or null if the key doesn't exist.
	 **************************************************************************/
	public static Byte getData( ItemStack item, MetaKeyByte key ) {
		return (Byte)getDataI( item, key );
	}
	
	/**************************************************************************
	 * Read a short from an item.
	 * 
	 * @param item Item to read from
	 * @param key  Short key to access
	 * @return     Short value, or null if the key doesn't exist.
	 **************************************************************************/
	public static Short getData( ItemStack item, MetaKeyShort key ) {
		return (Short)getDataI( item, key );
	}
	
	/**************************************************************************
	 * Read an integer from an item.
	 * 
	 * @param item Item to read from
	 * @param key  Integer key to access
	 * @return     Integer value, or null if the key doesn't exist.
	 **************************************************************************/
	public static Integer getData( ItemStack item, MetaKeyInt key ) {
		return (Integer)getDataI( item, key );
	}
	
	/**************************************************************************
	 * Read a long from an item.
	 * 
	 * @param item Item to read from
	 * @param key  Long key to access
	 * @return     Long value, or null if the key doesn't exist.
	 **************************************************************************/
	public static Long getData( ItemStack item, MetaKeyLong key ) {
		return (Long)getDataI( item, key );
	}
	
	/**************************************************************************
	 * Read a UUID from an item.
	 * 
	 * @param item Item to read from
	 * @param key  UUID key to access
	 * @return     UUID value, or null if the key doesn't exist.
	 **************************************************************************/
	public static UUID getData( ItemStack item, MetaKeyUUID key ) {
		return (UUID)getDataI( item, key );
	}

	/**************************************************************************
	 * Read a string from an item.
	 * 
	 * @param item Item to read from
	 * @param key  Text key to access
	 * @return     Content string, or null if the key doesn't exist.
	 **************************************************************************/
	public static String getData( ItemStack item, MetaKeyText key ) {
		return (String)getDataI( item, key );
	}
	
	/**************************************************************************
	 * Check if a flag exists.
	 * 
	 * Equivalent, but safer than hasData( item, key ).
	 * 
	 * @param item Item to read from
	 * @param key  Flag key to access
	 * @return     true if the flag is set, false if not.
	 **************************************************************************/
	public static boolean getData( ItemStack item, MetaKeyFlag key ) {
		return hasData( item, key );
	}
	
	/**************************************************************************
	 * Check if a data entry exists
	 * 
	 * @param item Item to check
	 * @param key  Key to look for
	 * @return     true if the key exists
	 **************************************************************************/ 
	public static boolean hasData( ItemStack item, MetaKey key ) {
		String data = getFirstLoreSafely( item );
		if( data == null ) return false;
		return data.contains( key.getFormattedKey() );
	}

	/**************************************************************************
	 * Create or modify a byte data entry.
	 * 
	 * @param item  Item to modify
	 * @param key   Byte key to access
	 * @param value New value to set
	 **************************************************************************/
	public static void setData( ItemStack item, MetaKeyByte key, Byte value ) {
		setDataI( item, key, value );
	}
	
	/**************************************************************************
	 * Create or modify a short data entry.
	 * 
	 * @param item  Item to modify
	 * @param key   Short key to access
	 * @param value New value to set
	 **************************************************************************/
	public static void setData( ItemStack item, MetaKeyShort key, Short value ) {
		setDataI( item, key, value ); 
	}
	
	/**************************************************************************
	 * Create or modify an integer data entry.
	 * 
	 * @param item  Item to modify
	 * @param key   Integer key to access
	 * @param value New value to set
	 **************************************************************************/
	public static void setData( ItemStack item, MetaKeyInt key, Integer value ) {
		setDataI( item, key, value ); 
	}
	
	/**************************************************************************
	 * Create or modify a long data entry.
	 * 
	 * @param item  Item to modify
	 * @param key   Long key to access
	 * @param value New value to set
	 **************************************************************************/
	public static void setData( ItemStack item, MetaKeyLong key, Long value ) {
		setDataI( item, key, value ); 
	}
	
	/**************************************************************************
	 * Create or modify a UUID data entry.
	 * 
	 * @param item  Item to modify
	 * @param key   UUID key to access
	 * @param value New value to set
	 **************************************************************************/
	public static void setData( ItemStack item, MetaKeyUUID key, UUID value ) {
		setDataI( item, key, value ); 
	}
	
	/**************************************************************************
	 * Create or modify a text (string) data entry.
	 * 
	 * The value should only contain ASCII characters, otherwise it may
	 * cause an error with LoreMeta or other plugins that hide embed data
	 * with color characters.
	 * 
	 * @param item  Item to modify
	 * @param key   Text key to access
	 * @param value New value to set
	 **************************************************************************/
	public static void setData( ItemStack item, MetaKeyText key, String value ) {
		setDataI( item, key, value ); 
	}
	
	/**************************************************************************
	 * Set a flag data entry.
	 * 
	 * @param item Item to modify
	 * @param key  Flag key to access
	 **************************************************************************/
	public static void setFlag( ItemStack item, MetaKeyFlag key ) {
		setDataI( item, key, Boolean.valueOf(true) );
	}
	
	/**************************************************************************
	 * Remove a flag data entry
	 * 
	 * @param item Item to modify
	 * @param key  Flag key to remove
	 **************************************************************************/
	public static void clearFlag( ItemStack item, MetaKeyFlag key ) {
		setDataI( item, key, null );
	}
	
	/**************************************************************************
	 * Read data from a Field
	 * 
	 * Fields are visible data in the item lore, created with the
	 * initialization function.
	 * 
	 * When accessing a field, color codes are ignored, and should not be
	 * put in the key.
	 * 
	 * @param item  Item to modify
	 * @param key   Field name to read 
	 * 
	 * @return      Value of the field, or null if the field was not found.
	 * 
	 * @see #initialize(ItemStack)
	 **************************************************************************/
	public static String getField( ItemStack item, String key ) {
		
		List<String> lore = getLoreSafely( item );
		if( lore == null ) return null;
		
		for( String loreEntry : lore ) {
			if( loreEntry.startsWith( TAG_FIELD ) ) {
				int splitter = loreEntry.indexOf( ":" );
				if( splitter == -1 ) continue; // malformed field!
				String fieldName = loreEntry.substring( 2, splitter );
				fieldName = stripFieldKey( fieldName );
				if( !fieldName.equals(key) ) continue;
				String fieldValue = loreEntry.substring( splitter+1 );
				return fieldValue.trim();
			}
		}
		return null;
	}
	
	/**************************************************************************
	 * Set a Field.
	 * 
	 * Fields are visible data in the item lore, created with the
	 * initialization function.
	 * 
	 * When accessing a field, color codes are ignored, and should not be
	 * put in the key.
	 * 
	 * @param item  Item to modify
	 * @param key   Field name to write to
	 * @param value Any value you would like to set the field to, the item
	 *              lore will display "FieldName: [value]" directly copied
	 *              from here.
	 * @return      true if the field was found and set
	 * 
	 * @see #initialize(ItemStack)
	 **************************************************************************/
	public static boolean setField( ItemStack item, String key, String value ) { 
		
		List<String> lore = getLoreSafely( item );
		if( lore == null ) return false;

		for( int i = 0; i < lore.size(); i++ ) {
			String loreEntry = lore.get(i);
			if( loreEntry.startsWith( TAG_FIELD ) ) {
				int splitter = loreEntry.indexOf( ":" );
				if( splitter == -1 ) continue; // malformed field!
				String fieldName = loreEntry.substring( 2, splitter );
				fieldName = stripFieldKey( fieldName );
				if( !fieldName.equals(key) ) continue;
				
				String newLore = loreEntry.substring( 0, splitter+1 ) + " " + value;
				lore.set( i, newLore );
				ItemMeta meta = item.getItemMeta();
				meta.setLore( lore );
				item.setItemMeta( meta );
				return true;
			}
		}
		
		return false;
	}
	
	/**************************************************************************
	 * Remove a field from an item.
	 * 
	 * This deletes a matching field the item lore, and it cannot 
	 * be brought back.
	 * 
	 * @param item Item to modify
	 * @param key  Field name to delete
	 * 
	 * @return     true if the field was removed, false if the field didn't
	 *             exist.
	 **************************************************************************/
	public static boolean removeField( ItemStack item, String key ) {
		List<String> lore = getLoreSafely( item );
		if( lore == null ) return false;

		for( int i = 0; i < lore.size(); i++ ) {
			String loreEntry = lore.get(i);
			if( loreEntry.startsWith( TAG_FIELD ) ) {
				int splitter = loreEntry.indexOf( ":" );
				if( splitter == -1 ) continue; // malformed field!
				String fieldName = loreEntry.substring( 2, splitter );
				fieldName = stripFieldKey( fieldName );
				if( !fieldName.equals(key) ) continue;
				
				lore.remove( i );
				ItemMeta meta = item.getItemMeta();
				meta.setLore( lore );
				item.setItemMeta( meta );
				return true;
			}
		}
		
		return false;
	}

	//---------------------------------------------------------------------------------------------
	private static String stripFieldKey( String string ) {
 
		return ChatColor.stripColor( string ).trim(); 
	}
	
	
	private static final char COLOR_CHAR = '\u00A7';
	 
	//---------------------------------------------------------------------------------------------
	// offset added to data when setting char values
	private static final int DATA_BASE = 0x100;
	
	// "fields" are prefixed by this, this replaces "##~" prefix in stock items
	private static final String TAG_FIELD = COLOR_CHAR + "\u0300";
	  
	//---------------------------------------------------------------------------------------------
	private static List<String> getLoreSafely( ItemStack item ) {
		if( item == null ) return null;
		if( !item.hasItemMeta() ) return null;
		ItemMeta meta = item.getItemMeta();
		if( !meta.hasLore() ) return null;
		return meta.getLore();
	}
	
	//---------------------------------------------------------------------------------------------
	private static String getFirstLoreSafely( ItemStack item ) {
		List<String> lore = getLoreSafely( item );
		if( lore== null || lore.size() == 0 ) return null;
		return lore.get(0);
	}
	
	//---------------------------------------------------------------------------------------------
	private static int getPackedInt( String data, int offset ) {
		return	(((int)(data.charAt(offset+0) - DATA_BASE))    ) | 
				(((int)(data.charAt(offset+2) - DATA_BASE))<<8 ) |
				(((int)(data.charAt(offset+4) - DATA_BASE))<<16) |
				(((int)(data.charAt(offset+6) - DATA_BASE))<<24);
	}
	
	//---------------------------------------------------------------------------------------------
	private static long getPackedLong( String data, int offset ) {
		return	( ((long)getPackedInt( data, offset   ))&0xFFFFFFFFL )| 
				( ((long)getPackedInt( data, offset+8 ))<<32 );
	}
	
	//---------------------------------------------------------------------------------------------
	private static Object getDataI( ItemStack item, MetaKey key ) {
		String data = getFirstLoreSafely( item );
		if( data == null ) return null;
		 
		int index = data.indexOf( key.getFormattedKey() );
		if( index == -1 ) return null;
		index += key.getFormattedKey().length() + 1; // points to first data byte now (skipping color char)
		
		switch( key.getDataType() ) {
		case TEXT:
			int length = (int)data.charAt( index ) - DATA_BASE;
			index += 2;
			StringBuilder string = new StringBuilder();
			for( int i = 0; i < length; i++ ) {
				string.append( data.charAt(index+i*2) );
				
			}
			return string.toString();
		case BYTE:
			return (byte)(data.charAt(index) - DATA_BASE) ;
		case SHORT:
			return (short)((int)(data.charAt(index) - DATA_BASE) | ((int)(data.charAt(index+2) - DATA_BASE) << 8));
		case INT:
			return getPackedInt( data, index );
		case LONG:
			return getPackedLong( data, index );
		case UID:
			long a = getPackedLong( data, index+16 );
			long b = getPackedLong( data, index  );

			Bukkit.broadcastMessage( "DEBUG: uuid-a " + a );
			Bukkit.broadcastMessage( "DEBUG: uuid-b " + b );
			return new UUID( getPackedLong( data, index+16 ), getPackedLong( data, index ) );
		case FLAG:
			return Boolean.valueOf(true);
		default:
			return null;
		}
	}
	
	//---------------------------------------------------------------------------------------------
	private static void removeEntry( StringBuilder builder, MetaKey key ) {
		int index = builder.indexOf( key.getFormattedKey() );
		if( index == -1 ) return;
		int length;
		switch( key.getDataType() ) {
		case TEXT:
			length = ((int)builder.charAt( index ) - DATA_BASE)*2 + 2;
			break;
		case BYTE:
			length = 2;
			break;
		case SHORT:
			length = 4;
			break;
		case INT:
			length = 8;
			break;
		case LONG:
			length = 16;
			break;
		case UID:
			length = 32;
			break;
		case FLAG:
			length = 0;
		default:
			return;
		}
		length += key.getFormattedKey().length();
		builder.delete( index, index + length );
	}
	
	//---------------------------------------------------------------------------------------------
	private static char dataChar( int value ) {
		return (char)( (value&0xFF) + DATA_BASE );
	}
	
	//---------------------------------------------------------------------------------------------
	private static void writeByte( StringBuilder output, byte value ) {
		char[] data = new char[2];
		data[0] = COLOR_CHAR;
		data[1] = dataChar(value);
		output.append( data );
	}
	
	//---------------------------------------------------------------------------------------------
	private static void writeShort( StringBuilder output, short value ) {
		char[] data = new char[4];
		data[0] = COLOR_CHAR;
		data[1] = dataChar(value);
		data[2] = COLOR_CHAR;
		data[3] = dataChar(value>>8);
		output.append( data );
	} 
	
	//---------------------------------------------------------------------------------------------
	private static void writeInt( StringBuilder output, int value ) {
		char[] data = new char[8];
		data[0] = COLOR_CHAR;
		data[1] = dataChar(value);
		data[2] = COLOR_CHAR;
		data[3] = dataChar(value>>8);
		data[4] = COLOR_CHAR;
		data[5] = dataChar(value>>16);
		data[6] = COLOR_CHAR;
		data[7] = dataChar(value>>24);
		output.append( data );
	}
	
	//---------------------------------------------------------------------------------------------
	private static void writeLong( StringBuilder output, long value ) { 
		writeInt( output, (int)(value & 0xFFFFFFFFL) );
		writeInt( output, (int)(value >> 32) );
	}
	
	//---------------------------------------------------------------------------------------------
	private static void writeUUID( StringBuilder output, UUID value ) {
		writeLong( output, value.getLeastSignificantBits() );
		writeLong( output, value.getMostSignificantBits() );
	}
	
	//---------------------------------------------------------------------------------------------
	private static void setDataAppendValue( StringBuilder output, MetaKey key, Object value ) {
		output.append( key.getFormattedKey() );
		
		switch( key.getDataType() ) {
		case TEXT:
			String valueString = (String)value; 
			output.append( "" + COLOR_CHAR + (char)(valueString.length()+DATA_BASE) );
			for( int i =0; i < valueString.length(); i++ ) {
				output.append( "" + COLOR_CHAR + valueString.charAt(i) );
			}
			
			break;
		case BYTE:
			writeByte( output, (Byte)value );
			 
			break;
		case SHORT:
			writeShort( output, (Short)value );
			
			break;
		case INT:
			writeInt( output, (Integer)value );
			break;
		case LONG:
			writeLong( output, (Long)value );
			break;
		case UID:
			writeUUID( output, (UUID)value );
			break;
		case FLAG:
			// no data for flags.
			break;
		default:
			break;
		}
	}
	
	//---------------------------------------------------------------------------------------------
	private static void setDataI( ItemStack item, MetaKey key, Object value ) {
		if( item == null ) return;
		ItemMeta meta = item.getItemMeta(); 
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
		String data = lore.size() == 0 ? "" : lore.get(0);
		
		StringBuilder output; 
		 
		// has lore data
		output = new StringBuilder( data );
		
		// remove data tag
		removeEntry( output, key );  
		
		// if value is null, the data is deleted from the lore
		if( value != null ) {
			
			setDataAppendValue( output, key, value );
		}
		if( lore.size() == 0 ) {
			lore.add( output.toString() );
		} else {
			lore.set( 0, output.toString() );
		}
		
		meta.setLore( lore );
		item.setItemMeta( meta );
	} 
	
	//---------------------------------------------------------------------------------------------
}
