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
package org.oslef.tools;

import java.io.File;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.oslef.format.Constants;
import org.oslef.format.Inspector;
import org.oslef.format.MapRecord;
import org.oslef.format.Record;
import org.oslef.format.Validator;
import org.oslef.format.Inspector.RecordIterator;
import org.oslef.format.Validator.CollectingListener;
import org.oslef.tools.db.DbLoan;
import org.oslef.tools.db.OslefBdbDb;
import org.oslef.tools.db.OslefDb;
import org.oslef.tools.db.Scanner;


public final class OslefDbTool {
	
	private final OslefDb db;
	
	public OslefDbTool(OslefDb db) {
		if (db == null) throw new NullPointerException();
		this.db = db;
	}

	public void populate(File oslefDir) throws Exception {
		
		System.out.println("Reading Loans");
		
		Inspector ins = new Inspector(oslefDir);
		
		RecordIterator lItr = ins.records(Inspector.LOANS_FILE);
		while (lItr.hasNext()) {
			
			Record record = lItr.next();
			
			DbLoan s = new DbLoan();
			s.id = UUID.randomUUID().toString();
			
			for (String colName : record.keys()) {
				s.loan.put(colName, record.get(colName));
			}

			db.putNoOverwrite(s);
		}
		lItr.close();
		
		System.out.println("Reading Installments");
		
		RecordIterator iItr = ins.records(Inspector.INSTALLMENTS_FILE);
		while (iItr.hasNext()) {
			
			Record record = iItr.next();
			DbLoan s = db.getByLoanId(record.get(Constants.LOAN_ID));
			if (s == null) throw new RuntimeException("No loan record for installment: " + record.get(Constants.LOAN_ID));
			
			MapRecord i = new MapRecord();
			for (String colName : record.keys()) {
				if (Constants.LOAN_ID.equals(colName)) 
					continue; // skip redundant loan ids
				i.put(colName, record.get(colName));
			}
			s.installments.add(i);
			
			db.put(s);
		}
		iItr.close();
		
		System.out.println("Reading History");
		
		RecordIterator hItr = ins.records(Inspector.HISTORY_FILE);
		while (hItr.hasNext()) {
			
			Record record = hItr.next();
			DbLoan s = db.getByLoanId(record.get(Constants.LOAN_ID));
			if (s == null) throw new RuntimeException("No loan record for installment: " + record.get(Constants.LOAN_ID));
			
			MapRecord h = new MapRecord();
			for (String colName : record.keys()) {
				if (!Constants.LOAN_ID.equals(colName)) {
					h.put(colName, record.get(colName));
				}
			}
			s.history.add(h);
			
			db.put(s);
		}
		hItr.close();
		
		db.sync();
	}
	
	private static ObjectWriter w;
	{
		ObjectMapper m = new ObjectMapper();
		w = m.defaultPrettyPrintingWriter();
	}
	
	private static String stringify(Object o) {
		try {
			return w.writeValueAsString(o);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
	
	public static void main(String[] args) throws Exception {
		
		File userDir = new File(System.getProperty("user.home"));
		File oslefDir = new File(userDir, "Desktop/oslef-data");
		File dbDir = new File(userDir, "Desktop/oslef-db");
		boolean reset = true;
		
		if (reset && dbDir.exists() && !deleteDir(dbDir))
			throw new RuntimeException("Could not clean the slate: " + dbDir);
		
		OslefBdbDb db = new OslefBdbDb(dbDir);
		db.start();
		
		OslefDbTool tool = new OslefDbTool(db);
		if (reset) tool.populate(oslefDir);
		
		db.scan(new Scanner<DbLoan>() {
			final CollectingListener l = new CollectingListener();
			final Validator v = new Validator(l);
			public void inspect(DbLoan next) {
				l.clear();
				v.validate(next);
				if (l.problems.size() > 0) {
					System.out.println("-- Found " + l.problems.size() + " problems for loan " + next.id + " ---------------------");
					System.out.println(stringify(l.problems));
				}
			}
		});
		
		System.out.println("Building Fingerprint");
		
		FingerprintBuilder fb = new FingerprintBuilder();
		fb.build(db);
		System.out.println(fb.fp());
		
		db.stop();
	}
}
