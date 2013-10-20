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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public final class Schema implements Constants {

	private boolean includeEmptyFields = false;
	
	private List<String> loan;
	private List<String> installments;
	private List<String> history;
	
	public Schema() {
		loan = new ArrayList<String>();
		installments = new ArrayList<String>();
		history = new ArrayList<String>();
	}
	
	public Schema setIncludeEmptyFields(boolean b) {
		this.includeEmptyFields = b;
		return this;
	}
	
	public void consider(Loan l) {
		
		for (String k : l.loan.keySet()) {
			add(k, l.loan.get(k), loan);
		}
		
		for (Record r : l.installments) {
			for (String k : r.keys()) {
				add(k, r.get(k), installments);
			}
		}
		
		for (Record r : l.history) {
			for (String k : r.keys()) {
				add(k, r.get(k), history);
			}
		}
		
		if (!installments.contains(LOAN_ID)) {
			installments.add(LOAN_ID);
		}
		
		if (!history.contains(LOAN_ID)) {
			history.add(LOAN_ID);
		}
	}
	
	public List<String> loan() {
		return loan;
	}
	
	public List<String> installmentPlan() {
		return installments;
	}
	
	public List<String> history() {
		return history;
	}
	
	public void sort() {
		sortLoan(loan);
		sortInstallment(installments);
		sortHistory(history);
	}
	
	private void add(String field, Object value, List<String> headerGroup) {
		if (nonEmpty(value) || includeEmptyFields) {
			addIfMissing(field, headerGroup);
		}
	}
	
	private void addIfMissing(String field, List<String> headerGroup) {
		if (!headerGroup.contains(field)) {
			headerGroup.add(field);
		}
	}
	
	private void sortLoan(List<String> columnNames) {

		final List<String> cols = new ArrayList<String>();
		cols.addAll(Arrays.asList(
			LOAN_ID,
			APP_CUSTOMER_ID,
			APP_SSN,
			APP_FIRST_NAME,
			APP_MIDDLE_INITIAL,
			APP_LAST_NAME,
			APP_DATE_OF_BIRTH,
			APP_DRIVERS_LICENSE_NUMBER,
			APP_EMAIL_ADDRESS,
			APP_PHONE_PREFIX,
			APP_RES_LINE_ONE_PREFIX,
			APP_RES_LINE_TWO_PREFIX,
			APP_RES_CITY_PREFIX,
			APP_RES_STATE_PREFIX,
			APP_RES_ZIP5_PREFIX,
			APP_RES_ZIP4_PREFIX,
			APP_REF_NAME_PREFIX,
			APP_REF_HOME_PHONE_PREFIX,
			APP_REF_WORK_PHONE_PREFIX,
			APP_INC_EMPLOYER_NAME_PREFIX,
			APP_INC_PAY_FREQUENCY_PREFIX,
			APP_INC_AMOUNT_PREFIX,
			APP_INC_NEXT_PAY_DATE_PREFIX,
			APP_INC_SHIFT_HOURS_PREFIX,
			APP_INC_MONTHS_OF_SERVICE_PREFIX,
			APP_INC_HAS_DIRECT_DEPOSIT_PREFIX,
			STRUCT_TYPE,
			STRUCT_WHEN_EXECUTED,
			STRUCT_LOAN_AMOUNT,
			STRUCT_FINANCED_FEE_PREFIX,
			TA_CHECK_BANK_NAME_PREFIX,
			TA_CHECK_ROUTING_NUMBER_PREFIX,
			TA_CHECK_ACCOUNT_NUMBER_PREFIX,
			TA_CHECK_NUMBER_PREFIX,
			TA_CHECK_AMOUNT_PREFIX,
			TA_CHECK_DATE_PREFIX,
			COL_VTITLE_YEAR_PREFIX,
			COL_VTITLE_COLOR_PREFIX,
			COL_VTITLE_MAKE_PREFIX,
			COL_VTITLE_MODEL_PREFIX,
			COL_VTITLE_LICENSE_NUMBER_PREFIX,
			COL_VTITLE_VIN_PREFIX,
			COL_VTITLE_CERT_NUMBER_PREFIX
		));
		
		Collections.sort(columnNames, new Comp(cols));
	}
	
	private void sortInstallment(List<String> columnNames) {

		final List<String> cols = new ArrayList<String>();
		cols.addAll(Arrays.asList(
			LOAN_ID,
			INST_DUE_DATE,
			INST_DUE_AMOUNT,
			INST_ACCOUNT_PREFIX
		));
		
		Collections.sort(columnNames, new Comp(cols));
	}
	
	private void sortHistory(List<String> columnNames) {

		final List<String> cols = new ArrayList<String>();
		cols.addAll(Arrays.asList(
			LOAN_ID,
			HIST_ACTION,
			HIST_WHEN_HAPPENED,
			HIST_AMOUNT,
			HIST_ACCOUNT_PREFIX
		));
		
		Collections.sort(columnNames, new Comp(cols));
	}
	
	private final class Comp implements Comparator<String> {
		
		final List<String> cols;
		
		Comp(List<String> cols) {
			this.cols = cols;
		}
		
		public int compare(String o1, String o2) {
			Integer i1 = findIndex(o1);
			Integer i2 = findIndex(o2);
			return i1.compareTo(i2);
		}
		
		Integer findIndex(String s) {
			for (String c : cols) {
				if (c.equals(s) || s.startsWith(c)) {
					return cols.indexOf(c);
				}
			}
			return cols.size();
		}
	}
	
	private static boolean isEmpty(Object o) {
		if (o == null) return true;
		else if (o instanceof String) return ((String)o).trim().length() == 0;
		else if (o instanceof BigDecimal) return ((BigDecimal)o).signum() == 0;
		else return false;
	}
	
	private static boolean nonEmpty(Object o) {
		return !isEmpty(o);
	}
}
