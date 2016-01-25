package fr.upmc.datacenter.software.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementOutboundPort;
import fr.upmc.datacenter.software.controller.interfaces.ControllerManagementI;
import fr.upmc.datacenter.software.controller.ports.ControllerManagementInboundPort;
import fr.upmc.datacenter.software.coordinators.CoordinatorDecision;
import fr.upmc.datacenter.software.coordinators.ProcessorCoordinator;
import fr.upmc.datacenter.software.coordinators.connectors.ProcessorCoordinatorServicesConnector;
import fr.upmc.datacenter.software.coordinators.interfaces.ProcessorCoordinatorServicesI;
import fr.upmc.datacenter.software.coordinators.ports.ProcessorCoordinatorServicesInboundPort;
import fr.upmc.datacenter.software.coordinators.ports.ProcessorCoordinatorServicesOutboundPort;
import fr.upmc.datacenter.software.interfaces.RingNetworkI;
import fr.upmc.datacenter.software.ports.RingNetworkInboundPort;
import fr.upmc.datacenter.software.ports.RingNetworkOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;

public class Controller extends AbstractComponent implements RequestDispatcherStateDataConsumerI, RingNetworkI {

    protected static final long THRESHOLD_AVG_ADJUSTMENT_MS = 5000;
    protected static final long MIN_THRESHOLD_AVG_ADJUSTMENT_MS = 1000;
    protected static final long DURATION_BETWEEN_ADJUSTMENT = 5000000000L; // 3s
    public static final boolean TRACE_GRAPH = true;
    public static final boolean TURN_ON_ADAPTATION = true;
    public static final long KEEP_VM_DURATION_S = 5L;

    /** the URI of the component. */
    protected String cURI;
    protected ScheduledFuture<?> pullingFuture;

    /**
     * map associate the difference of the avg request processing between two
     * adjustement with the number of cores to allocate
     **/
    protected Integer[] ladder;

    protected RequestDispatcherDynamicStateDataOutboundPort requestDispatcherDynamicStateDataOutboundPort;

    protected RingNetworkInboundPort rnetip;

    protected RingNetworkOutboundPort rnetop;

    protected RequestDispatcherManagementOutboundPort rdmop;

    protected ControllerManagementInboundPort cmip;

    /** OutboundPort uses to communicate with the AdmissionController */
    protected AdmissionControllerManagementOutboundPort acmop;
    protected String Filename = "";
    public static int nbMoyRecu = 0;
    protected Long lastAdaptation = 0l;
    protected double lastAVGTime = 0;

    private boolean flagVM = false;
    private boolean flagHaveVM = false;
    public String currentVMURI;
    public String currentVMRequestSubmissionInboundPortURI;

    protected Integer[] frequencies;

    protected Integer[] allocatedCoresNo;

    /** map associate processorURI with its allocated cores no **/
    protected Map<String, List<Integer>> processorCores;

    /** map associate processor URI with its outboundport **/
    protected Map<String, ProcessorCoordinatorServicesOutboundPort> pcsops;

    /** map associate processorURI with coordinator URI **/
    protected Map<String, String> processorCoordinator;
    
    /**map associate processorURI with ProcessorManagementOutboundPort. It's used to set frequency on the processor **/
    protected Map<String, ProcessorManagementOutboundPort> pmops;


    /** Coordinator Decision **/
    protected CoordinatorDecision decision = CoordinatorDecision.NONE;
    
    
    protected ProcessorCoordinatorServicesInboundPort pcsip;

    protected String pcsipURI;


    /** map associate processorsURI with processormanagementInboundPortURI **/
    protected Map<String, String> pmipURIs;

    public Controller(String cURI, String requestDispatcherURI, String cmip,
            String admissionControllerManagementOutboundPortURI, String requestDispatcherManagementOutboundPortURI,
            String rddsdip, String rnetipURI, String rnetopURI, Integer[] frequencies,
            Map<String, String> processorCoordinator, Map<String, List<Integer>> processorCores, Map<String, String> pmipURIs) throws Exception {

        super(3, 3);
        this.cURI = cURI;
        pcsipURI = cURI + "pcsip";
        this.requestDispatcherDynamicStateDataOutboundPort = new RequestDispatcherDynamicStateDataOutboundPort(this,
                requestDispatcherURI);
        this.addRequiredInterface(DataRequiredI.PullI.class);
        this.addOfferedInterface(DataRequiredI.PushI.class);
        this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class);
        this.addPort(this.requestDispatcherDynamicStateDataOutboundPort);
        this.requestDispatcherDynamicStateDataOutboundPort.publishPort();
        this.requestDispatcherDynamicStateDataOutboundPort.doConnection(rddsdip,
                DataConnector.class.getCanonicalName());

        this.addRequiredInterface(RingNetworkI.class);
        this.addOfferedInterface(RingNetworkI.class);

        this.rnetop = new RingNetworkOutboundPort(rnetopURI, this);
        this.addPort(this.rnetop);
        this.rnetop.publishPort();

        this.rnetip = new RingNetworkInboundPort(rnetipURI, this);
        this.addPort(this.rnetip);
        this.rnetip.publishPort();

        this.addRequiredInterface(RequestDispatcherManagementI.class);
        this.rdmop = new RequestDispatcherManagementOutboundPort(requestDispatcherManagementOutboundPortURI, this);
        this.addPort(this.rdmop);
        this.rdmop.publishPort();

        this.addOfferedInterface(ControllerManagementI.class);
        this.cmip = new ControllerManagementInboundPort(cmip, this);
        this.addPort(this.cmip);
        this.cmip.publishPort();
        
        this.addOfferedInterface(ProcessorCoordinatorServicesI.class);
        this.pcsip = new ProcessorCoordinatorServicesInboundPort(pcsipURI, this);
        this.addPort(this.pcsip);
        this.pcsip.publishPort();

        pcsops = new HashMap<>();
        pmops = new HashMap<>();
        
        Filename = "./" + Filename + cURI + "Courbe.txt";
        
        for (String processorURI : processorCores.keySet()) {

            String coordinatorURI = processorCoordinator.get(processorURI);

            // processor coordinator services
            this.addRequiredInterface(ProcessorCoordinatorServicesI.class);
            String pcsopURI = cURI + coordinatorURI + "op";
            ProcessorCoordinatorServicesOutboundPort pcsop = new ProcessorCoordinatorServicesOutboundPort(pcsopURI, this);
            this.pcsops.put(processorURI, pcsop);
            this.addPort(pcsop);
            pcsop.publishPort();
            pcsop.doConnection(coordinatorURI + "pcsip", ProcessorCoordinatorServicesConnector.class.getCanonicalName());

            // processor management 
            this.addRequiredInterface(ProcessorManagementI.class);
            String pmopURI = cURI + processorURI + "op";
            ProcessorManagementOutboundPort pmop = new ProcessorManagementOutboundPort(pmopURI, this);
            this.pmops.put(processorURI, pmop);
            this.addPort(pmop);
            pmop.publishPort();
            String pmipURI = pmipURIs.get(processorURI);
            pmop.doConnection(pmipURI, ProcessorManagementConnector.class.getCanonicalName());
            
            // attach to the controller
            pcsop.attachController(this.pcsipURI);

        }

        this.addRequiredInterface(AdmissionControllerManagementI.class);
        this.acmop = new AdmissionControllerManagementOutboundPort(admissionControllerManagementOutboundPortURI, this);
        this.addPort(this.acmop);
        this.acmop.publishPort();
        if (TRACE_GRAPH) {
            FileWriter f = new FileWriter(Filename, false);
            f.close();
        }
        this.frequencies = frequencies;

        ladder = new Integer[] { 2, 2, 2, 3, 3, 4, 5 };
        this.processorCoordinator = processorCoordinator;
        this.processorCores = processorCores;
        

    }

    public void startControlling() throws Exception {
        lastAdaptation = System.nanoTime();

        this.pullingFuture = this.scheduleTaskAtFixedRate(new ComponentI.ComponentTask() {

            @Override
            public void run() {
                try {

                    RequestDispatcherDynamicStateI rdds = getDynamicState();
                    print("timestamp      : " + rdds.getTimeStamp());
                    print("timestamper id : " + rdds.getTimeStamperId());
                    print("request time average : " + rdds.getRequestProcessingAvg() + " ms");
                    boolean adaptation = false;

                    if (TURN_ON_ADAPTATION) {

                        if (System.nanoTime() - lastAdaptation > DURATION_BETWEEN_ADJUSTMENT) {

                            // WE ARE ABOVE THE THRESHOLD
                            // ------------------------------------
                            if (rdds.getRequestProcessingAvg() > THRESHOLD_AVG_ADJUSTMENT_MS) {

                                int nbCoresToAllocate = 2;

                                if (lastAVGTime != 0) {
                                    int i = (int) ((rdds.getRequestProcessingAvg() - lastAVGTime) / 1000);
                                    nbCoresToAllocate = i >= ladder.length ? ladder[ladder.length - 1]
                                            : i > 0 ? ladder[i] : ladder[0];
                                    System.out.println("ladder[" + i + "] = " + nbCoresToAllocate);
                                }
                                System.out.println("ladder[0] = " + nbCoresToAllocate);

                                // Trying to increase frequency if we are refused we will try to add cores..
                                System.out.println("Trying to change frequencies");
                                boolean accepted = false;
                                for (Entry<String, ProcessorCoordinatorServicesOutboundPort> entry : pcsops.entrySet()) {
                                    for (int core : processorCores.get(entry.getKey())) {

                                        // If the coordinator accepts we change it otherwise we don't
                                        if (accepted = entry.getValue().frequencyDemand(cURI, core,frequencies[frequencies.length - 1]))                     
                                            pmops.get(entry.getKey()).setCoreFrequency(core, frequencies[frequencies.length - 1]);
                                    }
                                }

                                // Trying to add cores.. if no more cores
                                // available we add a new VM
                                if (!accepted) {
                                    System.out.println("Trying to add cores..." + nbCoresToAllocate);
                                    if (!acmop.addCores(rdds.getRequestDispatcherURI(), 2)) {
                                        System.out.println("Trying to add a new VM...");
                                        if (flagHaveVM) {
                                            print("VM found ! Connecting VM " + currentVMURI + " to RD + ");
                                            rdmop.connectVm(currentVMURI, currentVMRequestSubmissionInboundPortURI);
                                            flagHaveVM = false;
                                        } else {
                                            print("No VM found ! Waiting...");
                                            flagVM = true;
                                        }
                                    }
                                }

                                adaptation = true;
                                lastAdaptation = System.nanoTime();
                                lastAVGTime = rdds.getRequestProcessingAvg();
                            }

                            // WE ARE BELOW THE MINIMUM THRESHOLD
                            // ------------------------------
                            if (rdds.getRequestProcessingAvg() < MIN_THRESHOLD_AVG_ADJUSTMENT_MS) {
                                rdmop.disconnectVm();
        //TODO setfrequency

                                adaptation = true;
                                lastAdaptation = System.nanoTime();
                                lastAVGTime = rdds.getRequestProcessingAvg();
                            }

                        }
                    }

                    if (TRACE_GRAPH) { // Trace the graph if required
                        if (rdds.getRequestProcessingAvg() != 0) {
                            try {
                                FileWriter fw = new FileWriter(Filename, true);
                                if (!adaptation)
                                    fw.write(nbMoyRecu + " " + rdds.getRequestProcessingAvg() + "\n");
                                else
                                    fw.write(nbMoyRecu + " " + rdds.getRequestProcessingAvg() + " "
                                            + rdds.getRequestProcessingAvg() + "\n");
                                nbMoyRecu++;
                                fw.close();
                            } catch (IOException exception) {
                                System.out.println("Erreur lors de l'ecriture : " + exception.getMessage());
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1l, 1l, TimeUnit.SECONDS);
    }

    public RequestDispatcherDynamicStateI getDynamicState() throws Exception {
        return (RequestDispatcherDynamicStateI) requestDispatcherDynamicStateDataOutboundPort.request();
    }

    private void print(String s) {
        this.logMessage("[Controller " + cURI + "] " + s);
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            if (this.acmop.connected())
                this.acmop.doDisconnection();
        } catch (Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    @Override
    public void acceptRequestDispatcherDynamicData(String requestDispatcherURI,
            RequestDispatcherDynamicStateI currentDynamicState) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void sendVM(String vmURI, String requestSubmissionInboundPortURI) throws Exception {
        flagHaveVM = true;
        currentVMURI = vmURI;
        currentVMRequestSubmissionInboundPortURI = requestSubmissionInboundPortURI;
        if (flagVM) {
            print("Connecting VM " + vmURI + " to RD");
            rdmop.connectVm(vmURI, currentVMRequestSubmissionInboundPortURI);
            flagVM = false;
        } else {
            print("Keeping VM " + vmURI + " for " + KEEP_VM_DURATION_S + " s");
            Thread.sleep(KEEP_VM_DURATION_S * 1000);
            if (flagVM) {
                print("Connecting VM " + currentVMURI + " to RD");
                rdmop.connectVm(currentVMURI, currentVMRequestSubmissionInboundPortURI);
                flagVM = false;
            } else {
                if (flagHaveVM) {
                    flagHaveVM = false;
                    print("Sending VM " + vmURI + " in the ring");
                    rnetop.sendVM(vmURI, requestSubmissionInboundPortURI);
                }
            }
        }

    }

    public void notifyVMEndingItsRequests(String[] VmURis) throws Exception {
        print("Putting VM : " + VmURis[0] + " in the ring");
        rnetop.sendVM(VmURis[0], VmURis[1]);
    }

    static int cptCoordinator = 0;

    public void attachCoordinator(Map<String, List<Integer>> processorCores) throws Exception {

        for (Entry<String, List<Integer>> entry : processorCores.entrySet()) {
            String coordinatorURI = processorCoordinator.get(entry.getKey());
            String processorURI = entry.getKey();
            // if the controller doesn't already deal with this processor we add it
            if (pcsops.get(processorURI) == null) {

                // processor coordinator services
                String pcsopURI = cURI + coordinatorURI + "op";
                ProcessorCoordinatorServicesOutboundPort pcsop = new ProcessorCoordinatorServicesOutboundPort(pcsopURI, this);
                this.pcsops.put(processorURI, pcsop);
                this.addPort(pcsop);
                pcsop.publishPort();
                pcsop.doConnection(coordinatorURI + "pcsip", ProcessorCoordinatorServicesConnector.class.getCanonicalName());

                // processor management 
                this.addRequiredInterface(ProcessorManagementI.class);
                String pmopURI = cURI + processorURI + "op";
                ProcessorManagementOutboundPort pmop = new ProcessorManagementOutboundPort(pmopURI, this);
                this.pmops.put(processorURI, pmop);
                this.addPort(pmop);
                pmop.publishPort();
                String pmipURI = pmipURIs.get(processorURI);
                pmop.doConnection(pmipURI, ProcessorManagementConnector.class.getCanonicalName());


                // attach to the controller
                pcsop.attachController(this.pcsipURI);

            }
            this.processorCores.get(processorURI).addAll(entry.getValue());
        }

    }

    public void setCoordinatorDecision(CoordinatorDecision flag) {
        this.decision = flag;
    }

}
