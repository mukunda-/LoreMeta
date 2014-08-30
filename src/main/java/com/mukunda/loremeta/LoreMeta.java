package com.mukunda.loremeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class LoreMeta {
	
	public enum DataType {
		LOREMETA(0),BYTE(1),SHORT(2),INT(3),LONG(4),QUAD(5),ASCII(6);
		
		int type;
		private DataType(int type) {
			this.type = type;
		}
		
		public String getTag() {
			return "" + ChatColor.COLOR_CHAR + (char)(0x200+type);
		}
	}
	 
	private static final String TAG_FIELD      = ChatColor.COLOR_CHAR + "\u0300";
	
	private HashMap<String,String> data;
	private HashMap<String,String> fieldValues;
	
	public class Section {
		private ArrayList<String> fields;
		
		public Section() {
			fields = new ArrayList<String>();
		}
	}
	
	// 0 = upper section
	// 1 = primary section
	// 2 = LORE section
	// 3 = sub section
	private Section[] sections;

	public LoreMeta() {
		data = new HashMap<String,String>();
		sections = new Section[4];
	}
	
	// initialize an item stack intended for LoreMeta data
	public static LoreMeta init( ItemStack item ) {
		
	}
	
	public static LoreMeta load( ItemStack item ) {
		if( !item.hasItemMeta() || !item.getItemMeta().hasLore() ) {
			return new LoreMeta();
		}
		
		List<String> lore = item.getItemMeta().getLore();
		
		if( lore.get(0).startsWith( LOREMETA_TAG ) ) {
			// this item is formatted with LoreMeta.
			
			// parse lore data
			
			
			int currentSection = 0;
			for( String entry : lore ) {
				
			}
			
		} else {
			// import lore.
		}
		
	}
	
	public static String formatKey( String key, DataType type ) {
		StringBuilder builder = new StringBuilder();
		builder.append( type.getTag() );
	}
	
	public static LoreMeta read( ItemStack item, String key ) {
		
	}
	
	public static LoreMeta readF( ItemStack item, String formattedKey ) {
		
	}
}
