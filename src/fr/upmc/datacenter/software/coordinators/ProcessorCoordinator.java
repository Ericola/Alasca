package fr.upmc.datacenter.software.coordinators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.software.coordinators.connectors.ProcessorCoordinatorServicesConnector;
import fr.upmc.datacenter.software.coordinators.interfaces.ProcessorCoordinatorServicesI;
import fr.upmc.datacenter.software.coordinators.ports.ProcessorCoordinatorServicesInboundPort;

public class ProcessorCoordinator extends AbstractComponent {

    protected String pcURI;

    // Processor
    protected ProcessorManagementOutboundPort pmop;

    protected ProcessorCoordinatorServicesInboundPort pcsip;

    protected int defaultFrequency;
    protected int maxFrequencyGap;


    private boolean FLAG_SHOULD_INCREASE = false;
    private List<Integer> coreFrequencies;

    public ProcessorCoordinator(String uri, String pmipURI, int defaultFrequency, int maxFrequencyGap, int numberOfCores)
                    throws Exception {

        super(true, true);
        this.pcURI = uri;

        pmop = new ProcessorManagementOutboundPort(pcURI + "pmop", this);
        this.addRequiredInterface(ProcessorManagementI.class);
        this.addPort(pmop);
        pmop.publishPort();
        pmop.doConnection(pmipURI, ProcessorCoordinatorServicesConnector.class.getCanonicalName());

        this.addOfferedInterface(ProcessorCoordinatorServicesI.class);
        this.pcsip = new ProcessorCoordinatorServicesInboundPort(pcURI + "pcsip", this);
        this.addPort(pcsip);
        this.pcsip.publishPort();

        this.defaultFrequency = defaultFrequency;
        this.maxFrequencyGap = maxFrequencyGap;

        this.coreFrequencies = new ArrayList<>();
        for (int i = 0; i < numberOfCores; i++)
            coreFrequencies.add(defaultFrequency);

    }

    public void changeFrequenciesDemand(int coreNo, int f) throws Exception {
        int maxFrequencyOnACore = Collections.max(coreFrequencies);
        int minFrequencyOnACore = Collections.min(coreFrequencies);

        if (coreFrequencies.get(coreNo) < f) { // we increase the frequency of
                                               // the core
            coreFrequencies.set(coreNo, f);

            if (f > maxFrequencyOnACore)
                maxFrequencyOnACore = f;

            // if the difference of frequency between cores is too big with add
            // a flag for the next adjustment
            if (maxFrequencyOnACore - minFrequencyOnACore > maxFrequencyGap) {
                FLAG_SHOULD_INCREASE = true;
            }

        } else { // we decrease the frequency of the core

            if (coreFrequencies.get(coreNo) == maxFrequencyOnACore) {
                coreFrequencies.set(coreNo, f);
                maxFrequencyOnACore = Collections.max(coreFrequencies);
                if (maxFrequencyOnACore != coreFrequencies.get(coreNo)) {
                    if (!(maxFrequencyOnACore - f < maxFrequencyGap))
                        coreFrequencies.set(coreNo, coreFrequencies.get(coreNo) - maxFrequencyGap);
                }

            } else if (FLAG_SHOULD_INCREASE) {

                while (maxFrequencyOnACore - coreFrequencies.get(coreNo) > maxFrequencyGap) {
                    coreFrequencies.set(coreNo, coreFrequencies.get(coreNo) + maxFrequencyGap);
                }
                FLAG_SHOULD_INCREASE = false;
            } else {
                if (maxFrequencyOnACore - f < maxFrequencyGap)
                    coreFrequencies.set(coreNo, f);
                else
                    coreFrequencies.set(coreNo, coreFrequencies.get(coreNo) - maxFrequencyGap);
            }
        }
        pmop.setCoreFrequency(coreNo, coreFrequencies.get(coreNo));
    }

}
