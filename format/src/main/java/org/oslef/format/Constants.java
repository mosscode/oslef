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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public interface Constants {
	
	@Retention(RUNTIME) @Target({ElementType.FIELD})
	public @interface Required {}

	/**
	 * Uniquely identifies a loan record.
	 */
	public static final String LOAN_ID = "loan_id";
	
	/**
	 * This is also a test javadoc.
	 */
	public static final String APP_CUSTOMER_ID = "customer_id";
	public static final String APP_SSN = "ssn";
	public static final String APP_FIRST_NAME = "first_name";
	public static final String APP_MIDDLE_INITIAL = "middle_initial";
	public static final String APP_LAST_NAME = "last_name";
	public static final String APP_DATE_OF_BIRTH = "date_of_birth";
	public static final String APP_DRIVERS_LICENSE_NUMBER = "drivers_license_number";
	public static final String APP_EMAIL_ADDRESS = "email_address"; 
	public static final String APP_RACE = "race";
	public static final String APP_HEIGHT_INCHES = "heightInches"; 
	public static final String APP_GENDER = "gender"; 
	public static final String APP_PHONE_PREFIX = "phone:";
	public static final String APP_RES_LINE_ONE_PREFIX = "residence.line_one:"; 
	public static final String APP_RES_LINE_TWO_PREFIX = "residence.line_two:";
	public static final String APP_RES_CITY_PREFIX = "residence.city:"; 
	public static final String APP_RES_STATE_PREFIX = "residence.state:";
	public static final String APP_RES_ZIP5_PREFIX = "residence.zip5:"; 
	public static final String APP_RES_ZIP4_PREFIX = "residence.zip4:";
	public static final String APP_REF_NAME_PREFIX = "reference.name:"; 
	public static final String APP_REF_HOME_PHONE_PREFIX = "reference.home_phone:";
	public static final String APP_REF_WORK_PHONE_PREFIX = "reference.work_phone:"; 
	public static final String APP_INC_EMPLOYER_NAME_PREFIX = "income.employer_name:";
	public static final String APP_INC_PAY_FREQUENCY_PREFIX = "income.pay_frequency:"; 
	public static final String APP_INC_AMOUNT_PREFIX = "income.amount:";
	public static final String APP_INC_NEXT_PAY_DATE_PREFIX = "income.next_pay_date:"; 
	public static final String APP_INC_SHIFT_HOURS_PREFIX = "income.shift_hours:";
	public static final String APP_INC_MONTHS_OF_SERVICE_PREFIX = "income.months_of_service:";
	public static final String APP_INC_HAS_DIRECT_DEPOSIT_PREFIX = "income.has_direct_deposit:";

	public static final String STRUCT_TYPE = "loan_type"; 
	public static final String STRUCT_WHEN_EXECUTED = "when_executed";
	public static final String STRUCT_LOAN_AMOUNT = "loan_amount"; 
	public static final String STRUCT_DISCLOSED_APR = "disclosed_apr";
	public static final String STRUCT_INTEREST_RATE = "interest_rate"; 
	public static final String STRUCT_FINANCED_FEE_PREFIX = "financed_fee:";

	public static final String TA_CHECK_BANK_NAME_PREFIX = "held_check.bank_name:";
	public static final String TA_CHECK_ROUTING_NUMBER_PREFIX = "held_check.routing_number:";
	public static final String TA_CHECK_ACCOUNT_NUMBER_PREFIX = "held_check.account_number:"; 
	public static final String TA_CHECK_NUMBER_PREFIX = "held_check.number:";
	public static final String TA_CHECK_AMOUNT_PREFIX = "held_check.amount:"; 
	public static final String TA_CHECK_DATE_PREFIX = "held_check.date:";

	public static final String COL_VTITLE_YEAR_PREFIX = "vtitle.year:"; 
	public static final String COL_VTITLE_COLOR_PREFIX = "vtitle.color:";
	public static final String COL_VTITLE_MAKE_PREFIX = "vtitle.make:"; 
	public static final String COL_VTITLE_MODEL_PREFIX = "vtitle.model:";
	public static final String COL_VTITLE_LICENSE_NUMBER_PREFIX = "vtitle.license:"; 
	public static final String COL_VTITLE_VIN_PREFIX = "vtitle.vin:";
	public static final String COL_VTITLE_CERT_NUMBER_PREFIX = "vtitle.cert:";

	public static final String INST_DUE_DATE = "due_date"; 
	public static final String INST_DUE_AMOUNT = "due_amount";
	public static final String INST_ACCOUNT_PREFIX = "account:";

	public static final String HIST_ACTION = "action"; 
	public static final String HIST_WHEN_HAPPENED = "when_happened";
	public static final String HIST_WHEN_VOIDED = "when_voided"; 
	public static final String HIST_AMOUNT = "amount"; 
	public static final String HIST_ACCOUNT_PREFIX = "account:";
}
