import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;
public class N_set_Associative_Mapping{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scn = new Scanner(System.in);		
		System.out.println("Enter Block size:");
		int b = scn.nextInt(); // block size/ cache line size
		while (!ispowerof2(b) || b<=0) {
			System.out.println("Enter valid block size"); b = scn.nextInt();
		}
		System.out.println("Enter the number of cache lines:");
		int cl = scn.nextInt(); // no of cache lines
		while (!ispowerof2(cl) || cl<=0) {
			System.out.println("Enter valid number of cache lines"); cl = scn.nextInt();
		}
		System.out.println("Enter k for k way set associative");
		int k = scn.nextInt();
		while (!ispowerof2(k) || k<0) {
			System.out.println("Enter valid value of k"); k = scn.nextInt();
		}
		if (k>cl) {
			System.out.println("K can't be greater than the number of cache lines. Please enter a smaller value"); return;}
		int cols = b;
		ArrayList<Integer> freq1 = new ArrayList<>();
		ArrayList<Integer> freq2 = new ArrayList<>();
		System.out.println("Enter the number of test cases:");
		int tc = scn.nextInt();
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L1 = new HashMap<>();
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L2 = new HashMap<>();
		int s2 = cl / k; // total no of sets in L2
		int s1 = (cl / 2) / k; // total no of sets in L1

		for (int i = 0; i < tc; i++) {

			System.out.println("Enter the address:");
			int address = scn.nextInt();
			while (address > 65535 || address < 0) {
				System.out.println("Please enter a valid address, i.e. an integer from 0 to 65535");
				address = scn.nextInt();
			}
			get_address(address, b,s2);
			int r = address / cols; // block no // cache independent
			int c = address % cols; // index no
			System.out.println("Enter read/write command");
			String str = scn.next(); // command
			freq1.add(r);
			freq2.add(r);
			int sn1 = r % s1; // set number for L1
			int sn2 = r % s2; // set number for L2
			boolean cmd1 = str.toUpperCase().equals("READ");
			boolean cmd2 = str.toUpperCase().equals("WRITE");

			if (cmd1 || cmd2) {
				int data = Integer.MAX_VALUE;
				if (cmd2) {
					System.out.println("Enter data");
					data = scn.nextInt();
				}
				HashMap<Integer, ArrayList<Integer>> helper1 = L1.get(sn1);
				HashMap<Integer, ArrayList<Integer>> helper2 = L2.get(sn2);

				if (helper1 != null && helper1.containsKey(r)) {   // no block replacement
					System.out.println("Hit in Level1...!");
					if (cmd1) {
						if (helper1.get(r).get(c) == Integer.MAX_VALUE) {
							System.out.println("EMPTY!");
						} else {
							System.out.println(helper1.get(r).get(c));
						}
					} // if read
					else {
						helper1.get(r).set(c, data);
						helper2.get(r).set(c, data);
						L1.put(sn1, helper1);
						L2.put(sn2, helper2);
					} // if write

				} else if (helper2 != null && helper2.containsKey(r)) {

					System.out.println("Hit in Level2...!"); // read/write
					if (cmd1) {
						if (helper2.get(r).get(c) == Integer.MAX_VALUE) {
							System.out.println("EMPTY!");
						} else {
							System.out.println(L2.get(r).get(c));
						}
					} // if read
					else {
						helper2.get(r).set(c, data);
						L2.put(sn2, helper2);
					}

					if (helper1.size() < k) { 
						ArrayList<Integer> h = new ArrayList<>();
						for (int j = 0; j < b; j++) {
							h.add(Integer.MAX_VALUE);
						}
						if (cmd2) {
							h.set(c, data);
						}
						helper1.put(r, h);
						L1.put(sn1, helper1);
						continue;
					}

					// replacement in helper1
					int tempdis = Integer.MIN_VALUE;
					for (Entry<Integer, ArrayList<Integer>> mapElement : helper1.entrySet()) {
						int stackdis = 0;
						for (int h = freq1.size() - 1; h >= 0; h--) {

							if (freq1.get(h) == Integer.parseInt((mapElement.getKey()) + "")) {
								break;
							}
							stackdis++;
						}

						if (tempdis < stackdis) {
							tempdis = stackdis;
						}
					}

					int u = freq1.size() - tempdis - 1;
					int val = freq1.get(u);
					helper1.remove(val);
					System.out.print("Block "); getbinary(val, b); System.out.println(" gets replaced in L1 cache");
					helper1.put(r, helper2.get(r));
					freq1 = removal(freq1, val);
					// replacement done
					L1.put(sn1, helper1);

				} else {
					System.out.println("Address not found");

					if (helper1 == null && helper2 == null) {  // no block replacement needed
						ArrayList<Integer> arr = new ArrayList<>();
						for (int j = 0; j < b; j++) {
							arr.add(Integer.MAX_VALUE);
						}
						if (cmd2) {
							arr.set(c, data);
						}
						helper1 = new HashMap<>();
						helper1.put(r, arr);
						helper2 = new HashMap<>();
						helper2.put(r, arr);
						L1.put(sn1, helper1);
						L2.put(sn2, helper2);

					}

					else if (helper1 != null && helper2 == null) {
						ArrayList<Integer> arr = new ArrayList<>();
						for (int j = 0; j < b; j++) {
							arr.add(Integer.MAX_VALUE);
						}
						if (cmd2) {
							arr.set(c, data);
						}
						helper2 = new HashMap<>();
						helper2.put(r, arr);
						L2.put(sn2, helper2);
						// replace in L1
						int tempdis = Integer.MIN_VALUE;
						for (Entry<Integer, ArrayList<Integer>> mapElement : helper1.entrySet()) {
							int stackdis = 0;
							for (int h = freq1.size() - 1; h >= 0; h--) {

								if (freq1.get(h) == Integer.parseInt((mapElement.getKey()) + "")) {
									break;
								}
								stackdis++;
							}

							if (tempdis < stackdis) {
								tempdis = stackdis;
							}
						}

						int u = freq1.size() - tempdis - 1;
						int val = freq1.get(u);
						helper1.remove(val);
						System.out.print("Block "); getbinary(val, b); System.out.println(" gets replaced in L1 cache");
						freq1 = removal(freq1, val);
						helper1.put(r, arr);
						L1.put(sn1, helper1);
					}
					
					else if (helper1.size() == k && helper2.size() == k) { // replace both
						int tempdis = Integer.MIN_VALUE;
						for (Entry<Integer, ArrayList<Integer>> mapElement : helper1.entrySet()) {
							int stackdis = 0;
							for (int h = freq1.size() - 1; h >= 0; h--) {

								if (freq1.get(h) == Integer.parseInt((mapElement.getKey()) + "")) {
									break;
								}
								stackdis++;
							}

							if (tempdis < stackdis) {
								tempdis = stackdis;
							}
						}

						int u = freq1.size() - tempdis - 1;
						int val = freq1.get(u);
						helper1.remove(val);
						System.out.print("Block "); getbinary(val, b); System.out.println(" gets replaced in L1 cache");
						freq1 = removal(freq1, val);

						int tempdis2 = Integer.MIN_VALUE;
						for (Entry<Integer, ArrayList<Integer>> mapElement : helper2.entrySet()) {
							int stackdis = 0;
							for (int h = freq2.size() - 1; h >= 0; h--) {

								if (freq2.get(h) == Integer.parseInt((mapElement.getKey()) + "")) {
									break;
								}
								stackdis++;
							}

							if (tempdis2 < stackdis) {
								tempdis2 = stackdis;
							}
						}

						int u1 = freq2.size() - tempdis2 - 1;
						int val1 = freq2.get(u1);
						helper2.remove(val1);
					System.out.print("Block "); getbinary(val1, b); System.out.println(" gets replaced in L2 cache");
						freq2 = removal(freq2, val1);

						ArrayList<Integer> ar = new ArrayList<>();
						for (int j = 0; j < b; j++) {
							ar.add(Integer.MAX_VALUE);
						}
						if (cmd2) {
							ar.set(c, data);
						}
						helper1.put(r, ar);
						helper2.put(r, ar);
						L1.put(sn1, helper1);
						L2.put(sn2, helper2);
						// replace in both

					} else if (helper1.size() < k && helper2.size() < k) {   // no repl needed
						ArrayList<Integer> ar = new ArrayList<>();
						for (int j = 0; j < b; j++) {
							ar.add(Integer.MAX_VALUE);
						}
						if (cmd2) {
							ar.set(c, data);
						}
						helper1.put(r, ar);
						helper2.put(r, ar);
						L1.put(sn1, helper1);
						L2.put(sn2, helper2);

						// add in both
					} else { // space in L2 but not in L1---> replace in L1

						int tempdis1 = Integer.MIN_VALUE;
						for (Entry<Integer, ArrayList<Integer>> mapElement : helper1.entrySet()) {
							int stackdis = 0;
							for (int h = freq1.size() - 1; h >= 0; h--) {

								if (freq1.get(h) == Integer.parseInt((mapElement.getKey()) + "")) {
									break;
								}
								stackdis++;
							}

							if (tempdis1 < stackdis) {
								tempdis1 = stackdis;
							}
						}

						int u = freq1.size() - tempdis1 - 1;

						int val = freq1.get(u);
						helper1.remove(val);
						System.out.print("Block "); getbinary(val, b); System.out.println(" gets replaced in L1 cache");
						freq1 = removal(freq1, val);
						// replacement done
						ArrayList<Integer> ar = new ArrayList<>();
						for (int j = 0; j < b; j++) {
							ar.add(Integer.MAX_VALUE);
						}
						if (cmd2) {
							ar.set(c, data);
						}
						helper1.put(r, ar);
						helper2.put(r, ar);
						L1.put(sn1, helper1);
						L2.put(sn2, helper2);
					}
				}
			} else {
				System.out.println("INVALID COMMAND ENTERED!");
			}
		}

		// print map
		System.out.println("------------------------------OUTPUT----------------------------------");
		System.out.println("LEVEL ONE CACHE:");
		if (L1.size() == 0) {System.out.println("EMPTY CACHE");}
		else{PrintMap(L1, b);}
		System.out.println();
		System.out.println("LEVEL TWO CACHE:");
		if (L2.size() == 0) {System.out.println("EMPTY CACHE");}
		else {PrintMap(L2, b);}

	}

	public static ArrayList<Integer> removal(ArrayList<Integer> freq, int item) {

		ArrayList<Integer> arr = new ArrayList<>();
		for (int i = 0; i < freq.size(); i++) {
			if (freq.get(i) != item) {
				arr.add(freq.get(i));
			}
		}
		return arr;
	}

	public static void PrintMap(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> hm, int b) {

		System.out.println("Set number    Block number      Block Address             Block Content");
		for (Entry<Integer, HashMap<Integer, ArrayList<Integer>>> mapElement : hm.entrySet()) {
			System.out.print(mapElement.getKey());
			HashMap<Integer, ArrayList<Integer>> h = mapElement.getValue();

			for (Entry<Integer, ArrayList<Integer>> mapElement2 : h.entrySet()) {
System.out.print("     " + "\t" + "\t" + mapElement2.getKey() + "  \t       "); getbinary(mapElement2.getKey(), b);
System.out.print("   \t    ");
				for (int i = 0; i < mapElement2.getValue().size(); i++) {
					if (mapElement2.getValue().get(i) == Integer.MAX_VALUE) {
						System.out.print("Empty" + "  ");
					} else {
						System.out.print(mapElement2.getValue().get(i) + "  ");
					}
				}
				System.out.println();

			}

		}

	}
	
	public static boolean ispowerof2(int n) {
		if (n == 0) {
			return false;
		}
		else {
			return ((int) (Math.floor(((Math.log(n) / Math.log(2))))))+1 == ((int) (Math.ceil((Math.log(n) / Math.log(2)))))+1;
		}
	}
	
	public static void getbinary(int n, int b ) {
		
		b = getn(b);
		String str = Integer.toBinaryString(n);
		while (str.length() < 16 - b) {
			str = '0' + str;
		}
		System.out.print(str);

		
	}
	
	public static int getn(int n) {		
		return (int)(Math.log(n) / Math.log(2));}
	
	public static void get_address(int address, int b, int s) {
		String str = Integer.toBinaryString(address);
		while (str.length() < 16) {
			str = '0' + str;
		}
		int blocksize = getn(b);
		int setno = getn(s/2);
		int setno2 = getn(s);
		String offset1 = str.substring(str.length() - blocksize);
		String line1 = str.substring(str.length() - blocksize - setno, str.length() - blocksize);
		String tag1 = str.substring(0, str.length() - blocksize - setno);
		String offset2 = str.substring(str.length() - blocksize);
		String line2 = str.substring(str.length() - blocksize - setno2, str.length() - blocksize);
		String tag2 = str.substring(0, str.length() - blocksize - setno2);
		System.out.println("Physical Address: " + str + "; Tag: " + tag1 + ", Set-offset: " + line1 + ", Word-offset: "
				+ offset1+ " for L1 cache; "+" Tag: " + tag2 + ", Set-offset: " + line2 + ", Word-offset: "
				+ offset2+ " for L2 cache");

}}
