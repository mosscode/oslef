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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.oslef.model.History;
import org.oslef.model.LoanModel;
import org.oslef.model.Translator;
import org.oslef.tools.db.DbLoan;


public final class Fingerprint {

	public int loanCount = 0;
	public BigDecimal totalProceeds = BigDecimal.ZERO;
	public final Map<String, BigDecimal> totalFinancedFees = new HashMap<String, BigDecimal>();
	public final Map<String, BigDecimal> totalBalances = new HashMap<String, BigDecimal>();
	
	public void update(DbLoan s) {
		
		LoanModel loan = Translator.parse(s, null);
		
		loanCount++;
		totalProceeds = totalProceeds.add(loan.struct.loanAmount);
		
		for (String name : loan.struct.financedFees.keySet()) {
			BigDecimal value = loan.struct.financedFees.get(name);
			BigDecimal total = totalFinancedFees.get(name);
			if (total == null) total = BigDecimal.ZERO;
			total = total.add(value);
			totalFinancedFees.put(name, total);
		}
		
		for (History h : loan.history) {
			for (String name : h.accounts.keySet()) {
				BigDecimal value = h.accounts.get(name);
				BigDecimal total = totalBalances.get(name);
				if (total == null) total = BigDecimal.ZERO;
				total = total.add(value);
				totalBalances.put(name, total);
			}
		}
	}
	
	private static ObjectWriter w;
	{
		ObjectMapper m = new ObjectMapper();
		w = m.defaultPrettyPrintingWriter();
	}
	
	@Override
	public String toString() {
		try { return w.writeValueAsString(this); } 
		catch (Exception ex) { throw new RuntimeException(ex); }
	}
}
