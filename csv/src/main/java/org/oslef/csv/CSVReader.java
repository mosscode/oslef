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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;

public final class CSVReader {

	private char delimiter = ',';
	
	private BufferedReader in;
	private String currentLine;
	
	public CSVReader(File file) throws IOException {
		in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	}
	
	public CSVReader(URL url) throws IOException {
		in = new BufferedReader(new InputStreamReader(url.openStream()));
	}
	
	public CSVReader(InputStream in) {
		this.in = new BufferedReader(new InputStreamReader(in));
	}
	
	public CSVReader(Reader in) {
		this.in = new BufferedReader(in);
	}
	
	public boolean hasNext() throws IOException {
		return in.ready();
	}
	
	public String[] next() throws IOException {
		currentLine = null;
		String[] values;
		do{
			if (!hasNext()){throw new IOException("End of file");}
			currentLine = currentLine==null?in.readLine():currentLine + '\n' + in.readLine();
			values = RowParser.parse(currentLine, delimiter);
		}while(values==null);

		columns = values;

		return columns;
	}
	
	public String getCurrentLine() {
		return currentLine;
	}
	
	private String[] columns;
	
	public String[] getColumns() {
		return columns;
	}
	
	public String getStringValue(int column) {
		String s = getColumns()[column];
		return s;
	}
	
	public Boolean getBooleanValue(int column) {
		String value = getStringValue(column);
		if (value == null || value.equals("N") || value.equalsIgnoreCase("false")) {
			return Boolean.FALSE;
		}
		else {
			return Boolean.TRUE;
		}
	}
	
	public BigDecimal getBigDecimalValue(int column) {
		String value = getStringValue(column);
		if (value != null) {
			return new BigDecimal(value);
		}
		else {
			return null;
		}
	}
	
	public Long getLongValue(int column) {
		String value = getStringValue(column);
		
		if (value != null) {
			return new Long(value);
		}
		else {
			return null;
		}
	}
	
	public void close() throws IOException {
		if (in != null) in.close();
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}
}
