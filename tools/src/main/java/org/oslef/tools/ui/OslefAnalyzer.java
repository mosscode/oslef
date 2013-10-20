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
package org.oslef.tools.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.oslef.tools.Fingerprint;
import org.oslef.tools.FingerprintBuilder;
import org.oslef.tools.OslefDbTool;
import org.oslef.tools.db.OslefBdbDb;

public final class OslefAnalyzer {
	
	private final AnalyzerView view;
	private final JFrame window;
	
	public OslefAnalyzer() {
		view = new AnalyzerView();
		
		view.getOpenButton().addActionListener(new ActionListener() {
			final JFileChooser fc;
			{
				fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			}
			public void actionPerformed(ActionEvent e) {
				int result = fc.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file != null) {
						analyze(file);
					}
				}
			}
		});
		
		window = new JFrame("OSLEF Analyzer");
		window.setSize(500, 400);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.getContentPane().add(view);
	}
	
	public void show() {
		window.setVisible(true);
	}
	
	private void analyze(File oslefDir) {
		Thread t = new Thread(new Analyzer(oslefDir), "AnalyzerThread");
		t.start();
	}
	
	private void show(JPanel p) {
		view.getMainPanel().removeAll();
		view.getMainPanel().add(p, BorderLayout.CENTER);
		view.getMainPanel().invalidate();
		view.validate();
	}
	
	private final class Analyzer implements Runnable {
		
		final File oslefDir;
		
		Analyzer(File oslefDir) {
			this.oslefDir = oslefDir.getAbsoluteFile();
		}

		public void run() {
			
			OslefBdbDb db = null;
			String failureMsg = null;
			try {
				System.out.println("Starting analysis of OSLEF dir: " + oslefDir);
				
				final ProgressView progress = new ProgressView();
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						window.setTitle("OSLEF Analyzer - " + oslefDir.getAbsolutePath());
						view.getOpenButton().setEnabled(false);
						progress.getProgressBar().setString("Examining OSLEF Data");
						progress.getProgressBar().setIndeterminate(true);
						show(progress);
					}
				});

				File envDir;
				try {
					envDir = File.createTempFile("oslef", "fingerprinter-env");
					envDir.delete();
					if (!envDir.mkdirs()) throw new RuntimeException("Cannot create db dir: " + envDir);
				}
				catch (IOException ex) {
					throw new RuntimeException(ex);
				}

				db = new OslefBdbDb(envDir);
				db.start();
				
				OslefDbTool tool = new OslefDbTool(db);
				tool.populate(oslefDir);
				
				final FingerprintBuilder fb = new FingerprintBuilder();
				fb.build(db);
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						Fingerprint fp = fb.fp();
						
						StringWriter w = new StringWriter();
						PrintWriter pw = new PrintWriter(w);
						
						pw.println("Loan Count: " + fp.loanCount);
						pw.println();
						
						pw.println("Total Proceeds: " + fp.totalProceeds);
						pw.println();
						
						pw.println("Total Financed Fees:");
						for (String k : fp.totalFinancedFees.keySet()) {
							BigDecimal d = fp.totalFinancedFees.get(k);
							pw.println("    '" + k + "' = " + d);
						}
						pw.println();
						
						pw.println("Total Balances:");
						for (String k : fp.totalBalances.keySet()) {
							BigDecimal d = fp.totalBalances.get(k);
							pw.println("    '" + k + "' = " + d);
						}
						
						ResultsView rv = new ResultsView();
						rv.getFingerprintDesc().setText(w.getBuffer().toString());
						show(rv);
					}
				});
				
				System.out.println("Analysis Complete");
			}
			catch (Exception ex) {
				ex.printStackTrace();
				failureMsg = ex.getMessage();
			}
			finally {
				
				if (db != null) {
					db.stop();
				}
				
				final String fail = failureMsg;
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (fail != null) {
							FailureView fv = new FailureView();
							fv.getFailureMessage().setText("OSLEF analysis failed in an unrecoverable way:\n\n    " + fail);
							show(fv);
						}
						view.getOpenButton().setEnabled(true);
					}
				});
			}
		}
	}
	
	private void analysisComplete() {
		ResultsView rv = new ResultsView();
		show(rv);
	}
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		OslefAnalyzer fp = new OslefAnalyzer();
		fp.show();
	}
}
