/*
 * Copyright 2008-2010 by Emeric Vernat
 *
 *     This file is part of Java Melody.
 *
 * Java Melody is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java Melody is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java Melody.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.bull.javamelody;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Test unitaire de la classe PdfOtherReport.
 * @author Emeric Vernat
 */
//CHECKSTYLE:OFF
public class TestPdfOtherReport {
	private static final String TEST_APP = "test app";

	/** Check. */
	@Before
	public void setUp() {
		Utils.initialize();
	}

	/** Test.
	 * @throws IOException e */
	@Test
	public void testWriteHeapHistogram() throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final InputStream input = getClass().getResourceAsStream("/heaphisto.txt");
		try {
			final PdfOtherReport pdfOtherReport = new PdfOtherReport(TEST_APP, output);
			final HeapHistogram heapHistogram = new HeapHistogram(input, false);
			pdfOtherReport.writeHeapHistogram(heapHistogram);
		} finally {
			input.close();
		}
		assertNotEmptyAndClear(output);

		final InputStream input2 = getClass().getResourceAsStream("/heaphisto_jrockit.txt");
		try {
			final PdfOtherReport pdfOtherReport = new PdfOtherReport(TEST_APP, output);
			final HeapHistogram heapHistogram = new HeapHistogram(input2, true);
			pdfOtherReport.writeHeapHistogram(heapHistogram);
		} finally {
			input2.close();
		}
		assertNotEmptyAndClear(output);
	}

	/** Test.
	 * @throws IOException e */
	@Test
	public void testWriteSessionInformations() throws IOException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		final List<SessionInformations> sessions = new ArrayList<SessionInformations>();
		sessions.add(new SessionInformations(new SessionTestImpl(true), false));
		sessions.add(new SessionInformations(new SessionTestImpl(false), false));
		final SessionTestImpl serializableButNotSession = new SessionTestImpl(true);
		serializableButNotSession.setAttribute("serializable but not",
				Collections.singleton(new Object()));
		sessions.add(new SessionInformations(serializableButNotSession, false));
		PdfOtherReport pdfOtherReport = new PdfOtherReport(TEST_APP, output);
		pdfOtherReport.writeSessionInformations(Collections.<SessionInformations> emptyList());
		assertNotEmptyAndClear(output);

		pdfOtherReport = new PdfOtherReport(TEST_APP, output);
		pdfOtherReport.writeSessionInformations(sessions);
		assertNotEmptyAndClear(output);

		// aucune session sérialisable
		pdfOtherReport = new PdfOtherReport(TEST_APP, output);
		pdfOtherReport.writeSessionInformations(Collections.singletonList(new SessionInformations(
				new SessionTestImpl(false), false)));
		assertNotEmptyAndClear(output);

		// pays non existant
		final SessionTestImpl sessionPays = new SessionTestImpl(true);
		sessionPays.setCountry("nimporte.quoi");
		pdfOtherReport = new PdfOtherReport(TEST_APP, output);
		pdfOtherReport.writeSessionInformations(Collections.singletonList(new SessionInformations(
				sessionPays, false)));
		assertNotEmptyAndClear(output);

		// pays null
		sessionPays.setCountry(null);
		assertNull("countryDisplay null",
				new SessionInformations(sessionPays, false).getCountryDisplay());
		pdfOtherReport = new PdfOtherReport(TEST_APP, output);
		pdfOtherReport.writeSessionInformations(Collections.singletonList(new SessionInformations(
				sessionPays, false)));
		assertNotEmptyAndClear(output);
	}

	private void assertNotEmptyAndClear(ByteArrayOutputStream output) {
		assertTrue("rapport vide", output.size() > 0);
		output.reset();
	}
}