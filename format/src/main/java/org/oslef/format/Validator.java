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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.oslef.format.Validator.LexicalProblem;
import org.oslef.format.Validator.ProblemListener;
import org.oslef.format.Validator.ProblemLocation;
import org.oslef.format.Validator.Severity;


public final class Validator implements Constants {
	
	public static enum Severity {
		ERROR,
		WARNING
	}
	
	public static class Problem {

		public Severity severity;
		public String description;
		
		public Problem() {}

		public Problem(Severity severity, String description) {
			this.severity = severity;
			this.description = description;
		}
	}
	
	public static enum ProblemLocation {
		LOAN,
		INSTALLMENT,
		HISTORY
	}
	
	public static class LexicalProblem extends Problem {
		
		/**
		 * Indicates which record contains the problem field.
		 */
		public ProblemLocation location;

		/**
		 * For problems with the loan record, this is -1. For problems relating
		 * to installments or history it is >= 0.
		 */
		public int index;
		
		/**
		 * Names the specific field that is in question.
		 */
		public String fieldName;

		public LexicalProblem() {}

		public LexicalProblem(Severity severity, String description) {
			super(severity, description);
		}
	}
	
	public static interface ProblemListener {
		void problemEncountered(Problem p);
	}
	
	public static final class CollectingListener implements ProblemListener {
		public final List<Problem> problems = new ArrayList<Problem>();
		public CollectingListener clear() {
			problems.clear();
			return this;
		}
		public void problemEncountered(Problem p) {
			problems.add(p);
		}
	}
	
	private final RecordValidator lv, iv, hv;
	
	public Validator(ProblemListener l) {

		if (l == null) throw new NullPointerException();
		
		final String statesRegex = 
			"AK|AL|AR|AZ|CA|CO|CT|DC|DE|FG|" +
			"FL|GA|HI|IA|ID|IL|IN|KS|KY|LA|" +
			"MA|MD|ME|MI|MN|MO|MS|MT|NC|ND|" +
			"NE|NH|NJ|NM|NV|NY|OH|OK|OR|PA|" +
			"RI|SC|SD|TN|TX|UT|VA|VT|WA|WI|" +
			"WV|WY";
		
		final Pattern phoneRegex = Pattern.compile("[0-9]{10}");
		final Pattern intRegex = Pattern.compile("[0-9]+");
		final Pattern amountRegex = Pattern.compile("(-)?([0-9]+)(\\.[0-9]+)?");
		
		final YMDValidator ymdValidator = new YMDValidator();
		final InstantValidator instantValidator = new InstantValidator();
	
		lv = new RecordValidator(l)
		.req(LOAN_ID)
		.op(APP_SSN, "[0-9]{9}")
		.op(APP_FIRST_NAME)
		.op(APP_MIDDLE_INITIAL, ".?")
		.op(APP_LAST_NAME)
		.op(APP_DATE_OF_BIRTH, ymdValidator)
		.op(APP_DRIVERS_LICENSE_NUMBER, Pattern.compile("(" + statesRegex + ")([a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE))
		.op(APP_EMAIL_ADDRESS, ".+@.+")
		.op(APP_HEIGHT_INCHES, intRegex)
		.op(APP_GENDER, Pattern.compile("MALE|FEMALE", Pattern.CASE_INSENSITIVE))
		.op(APP_PHONE_PREFIX, phoneRegex)
		.op(APP_RES_STATE_PREFIX, Pattern.compile(statesRegex, Pattern.CASE_INSENSITIVE))
		.op(APP_RES_ZIP5_PREFIX, "[0-9]{5}")
		.op(APP_RES_ZIP4_PREFIX, "[0-9]{4}")
		.op(APP_REF_HOME_PHONE_PREFIX, phoneRegex)
		.op(APP_REF_WORK_PHONE_PREFIX, phoneRegex)
		.op(APP_INC_PAY_FREQUENCY_PREFIX, Pattern.compile("WEEKLY|BIWEEKLY|SEMIMONTHLY|MONTHLY", Pattern.CASE_INSENSITIVE))
		.op(APP_INC_AMOUNT_PREFIX, amountRegex)
		.op(APP_INC_NEXT_PAY_DATE_PREFIX, "[0-9]{4}-[0-9]{2}-[0-9]{2}")
		.op(APP_INC_SHIFT_HOURS_PREFIX)
		.op(APP_INC_MONTHS_OF_SERVICE_PREFIX, intRegex)
		.op(APP_INC_HAS_DIRECT_DEPOSIT_PREFIX, "true|false")
		.op(STRUCT_TYPE)
		.req(STRUCT_WHEN_EXECUTED, instantValidator)
		.req(STRUCT_LOAN_AMOUNT, amountRegex)
		.op(STRUCT_DISCLOSED_APR, amountRegex)
		.op(STRUCT_INTEREST_RATE, amountRegex)
		.op(STRUCT_FINANCED_FEE_PREFIX, amountRegex)
		.op(TA_CHECK_BANK_NAME_PREFIX)
		.op(TA_CHECK_ROUTING_NUMBER_PREFIX, new RoutingNumberValidator())
		.op(TA_CHECK_ACCOUNT_NUMBER_PREFIX, intRegex)
		.op(TA_CHECK_NUMBER_PREFIX, intRegex)
		.op(TA_CHECK_AMOUNT_PREFIX, amountRegex)
		.op(TA_CHECK_DATE_PREFIX, ymdValidator)
		.op(COL_VTITLE_YEAR_PREFIX, ymdValidator)
		;
		
		iv = new RecordValidator(l)
		.req(INST_DUE_DATE, ymdValidator)
		.req(INST_DUE_AMOUNT, amountRegex)
		.op(INST_ACCOUNT_PREFIX, amountRegex)
		;
	
		hv = new RecordValidator(l)
		.req(HIST_ACTION, Pattern.compile("DRAW|DEPOSIT|BOUNCE|PAYMENT|ACCRUAL|EXEMPTION|INTEREST_HALT|INTEREST_RESUME|PAYMENT_DEADLINE|CHARGE|WRITE_OFF|EXTENDED_PAYMENT_PLAN_EXECUTION", Pattern.CASE_INSENSITIVE))
		.req(HIST_WHEN_HAPPENED, instantValidator)
		.op(HIST_WHEN_VOIDED, instantValidator)
		.req(HIST_AMOUNT, amountRegex)
		.op(HIST_ACCOUNT_PREFIX, amountRegex);
	}

	public void validate(Loan loan) {
		
		/*
		 * Lexical Validation
		 */
		
		lv.validate(loan.loan, ProblemLocation.LOAN, -1);
		for (Record r : loan.installments) 
			iv.validate(r, ProblemLocation.INSTALLMENT, loan.installments.indexOf(r));
		for (Record r : loan.history) 
			hv.validate(r, ProblemLocation.HISTORY, loan.history.indexOf(r));
	}
}

final class FieldReqirements {
	boolean optional = false;
	FieldValidator validator;
}

final class RecordValidator {
	
	final ProblemListener l;
	final Map<String, FieldReqirements> reqs = new HashMap<String, FieldReqirements>();
	
	RecordValidator(ProblemListener l) {
		if (l == null) throw new NullPointerException();
		this.l = l;
	}
	
	RecordValidator req(String k) {
		return add(k, false, (FieldValidator)null);
	}

	RecordValidator req(String k, String regex) {
		return add(k, false, new RegexValidator(Pattern.compile(regex), null));
	}
	
	RecordValidator req(String k, Pattern p) {
		return add(k, false, new RegexValidator(p, null));
	}
	
	RecordValidator req(String k, String regex, String explanation) {
		return add(k, false, new RegexValidator(Pattern.compile(regex), explanation));
	}
	
	RecordValidator req(String k, Pattern p, String explanation) {
		return add(k, false, new RegexValidator(p, explanation));
	}
	
	RecordValidator req(String k, FieldValidator v) {
		return add(k, false, v);
	}
	
	RecordValidator op(String k) { // not much point to this one
		return add(k, true, (FieldValidator)null);
	}
	
	RecordValidator op(String k, String regex) {
		return add(k, true, new RegexValidator(Pattern.compile(regex), null));
	}
	
	RecordValidator op(String k, Pattern p) {
		return add(k, true, new RegexValidator(p, null));
	}
	
	RecordValidator op(String k, String regex, String explanation) {
		return add(k, true, new RegexValidator(Pattern.compile(regex), explanation));
	}
	
	RecordValidator op(String k, Pattern p, String explanation) {
		return add(k, true, new RegexValidator(p, explanation));
	}
	
	RecordValidator op(String k, FieldValidator v) {
		return add(k, true, v);
	}
	
	RecordValidator add(String k, boolean optional, String regex) {
		return add(k, optional, new RegexValidator(Pattern.compile(regex), null));
	}
	
	RecordValidator add(String k, boolean optional, Pattern p) {
		return add(k, optional, new RegexValidator(p, null));
	}
	
	RecordValidator add(String k, boolean optional, String regex, String explanation) {
		return add(k, optional, new RegexValidator(Pattern.compile(regex), explanation));
	}
	
	RecordValidator add(String k, boolean optional, Pattern p, String explanation) {
		return add(k, optional, new RegexValidator(p, explanation));
	}
	
	RecordValidator add(String k, boolean optional, FieldValidator v) {
		checkDup(k);
		FieldReqirements fq = new FieldReqirements();
		fq.optional = optional;
		fq.validator = v;
		reqs.put(k, fq);
		return this;
	}
	
	RecordValidator validate(Record r, ProblemLocation loc, int index) {
		
		for (String k : reqs.keySet()) {
			FieldReqirements fq = reqs.get(k);
			if (!fq.optional && !r.has(k)) {
				LexicalProblem p = new LexicalProblem(Severity.ERROR, "Field '" + k + "' not found, field is required");
				p.location = loc;
				p.index = index;
				p.fieldName = k;
				l.problemEncountered(p);
			}
		}
		
		for (String fieldName : r.keys()) {
			
			String k;
			int prefixEnd = fieldName.indexOf(":");
			if (prefixEnd > -1) {
				k = fieldName.substring(0, prefixEnd + 1);
			}
			else k = fieldName;
			
			FieldReqirements fq = reqs.get(k);
			
			if (fq != null) {
				
				String v = r.get(fieldName);
				
				boolean empty = StringUtil.isEmpty(v);
				
				if (empty) {
					if (!fq.optional) {
						LexicalProblem p = new LexicalProblem(Severity.ERROR, "Field '" + fieldName + "' cannot be empty, field is required");
						p.location = loc;
						p.index = index;
						p.fieldName = fieldName;
						l.problemEncountered(p);
					}
					else; // ignore it
				}
				else if (fq.validator != null) {
					try {
						List<String> result = fq.validator.validate(v);
						if (result != null) {
							for (String warning : result) {
								LexicalProblem p = new LexicalProblem(Severity.WARNING, "Value '" + v + "' for field '" + fieldName + "': " + warning);
								p.location = loc;
								p.index = index;
								p.fieldName = fieldName;
								l.problemEncountered(p);
							}
						}
					}
					catch (InvalidFieldError err) {
						LexicalProblem p = new LexicalProblem(Severity.ERROR, "Value '" + v + "' for field '" + fieldName + "' is not valid: " + err.getMessage());
						p.location = loc;
						p.index = index;
						p.fieldName = fieldName;
						l.problemEncountered(p);
					}
				}
			}
		}
		
		return this;
	}
	
	void checkDup(String k) {
		if (reqs.containsKey(k)) throw new RuntimeException("Duplicate requirement: " + k);
	}
}

final class InvalidFieldError extends RuntimeException {
	InvalidFieldError(String message) {
		super(message);
	}
}

interface FieldValidator {
	List<String> validate(String s) throws InvalidFieldError;
}

final class YMDValidator implements FieldValidator {
	
	private final Pattern p = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})");
	
	public List<String> validate(String s) {
		Matcher m = p.matcher(s);
		if (!m.matches()) throw new InvalidFieldError("A year-month-day must match the regex '" + p + "'");
		
		int year = Integer.parseInt(m.group(1));
		int month = Integer.parseInt(m.group(2));
		int day = Integer.parseInt(m.group(3));
		
		if (month < 1 || month > 12) throw new InvalidFieldError("Not a valid month");
		if (day < 1 || month > 31) throw new InvalidFieldError("Not a valid day");
		
		List<String> msg = new ArrayList<String>();
		if (year < 1900) msg.add("Year < 1900");
		return msg;
	}
}

final class InstantValidator implements FieldValidator {
	
	private final Pattern p = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2})\\:([0-9]{2})\\:([0-9]{2})\\.([0-9]{3})Z");
	
	public List<String> validate(String s) {
		Matcher m = p.matcher(s);
		if (!m.matches()) throw new InvalidFieldError("A year-month-day must match the regex '" + p + "'");
		
		int year = Integer.parseInt(m.group(1));
		int month = Integer.parseInt(m.group(2));
		int day = Integer.parseInt(m.group(3));
		int hours = Integer.parseInt(m.group(4));
		int minutes = Integer.parseInt(m.group(5));
		int seconds = Integer.parseInt(m.group(6));
		int millis = Integer.parseInt(m.group(7));
		
		if (month < 1 || month > 12) throw new InvalidFieldError("Not a valid month");
		if (day < 1 || month > 31) throw new InvalidFieldError("Not a valid day");
		if (hours > 23) throw new InvalidFieldError("Not a valid hour");
		if (minutes > 59) throw new InvalidFieldError("Not a valid minute");
		if (seconds > 59) throw new InvalidFieldError("Not a valid second");
		if (millis > 999) throw new InvalidFieldError("Not a valid millis"); // not going to happen since the regex prevents this
		
		List<String> msg = new ArrayList<String>();
		if (year < 1900) msg.add("Year < 1900");
		return msg;
	}
}

final class RegexValidator implements FieldValidator {
	
	private final Pattern p;
	private final String explanation;
	
	RegexValidator(Pattern p, String explanation) {
		if (p == null) throw new NullPointerException();
		this.p = p;
		this.explanation = explanation;
	}

	public List<String> validate(String s) {
		if (!p.matcher(s).matches()) {
			if (explanation != null) throw new InvalidFieldError(explanation);
			else throw new InvalidFieldError("Must match regex '" + p + "'");
		}
		return null;
	}
}

final class RoutingNumberValidator implements FieldValidator {
	
	private final Pattern p;
	
	public RoutingNumberValidator() {
		p = Pattern.compile("[0-9]{9}");
	}
	
	public List<String> validate(String s) {
		if (!p.matcher(s).matches()) throw new InvalidFieldError("A bank routing number must match the following regex: " + p);
		
		int[] digits = new int[s.length()];
		for (int i=0; i<s.length(); i++) {
			digits[i] = Integer.parseInt(s.substring(i, i + 1));
		}
		
		int check = checksumAlgorithm(digits);
		if (check != digits[8]) throw new InvalidFieldError("Checksum is incorrect (is " + digits[8] + ", should be " + check + "?)");
		
		return null;
	}
	
	private static int checksumAlgorithm(int[] digits){
		
		if (digits.length < 8) {
			throw new IllegalArgumentException("cannot calculate a check digit with less than 8 input digits");
		}
		
		int sum = 0;
		
		sum += digits[0] * 3;
		sum += digits[1] * 7;
		sum += digits[2] * 1;
		sum += digits[3] * 3;
		sum += digits[4] * 7;
		sum += digits[5] * 1;
		sum += digits[6] * 3;
		sum += digits[7] * 7;
		
		int nextHighestMultipleOf10 = sum;
		
		while (nextHighestMultipleOf10 % 10 != 0) {
			nextHighestMultipleOf10++;
		}
		
		int checkDigit = nextHighestMultipleOf10 - sum;
		
		return checkDigit;
	}
}



