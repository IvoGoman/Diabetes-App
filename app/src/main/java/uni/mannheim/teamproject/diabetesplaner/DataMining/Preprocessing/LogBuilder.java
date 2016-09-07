package uni.mannheim.teamproject.diabetesplaner.DataMining.Preprocessing;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ivo on 5/20/2016.
 * With the help of this class XLog Objects can be created
 * It is used in Combination with the Fuzzy Miner and Heuristics Miner
 */
public class LogBuilder {

    private final XFactory oFactory;
    private XLog oLog = null;
    private final XConceptExtension oConceptInstance = XConceptExtension.instance();
    private XTrace oCurrentTrace = null;
    private List<XEvent> oEventList = new ArrayList<>();
    private final StringBuilder oConversionErrors;
    private boolean bHasStartEvents = false;
    private boolean bErrorDetected = false;

    private XEvent oCurrentEvent;
    private XEvent oCurrentStartEvent;

    private int iCurrentEventMultiplicity;
/*
Initialize the Factory Class for the LogBuilder
 */
    public LogBuilder(){
        this.oFactory = XFactoryRegistry.instance().currentDefault();
        this.oConversionErrors = new StringBuilder();
    }
/*
    Create a new log with a given name
 */
    public LogBuilder startLog(String sName){
        oLog = oFactory.createLog();
        if(oLog!=null){
            XConceptExtension.instance().assignName(oLog,sName);
        }
        return this;
    }
//   add a Trace to the Log. In our case we are adding cases.
    public LogBuilder addTrace(String sName) {
        return addTrace(sName, 1);
    }
    //    add a trace in our case this are the different cases
    public LogBuilder addTrace(String sName, int iNumberOfTraces){
        if(oLog==null){
            throw new IllegalStateException("The Log has not been initialized!");
        }
        if(oCurrentEvent != null){
            addCurrentEventToTrace();
        }
        if (oCurrentTrace != null) {
            addCurrentTraceToLog();
            oCurrentEvent = null;
        }
        oCurrentTrace = oFactory.createTrace();
        if (sName != null) {
            oConceptInstance.assignName(oCurrentTrace, sName);
        }
        iCurrentEventMultiplicity = iNumberOfTraces;
        return this;
    }
    private void addCurrentTraceToLog() {
        oLog.add(oCurrentTrace);
        if (iCurrentEventMultiplicity > 1) {
            for (int i = 0; i < iCurrentEventMultiplicity - 1; i++) {
                XTrace clone = (XTrace) oCurrentTrace.clone();
                String name = oConceptInstance.extractName(clone);
                if (name != null) {
                    oConceptInstance.assignName(clone, name.concat("-").concat(String.valueOf(i+1)));
                }
                oLog.add(clone);
            }
        }
    }
//    Add an event to a trace in our case this is an event from a case
    public LogBuilder addEvent(String sName) {
        addEvent(sName, 1);
        return this;
    }
//    Add an event to a trace in our case this is an event from a case
    public LogBuilder addEvent(String sName, int iNumberOfEvents) {
        if (oCurrentTrace == null) {
            throw new IllegalStateException("Please call 'addTrace' first!");
        }
        if (oCurrentEvent != null) {
            addCurrentEventToTrace();
        }
        oCurrentEvent = oFactory.createEvent();
        XConceptExtension.instance().assignName(oCurrentEvent, sName);
        iCurrentEventMultiplicity = iNumberOfEvents;
        return this;
    }

    private void addCurrentEventToTrace() {
        oCurrentTrace.add(oCurrentEvent);
        if (iCurrentEventMultiplicity > 1) {
            for (int i = 0; i < iCurrentEventMultiplicity - 1; i++) {
                oCurrentTrace.add((XEvent) oCurrentEvent.clone());
            }
        }
    }
//    Methods to add attributes of different types to the events/traces

    public LogBuilder addAttribute(XAttribute oAttribute) {
        addAttributeInternal(oAttribute.getKey(), oAttribute);
        return this;
    }

    public LogBuilder addAttribute(String sName, boolean bValue) {
        XAttribute attribute = oFactory.createAttributeBoolean(sName, bValue, null);
        addAttributeInternal(sName, attribute);
        return this;
    }


    public LogBuilder addAttribute(String sName, long lValue) {
        XAttribute attribute = oFactory.createAttributeDiscrete(sName, lValue, null);
        addAttributeInternal(sName, attribute);
        return this;
    }


    public LogBuilder addAttribute(String sName, String sValue) {
        XAttribute attribute = oFactory.createAttributeLiteral(sName, sValue, null);
        addAttributeInternal(sName, attribute);
        return this;
    }

    public LogBuilder addAttribute(String sName, Date dValue) {
        XAttribute attribute = oFactory.createAttributeTimestamp(sName, dValue, null);
        addAttributeInternal(sName, attribute);
        return this;
    }

    public LogBuilder addAttribute(String sName, double dValue) {
        XAttribute attribute = oFactory.createAttributeContinuous(sName, dValue, null);
        addAttributeInternal(sName, attribute);
        return this;
    }

    private void addAttributeInternal(String name, XAttribute attribute) {
        if (oCurrentEvent == null && oCurrentTrace == null) {
            throw new IllegalStateException("Please call 'addEvent' or 'addTrace' first!");
        }

        if (oCurrentEvent == null) {
            // Trace Attributes
            oCurrentTrace.getAttributes().put(name, attribute);
        } else {
            // Event Attributes
            oCurrentEvent.getAttributes().put(name, attribute);
        }
    }
//    Creates the XLog Object from the data provided to the Builder but is only called once
    public XLog build() {
        if (oCurrentEvent != null) {
            addCurrentEventToTrace();
        }
        if (oCurrentEvent != null) {
            addCurrentTraceToLog();
        }
        return oLog;
    }
}
