package fr.upmc.datacenterclient.applicationprovider.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.admissioncontroller.AdmissionController;
import fr.upmc.datacenter.software.coordinators.ProcessorCoordinator;
import fr.upmc.datacenterclient.applicationprovider.ApplicationProvider;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationNotificationConnector;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationProviderManagementConnector;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationSubmissionConnector;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationNotificationOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationProviderManagementOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationSubmissionOutboundPort;

/**
 * This runs on a single JVM :</br>
 * <ul>
 * <li>NB_COMPUTER (initially 2) computers with 2 processors of 2 cores (can be
 * modified by changing the variable NB_COMPUTER in the class TestCVM2Computers)
 * </li>
 * <li>admission controller that create a requestdispatcher and an applicationVM
 * each time it receives an application and allocate 4 cores to that
 * applicationVM</li>
 * </ul>
 * <li>NB_APPLICATION_PROVIDER (initially 3) applications provider try to send
 * application to the admission controller but only 2 are accepted the 3rd is
 * refused cuz there is not enough resources applications provider that are
 * accepted create a RequestGenerator that will send request to the
 * corresponding requestDispatcher</li>
 * </ul>
 */
public class TestCVM2AP extends AbstractCVM {

    private static final int NB_COMPUTER = 2;
    private static final int NB_APPLICATION_PROVIDER = 2;

    protected ComputerServicesOutboundPort csop[];
    protected ApplicationSubmissionOutboundPort asop[];
    protected ApplicationNotificationOutboundPort anop[];
    protected ApplicationProvider ap[];
    protected ApplicationProviderManagementOutboundPort apmop[];

    @Override
    public void deploy() throws Exception {
        // --------------------------------------------------------------------
        // Create and deploy a NB_COMPUTER computer component with its 2
        // processors and
        // each with 2 cores.
        // --------------------------------------------------------------------
        int numberOfProcessors = 4;
        int numberOfCores = 4;
        Set<Integer> admissibleFrequencies = new HashSet<Integer>();
        admissibleFrequencies.add(1500); // Cores can run at 1,5 GHz
        admissibleFrequencies.add(3000); // and at 3 GHz
        Map<Integer, Integer> processingPower = new HashMap<Integer, Integer>();
        processingPower.put(1500, 1500000); // 1,5 GHz executes 1,5 Mips
        processingPower.put(3000, 3000000); // 3 GHz executes 3 Mips

        // map associate processor uri with uri of inbound port
        Map<String, String> pmipURIs = new HashMap<>();
        Map<String, String> processorCoordinators = new HashMap<>();

        for (int i = 0; i < NB_COMPUTER; ++i) {
            Computer c = new Computer("computer" + i, admissibleFrequencies, processingPower, 1500, 1500,
                    numberOfProcessors, numberOfCores, "csip" + i, "cssdip" + i, "cdsdip" + i);
            this.addDeployedComponent(c);

            Map<Integer, String> processorURIs = c.getStaticState().getProcessorURIs();
            for (Map.Entry<Integer, String> entry : processorURIs.entrySet()) {
                Map<ProcessorPortTypes, String> pPortsList = c.getStaticState().getProcessorPortMap()
                        .get(entry.getValue());
                pmipURIs.put(entry.getValue(), pPortsList.get(Processor.ProcessorPortTypes.MANAGEMENT));
            }

            // --------------------------------------------------------------------
            // Create and deploy Processors coordinator
            // --------------------------------------------------------------------

            int j = 0;
            for (Map.Entry<Integer, String> entry : processorURIs.entrySet()) {
                ProcessorCoordinator pc = new ProcessorCoordinator("pc" + i , pmipURIs.get(entry.getValue()),1500, 1500, numberOfCores );
                processorCoordinators.put(entry.getValue(), "pc" + j);
                this.addDeployedComponent(pc);
            }
        }

        // --------------------------------------------------------------------

        // --------------------------------------------------------------------
        // Create and deploy an AdmissionController component
        // --------------------------------------------------------------------

        String csop[] = new String[NB_COMPUTER];
        String computer[] = new String[NB_COMPUTER];
        for (int i = 0; i < NB_COMPUTER; ++i) {
            computer[i] = "computer" + i;
            csop[i] = "csop" + i;
        }
        final int[] nbAvailableCoresPerComputer = new int[NB_COMPUTER];
        for (int i = 0; i < NB_COMPUTER; ++i)
            nbAvailableCoresPerComputer[i] = numberOfProcessors * numberOfCores;

        // TODO pmipURIs
        Integer[] frequencies = { 1500, 3000 };
        AdmissionController ac = new AdmissionController("ac", "asip", "rdvenip", "anip", "acmip", "rnetip", "rnetop",
                csop, computer, nbAvailableCoresPerComputer, pmipURIs, frequencies, processorCoordinators);

        this.csop = new ComputerServicesOutboundPort[NB_COMPUTER];
        for (int i = 0; i < NB_COMPUTER; ++i) {
            this.csop[i] = (ComputerServicesOutboundPort) ac.findPortFromURI("csop" + i);
            this.csop[i].doConnection("csip" + i, ComputerServicesConnector.class.getCanonicalName());
        }

        ac.toggleTracing();
        ac.toggleLogging();
        this.addDeployedComponent(ac);

        // --------------------------------------------------------------------
        // Create and deploy NB_APPLICATION_PROVIDER ApplicationProvider
        // component
        // --------------------------------------------------------------------
        this.ap = new ApplicationProvider[NB_APPLICATION_PROVIDER];
        this.asop = new ApplicationSubmissionOutboundPort[NB_APPLICATION_PROVIDER];
        this.anop = new ApplicationNotificationOutboundPort[NB_APPLICATION_PROVIDER];
        this.apmop = new ApplicationProviderManagementOutboundPort[NB_APPLICATION_PROVIDER];
        for (int i = 0; i < NB_APPLICATION_PROVIDER; i++) {
            ap[i] = new ApplicationProvider("ap" + i, "asop" + i, "anop" + i, "apmip" + i);
            this.addDeployedComponent(ap[i]);
            ap[i].toggleTracing();
            ap[i].toggleLogging();

            // Connect asop -- asip
            this.asop[i] = (ApplicationSubmissionOutboundPort) ap[i].findPortFromURI("asop" + i);
            this.asop[i].doConnection("asip", ApplicationSubmissionConnector.class.getCanonicalName());

            // Connect anop -- anip
            this.anop[i] = (ApplicationNotificationOutboundPort) ap[i].findPortFromURI("anop" + i);
            this.anop[i].doConnection("anip", ApplicationNotificationConnector.class.getCanonicalName());

            // Connect apmop -- apmip
            this.apmop[i] = new ApplicationProviderManagementOutboundPort("apmop" + i, new AbstractComponent() {
            });
            this.apmop[i].publishPort();
            this.apmop[i].doConnection("apmip" + i, ApplicationProviderManagementConnector.class.getCanonicalName());
        }
        super.deploy();
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    public void test() throws Exception {
        for (int i = 0; i < NB_APPLICATION_PROVIDER; i++)
            apmop[i].sendApplication();
    }

    @Override
    public void shutdown() throws Exception {
        for (int i = 0; i < NB_COMPUTER; ++i) {
            csop[i].doDisconnection();
            asop[i].doDisconnection();
            anop[i].doDisconnection();
            apmop[i].doDisconnection();
        }
        super.shutdown();
    }

    public static void main(String[] args) {
        final TestCVM2AP test = new TestCVM2AP();
        try {
            test.deploy();
            test.start();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        test.test();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(20000L);
            System.out.println("shutting down...");
            test.shutdown();
            System.out.println("ending...");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
