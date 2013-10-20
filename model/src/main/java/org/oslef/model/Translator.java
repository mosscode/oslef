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
package org.oslef.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oslef.format.Constants;
import org.oslef.format.Loan;
import org.oslef.format.MapRecord;
import org.oslef.format.Record;
import org.oslef.model.Application.Gender;
import org.oslef.model.Application.Income;
import org.oslef.model.Application.PayFrequency;
import org.oslef.model.Application.Reference;
import org.oslef.model.Application.UsaAddress;
import org.oslef.model.History.HistoryAction;


public final class Translator implements Constants {
	
	public static LoanModel parse(Loan source) {
		return parse(source, null);
	}
	
	public static LoanModel parse(Loan source, LoanModel target) {
		
		Record loanRecord = source.loan;
		
		if (loanRecord == null) throw new NullPointerException();
		if (target == null) target = new LoanModel();
		
		String loanId = null;
		
		if (loanRecord.has(LOAN_ID)) 
			loanId = loanRecord.get(LOAN_ID);
		
		Application app;
		{
			app = new Application();
			
			if (loanRecord.has(APP_CUSTOMER_ID)) 
				app.customerId = loanRecord.get(APP_CUSTOMER_ID);
			
			if (loanRecord.has(APP_SSN))
				app.ssn = loanRecord.get(APP_SSN);
			
			if (loanRecord.has(APP_FIRST_NAME))
				app.firstName = loanRecord.get(APP_FIRST_NAME);
			
			if (loanRecord.has(APP_MIDDLE_INITIAL))
				app.middleInitial = loanRecord.get(APP_MIDDLE_INITIAL);
			
			if (loanRecord.has(APP_LAST_NAME))
				app.lastName = loanRecord.get(APP_LAST_NAME);
			
			if (loanRecord.has(APP_DRIVERS_LICENSE_NUMBER))
				app.driversLicenseNumber = loanRecord.get(APP_DRIVERS_LICENSE_NUMBER);
			
			if (loanRecord.has(APP_DATE_OF_BIRTH))
				app.dateOfBirth = new SimpleYMD(loanRecord.get(APP_DATE_OF_BIRTH));
			
			if (loanRecord.has(APP_EMAIL_ADDRESS))
				app.emailAddress = loanRecord.get(APP_EMAIL_ADDRESS);
			
			if (loanRecord.has(APP_RACE))
				app.race = loanRecord.get(APP_RACE);
			
			if (loanRecord.has(APP_HEIGHT_INCHES))
				app.heightInches = new BigDecimal(loanRecord.get(APP_HEIGHT_INCHES));
			
			if (loanRecord.has(APP_GENDER))
				app.gender = Gender.valueOf(loanRecord.get(APP_GENDER).toUpperCase());
			
			app.phoneNumbers = new HashMap<String, String>();
			app.residences = new HashMap<String, UsaAddress>();
			app.references = new HashMap<String, Reference>();
			app.incomes = new HashMap<String, Income>();
			
			for (String col : loanRecord.keys()) {
				
				if (col.startsWith(APP_PHONE_PREFIX)) {
					String name = col.substring(APP_PHONE_PREFIX.length());
					if (app.phoneNumbers.containsKey(name))
						throw new RuntimeException("Key already exists: " + name);
					app.phoneNumbers.put(name, loanRecord.get(col));
				}
				else if (col.startsWith(APP_RES_LINE_ONE_PREFIX)) {
					String name = col.substring(APP_RES_LINE_ONE_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						UsaAddress res = app.residences.get(name);
						if (res == null) {
							res = new UsaAddress();
							app.residences.put(name, res);
						}
						res.lineOne = value;
					}
				}
				else if (col.startsWith(APP_RES_LINE_TWO_PREFIX)) {
					String name = col.substring(APP_RES_LINE_TWO_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						UsaAddress res = app.residences.get(name);
						if (res == null) {
							res = new UsaAddress();
							app.residences.put(name, res);
						}
						res.lineTwo = value;
					}
				}
				else if (col.startsWith(APP_RES_CITY_PREFIX)) {
					String name = col.substring(APP_RES_CITY_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						UsaAddress res = app.residences.get(name);
						if (res == null) {
							res = new UsaAddress();
							app.residences.put(name, res);
						}
						res.city = value;
					}
				}
				else if (col.startsWith(APP_RES_STATE_PREFIX)) {
					String name = col.substring(APP_RES_STATE_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						UsaAddress res = app.residences.get(name);
						if (res == null) {
							res = new UsaAddress();
							app.residences.put(name, res);
						}
						res.state = value;
					}
				}
				else if (col.startsWith(APP_RES_ZIP5_PREFIX)) {
					String name = col.substring(APP_RES_ZIP5_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						UsaAddress res = app.residences.get(name);
						if (res == null) {
							res = new UsaAddress();
							app.residences.put(name, res);
						}
						res.zip5 = value;
					}
				}
				else if (col.startsWith(APP_RES_ZIP4_PREFIX)) {
					String name = col.substring(APP_RES_ZIP4_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						UsaAddress res = app.residences.get(name);
						if (res == null) {
							res = new UsaAddress();
							app.residences.put(name, res);
						}
						res.zip4 = value;
					}
				}
				else if (col.startsWith(APP_REF_NAME_PREFIX)) {
					String name = col.substring(APP_REF_NAME_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						Reference r = app.references.get(name);
						if (r == null) {
							r = new Reference();
							app.references.put(name, r);
						}
						r.name = value;
					}
				}
				else if (col.startsWith(APP_REF_HOME_PHONE_PREFIX)) {
					String name = col.substring(APP_REF_HOME_PHONE_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						Reference r = app.references.get(name);
						if (r == null) {
							r = new Reference();
							app.references.put(name, r);
						}
						r.homePhoneNumber = value;
					}
				}
				else if (col.startsWith(APP_REF_WORK_PHONE_PREFIX)) {
					String name = col.substring(APP_REF_WORK_PHONE_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						Reference r = app.references.get(name);
						if (r == null) {
							r = new Reference();
							app.references.put(name, r);
						}
						r.workPhoneNumber = value;
					}
				}
				
				///////////////////////////////////////////////////////////////
				
				else if (col.startsWith(APP_INC_EMPLOYER_NAME_PREFIX)) {
					String name = col.substring(APP_INC_EMPLOYER_NAME_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						Income i = app.incomes.get(name);
						if (i == null) {
							i = new Income();
							app.incomes.put(name, i);
						}
						i.employerName = value;
					}
				}
				else if (col.startsWith(APP_INC_PAY_FREQUENCY_PREFIX)) {
					String name = col.substring(APP_INC_PAY_FREQUENCY_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						Income i = app.incomes.get(name);
						if (i == null) {
							i = new Income();
							app.incomes.put(name, i);
						}
						if (nonEmpty(value)) 
							i.payFrequency = PayFrequency.valueOf(value.toUpperCase());
					}
				}
				else if (col.startsWith(APP_INC_AMOUNT_PREFIX)) {
					String name = col.substring(APP_INC_AMOUNT_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						Income i = app.incomes.get(name);
						if (i == null) {
							i = new Income();
							app.incomes.put(name, i);
						}
						i.amount = new BigDecimal(value);
					}
				}
				else if (col.startsWith(APP_INC_NEXT_PAY_DATE_PREFIX)) {
					String name = col.substring(APP_INC_NEXT_PAY_DATE_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						Income i = app.incomes.get(name);
						if (i == null) {
							i = new Income();
							app.incomes.put(name, i);
						}
						i.nextPayDate = new SimpleYMD(value);
					}
				}
				else if (col.startsWith(APP_INC_SHIFT_HOURS_PREFIX)) {
					String name = col.substring(APP_INC_SHIFT_HOURS_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						Income i = app.incomes.get(name);
						if (i == null) {
							i = new Income();
							app.incomes.put(name, i);
						}
						i.workShiftHours = value;
					}
				}
				else if (col.startsWith(APP_INC_MONTHS_OF_SERVICE_PREFIX)) {
					String name = col.substring(APP_INC_MONTHS_OF_SERVICE_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						Income i = app.incomes.get(name);
						if (i == null) {
							i = new Income();
							app.incomes.put(name, i);
						}
						i.monthsOfService = Integer.valueOf(value);
					}
				}
				else if (col.startsWith(APP_INC_HAS_DIRECT_DEPOSIT_PREFIX)) {
					String name = col.substring(APP_INC_HAS_DIRECT_DEPOSIT_PREFIX.length());
					String value = loanRecord.get(col);
					
					if (value != null && value.trim().length() > 0) {
						Income i = app.incomes.get(name);
						if (i == null) {
							i = new Income();
							app.incomes.put(name, i);
						}
						i.hasDirectDeposit = Boolean.valueOf(value);
					}
				}
			}
		}
		
		Structure struct;
		{
			struct = new Structure();
			struct.type = loanRecord.get(Constants.STRUCT_TYPE);
			struct.whenExecuted = new SimpleInstant(loanRecord.get(STRUCT_WHEN_EXECUTED));
			struct.loanAmount = new BigDecimal(loanRecord.get(STRUCT_LOAN_AMOUNT));
			struct.financedFees = new HashMap<String, BigDecimal>();
			struct.installmentPlan = new ArrayList<Installment>();
			
			if (nonEmpty(loanRecord.get(STRUCT_DISCLOSED_APR))) {
				struct.disclosedApr = new BigDecimal(loanRecord.get(STRUCT_DISCLOSED_APR));
			}
			
			if (nonEmpty(loanRecord.get(STRUCT_INTEREST_RATE))) {
				struct.interestRate = new BigDecimal(loanRecord.get(STRUCT_INTEREST_RATE));
			}
			
			for (String col : loanRecord.keys()) {
				
				if (col.startsWith(STRUCT_FINANCED_FEE_PREFIX)) {
					String name = col.substring(STRUCT_FINANCED_FEE_PREFIX.length());
					if (struct.financedFees.containsKey(name))
						throw new RuntimeException("Key already exists: " + name);
					String value = loanRecord.get(col);
					if (value != null && value.trim().length() > 0) {
						struct.financedFees.put(name, new BigDecimal(value));
					}
				}
			}
			
			struct.installmentPlan = new ArrayList<Installment>();
			
			for (Record r : source.installments) {

				Installment i = new Installment();
				i.dueDate = new SimpleYMD(r.get(INST_DUE_DATE));
				i.amount = new BigDecimal(r.get(INST_DUE_AMOUNT));
				i.accounts = new HashMap<String, BigDecimal>();
				
				for (String col : r.keys()) {
					
					if (col.startsWith(INST_ACCOUNT_PREFIX)) {
						String name = col.substring(INST_ACCOUNT_PREFIX.length());
						if (i.accounts.containsKey(name))
							throw new RuntimeException("Key already exists: " + name);
						String value = r.get(col);
						if (value != null && value.trim().length() > 0) {
							i.accounts.put(name, new BigDecimal(value));	
						}
					}
				}
				
				struct.installmentPlan.add(i);
			}
		}
		
		Map<String, HeldCheck> heldChecks;
		{
			heldChecks = new HashMap<String, HeldCheck>();
			
			for (String col : loanRecord.keys()) {
				
				String value = loanRecord.get(col);
				if (isEmpty(value)) continue;
				
				if (col.startsWith(TA_CHECK_BANK_NAME_PREFIX)) {
					String name = col.substring(TA_CHECK_BANK_NAME_PREFIX.length());
					HeldCheck c = heldChecks.get(name);
					if (c == null) {
						c = new HeldCheck();
						heldChecks.put(name, c);
					}
					c.bankName = loanRecord.get(col);
				}
				else if (col.startsWith(TA_CHECK_ROUTING_NUMBER_PREFIX)) {
					String name = col.substring(TA_CHECK_ROUTING_NUMBER_PREFIX.length());
					HeldCheck c = heldChecks.get(name);
					if (c == null) {
						c = new HeldCheck();
						heldChecks.put(name, c);
					}
					c.routingNumber = loanRecord.get(col);
				}
				else if (col.startsWith(TA_CHECK_ACCOUNT_NUMBER_PREFIX)) {
					String name = col.substring(TA_CHECK_ACCOUNT_NUMBER_PREFIX.length());
					HeldCheck c = heldChecks.get(name);
					if (c == null) {
						c = new HeldCheck();
						heldChecks.put(name, c);
					}
					c.accountNumber = loanRecord.get(col);
				}
				else if (col.startsWith(TA_CHECK_NUMBER_PREFIX)) {
					String name = col.substring(TA_CHECK_NUMBER_PREFIX.length());
					HeldCheck c = heldChecks.get(name);
					if (c == null) {
						c = new HeldCheck();
						heldChecks.put(name, c);
					}
					c.checkNumber = loanRecord.get(col);
				}
				else if (col.startsWith(TA_CHECK_AMOUNT_PREFIX)) {
					String name = col.substring(TA_CHECK_AMOUNT_PREFIX.length());
					HeldCheck c = heldChecks.get(name);
					if (c == null) {
						c = new HeldCheck();
						heldChecks.put(name, c);
					}
					c.amount = new BigDecimal(loanRecord.get(col));
				}
				else if (col.startsWith(TA_CHECK_DATE_PREFIX)) {
					String name = col.substring(TA_CHECK_DATE_PREFIX.length());
					HeldCheck c = heldChecks.get(name);
					if (c == null) {
						c = new HeldCheck();
						heldChecks.put(name, c);
					}
					c.date = new SimpleYMD(loanRecord.get(col));
				}
			}
		}
		
		Map<String, VehicleTitle> vehicleTitles;
		{
			vehicleTitles = new HashMap<String, VehicleTitle>();
			
			for (String col : loanRecord.keys()) {
				
				String value = loanRecord.get(col);
				if (isEmpty(value)) continue;
				
				if (col.startsWith(COL_VTITLE_YEAR_PREFIX)) {
					String name = col.substring(COL_VTITLE_YEAR_PREFIX.length());
					VehicleTitle t = vehicleTitles.get(name);
					if (t == null) {
						t = new VehicleTitle();
						vehicleTitles.put(name, t);
					}
					t.year = new Integer(value);
				}
				else if (col.startsWith(COL_VTITLE_COLOR_PREFIX)) {
					String name = col.substring(COL_VTITLE_YEAR_PREFIX.length());
					VehicleTitle t = vehicleTitles.get(name);
					if (t == null) {
						t = new VehicleTitle();
						vehicleTitles.put(name, t);
					}
					t.color = value;
				}
				else if (col.startsWith(COL_VTITLE_MAKE_PREFIX)) {
					String name = col.substring(COL_VTITLE_MAKE_PREFIX.length());
					VehicleTitle t = vehicleTitles.get(name);
					if (t == null) {
						t = new VehicleTitle();
						vehicleTitles.put(name, t);
					}
					t.make = value;
				}
				else if (col.startsWith(COL_VTITLE_MODEL_PREFIX)) {
					String name = col.substring(COL_VTITLE_MODEL_PREFIX.length());
					VehicleTitle t = vehicleTitles.get(name);
					if (t == null) {
						t = new VehicleTitle();
						vehicleTitles.put(name, t);
					}
					t.model = value;
				}
				else if (col.startsWith(COL_VTITLE_LICENSE_NUMBER_PREFIX)) {
					String name = col.substring(COL_VTITLE_LICENSE_NUMBER_PREFIX.length());
					VehicleTitle t = vehicleTitles.get(name);
					if (t == null) {
						t = new VehicleTitle();
						vehicleTitles.put(name, t);
					}
					t.licenseNumber = value;
				}
				else if (col.startsWith(COL_VTITLE_VIN_PREFIX)) {
					String name = col.substring(COL_VTITLE_VIN_PREFIX.length());
					VehicleTitle t = vehicleTitles.get(name);
					if (t == null) {
						t = new VehicleTitle();
						vehicleTitles.put(name, t);
					}
					t.vin = value;
				}
				else if (col.startsWith(COL_VTITLE_CERT_NUMBER_PREFIX)) {
					String name = col.substring(COL_VTITLE_CERT_NUMBER_PREFIX.length());
					VehicleTitle t = vehicleTitles.get(name);
					if (t == null) {
						t = new VehicleTitle();
						vehicleTitles.put(name, t);
					}
					t.certNumber = value;
				}
			}
		}
		
		List<History> history;
		{
			history = new ArrayList<History>();
			
			for (Record r : source.history) {
				
				History h = new History();
				h.action = HistoryAction.valueOf(r.get(HIST_ACTION).toUpperCase());
				h.amount = new BigDecimal(r.get(HIST_AMOUNT));
				h.whenHappened = new SimpleInstant(r.get(HIST_WHEN_HAPPENED));
				if (r.has(HIST_WHEN_VOIDED))
					h.whenVoided = new SimpleInstant(r.get(HIST_WHEN_VOIDED));
				h.accounts = new HashMap<String, BigDecimal>();
				
				for (String col : r.keys()) {
					
					if (col.startsWith(HIST_ACCOUNT_PREFIX)) {
						String name = col.substring(HIST_ACCOUNT_PREFIX.length());
						if (h.accounts.containsKey(name))
							throw new RuntimeException("Key already exists: " + name);
						String value = r.get(col);
						if (value != null && value.trim().length() > 0) {
							h.accounts.put(name, new BigDecimal(value));	
						}
					}
				}
				
				history.add(h);
			}
		}
		
		target.id = loanId;
		target.app = app;
		target.struct = struct;
		target.heldChecks = heldChecks;
		target.history = history;
		
		return target;
	}
	
	public static Loan generate(LoanModel source) {
		return generate(source, null);
	}
	
	public static Loan generate(LoanModel source, Loan target) {
		
		if (target == null) target = new Loan();
		if (target.loan == null) target.loan = new MapRecord();
		
		Record loan = target.loan;

		loan.put(LOAN_ID, source.id);

		loan.put(APP_CUSTOMER_ID, source.app.customerId);
		loan.put(APP_SSN, source.app.ssn);
		loan.put(APP_FIRST_NAME, source.app.firstName);
		loan.put(APP_MIDDLE_INITIAL, source.app.middleInitial);
		loan.put(APP_LAST_NAME, source.app.lastName);
		loan.put(APP_DATE_OF_BIRTH, source.app.dateOfBirth.toString());
		loan.put(APP_DRIVERS_LICENSE_NUMBER, source.app.driversLicenseNumber);
		loan.put(APP_EMAIL_ADDRESS, source.app.emailAddress);
		loan.put(APP_RACE, source.app.race);
		loan.put(APP_HEIGHT_INCHES, source.app.heightInches != null ? source.app.heightInches.toString() : null);
		loan.put(APP_GENDER, source.app.gender != null ? source.app.gender.toString() : null);

		for (String name : source.app.phoneNumbers.keySet()) {
			loan.put(APP_PHONE_PREFIX + name, source.app.phoneNumbers.get(name));
		}

		for (String name : source.app.residences.keySet()) {
			UsaAddress a = source.app.residences.get(name);
			loan.put(APP_RES_LINE_ONE_PREFIX + name, a.lineOne);
			loan.put(APP_RES_LINE_TWO_PREFIX + name, a.lineTwo);
			loan.put(APP_RES_CITY_PREFIX + name, a.city);
			loan.put(APP_RES_STATE_PREFIX + name, a.state);
			loan.put(APP_RES_ZIP5_PREFIX + name, a.zip5);
			loan.put(APP_RES_ZIP4_PREFIX + name, a.zip4);
		}

		for (String name : source.app.references.keySet()) {
			Reference r = source.app.references.get(name);
			loan.put(APP_REF_NAME_PREFIX + name, r.name);
			loan.put(APP_REF_HOME_PHONE_PREFIX + name, r.homePhoneNumber);
			loan.put(APP_REF_WORK_PHONE_PREFIX + name, r.workPhoneNumber);
		}

		for (String name : source.app.incomes.keySet()) {
			Income i = source.app.incomes.get(name);
			loan.put(APP_INC_EMPLOYER_NAME_PREFIX + name, i.employerName);
			loan.put(APP_INC_PAY_FREQUENCY_PREFIX + name, i.payFrequency == null ? null : i.payFrequency.toString().toLowerCase());
			loan.put(APP_INC_AMOUNT_PREFIX + name, i.amount.toString());
			loan.put(APP_INC_NEXT_PAY_DATE_PREFIX + name, i.nextPayDate.toString());
			loan.put(APP_INC_SHIFT_HOURS_PREFIX + name, i.workShiftHours);
			loan.put(APP_INC_MONTHS_OF_SERVICE_PREFIX + name, i.monthsOfService == null ? null : i.monthsOfService.toString());
			loan.put(APP_INC_HAS_DIRECT_DEPOSIT_PREFIX + name, i.hasDirectDeposit == null ? null : i.hasDirectDeposit.toString());
		}

		loan.put(STRUCT_TYPE, source.struct.type);
		loan.put(STRUCT_WHEN_EXECUTED, source.struct.whenExecuted.toString());
		loan.put(STRUCT_LOAN_AMOUNT, source.struct.loanAmount.toString());
		
		if (source.struct.disclosedApr != null) {
			loan.put(STRUCT_DISCLOSED_APR, source.struct.disclosedApr.toString());
		}
		
		if (source.struct.interestRate != null) {
			loan.put(STRUCT_INTEREST_RATE, source.struct.interestRate.toString());
		}

		for (String name : source.struct.financedFees.keySet()) {
			loan.put(STRUCT_FINANCED_FEE_PREFIX + name, source.struct.financedFees.get(name).toString());
		}
		
		for (Installment i : source.struct.installmentPlan) {
			
			MapRecord r = new MapRecord();
			r.put(INST_DUE_DATE, i.dueDate.toString());
			r.put(INST_DUE_AMOUNT, i.amount.toString());

			for (String name : i.accounts.keySet()) {
				r.put(INST_ACCOUNT_PREFIX + name, i.accounts.get(name).toString());
			}
			
			target.installments.add(r);
		}

		for (String name : source.heldChecks.keySet()) {
			HeldCheck c = source.heldChecks.get(name);
			loan.put(TA_CHECK_BANK_NAME_PREFIX + name, c.bankName);
			loan.put(TA_CHECK_ROUTING_NUMBER_PREFIX + name, c.routingNumber);
			loan.put(TA_CHECK_ACCOUNT_NUMBER_PREFIX + name, c.accountNumber);
			loan.put(TA_CHECK_NUMBER_PREFIX + name, c.checkNumber);
			loan.put(TA_CHECK_AMOUNT_PREFIX + name, c.amount.toString());
			loan.put(TA_CHECK_DATE_PREFIX + name, c.date.toString());
		}
		
		for (String name : source.heldChecks.keySet()) {
			HeldCheck c = source.heldChecks.get(name);
			loan.put(TA_CHECK_BANK_NAME_PREFIX + name, c.bankName);
			loan.put(TA_CHECK_ROUTING_NUMBER_PREFIX + name, c.routingNumber);
			loan.put(TA_CHECK_ACCOUNT_NUMBER_PREFIX + name, c.accountNumber);
			loan.put(TA_CHECK_NUMBER_PREFIX + name, c.checkNumber);
			loan.put(TA_CHECK_AMOUNT_PREFIX + name, c.amount.toString());
			loan.put(TA_CHECK_DATE_PREFIX + name, c.date.toString());
		}
		
		for (String name : source.vehicleTitles.keySet()) {
			VehicleTitle t = source.vehicleTitles.get(name);
			loan.put(COL_VTITLE_YEAR_PREFIX + name, t.year != null ? t.year.toString() : null);
			loan.put(COL_VTITLE_COLOR_PREFIX + name, t.color);
			loan.put(COL_VTITLE_MAKE_PREFIX + name, t.make);
			loan.put(COL_VTITLE_MODEL_PREFIX + name, t.model);
			loan.put(COL_VTITLE_LICENSE_NUMBER_PREFIX + name, t.licenseNumber);
			loan.put(COL_VTITLE_VIN_PREFIX + name, t.vin);
			loan.put(COL_VTITLE_CERT_NUMBER_PREFIX + name, t.certNumber);
		}
		
		for (History h : source.history) {
			
			MapRecord r = new MapRecord();
			r.put(HIST_ACTION, h.action.toString().toLowerCase());
			r.put(HIST_WHEN_HAPPENED, h.whenHappened.toString());
			if (h.whenVoided != null) r.put(HIST_WHEN_VOIDED, h.whenVoided.toString());
			r.put(HIST_AMOUNT, h.amount.toString());

			for (String name : h.accounts.keySet()) {
				r.put(HIST_ACCOUNT_PREFIX + name, h.accounts.get(name).toString());
			}
			
			target.history.add(r);
		}
		
		return target;
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
