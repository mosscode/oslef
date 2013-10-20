/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of oslef.
 *
 * oslef is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * oslef is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with oslef; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.oslef.csv;

import java.util.LinkedList;
import java.util.List;

public final class RowParser {
	/**
	 * 
	 * @param line A line of CSV text.
	 * @param delimiter The delimiter character used to distinguish between fields
	 * @return null if the line was incomplete (i.e. if the last field continues past the end of the line).  Otherwise, returns a String array containing the value of each field.
	 */
	public static String[] parse(String line, char delimiter){
		boolean lineWasComplete;
		
		List<String> fields = new LinkedList<String>();
		
		StringBuffer field = new StringBuffer();
		char c;
		boolean inQuotes = false;
		boolean fieldOpen = true;
		for(int x=0;x<line.length();x++){
			c = line.charAt(x);
			if(c==delimiter && !inQuotes){
				fields.add(field.toString());
				field = new StringBuffer();
				fieldOpen=true;
			}else if(c=='"' && !inQuotes){
				inQuotes = true;
			}else if(c=='"'){
				if(x>0 && line.charAt(x-1)=='\\'){
					// this is an escaped quote
					field.append(c);
				}else{
					inQuotes = false;
				}
//				fieldOpen=false;
			}else{
				field.append(c);
			}
		}
		if(fieldOpen){
			fields.add(field.toString());
			if(inQuotes){
				lineWasComplete = false;
			}else{
				lineWasComplete = true;
			}
		}else{
			lineWasComplete = true;
		}
		if(lineWasComplete){
			return fields.toArray(new String[]{});
		}else{
			return null;
		}
	}
}
