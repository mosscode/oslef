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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.oslef.csv.table.Row;
import org.oslef.csv.table.Table;
import org.oslef.csv.table.TableIterator;

/**
 * Merges the oslef data in memory and iterates over it. Only useful
 * if you can fit all the supporting details for the OSLEF data you want
 * to read in memory.
 */
public final class Reader implements Iterator<Loan>, Iterable<Loan> {
	
	private final Table loansTable;
	private final TableIterator loansIterator;
	private final Table historyTable;
	private final Table installmentsTable;
	
	private Map<String, List<Row>> installmentRecords;
	private Map<String, List<Row>> historyRecords;
	
	public Reader(File oslefDir) throws IOException {
		if (oslefDir == null) throw new NullPointerException();
		loansTable = new Table(new File(oslefDir, "loans.csv"));
		loansIterator = loansTable.iterator();
		historyTable = new Table(new File(oslefDir, "history.csv"));
		installmentsTable = new Table(new File(oslefDir, "installments.csv"));
	}

	public boolean hasNext() {
		return loansIterator.hasNext();
	}
	
	public Loan next() {
		
		if (installmentRecords == null) {
			installmentRecords = new HashMap<String, List<Row>>();
			
			System.out.println("Loading installment records");
			
			List<Row> list;
			for (Row row : installmentsTable) {
				String loanId = row.get(Constants.LOAN_ID);
				list = installmentRecords.get(loanId);
				if (list == null) {
					list = new ArrayList<Row>();
					installmentRecords.put(loanId, list);
				}
				list.add(row);
			}
		}
		
		if (historyRecords == null) {
			historyRecords = new HashMap<String, List<Row>>();
			
			System.out.println("Loading history records");
			
			List<Row> list;
			for (Row row : historyTable) {
				String loanId = row.get(Constants.LOAN_ID);
				list = historyRecords.get(loanId);
				if (list == null) {
					list = new ArrayList<Row>();
					historyRecords.put(loanId, list);
				}
				list.add(row);
			}
		}
		
		Row loanRow = loansIterator.next();
		Loan l = new Loan();
		l.loan = new MapRecord();
		read(loanRow, l.loan);
		
		List<Row> iRecords = installmentRecords.get(l.loan.get(Constants.LOAN_ID));
		if (iRecords == null) iRecords = new ArrayList<Row>();
		for (Row row : iRecords) {
			MapRecord r = new MapRecord();
			read(row, r);
			l.installments.add(r);
		}
		
		List<Row> hRecords = historyRecords.get(l.loan.get(Constants.LOAN_ID));
		if (hRecords == null) hRecords = new ArrayList<Row>();
		for (Row row : hRecords) {
			MapRecord r = new MapRecord();
			read(row, r);
			l.history.add(r);
		}
		
		return l;
	}
	
	private static void read(Row row, Record record) {
		for (String k : row.header()) {
			String v = row.get(k);
			if (StringUtil.nonEmpty(v)) record.put(k, v);
		}
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public Iterator<Loan> iterator() {
		return this;
	}
	
	public void close() throws IOException {
		loansIterator.close();
	}
}
