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


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/* for tests
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;*/

/******************************************************************************
 * LoreMeta Bukkit plugin
 * 
 * This interface is only used to check for availability.
 * The actual work is all done in static functions in the LoreMeta class
 *  
 * @author mukunda
 *
 ******************************************************************************/
public class LoreMetaPlugin extends JavaPlugin {
	 
	
	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
		/*
		if( cmd.getName().equalsIgnoreCase( "loremeta_test" ) ){
			// run tests
			Bukkit.broadcastMessage( ((Integer)((int)-1 & 0xFF)).toString() );
			try {
				ItemStack test = new ItemStack( Material.COBBLESTONE );
				if( LoreMeta.getData( test, new MetaKeyByte("bar") ) != null ) {
					throw new Exception( "1" );
				}
				
				LoreMeta.setData( test, new MetaKeyByte("bar"), (byte)-128 );
				if( LoreMeta.getData( test, new MetaKeyByte("bar") ) != -128 ) {
					throw new Exception( "2" );
				}
				
				LoreMeta.setData( test, new MetaKeyShort("bar"), (short)-450 );
				if( LoreMeta.getData( test, new MetaKeyShort("bar") ) != -450 ) {
					throw new Exception( "3" );
				}
				
				for( int i = 0; i < 20; i++ ) {
					int value = -2;//(int)(-Integer.MIN_VALUE + Math.random() * (Integer.MAX_VALUE-Integer.MIN_VALUE));
					LoreMeta.setData( test, new MetaKeyInt("bar"), value );
					if( LoreMeta.getData( test, new MetaKeyInt("bar") ) != value ) {
						throw new Exception( "4" );
					
					}
				}
				LoreMeta.setData( test, new MetaKeyInt("bar"), 1 );
				
				LoreMeta.setData( test, new MetaKeyLong("bar"), 37L );
				if( LoreMeta.getData( test, new MetaKeyLong("bar") ) != 37L ) {
					throw new Exception( "5" );
				}
				
				UUID id = UUID.fromString("57369bdf-9479-40d0-8970-b8be56320fee");// UUID.randomUUID();
				Bukkit.broadcastMessage( "DEBUG: uuid= " + id.getMostSignificantBits() + "," + id.getLeastSignificantBits() );
				LoreMeta.setData( test, new MetaKeyUUID("bar"), id );
				UUID id2 = LoreMeta.getData( test, new MetaKeyUUID("bar") );
				Bukkit.broadcastMessage( "DEBUG: uuid= " + id2.getMostSignificantBits() + "," + id2.getLeastSignificantBits() );
				
				if( !id2.equals(id) ) {
					throw new Exception( "6" );
				}
				
				
				if( LoreMeta.getData( test, new MetaKeyLong("bar") ) != 37L ) {
					throw new Exception( "7" );
				}
				if( LoreMeta.getData( test, new MetaKeyInt("bar") ) != 1 ) {
					throw new Exception( "8" );
				}
				if( LoreMeta.getData( test, new MetaKeyShort("bar") ) != -450 ) {
					throw new Exception( "9" );
				}
				if( LoreMeta.getData( test, new MetaKeyByte("bar") ) != -128 ) {
					throw new Exception( "10" );
				}
				
				LoreMeta.setData( test, new MetaKeyText("bar"), "testes" );
				if( !LoreMeta.getData( test, new MetaKeyText("bar") ).equals("testes") ) {
					throw new Exception( "11" );
				}
				
				List<String> lore = test.getItemMeta().getLore();
				Bukkit.broadcastMessage( "lore result:" );
				for( String string : lore ) {
					Bukkit.broadcastMessage( " -- " );
					StringBuilder text = new StringBuilder();
					for( int i = 0; i < string.length(); i++ ) {
						if( string.charAt(i) == ChatColor.COLOR_CHAR ) {
							text.append( "#" );
							
						} else {
							if( (int)string.charAt(i) >= 0x100 && (int)string.charAt(i) < 0x200 ) {
								text.append( "{" + ((int)string.charAt(i)-0x100) + "}" );								
							} else if( (int)string.charAt(i) >= 0x200 ) {
								text.append( "\n" + String.format( "[%x]", (int)string.charAt(i) ) );			
								
							} else {
								text.append( "c=" + string.charAt(i) );
							}
						}
					}
					Bukkit.broadcastMessage( "  " + text );
				}
				
				ArrayList<String> newLore = new ArrayList<String>();
				newLore.add( "Legendary SWORD" );
				newLore.add( "" );
				newLore.add( "##TestField: 1" );
				newLore.add( "##TestField2: 2" );
				newLore.add( "@@[I:poop:5]" );
				ItemMeta meta = test.getItemMeta();
				meta.setLore(newLore);
				test.setItemMeta(meta);
				LoreMeta.initialize( test );
				
				if( LoreMeta.getData( test, new MetaKeyInt("poop") ) != 5 ) {
					throw new Exception( "12" );
				}

				if( Integer.parseInt( LoreMeta.getField( test, "TestField" ) ) != 1 ) {
					throw new Exception( "13" );
				}
				
				LoreMeta.setField( test, "TestField", "-50" );
				
				if( Integer.parseInt( LoreMeta.getField( test, "TestField2" ) ) != 2 ) {
					throw new Exception( "14" );
				}
				LoreMeta.setField( test, "TestField2", "Horse" );
				

				if( Integer.parseInt( LoreMeta.getField( test, "TestField" ) ) != -50 ) {
					throw new Exception( "15" );
				}
				if( !LoreMeta.getField( test, "TestField2" ).equals("Horse") ) {
					throw new Exception( "16" );
				}
				
				Bukkit.broadcastMessage( ChatColor.GREEN + "Tests passed!" );
				
			} catch ( Exception e ) {
				Bukkit.broadcastMessage( "failed: " + e.getMessage() );
			}
			
			
			
			return true;
		}*/
		return false;
	}
}
