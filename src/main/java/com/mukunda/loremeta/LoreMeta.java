package com.mukunda.loremeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class LoreMeta {
	
	public enum DataType {
		// types of data an entry can hold
		
		LOREMETA(0),BYTE(1),SHORT(2),INT(3),LONG(4),UID(5),TEXT(6);
		
		int type;
		private DataType(int type) {
			this.type = type;
		}
		
		public String getTag() {
			return "" + ChatColor.COLOR_CHAR + (char)(0x200+type);
		}
	}

	// offset added to data when setting char values
	private static final int DATA_BASE = 0x100;
	
	// "fields" are prefixed by this, this replaces "##~" prefix in stock items
	private static final String TAG_FIELD = ChatColor.COLOR_CHAR + "\u0300";
	
	public class MetaKeyByte extends MetaKey {
		public MetaKeyByte( String key ) {
			super( key, DataType.BYTE );
		}
	}

	public class MetaKeyShort extends MetaKey {
		public MetaKeyShort( String key ) {
			super( key, DataType.SHORT );
		}
	}

	public class MetaKeyInt extends MetaKey {
		public MetaKeyInt( String key ) {
			super( key, DataType.INT );
		}
	}
	
	public class MetaKeyLong extends MetaKey {
		public MetaKeyLong( String key ) {
			super( key, DataType.LONG );
		}
	}
	
	public class MetaKeyUUID extends MetaKey {
		public MetaKeyUUID( String key ) {
			super( key, DataType.UID );
		}
	}
	public class MetaKeyText extends MetaKey {
		public MetaKeyText( String key ) {
			super( key, DataType.TEXT );
		}
	}
		
	private class MetaKey {
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
	}
	  
	
	// initialize an item stack intended for LoreMeta data
	public static void init( ItemStack item ) {
		
	}
	 
	
	private static List<String> getLoreSafely( ItemStack item ) {
		if( item == null ) return null;
		if( !item.hasItemMeta() ) return null;
		ItemMeta meta = item.getItemMeta();
		if( !meta.hasLore() ) return null;
		return meta.getLore();
	}
	
	private static String getFirstLoreSafely( ItemStack item ) {
		List<String> lore = getLoreSafely( item );
		if( lore== null || lore.size() == 0 ) return null;
		return lore.get(0);
	}
	
	private static int getPackedInt( String data, int offset ) {
		return  (((int)(data.charAt(offset+0) - DATA_BASE))    ) + 
				(((int)(data.charAt(offset+2) - DATA_BASE))<<8 ) + 
				(((int)(data.charAt(offset+4) - DATA_BASE))<<16) + 
				(((int)(data.charAt(offset+6) - DATA_BASE))<<24);
	}
	
	private static long getPackedLong( String data, int offset ) {
		return  (((long)getPackedInt( data, offset   ))    )+ 
				(((long)getPackedInt( data, offset+8 ))<<32);
	}
	
	public static Object getDataI( ItemStack item, MetaKey key ) {
		String data = getFirstLoreSafely( item );
		if( data == null ) return null;
		if( !data.startsWith( DataType.LOREMETA.getTag() ) ) return null;
		
		int index = data.indexOf( key.formatted );
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
			return (short)((int)(data.charAt(index) - DATA_BASE) + ((int)(data.charAt(index+2) - DATA_BASE) << 8));
		case INT:
			return getPackedInt( data, index );
		case LONG:
			return getPackedLong( data, index );
		case UID:
			return new UUID( getPackedLong( data, index+16 ), getPackedLong( data, index ) ); 
		default:
			return null;
		}
	}
	
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
		default:
			return;
		}
		builder.delete( index, index + length );
	}
	
	private static char dataChar( int value ) {
		return (char)( (value&0xFF) + DATA_BASE );
	}
	
	private static void writeByte( StringBuilder output, byte value ) {
		char[] data = new char[2];
		data[0] = ChatColor.COLOR_CHAR;
		data[1] = dataChar(value);
		output.append( data );
	}
	
	private static void writeShort( StringBuilder output, short value ) {
		char[] data = new char[4];
		data[0] = ChatColor.COLOR_CHAR;
		data[1] = dataChar(value);
		data[2] = ChatColor.COLOR_CHAR;
		data[3] = dataChar(value>>8);
		output.append( data );
	} 

	private static void writeInt( StringBuilder output, int value ) {
		char[] data = new char[8];
		data[0] = ChatColor.COLOR_CHAR;
		data[1] = dataChar(value);
		data[2] = ChatColor.COLOR_CHAR;
		data[3] = dataChar(value>>8);
		data[4] = ChatColor.COLOR_CHAR;
		data[5] = dataChar(value>>16);
		data[6] = ChatColor.COLOR_CHAR;
		data[7] = dataChar(value>>24);
		output.append( data );
	}
	

	private static void writeLong( StringBuilder output, long value ) {
		writeInt( output, (int)(value & 0xFFFFFFFF) );
		writeInt( output, (int)(value >> 32) );
	}
	
	private static void writeUUID( StringBuilder output, UUID value ) {
		writeLong( output, value.getLeastSignificantBits() );
		writeLong( output, value.getMostSignificantBits() );
	}
	
	private static void setDataI( ItemStack item, MetaKey key, Object value ) {
		if( item == null ) return;
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
		String data = lore.size() == 0 ? "" : lore.get(0);
		
		StringBuilder output; 
		 
		if( !data.startsWith( DataType.LOREMETA.getTag() ) ) {
			
			// no lore data
			output = new StringBuilder( DataType.LOREMETA.getTag() + data );
			
		} else {
			// has lore data
			output = new StringBuilder( data );
			
			// remove data tag
			removeEntry( output, key ); 
		}
		
		// if value is null, the data is deleted from the lore
		if( value != null ) {
			
			output.append( key.getFormattedKey() );
			
			switch( key.getDataType() ) {
			case TEXT:
				String valueString = (String)value; 
				output.append( "" + ChatColor.COLOR_CHAR + (char)(valueString.length()+DATA_BASE) );
				for( int i =0; i < valueString.length(); i++ ) {
					output.append( "" + ChatColor.COLOR_CHAR + valueString.charAt(i) );
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
			default:
				return;
			}
		}
		if( lore.size() == 0 ) {
			lore.add( output.toString() );
		} else {
			lore.set( 0, output.toString() );
		}
		
		meta.setLore( lore );
		item.setItemMeta( meta );
	}
	
	public static Byte getData( ItemStack item, MetaKeyByte key ) {
		return (Byte)getDataI( item, key );
	}

	public static Short getData( ItemStack item, MetaKeyShort key ) {
		return (Short)getDataI( item, key );
	}

	public static Integer getData( ItemStack item, MetaKeyInt key ) {
		return (Integer)getDataI( item, key );
	}
	
	public static Long getData( ItemStack item, MetaKeyLong key ) {
		return (Long)getDataI( item, key );
	}

	public static UUID getData( ItemStack item, MetaKeyUUID key ) {
		return (UUID)getDataI( item, key );
	}
	
	public static String getData( ItemStack item, MetaKeyText key ) {
		return (String)getDataI( item, key );
	}

	public static void setData( ItemStack item, MetaKeyByte key, Byte value ) {
		setDataI( item, key, value );
	}
	
	public static void setData( ItemStack item, MetaKeyShort key, Short value ) {
		setDataI( item, key, value ); 
	}

	public static void setData( ItemStack item, MetaKeyInt key, Integer value ) {
		setDataI( item, key, value ); 
	}
	
	public static void setData( ItemStack item, MetaKeyLong key, Long value ) {
		setDataI( item, key, value ); 
	}
	
	public static void setData( ItemStack item, MetaKeyUUID key, UUID value ) {
		setDataI( item, key, value ); 
	}

	public static void setData( ItemStack item, MetaKeyText key, String value ) {
		setDataI( item, key, value ); 
	}
	
	
	public static String getField( ItemStack item, String key ) {
		// todo..
	}

	public static boolean setField( ItemStack item, String key, String value ) {
		// todo..
	}
}
