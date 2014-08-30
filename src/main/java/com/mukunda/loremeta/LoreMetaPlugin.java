package com.mukunda.loremeta;

import org.bukkit.plugin.java.JavaPlugin;

public class LoreMetaPlugin extends JavaPlugin {
	
	// lore sections
	//
	// first line contains lore data
	
	// <item name>
	// <lore meta><blank line>
	// 
	
	// line markers:
	// first line is loaded with FastMETA {META}{METATAG}<DATA>...
	// {TOPSECTION} - {ENTRY}
	// <space>
	// {MIDSECTION} {ID}
	// <space>
	// {LORE} lore...
	// <space>
	// {POSTSECTION} {ID}
	
	// META TYPES:
	// Byte  1 byte of data [MI:A:testes]
	// Short 2 bytes of data
	// Int   4 bytes of data
	// Long  8 bytes of data
	// Quad  16 bytes of data
	// Ascii, variable length with terminator, should avoid using unicode chars for compat reasons.
	// 
	
	// data entries have a magic code
	// to read a data entry
}
