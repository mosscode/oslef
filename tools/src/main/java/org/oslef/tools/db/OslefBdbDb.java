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
package org.oslef.tools.db;

import java.io.File;

import org.codehaus.jackson.map.ObjectMapper;
import org.oslef.format.Constants;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

public final class OslefBdbDb implements OslefDb {
	
	private File envDir;
	private Environment env;
	private Database loans;
	private SecondaryDatabase loansByLoanId;
	
	private final ObjectMapper m = new ObjectMapper();
	
	public OslefBdbDb(File envDir) {
		if (envDir == null) throw new NullPointerException();
		this.envDir = envDir;
	}

	public void start() {
		
		if (!envDir.exists() && !envDir.mkdirs())
			throw new RuntimeException("Cannot create dir: " + envDir);
		
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setLocking(false);
		envConfig.setTransactional(false);
		env = new Environment(envDir, envConfig);
		
		DatabaseConfig loansConfig = new DatabaseConfig();
		loansConfig.setAllowCreate(true);
		loansConfig.setTransactional(false);
		loansConfig.setSortedDuplicates(false);
		loansConfig.setDeferredWrite(true);
		loans = env.openDatabase(null, "loans", loansConfig);
		
		SecondaryConfig byIdConfig = new SecondaryConfig();
		byIdConfig.setAllowCreate(true);
		byIdConfig.setTransactional(false);
		byIdConfig.setSortedDuplicates(true);
		byIdConfig.setDeferredWrite(true);
		byIdConfig.setAllowPopulate(true);
		byIdConfig.setImmutableSecondaryKey(false);
		byIdConfig.setKeyCreator(new SecondaryKeyCreator() {
			public boolean createSecondaryKey(SecondaryDatabase secondary, DatabaseEntry key, DatabaseEntry data, DatabaseEntry result) {
				DbLoan l = deserializeObject(DbLoan.class, data.getData());
				String loanId = l.loan.get(Constants.LOAN_ID);
				if (loanId == null) return false;
				else {
					try {
						result.setData(loanId.getBytes("UTF8"));
					}
					catch (Exception ex) {
						throw new RuntimeException(ex);
					}
					return true;
				}
			}
		});
		
		loansByLoanId = env.openSecondaryDatabase(null, "loans-by-loan-id", loans, byIdConfig);
	}
	
	public void sync() {
		env.sync();
	}
	
	public void stop() {
		loansByLoanId.close();
		loans.close();
		env.close();
	}
	
	private byte[] serializeKey(String s) {
		try {
			return s.getBytes("UTF8");
		}
		catch (Exception ex) {
			throw new RuntimeException();
		}
	}
	
	private String deserializeKey(byte[] bytes) {
		try {
			return new String(bytes, "UTF8");
		}
		catch (Exception ex) {
			throw new RuntimeException();
		}
	}
	
	private byte[] serializeObject(Object obj) {
		try {
			return m.writeValueAsBytes(obj);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private <T> T deserializeObject(Class<T> type, byte[] data) {
		try {
			return m.readValue(data, 0, data.length, type);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public DbLoan get(String id) {
		DatabaseEntry k = new DatabaseEntry(serializeKey(id));
		DatabaseEntry v = new DatabaseEntry();
		OperationStatus s = loans.get(null, k, v, null);
		if (s != OperationStatus.SUCCESS) return null;
		else return deserializeObject(DbLoan.class, v.getData());
	}
	
	public DbLoan getByLoanId(String id) {
		DatabaseEntry k = new DatabaseEntry(serializeKey(id));
		DatabaseEntry v = new DatabaseEntry();
		OperationStatus s = loansByLoanId.get(null, k, v, null);
		if (s != OperationStatus.SUCCESS) return null;
		else return deserializeObject(DbLoan.class, v.getData());
	}
	
	public void put(DbLoan loan) {
		DatabaseEntry k = new DatabaseEntry(serializeKey(loan.id));
		DatabaseEntry v = new DatabaseEntry(serializeObject(loan));
		loans.put(null, k, v);
	}
	
	public void putNoOverwrite(DbLoan loan) {
		DatabaseEntry k = new DatabaseEntry(serializeKey(loan.id));
		DatabaseEntry v = new DatabaseEntry(serializeObject(loan));
		OperationStatus s = loans.putNoOverwrite(null, k, v);
		if (s == OperationStatus.KEYEXIST) throw new RuntimeException("Key is not unique: " + loan.id);
	}
	
	public void delete(String id) {
		DatabaseEntry k = new DatabaseEntry(serializeKey(id));
		loans.delete(null, k);
	}
	
	public void scan(Scanner<DbLoan> scanner) {
		
		CursorConfig cursorConfig = new CursorConfig();
		cursorConfig.setReadUncommitted(true);
		
		Cursor loopCursor = loans.openCursor(null, cursorConfig);
		try {
			DatabaseEntry key = new DatabaseEntry();
			DatabaseEntry data = new DatabaseEntry();
			
			while (OperationStatus.SUCCESS == loopCursor.getNext(key, data, null)) {
				
				DbLoan loan = deserializeObject(DbLoan.class, data.getData());
				scanner.inspect(loan);
			}
		}
		finally {
			if (loopCursor != null) {
				loopCursor.close();
			}
		}
	}
}
