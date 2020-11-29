import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;
// DIRECT MAPPED CACHE
public class directMapping {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scn = new Scanner(System.in);
		System.out.println("Enter Block size:");
		int b = scn.nextInt(); // block size/ cache line size
		while (!ispowerof2(b) || b<=0) {
			System.out.println("Enter valid block size");  b = scn.nextInt();		
		}
		System.out.println("Enter the number of cache lines:");
		int cl = scn.nextInt(); // no of cache lines
		while (!ispowerof2(cl) || cl<=0) {
			System.out.println("Enter valid number of cache lines");  cl = scn.nextInt();		
		}
		int cols = b;
		System.out.println("Enter the number of test cases:");
		int tc = scn.nextInt();
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L1 = new HashMap<>();
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> L2 = new HashMap<>();
		for (int i = 0; i < tc; i++) {
			System.out.println("Enter the address:");
			int address = scn.nextInt();		
			while (address > 65535 || address < 0) {
				System.out.println("Please enter a valid address, i.e. an integer from 0 to 65535");
				address = scn.nextInt();
			}
			get_address(address, b, cl);
			int r = address / cols; // block no // cache independent
			int c = address % cols; // index no
			System.out.println("Enter read/write command");
			String str = scn.next();
			int cn2 = r % cl;
			int cn1 = r % (cl / 2);
			boolean cmd1 = str.toUpperCase().equals("READ");
			boolean cmd2 = str.toUpperCase().equals("WRITE");

			if (cmd1 || cmd2) {
				int data = Integer.MAX_VALUE;
				if (cmd2) {
					System.out.println("Enter data");
					data = scn.nextInt();
				}

				HashMap<Integer, ArrayList<Integer>> helper1 = L1.get(cn1);
				HashMap<Integer, ArrayList<Integer>> helper2 = L2.get(cn2);
				if (helper1 != null && helper1.containsKey(r)) {   // no replacement
					System.out.println("Hit in Level1...!");
					if (cmd1) {  // if read
						if (helper1.get(r).get(c) == Integer.MAX_VALUE) {
							System.out.println("EMPTY!");
						} else {
							System.out.println(helper1.get(r).get(c));
						}
					} 
					else {
						helper1.get(r).set(c, data);
						helper2.get(r).set(c, data);
						L1.put(cn1, helper1);
						L2.put(cn2, helper2);
					} // if write

				} else if (helper2 != null && helper2.containsKey(r)) {   // replacement in L1
					System.out.println("Hit in Level2...!"); // read/write
					if (cmd1) {
						if (helper2.get(r).get(c) == Integer.MAX_VALUE) {
							System.out.println("EMPTY!");
						} else {
							System.out.println(helper2.get(r).get(c));
						}
					} // if read
					else {
						helper2.get(r).set(c, data);
					}
					// replace in L1
					HashMap<Integer, ArrayList<Integer>> hp = new HashMap<>();
					for (Entry<Integer, ArrayList<Integer>> mapElement : helper1.entrySet()) {
						System.out.print("Block "); getbinary(Integer.parseInt(mapElement.getKey()+""), b); System.out.println(" gets replaced in L1 cache");
						}
					hp.put(r, helper2.get(r));
					L1.put(cn1, hp);
					L2.put(cn2, helper2);

				} else {
					System.out.println("Address not found");

					if (helper1 == null && helper2 == null) {
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
						L1.put(cn1, helper1);
						L2.put(cn2, helper2);

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
						L2.put(cn2, helper2);
						// replace in L1
		for (Entry<Integer, ArrayList<Integer>> mapElement : helper1.entrySet()) {
			System.out.print("Block "); getbinary(Integer.parseInt(mapElement.getKey()+""), b); System.out.println(" gets replaced in L1 cache");
			}
						L1.put(cn1, helper1);

					} else { // replacement in both
						ArrayList<Integer> arr = new ArrayList<>();
						for (int j = 0; j < b; j++) {
							arr.add(Integer.MAX_VALUE);
						}
						if (cmd2) {
							arr.set(c, data);
						}
						HashMap<Integer, ArrayList<Integer>> hp = new HashMap<>();
						hp.put(r, arr);
		for (Entry<Integer, ArrayList<Integer>> mapElement : helper1.entrySet()) {
		System.out.print("Block "); getbinary(Integer.parseInt(mapElement.getKey()+""), b); System.out.println(" gets replaced in L1 cache");
		}
		for (Entry<Integer, ArrayList<Integer>> mapElement : helper2.entrySet()) {
			System.out.print("Block "); getbinary(Integer.parseInt(mapElement.getKey()+""), b); System.out.println(" gets replaced in L2 cache");
			}
						L1.put(cn1, hp);
						L2.put(cn2, hp);

					}

				}

			} else {
				System.out.println("INVALID COMMAND");
			}

		}

		System.out.println("----------------------OUTPUT----------------------------------");
		System.out.println("LEVEL ONE CACHE:");
		if (L1.size() == 0) {
			System.out.println("EMPTY CACHE");
		} else {
			printMapString(L1, b);
		}
		System.out.println();
		System.out.println("LEVEL TWO CACHE:");
		if (L2.size() == 0) {
			System.out.println("EMPTY CACHE");
		} else {
			printMapString(L2, b);
		}

	}

	public static void printMapString(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> hm, int b) { 
		System.out.println("CacheLine number     Block number    Block Address         Block Content");
		for (Entry<Integer, HashMap<Integer, ArrayList<Integer>>> mapElement : hm.entrySet()) {
			HashMap<Integer, ArrayList<Integer>> h = mapElement.getValue();
			for (Entry<Integer, ArrayList<Integer>> mapElement2 : h.entrySet()) {
				System.out.print(mapElement.getKey() + "  \t" + "\t" + "\t" + mapElement2.getKey() + "  \t    ");
				getbinary(mapElement2.getKey(), b);
				System.out.print("   \t ");
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
			return ((int) (Math.floor(((Math.log(n) / Math.log(2))))))
					+ 1 == ((int) (Math.ceil((Math.log(n) / Math.log(2))))) + 1;
		}
	}

	public static void getbinary(int n, int b) {
		b = getn(b);
		String str = Integer.toBinaryString(n);
		while (str.length() < 16 - b) {
			str = '0' + str;
		}
		System.out.print(str);

	}

	public static int getn(int n) {
		return (int) (Math.log(n) / Math.log(2));}

	public static void get_address(int address, int b, int cl) {
		String str = Integer.toBinaryString(address);
		while (str.length() < 16) {
			str = '0' + str;
		}
		int blocksize = getn(b);
		int lineno = getn(cl/2);
		int lineno2 = getn(cl);
		String offset1 = str.substring(str.length() - blocksize);
		String line1 = str.substring(str.length() - blocksize - lineno, str.length() - blocksize);
		String tag1 = str.substring(0, str.length() - blocksize - lineno);
		String offset2 = str.substring(str.length() - blocksize);
		String line2 = str.substring(str.length() - blocksize - lineno2, str.length() - blocksize);
		String tag2 = str.substring(0, str.length() - blocksize - lineno2);
		System.out.println("Physical Address: " + str + "; Tag: " + tag1 + ", Line-offset: " + line1 + ", Word-offset: "
				+ offset1+ " for L1 cache; "+" Tag: " + tag2 + ", Line-offset: " + line2 + ", Word-offset: "
				+ offset2+ " for L2 cache");
	}
}
