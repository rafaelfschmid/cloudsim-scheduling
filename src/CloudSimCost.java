import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * A simple example showing how to create a datacenter with two hosts and run
 * two cloudlets on it. The cloudlets run in VMs with different MIPS
 * requirements. The cloudlets will take different time to complete the
 * execution depending on the requested VM performance.
 */
public class CloudSimCost {

	private native void segmented_sort(float machines[], int task_index[],
			int segments[], int t, int m);
	
	private native void segmented_sort_desc(float machines[], int task_index[],
			int segments[], int t, int m);

	static {
		System.loadLibrary("CloudSimCost");
	}

	private static void segmentedSorting(float machines[], int task_index[],
			int machine_current_index[], int task_map[],
			float completion_times[], int t, int m) {

		int segments[] = new int[m];

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < t; j++) {
				machines[i * t + j] = (float) expectedTime(cloudletList.get(j),
						vmlist.get(i));
				task_index[i * t + j] = j;
				task_map[j] = -1;
			}
			segments[i] = i * t;
			machine_current_index[i] = 0;
			completion_times[i] = 0;
		}

		new CloudSimCost().segmented_sort(machines, task_index, segments, t, m);
	}
	
	private static void segmentedSorting2(float machines[], int task_index[],
			int machine_current_index[], int task_map[],
			float completion_times[], int t, int m) {

		int segments[] = new int[m];

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < t; j++) {
				machines[i * t + j] = (float) expectedTime(cloudletList.get(j),
						vmlist.get(i));
				task_index[i * t + j] = j;
				task_map[j] = -1;
			}
			segments[i] = i * t;
			machine_current_index[i] = 0;
			completion_times[i] = 0;
		}

		new CloudSimCost().segmented_sort_desc(machines, task_index, segments, t,	m);
	}

	private static void min_min_gpu(DatacenterBroker broker) {
		// Task* machines, float* completion_times, int* task_map, uint*
		// machine_current_index, int m, int t) {

		int m = vmlist.size();
		int t = cloudletList.size();

		float machines[] = new float[t * m];
		float completion_times[] = new float[m];
		int machine_current_index[] = new int[m];
		int task_index[] = new int[t * m];
		int task_map[] = new int[t];

		segmentedSorting(machines, task_index, machine_current_index, task_map,
				completion_times, t, m);

		int min = 0;
		int imin = 0;
		float min_value;

		for (int k = 0; k < t; k++) {

			min_value = Float.MAX_VALUE;

			for (int i = 0; i < m; i++) {

				int j = machine_current_index[i];
				while (task_map[task_index[i * t + j]] != -1) {
					j++;
				}
				machine_current_index[i] = j;

				if (completion_times[i] + machines[i * t + j] < min_value) {
					min = i * t + j;
					imin = i;
					min_value = completion_times[imin] + machines[min];
				}
			}
			
			int cloudletId = cloudletList.get(task_index[min]).getCloudletId();
			int vmId = vmlist.get(imin).getId();
			
			task_map[task_index[min]] = imin;
			broker.bindCloudletToVm(cloudletId, vmId);
			completion_times[imin] = min_value;
		}

	}

	private static void min_min_cpu(DatacenterBroker broker) {
		float completion_times[] = new float[vmlist.size()];
		List<Cloudlet> cloudletAux = new ArrayList<Cloudlet>();
		cloudletAux.addAll(cloudletList);

		for (int j = 0; j < vmlist.size(); j++) {
			completion_times[j] = 0;
		}
		
		int imin = 0;
		int jmin = 0;
		float min_value;
		for (int k = 0; k < cloudletList.size(); k++) {

			min_value = Float.MAX_VALUE;

			for (int i = 0; i < cloudletAux.size(); i++) {
				for (int j = 0; j < vmlist.size(); j++) {
					float time = (float) expectedTime(cloudletAux.get(i),
							vmlist.get(j));
					if (completion_times[j] + time < min_value) {
						imin = i;
						jmin = j;
						min_value = completion_times[jmin] + time;
					}
				}
			}
			int cloudletId = cloudletAux.get(imin).getCloudletId();
			int vmId = vmlist.get(jmin).getId();

			cloudletAux.remove(imin);
			broker.bindCloudletToVm(cloudletId, vmId);
			completion_times[jmin] = min_value;
		}

	}
	
	private static void max_min_gpu(DatacenterBroker broker) {
		// Task* machines, float* completion_times, int* task_map, uint*
		// machine_current_index, int m, int t) {

		int m = vmlist.size();
		int t = cloudletList.size();

		float machines[] = new float[t * m];
		float completion_times[] = new float[m];
		int machine_current_index[] = new int[m];
		int task_index[] = new int[t * m];
		int task_map[] = new int[t];

		for (int j = 0; j < vmlist.size(); j++) {
			completion_times[j] = 0;
		}
		
		segmentedSorting2(machines, task_index, machine_current_index, task_map,
				completion_times, t, m);

		/*for(int i = 0; i < m; i++) {
			for(int j = 0; j < t; j++) {
				System.out.print(task_index[i*t+j] + " ");
			}
			System.out.println();
		}
		
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < t; j++) {
				System.out.print(machines[i*t+j] + " ");
			}
			System.out.println();
		}*/
		
		int min = 0;
		int imin = 0;
		float max_value;

		for (int k = 0; k < t; k++) {

			max_value = 0;

			for (int i = 0; i < m; i++) {

				int j = machine_current_index[i];
				while (task_map[task_index[i * t + j]] != -1) {
					j++;
				}
				machine_current_index[i] = j;

				if (completion_times[i] + machines[i * t + j] > max_value) {
					min = i * t + j;
					max_value = completion_times[i] + machines[min];
				}
			}
			
			for (int i = 0; i < vmlist.size(); i++) {

				float time = (float) expectedTime(cloudletList.get(task_index[min]),
						vmlist.get(i));
				
				if (completion_times[i] + time < max_value) {
					imin = i;
					max_value = completion_times[i] + time;
				}
			}
			
			int cloudletId = cloudletList.get(task_index[min]).getCloudletId();
			int vmId = vmlist.get(imin).getId();
			
			task_map[task_index[min]] = imin;
			broker.bindCloudletToVm(cloudletId, vmId);
			completion_times[imin] = max_value;
		}

	}
	
	private static void max_min_cpu(DatacenterBroker broker) {
		float completion_times[] = new float[vmlist.size()];
		List<Cloudlet> cloudletAux = new ArrayList<Cloudlet>();
		cloudletAux.addAll(cloudletList);

		for (int j = 0; j < vmlist.size(); j++) {
			completion_times[j] = 0;
		}
		
		int imin = 0;
		int jmin = 0;
		float max_value;
		for (int k = 0; k < cloudletList.size(); k++) {

			max_value = 0;

			for (int i = 0; i < cloudletAux.size(); i++) {
				for (int j = 0; j < vmlist.size(); j++) {
					float time = (float) expectedTime(cloudletAux.get(i),
							vmlist.get(j));
					if (completion_times[j] + time > max_value) {
						imin = i;
						max_value = completion_times[jmin] + time;
					}
				}
			}
			
			for (int j = 0; j < vmlist.size(); j++) {
				float time = (float) expectedTime(cloudletAux.get(imin),
						vmlist.get(j));
				if (completion_times[j] + time < max_value) {
					jmin = j;
					max_value = completion_times[j] + time;
				}
			}
			
			int cloudletId = cloudletAux.get(imin).getCloudletId();
			int vmId = vmlist.get(jmin).getId();

			cloudletAux.remove(imin);
			broker.bindCloudletToVm(cloudletId, vmId);
			completion_times[jmin] = max_value;
		}
	}
	
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;
	
	private static double expectedTime(Cloudlet cloudlet, Vm vm) {

		double cpu = (double) cloudlet.getCloudletLength()
				/ (double) vm.getMips();
		double memory = 0.0;// (double)cloudlet.getCloudletOutputSize() /
							// (double)vm.getBw();

		return cpu > memory ? cpu : memory;
	}

	private static int currenthost = 0;
	
	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			Log.printLine("ERROR - Parameters: <algorithm> <machine file> <task file> <print log>");
		} else {
			if(args[3].compareTo("all") != 0) {
				Log.disable();
			}
			
			Log.printLine("Starting CloudSimExample3...");

			try {
				// First step: Initialize the CloudSim package. It should be
				// called
				// before creating any entities.
				int num_user = 1; // number of cloud users
				Calendar calendar = Calendar.getInstance();
				boolean trace_flag = false; // mean trace events

				// Initialize the CloudSim library
				CloudSim.init(num_user, calendar, trace_flag);

				File file = new File(args[1]);
				BufferedReader br_machines = new BufferedReader(new FileReader(file));
				// VM description
				int number_of_vms = Integer.parseInt(br_machines.readLine());
				
				List<Integer> lstMips = new ArrayList<Integer>();
				int max_mips = 0;
				for (int vmid = 0; vmid < number_of_vms; vmid++) {
					int mips = Integer.parseInt(br_machines.readLine());
					if(max_mips < mips)
						max_mips = mips;
					lstMips.add(mips);
				}
				
				// Second step: Create Datacenters
				// Datacenters are the resource providers in CloudSim. We need
				// at least one of them to run a CloudSim simulation
				@SuppressWarnings("unused")
				Datacenter datacenter0 = createDatacenter("Datacenter_0", max_mips);
				@SuppressWarnings("unused")
				Datacenter datacenter1 = createDatacenter("Datacenter_1", max_mips);
				@SuppressWarnings("unused")
				Datacenter datacenter2 = createDatacenter("Datacenter_2", max_mips);
				@SuppressWarnings("unused")
				Datacenter datacenter3 = createDatacenter("Datacenter_3", max_mips);
				@SuppressWarnings("unused")
				Datacenter datacenter4 = createDatacenter("Datacenter_4", max_mips);
				@SuppressWarnings("unused")
				Datacenter datacenter5 = createDatacenter("Datacenter_5", max_mips);			
				@SuppressWarnings("unused")
				Datacenter datacenter6 = createDatacenter("Datacenter_6", max_mips);
				
				// Third step: Create Broker
				DatacenterBroker broker = createBroker();
				int brokerId = broker.getId();

				// Fourth step: Create one virtual machine
				vmlist = new ArrayList<Vm>();

				// VM description
				long size = 10000; // image size (MB)
				int ram = 2048; // vm memory (MB)
				long bw = 1000;
				int pesNumber = 1; // number of cpus
				String vmm = "Xen"; // VMM name

				for (int vmid = 0; vmid < number_of_vms; vmid++) {
					// create VMs
					//float mips = Integer.parseInt(br_machines.readLine());
					Vm vm = new Vm(vmid, brokerId, lstMips.get(vmid),
							pesNumber, ram, bw, size, vmm,
							new CloudletSchedulerSpaceShared());

					vmlist.add(vm);
				}
				br_machines.close();
				
				// submit vm list to the broker
				broker.submitVmList(vmlist);

				// Fifth step: Create two Cloudlets
				cloudletList = new ArrayList<Cloudlet>();

				
				file = new File(args[2]);
				BufferedReader br_cloudlets = new BufferedReader(new FileReader(file));
				// Cloudlet properties
				// int id = 0;
				long length = 4000;
				long fileSize = 300;
				long outputSize = 300;
				UtilizationModel utilizationModel = new UtilizationModelFull();

				int numberOfCloudlets = Integer.parseInt(br_cloudlets.readLine());
				for (int id = 0; id < numberOfCloudlets; id++) {
					Cloudlet cloudlet = new Cloudlet(id, Integer.parseInt(br_cloudlets
							.readLine()), pesNumber, fileSize, outputSize,
							utilizationModel, utilizationModel,
							utilizationModel);
					cloudlet.setUserId(brokerId);

					// add the cloudlets to the list
					cloudletList.add(cloudlet);
				}
				br_cloudlets.close();

				// submit cloudlet list to the broker
				broker.submitCloudletList(cloudletList);

				//NetworkTopology.addLink(datacenter0.getId(), broker.getId(),10.0, 10);
				//NetworkTopology.addLink(datacenter1.getId(), broker.getId(),20.0, 10);

				long startTime = 0, stopTime = 0;
				if (args[0].compareTo("minmin") == 0) {
					startTime = System.currentTimeMillis();
					min_min_gpu(broker);
					stopTime = System.currentTimeMillis();
				} else if (args[0].compareTo("mincpu") == 0) {
					startTime = System.currentTimeMillis();
					min_min_cpu(broker);
					stopTime = System.currentTimeMillis();
				} else if (args[0].compareTo("maxmin") == 0) {
					startTime = System.currentTimeMillis();
					max_min_gpu(broker);
					stopTime = System.currentTimeMillis();
				}
				else if (args[0].compareTo("maxcpu") == 0) {
					startTime = System.currentTimeMillis();
					max_min_cpu(broker);
					stopTime = System.currentTimeMillis();
				}
				/*
				 * else if(args[0].compareTo("fcfs") == 0){ stopTime = startTime
				 * = System.currentTimeMillis(); }
				 */
				long estimatedTime = stopTime - startTime;
				// min_min_sorted();

				// Sixth step: Starts the simulation
				CloudSim.startSimulation();

				// Final step: Print results when simulation is over
				List<Cloudlet> newList = broker.getCloudletReceivedList();

				CloudSim.stopSimulation();

				printCloudletList(newList);
				
				if(args[3].compareTo("all") != 0) {
					DecimalFormat dft = new DecimalFormat("###.####");
					double f = cloudletList.get(cloudletList.size()-1).getFinishTime();
					double s = estimatedTime/1000.000;
					double sum = f + s;
					
					System.out.println( dft.format(f) + "\t" +  dft.format(s) + "\t" +  dft.format(sum));
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.printLine("The simulation has been terminated due to an unexpected error");
			}
		}
	}

	private static Datacenter createDatacenter(String name, int max_mips) {

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store one or more
		// Machines
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore,
		// should
		// create a list to store these PEs before creating
		// a Machine.
		List<Pe> peList1 = new ArrayList<Pe>();

		int mips = max_mips;
		//int mips = power;

		// 3. Create PEs and add these into the list.
		// for a quad-core machine, a list of 4 PEs is required:
		// need to store Pe id and MIPS Rating
		peList1.add(new Pe(0, new PeProvisionerSimple(mips))); 
		peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
//		peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
//		peList1.add(new Pe(3, new PeProvisionerSimple(mips)));

		// Another list, for a dual-core machine
		List<Pe> peList2 = new ArrayList<Pe>();
		peList2.add(new Pe(0, new PeProvisionerSimple(mips))); 
//		peList2.add(new Pe(1, new PeProvisionerSimple(mips)));
		
		// 4. Create Hosts with its id and list of PEs and add them to the list
		// of machines
		int hostId = 0;
		int ram = 32768; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000;

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw), storage, peList1,
				new VmSchedulerTimeShared(peList1))); // This is our first
														// machine

		hostId++;

		hostList.add(new Host(hostId, new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw), storage, peList2,
				new VmSchedulerTimeShared(peList2))); // Second machine

		// 5. Create a DatacenterCharacteristics object that stores the
		// properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.1; // the cost of using storage in this
										// resource
		double costPerBw = 0.1; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are
																		// not
																		// adding
																		// SAN
																		// devices
																		// by
																		// now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics,
					new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	// We strongly encourage users to develop their own broker policies, to
	// submit vms and cloudlets according
	// to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker() {

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * 
	 * @param list
	 *            list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}

	}
}
