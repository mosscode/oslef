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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/*
 * The metadata defined here should probably be merged with the metadata that 
 * lives in Validator. It will be less work to keep this info and the validation
 * info in sync if its all in the same spot.
 */
public final class DocGen implements Constants {
	
	public void gen(Reader template, Writer output) throws Exception {
		
		final String dateFormat = "A date in the format YYYY-MM-DD";
		final String phoneFormat = "A 10-digit phone number. (9999999999)";
		final String amountFormat = "A numeric value. (234.23)";
		final String intFormat = "An integer value.";
		final String instantFormat = "An ISO8601 time in the format yyyy-MM-ddTHH:mm:ss.SSSZZ. (2011-13-07T16:29:54.788Z)";

		Col[] loanCols = new Col[] {
			new Col(
				LOAN_ID, 
				true, 
				"Uniquely identifies a loan record",
				null
			),
			new Col(
				APP_CUSTOMER_ID, 
				false, 
				"Uniquely identifies the customer associated with a loan",
				null
			),
			new Col(
				APP_SSN,
				false,
				"A customer's social security number.",
				"9 numeric characters (999999999)"
			),
			new Col(APP_FIRST_NAME, false, "A customer's first name."),
			new Col(
				APP_MIDDLE_INITIAL, 
				false, 
				"A customer's middle initial.", 
				"One character in length, of any type."
			),
			new Col(APP_LAST_NAME, false, "A customer's last name."),
			new Col(APP_DATE_OF_BIRTH, 
				false, 
				"The customer's birth date.", 
				dateFormat
			),
			new Col(
				APP_DRIVERS_LICENSE_NUMBER, 
				false, 
				"The customer's driver's license number.", 
				"A two character US State identifier followed by the drivers license number. (TN2342343)"
			),
			new Col(
				APP_EMAIL_ADDRESS, 
				false, 
				"The customer's email address.", 
				"A standard email address (name@domain.tld)"
			),
			new Col(APP_RACE, false, "The customer's race."),
			new Col(
				APP_HEIGHT_INCHES, 
				false, 
				"The customer's height in inches.", 
				"A numeric value. (72.5)"
			),
			new Col(
				APP_GENDER, 
				false, 
				"The customer's gender.", 
				"Must be a value of 'male', 'female', or 'other'."
			),
			new Col(
				APP_PHONE_PREFIX, 
				false, 
				"One of a customer's phone numbers.", 
				phoneFormat
			),
			new Col(
				APP_RES_LINE_ONE_PREFIX, 
				false, 
				"Line one of a customer's residence address." 
			),
			new Col(
				APP_RES_LINE_TWO_PREFIX, 
				false, 
				"Line two of a customer's residence address." 
			),
			new Col(
				APP_RES_CITY_PREFIX, 
				false, 
				"The city of a customer's residence address." 
			),
			new Col(
				APP_RES_STATE_PREFIX, 
				false, 
				"The state of a customer's residence address.",
				"A two-character state abbreviation. (TN)"
			),
			new Col(
				APP_RES_ZIP5_PREFIX, 
				false, 
				"The zip code of a customer's residence address.", 
				"A 5-digit zip code. (23432)"
			),
			new Col(
				APP_RES_ZIP4_PREFIX, 
				false, 
				"The plus-4 zip code of a customer's residence address.", 
				"A 4-digit zip code. (2343)"
			),
			new Col(
				APP_REF_NAME_PREFIX, 
				false, 
				"The name of a customer reference." 
			),
			new Col(
				APP_REF_HOME_PHONE_PREFIX, 
				false, 
				"The home phone number of a customer reference.", 
				phoneFormat
			),
			new Col(
				APP_REF_WORK_PHONE_PREFIX, 
				false, 
				"The work phone number of a customer reference.", 
				phoneFormat
			),
			new Col(
				APP_INC_EMPLOYER_NAME_PREFIX, 
				false, 
				"The name of a customer's employer." 
			),
			new Col(
				APP_INC_PAY_FREQUENCY_PREFIX, 
				false, 
				"The frequency with which a customer is paid.", 
				intFormat
			),
			new Col(
				APP_INC_AMOUNT_PREFIX, 
				false, 
				"The amount that a customer is paid when paid.", 
				amountFormat
			),
			new Col(
				APP_INC_NEXT_PAY_DATE_PREFIX, 
				false, 
				"A customer's next pay date.", 
				dateFormat
			),
			new Col(
				APP_INC_SHIFT_HOURS_PREFIX, 
				false, 
				"A text description of the hours a customer works." 
			),
			new Col(
				APP_INC_MONTHS_OF_SERVICE_PREFIX, 
				false, 
				"The number of months the customer has been employed by the same employer.", 
				intFormat
			),
			new Col(
				APP_INC_HAS_DIRECT_DEPOSIT_PREFIX, 
				false, 
				"Indicates whether or not a customer is paid via direct deposit.", 
				"Must be either 'true' or 'false'."
			),
			new Col(
				STRUCT_TYPE, 
				false, 
				"A freeform string describing the nature of a loan." 
			),
			new Col(
				STRUCT_WHEN_EXECUTED, 
				false, 
				"The time when the loan was executed.",
				instantFormat
			),
			new Col(
				STRUCT_LOAN_AMOUNT, 
				false, 
				"The proceeds amount of the loan.", 
				amountFormat
			),
			new Col(
				STRUCT_DISCLOSED_APR, 
				false, 
				"The loan's disclosed APR.", 
				amountFormat
			),
			new Col(
				STRUCT_INTEREST_RATE, 
				false, 
				"The loan's interest rate.", 
				amountFormat
			),
			new Col(
				STRUCT_FINANCED_FEE_PREFIX, 
				false, 
				"A financed fee.", 
				amountFormat
			),
			new Col(
				TA_CHECK_BANK_NAME_PREFIX, 
				false, 
				"The held check's bank name." 
			),
			new Col(
				TA_CHECK_ROUTING_NUMBER_PREFIX, 
				false, 
				"The held check bank routing number.", 
				"A nine-digit numeric ABA number. (262084356)"
			),
			new Col(
				TA_CHECK_ACCOUNT_NUMBER_PREFIX, 
				false, 
				"The held check bank account number.", 
				intFormat
			),
			new Col(
				TA_CHECK_NUMBER_PREFIX, 
				false, 
				"The held check number.", 
				intFormat
			),
			new Col(
				TA_CHECK_AMOUNT_PREFIX, 
				false, 
				"The held check amount", 
				amountFormat
			),
			new Col(
				TA_CHECK_DATE_PREFIX, 
				false, 
				"The held check date.", 
				dateFormat
			),
			new Col(
				COL_VTITLE_YEAR_PREFIX, 
				false, 
				"The vehicle year.", 
				"A year in the format YYYY. (1944)"
			),
			new Col(
				COL_VTITLE_COLOR_PREFIX, 
				false, 
				"The vehicle color." 
			),
			new Col(
				COL_VTITLE_MAKE_PREFIX, 
				false, 
				"The vehicle make." 
			),
			new Col(
				COL_VTITLE_MODEL_PREFIX, 
				false, 
				"The vehicle model." 
			),
			new Col(
				COL_VTITLE_LICENSE_NUMBER_PREFIX, 
				false, 
				"The vehicle title license number." 
			),
			new Col(
				COL_VTITLE_VIN_PREFIX, 
				false, 
				"The VIN number." 
			),
			new Col(
				COL_VTITLE_CERT_NUMBER_PREFIX, 
				false, 
				"The vehicle registration certificate number." 
			)
		};

		Col[] instCols = new Col[] {
			new Col(
				LOAN_ID, 
				true, 
				"Identifies the loan to which an installment applies." 
			),
			new Col(
				INST_DUE_DATE, 
				false, 
				"The date an installment is due.", 
				dateFormat
			),
			new Col(
				INST_DUE_AMOUNT, 
				false, 
				"The amount due to be paid for a given installment.", 
				amountFormat
			),
			new Col(
				INST_ACCOUNT_PREFIX, 
				false, 
				"The amount to be applied for a given installment to a loan sub-account.", 
				amountFormat
			),
		};

		Col[] histCols = new Col[] {
			new Col(
				LOAN_ID, 
				true, 
				"Identifies the loan to which this loan event applies." 
			),
			new Col(
				HIST_ACTION, 
				true, 
				"Describes the nature of a historical event.", 
				"Must be one of the following values: draw, deposit, bounce, payment, accrual, exemption, interest_halt, interest_resume, payment_deadline, charge, write_off, or extended_payment_plan_execution."
			),
			new Col(
				HIST_WHEN_HAPPENED, 
				true, 
				"Indicates when a historical event occurred.", 
				instantFormat
			),
			new Col(
				HIST_WHEN_VOIDED, 
				false, 
				"If non-empty, indicates when a historical event was voided.", 
				instantFormat
			),
			new Col(
				HIST_AMOUNT, 
				false, 
				"The total amount by which a historical event affects a loan's balance.", 
				amountFormat
			),
			new Col(
				HIST_ACCOUNT_PREFIX, 
				false, 
				"The amount by which a historical event affects a specific loan sub-account balance.", 
				amountFormat
			),
		};
		
		Map<String, String> bindings = new HashMap<String, String>();
		bindings.put("loanColumns", printCols(loanCols));
		bindings.put("instColumns", printCols(instCols));
		bindings.put("histColumns", printCols(instCols));
		
		StringTemplate st = new StringTemplate(readTemplate(template));
		String result = st.create(bindings);
		
		output.write(result);
		output.close();
	}
	
	private String readTemplate(Reader in) throws IOException {
		StringWriter out = new StringWriter();
		char[] buffer = new char[1024 * 10]; //10k buffer
		for(int numRead = in.read(buffer); numRead!=-1; numRead = in.read(buffer)){
			out.write(buffer, 0, numRead);
		}
		in.close();
		return out.getBuffer().toString();
	}

	private String printCols(Col[] cols) {

		StringWriter w = new StringWriter();
		PrintWriter pw = new PrintWriter(w);

		pw.println("<table>");
		pw.println("<thead>");
		pw.println("	<tr>");
		pw.println("		<th>Name</th>");
		pw.println("		<th>Type</th>");
		pw.println("		<th>Required</th>");
		pw.println("		<th>Description</th>");
		pw.println("		<th>Format</th>");
		pw.println("	</tr>");
		pw.println("</thead>");
		pw.println("<tbody>");
		for (Col col : cols) {
			pw.println("	<tr>");
			pw.println("		<td>" + col.name + "</td>");
			pw.println("		<td>" + (col.prefix ? "prefix" : "simple") + "</td>");
			pw.println("		<td>" + col.required + "</td>");
			pw.println("		<td>" + col.description + "</td>");
			pw.println("		<td>" + (col.format == null ? "No specific format." : col.format) + "</td>");
			pw.println("	</tr>");
		}
		pw.println("</tbody>");
		pw.println("</table>");

		return w.getBuffer().toString();
	}
	
	private final class Col {
		String name;
		boolean required;
		String description;
		String format;
		boolean prefix;

		Col(String name, boolean required, String description) {
			this(name, required, description, null);
		}

		Col(String name, boolean required, String description, String format) {
			this.name = name;
			this.required = required;
			this.description = description;
			this.format = format;
			this.prefix = name.endsWith(":");
		}
	}

	private final class StringTemplate {
		
		private final String contents;
		
		public StringTemplate(String contents) {
			if (contents == null) {
				throw new NullPointerException();
			}
			this.contents = contents;
		}
		
		public String create(Map<String, String> params) {
			String s = contents;
			for (String key : params.keySet()) {
				String value = params.get(key);
				s = s.replaceAll("\\$\\{" + key + "\\}", Matcher.quoteReplacement(value));
			}
			return s;
		}
	}

	public static void main(String[] args) throws Exception {
		
		File tpl = new File("src/main/docs/spec-template.html");
		File out = new File("src/main/docs/spec.html");

		DocGen g = new DocGen();
		g.gen(new FileReader(tpl), new FileWriter(out));
	}
}
