package fr.upmc.datacenter.software.coordinators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.software.coordinators.connectors.ProcessorCoordinatorServicesConnector;
import fr.upmc.datacenter.software.coordinators.interfaces.ProcessorCoordinatorServicesI;
import fr.upmc.datacenter.software.coordinators.ports.ProcessorCoordinatorServicesInboundPort;
import fr.upmc.datacenter.software.coordinators.ports.ProcessorCoordinatorServicesOutboundPort;

/**
 * The class <code>ProcessorCoordinator</code> implements the component
 * representing processor coordinator in the data center.
 * 
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * A processor coordinator is linked to a processor and manage the availability of its cores.
 * When a controller 
 * 
 * The <code>ProcessorCoordinator</code> offers the interface
 * <code>ProcessorCoordinatorService</code> to attach a new controller to the coordinator.
 * Indeed the coordinator needs to know all of the controller using the 
 * processor to coordinate them.
 * Also it allows a controller to ask for a frequency  
 * 
 */
public class ProcessorCoordinator extends AbstractComponent {

    protected String pcURI;

    // Processor

    protected Map<String, ProcessorCoordinatorServicesOutboundPort> pcsops;

    protected ProcessorCoordinatorServicesInboundPort pcsip;

    protected int defaultFrequency;
    protected int maxFrequencyGap;

    private List<Integer> coreFrequencies;

    public ProcessorCoordinator(String uri, String pmipURI, int defaultFrequency, int maxFrequencyGap, int numberOfCores)
                    throws Exception {

        super(true, true);
        this.pcURI = uri;

  

        this.pcsip = new ProcessorCoordinatorServicesInboundPort(pcURI + "pcsip", this);
        this.addOfferedInterface(ProcessorCoordinatorServicesI.class);
        this.addPort(this.pcsip);
        this.pcsip.publishPort();

        this.defaultFrequency = defaultFrequency;
        this.maxFrequencyGap = maxFrequencyGap;

        this.coreFrequencies = new ArrayList<>();
        for (int i = 0; i < numberOfCores; i++)
            coreFrequencies.add(defaultFrequency);
        
        pcsops = new HashMap<>();

    }


    public Boolean frequencyDemand(String controllerURI, int coreNo, int f) throws Exception {
        print("Frequency change request received");
        int maxFrequencyOnACore;
        int minFrequencyOnACore;

        if (coreFrequencies.get(coreNo) == f) {
            print("The frequency is already to the maximum possible -> REFUSED");
            return false;
        }
        if (coreFrequencies.get(coreNo) < f) { // we increase the frequency of the core
            print("demand for an increase accepted");
            coreFrequencies.set(coreNo, f);
            maxFrequencyOnACore = Collections.max(coreFrequencies);
            minFrequencyOnACore = Collections.min(coreFrequencies);
            
            // if the difference of frequency between cores is too big with add a flag for the next adjustment
            if (maxFrequencyOnACore - minFrequencyOnACore > maxFrequencyGap) {
                print("Difference of frequency is TOO BIG");
                
                // Force others controller that shares this core to increase the frequency during the next adaptation
                for (Entry<String, ProcessorCoordinatorServicesOutboundPort> e : pcsops.entrySet()) {
                    if (!e.getKey().equals(controllerURI)) {
                        print("Force the controller "+e.getKey()+" to increase the frequency for the next adaptation");
                        e.getValue().setCoordinatorDecision(CoordinatorDecision.SHOULD_INCREASE_FREQUENCY);
                    }
                }
            }
            return true;

        }
        coreFrequencies.set(coreNo, f);
        maxFrequencyOnACore = Collections.max(coreFrequencies);
        minFrequencyOnACore = Collections.min(coreFrequencies);
        
        // we decrease the frequency of the core
        print("demand for a decrease");
        if (maxFrequencyOnACore - minFrequencyOnACore > maxFrequencyGap) {
            coreFrequencies.set(coreNo, coreFrequencies.get(coreNo) + f);
            print("The difference is too big now, so we REFUSE");
            return false;
        }else
            print("Decreasing core frequency");
        return true;
    }


    public void attachController(String controllerInboundPortURI) throws Exception {
        print("Attach a controller the the Coordinator");
        this.addRequiredInterface(ProcessorCoordinatorServicesI.class);
        String pcsopURI = pcURI + controllerInboundPortURI + "op";
        ProcessorCoordinatorServicesOutboundPort pcsop = new ProcessorCoordinatorServicesOutboundPort(pcsopURI, this);
        this.pcsops.put(pcsopURI, pcsop);
        this.addPort(pcsop);
        pcsop.publishPort();
        pcsop.doConnection(controllerInboundPortURI, ProcessorCoordinatorServicesConnector.class.getCanonicalName());
        
    }


    private void print(String s) {
        this.logMessage("[Coordinateur " + pcURI + "] " + s);
    }

}
