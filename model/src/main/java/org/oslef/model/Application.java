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
import java.util.Map;

public final class Application {

	public enum Gender{MALE, FEMALE}

	public static final class UsaAddress {
		public String lineOne;
		public String lineTwo;
		public String city;
		public String state;
		public String zip5;
		public String zip4;
	}
	
	public static final class Reference {
		public String name;
		public String homePhoneNumber;
		public String workPhoneNumber;
	}
	
	public static enum PayFrequency {
		WEEKLY,
		BIWEEKLY,
		SEMIMONTHLY,
		MONTHLY,
	}
	
	public static final class Income {
		
		public String employerName;
		public PayFrequency payFrequency;
		public BigDecimal amount;
		public SimpleYMD nextPayDate;
		public String workShiftHours;
		public Integer monthsOfService;
		public Boolean hasDirectDeposit;
	}
	
	public String customerId;
	public String ssn;
	public String firstName;
	public String lastName;
	public String middleInitial;
	public String race;
	public Gender gender;
	public BigDecimal heightInches;
	
	public Map<String, UsaAddress> residences;
	public String driversLicenseNumber;
	public Map<String, String> phoneNumbers;
	public SimpleYMD dateOfBirth;
	public String emailAddress;
	
	public Map<String, Reference> references;
	public Map<String, Income> incomes;
}
