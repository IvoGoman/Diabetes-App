/*
 * Copyright (c) 2007 Christian W. Guenther (christian@deckfour.org)
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * License to link and use is also granted to open source programs which are not
 * licensed under the terms of the GPL, given that they satisfy one or more of
 * the following conditions: 1) Explicit license is granted to the ProM and
 * ProMimport programs for usage, linking, and derivative work. 2) Carte blance
 * license is granted to all programs developed at Eindhoven Technical
 * University, The Netherlands, or under the umbrella of STW Technology
 * Foundation, The Netherlands. For further exemptions not covered by the above
 * conditions, please contact the author of this code.
 */
package uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.metrics.trace;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.HashSet;

import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.util.FMLog;
import uni.mannheim.teamproject.diabetesplaner.DataMining.FuzzyMiner.util.FuzzyMinerLog;

/**
 * @author christian
 * 
 */
public class TraceScatterMetric extends TraceMetric {

	/**
	 * @param aName
	 * @param aDescription
	 * @param log
	 */
	public TraceScatterMetric(XLogInfo logSummary) {
		super("Trace scatter",
				"Measures traces by their number of distinct event classes relative to their overall size.", logSummary);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.mining.fuzzymining.metrics.TraceMetric#measure(org.
	 * processmining.framework.log.LogReader)
	 */

	public void measure(XLog log) {
		HashSet<String> eventSet;
		String eventName, eventType;
		int i = 0;
		for (XTrace pi : FuzzyMinerLog.getTraces(log)) {
			eventSet = new HashSet<String>();
			for (XEvent ate : FuzzyMinerLog.getEvents(pi)) {
				try {
					eventName = FMLog.getConceptName(ate);
					eventType = FMLog.getLifecycleTransition(ate);
					eventSet.add(eventName + eventType);
				} catch (IndexOutOfBoundsException e) {
					// no critical error, fail gracefully
					e.printStackTrace();
				}
			}
			values[i] = ((double) eventSet.size() / (double) pi.size());
			i++;
		}
	}

}
