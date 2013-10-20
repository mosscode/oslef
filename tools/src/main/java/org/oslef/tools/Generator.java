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
import java.io.IOException;

import org.oslef.format.Constants;
import org.oslef.format.Loan;
import org.oslef.format.MapRecord;
import org.oslef.format.Schema;
import org.oslef.format.Writer;


public final class Generator implements Constants {
	
	private final File oslefDir; 
	private final RandomNamer namer;
	private final Counter loanIds, custIds, ssns;
	
	public Generator(File oslefDir) {
		if (oslefDir == null) throw new NullPointerException();
		this.oslefDir = oslefDir;
		try {
			namer = new RandomNamer();
		}
		catch (Exception ex) {
			throw new RuntimeException("Failed to init random names", ex);
		}
		loanIds = new Counter();
		custIds = new Counter();
		ssns = new Counter(0l, 9);
	}

	public void generate(int loanCount) throws IOException {
		
		Schema s = new Schema();
		s.consider(randomLoan());
		
		Writer w = new Writer(s, oslefDir);
		for (int i=0; i<loanCount; i++) {
			w.write(randomLoan());
		}
		w.close();
	}
	
	private Loan randomLoan() {
		
		Loan l = new Loan()
		.put(LOAN_ID, loanIds.next())
		.put(APP_CUSTOMER_ID, custIds.next())
		.put(APP_SSN, ssns.next())
		.put(APP_FIRST_NAME, "f")
		.put(APP_MIDDLE_INITIAL, "Q")
		.put(APP_LAST_NAME, "f")
		.put(APP_DATE_OF_BIRTH, "2010-02-01")
		.put(APP_DRIVERS_LICENSE_NUMBER, "TN12345")
		.put(APP_EMAIL_ADDRESS, "foo@domain.com")
		.put(APP_PHONE_PREFIX + "default", "2342342342")
		.put(APP_RES_LINE_ONE_PREFIX + "default", "lineone")
		.put(APP_RES_LINE_TWO_PREFIX + "default", "linetwo")
		.put(APP_RES_CITY_PREFIX + "default", "city")
		.put(APP_RES_STATE_PREFIX + "default", "TN")
		.put(APP_RES_ZIP5_PREFIX + "default", "23423")
		.put(APP_RES_ZIP4_PREFIX + "default", "2344")
		.put(APP_REF_NAME_PREFIX + "default", "Pudge")
		.put(APP_REF_HOME_PHONE_PREFIX + "default", "2342342344")
		.put(APP_REF_WORK_PHONE_PREFIX + "default", "2342342344")
		.put(APP_INC_EMPLOYER_NAME_PREFIX + "default", "WerkU2Deth")
		.put(APP_INC_PAY_FREQUENCY_PREFIX + "default", "WEEKLY")
		.put(APP_INC_AMOUNT_PREFIX + "default", "100")
		.put(APP_INC_NEXT_PAY_DATE_PREFIX + "default", "2010-01-01")
		.put(APP_INC_SHIFT_HOURS_PREFIX + "default", "always")
		.put(APP_INC_MONTHS_OF_SERVICE_PREFIX + "default", "12")
		.put(APP_INC_HAS_DIRECT_DEPOSIT_PREFIX + "default", "true")
		.put(STRUCT_TYPE, "funny")
		.put(STRUCT_WHEN_EXECUTED, "2011-02-07T16:29:54.778Z")
		.put(STRUCT_LOAN_AMOUNT, "100.00")
		.put(STRUCT_FINANCED_FEE_PREFIX + "fee", "10.00")
		.put(TA_CHECK_BANK_NAME_PREFIX + "default", "funbankz")
		.put(TA_CHECK_ROUTING_NUMBER_PREFIX + "default", "262084356")
		.put(TA_CHECK_ACCOUNT_NUMBER_PREFIX + "default", "01234")
		.put(TA_CHECK_NUMBER_PREFIX + "default", "346")
		.put(TA_CHECK_AMOUNT_PREFIX + "default", "110.00")
		.put(TA_CHECK_DATE_PREFIX + "default", "2010-01-12")
		;
		
		MapRecord i = new MapRecord();
		i.put(INST_DUE_DATE, "2011-01-01");
		i.put(INST_DUE_AMOUNT, "110.00");
		i.put(INST_ACCOUNT_PREFIX + "principal", "100.00");
		i.put(INST_ACCOUNT_PREFIX + "fee", "10.00");
		l.installments.add(i);
		
		MapRecord h = new MapRecord();
		h.put(HIST_ACTION, "DRAW");
		h.put(HIST_WHEN_HAPPENED, "2011-02-07T16:29:54.778Z");
		h.put(HIST_AMOUNT, "100.00");
		h.put(HIST_ACCOUNT_PREFIX + "principal", "100.00");
		l.history.add(h);
		
		return l;
	}
}

final class Counter {
	
	private long next;
	private int leftPad;
	
	Counter() {
		this(0l, 0);
	}
	
	Counter(long next, int leftPad) {
		if (next < 0) throw new IllegalArgumentException();
		if (leftPad < 0) throw new IllegalArgumentException();
		this.next = next;
		this.leftPad = leftPad;
	}
	
	String next() {
		String s = leftPad(Long.toString(next), '0', leftPad);
		next++;
		return s;
	}

	private String leftPad(String s, char c, int length) {
		
		int charsToAdd = length - s.length();
		
		if (charsToAdd < 1) {
			return s;
		}
		
		for (int i=0; i<charsToAdd; i++) {
			s = c + s;
		}
		
		return s;
	}
}

final class RandomNamer {
	
}
