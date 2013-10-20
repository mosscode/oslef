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
package org.oslef.format;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.oslef.csv.table.Row;
import org.oslef.csv.table.TableIterator;

/**
 * Provides a handy way to iterate over OSLEF records individually without
 * needing to hard-code the file names or directly refer to CSV libraries.
 */
public final class Inspector {
	
	public static final String
		LOANS_FILE = "loans.csv",
		INSTALLMENTS_FILE = "installments.csv",
		HISTORY_FILE = "history.csv";
	
	private final File oslefDir;

	public Inspector(File oslefDir) throws IOException {
		if (oslefDir == null) throw new NullPointerException();
		this.oslefDir = oslefDir;
	}
	
	public RecordIterator records(String fileName) throws IOException {
		File f = new File(oslefDir, fileName);
		TableIterator itr = new TableIterator(f);
		return new RecordIterator(itr);
	}
	
	public static final class RecordIterator implements Iterator<Record>, Iterable<Record> {
		
		private final TableIterator itr;
		
		public RecordIterator(TableIterator itr) {
			super();
			this.itr = itr;
		}

		public boolean hasNext() {
			return itr.hasNext();
		}
		
		public Record next() {
			Row row = itr.next();
			Record record = new MapRecord();
			for (String k : row.header()) {
				String v = row.get(k);
				if (StringUtil.nonEmpty(v)) record.put(k, v);
			}
			return record;
		}
		
		public void remove() {
			itr.remove();
		}
		
		public Iterator<Record> iterator() {
			return this;
		}
		
		public void close() throws IOException {
			itr.close();
		}
	}
}


