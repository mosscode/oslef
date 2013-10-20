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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.oslef.format.Constants;
import org.oslef.format.Loan;
import org.oslef.format.MapRecord;
import org.oslef.format.Record;
import org.oslef.format.Validator;
import org.oslef.format.Validator.LexicalProblem;
import org.oslef.format.Validator.Problem;
import org.oslef.format.Validator.ProblemListener;
import org.oslef.format.Validator.ProblemLocation;


public final class TestValidator implements Constants {

	private final List<Problem> problems = new ArrayList<Problem>();
	private final Validator v;
	
	public TestValidator() {
		v = new Validator(new ProblemListener() {
			public void problemEncountered(Problem p) {
				problems.add(p);
			}
		});
	}
	
	@Before
	public void setup() {
		problems.clear();
	}
	
	@Test
	public void requiredFieldsMissing() {
		Loan l = new Loan();
		v.validate(l);
		assertProblems(3);
	}
	
	@Test
	public void requiredFieldsMet() {
		Loan l = new Loan();
		l.put(LOAN_ID, "1");
		l.put(STRUCT_LOAN_AMOUNT, "100.00");
		l.put(STRUCT_WHEN_EXECUTED, "2011-02-07T16:29:54.778Z");
		v.validate(l);
		assertProblems(0);
	}
	
	@Test
	public void allFieldsMet() {
		Loan l = new Loan()
		.put(LOAN_ID, "1")
		.put(APP_CUSTOMER_ID, "11")
		.put(APP_SSN, "234342344")
		.put(APP_FIRST_NAME, "First")
		.put(APP_MIDDLE_INITIAL, "Q")
		.put(APP_LAST_NAME, "Last")
		.put(APP_DATE_OF_BIRTH, "2010-02-01")
		.put(APP_DRIVERS_LICENSE_NUMBER, "TN12345")
		.put(APP_EMAIL_ADDRESS, "foo@bar.com")
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

		v.validate(l);
		assertProblems(0);
	}
	
	@Test
	public void fieldsInvalid() {
		
		Loan l = new Loan()
		.put(APP_SSN, "23434234")
		.put(APP_MIDDLE_INITIAL, "QR")
		.put(APP_DATE_OF_BIRTH, "2010-02-013")
		.put(APP_DRIVERS_LICENSE_NUMBER, "tr12345")
		.put(APP_EMAIL_ADDRESS, "foolbar.com")
		.put(APP_PHONE_PREFIX + "default", "23423423444")
		.put(APP_RES_STATE_PREFIX + "default", "tnnl")
		.put(APP_RES_ZIP5_PREFIX + "default", "3333")
		.put(APP_RES_ZIP4_PREFIX + "default", "23443")
		.put(APP_REF_HOME_PHONE_PREFIX + "default", "234&342344")
		.put(APP_REF_WORK_PHONE_PREFIX + "default", "12342342344")
		.put(APP_INC_PAY_FREQUENCY_PREFIX + "default", "NIGHTLY")
		.put(APP_INC_AMOUNT_PREFIX + "default", ".100")
		.put(APP_INC_NEXT_PAY_DATE_PREFIX + "default", "201-01-01")
		.put(APP_INC_MONTHS_OF_SERVICE_PREFIX + "default", "12$")
		.put(APP_INC_HAS_DIRECT_DEPOSIT_PREFIX + "default", "no")
		.put(STRUCT_WHEN_EXECUTED, "2011-02-07Q16:29:54.778Z")
		.put(STRUCT_LOAN_AMOUNT, "(100.00)")
		.put(STRUCT_FINANCED_FEE_PREFIX + "fee", "10,00")
		.put(TA_CHECK_ROUTING_NUMBER_PREFIX + "default", "012345678")
		.put(TA_CHECK_AMOUNT_PREFIX + "default", "110.00.")
		.put(TA_CHECK_DATE_PREFIX + "default", "2010-01-1234")
		;
		
		MapRecord i = new MapRecord();
		i.put(INST_DUE_DATE, "2011-13-01");
		i.put(INST_DUE_AMOUNT, "1,110.00");
		i.put(INST_ACCOUNT_PREFIX + "principal", "10.0.00");
		i.put(INST_ACCOUNT_PREFIX + "fee", "1#222");
		l.installments.add(i);
		
		MapRecord h = new MapRecord();
		h.put(HIST_ACTION, "DRAWN");
		h.put(HIST_WHEN_HAPPENED, "2011-13-07T16:29:54.788Z");
		h.put(HIST_AMOUNT, "1,000.00");
		h.put(HIST_ACCOUNT_PREFIX + "principal", "$10000");
		l.history.add(h);

		v.validate(l);
		
		for (String k : l.loan.keySet()) {
			LexicalProblem p = forLoan(k);
			Assert.assertNotNull("Expected to find a problem with loan field " + k, p);
		}
		
		for (Record r : l.installments) {
			for (String k : r.keys()) {
				int index = l.installments.indexOf(r);
				LexicalProblem p = forInst(index, k);
				Assert.assertNotNull("Expected to find a problem with installments[" + index + "]-> " + k + " '" + r.get(k) + "'", p);
			}
		}
		
		for (Record r : l.history) {
			for (String k : r.keys()) {
				int index = l.history.indexOf(r);
				LexicalProblem p = forHist(index, k);
				Assert.assertNotNull("Expected to find a problem with history[" + index + "] -> " + k + " '" + r.get(k) + "'", p);
			}
		}
		
//		print(problems);
	}
	
	private LexicalProblem forLoan(String fieldName) {
		return problemWith(ProblemLocation.LOAN, -1, fieldName);
	}
	
	private LexicalProblem forInst(int index, String fieldName) {
		return problemWith(ProblemLocation.INSTALLMENT, index, fieldName);
	}
	
	private LexicalProblem forHist(int index, String fieldName) {
		return problemWith(ProblemLocation.HISTORY, index, fieldName);
	}
	
	private LexicalProblem problemWith(ProblemLocation loc, int index, String fieldName) {
		for (Problem p : problems) {
			if (p instanceof LexicalProblem) {
				LexicalProblem lp = (LexicalProblem)p;
				if (loc == lp.location && index == lp.index && fieldName.equals(lp.fieldName)) {
					return lp;
				}
			}
		}
		return null;
	}
	
	private void assertProblems(int count) {
		Assert.assertTrue("Expected " + count + " problem(s), found " + problems.size() + ": " + stringify(problems), problems.size() == count);
	}
	
	private static ObjectWriter w;
	{
		ObjectMapper m = new ObjectMapper();
		w = m.defaultPrettyPrintingWriter();
	}
	
	private String stringify(Object o) {
		try {
			return w.writeValueAsString(problems);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private void print(Object o) {
		System.out.println(stringify(o));		
	}
}
