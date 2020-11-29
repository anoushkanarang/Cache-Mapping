import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

public class fullyAssociativemapping{ // FULLY ASSOCIATIVE CACHE
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
		while (!ispowerof2(cl) || cl<= 0) {
			System.out.println("Enter valid number of cache lines"); cl = scn.nextInt();
		}
		int cols = b;
		ArrayList<Integer> freq1 = new ArrayList<>();
		ArrayList<Integer> freq2 = new ArrayList<>();
		System.out.println("Enter the number of test cases:");
		int tc = scn.nextInt();
		HashMap<Integer, ArrayList<Integer>> L1 = new HashMap<>();
		HashMap<Integer, ArrayList<Integer>> L2 = new HashMap<>();

		for (int i = 0; i < tc; i++) {
			System.out.println("Enter the address:");
			int address = scn.nextInt();
			while (address > 65535 || address < 0) {
				System.out.println("Please enter a valid address, i.e. an integer from 0 to 65535");
				address = scn.nextInt();
			}
			get_address(address,b);
			int r = address / cols; // block no // cache independent
			int c = address % cols; // index no
			freq1.add(r);
			freq2.add(r);
			System.out.println("Enter read/write command");
			String str = scn.next();
			boolean cmd1 = str.toUpperCase().equals("READ");
			boolean cmd2 = str.toUpperCase().equals("WRITE");

			if (cmd1 || cmd2) {
				int data = Integer.MAX_VALUE;
				if (cmd2) {
					System.out.println("Enter data to be stored");
					data = scn.nextInt();
				}

				if (L1.containsKey(r)) {
					System.out.println("Hit in Level1...!");
					if (cmd1) {   // if read
						if (L1.get(r).get(c) == Integer.MAX_VALUE) {
							System.out.println("EMPTY!");
						} else {
							System.out.println(L1.get(r).get(c));
						}}
					else {   // if write
						L1.get(r).set(c, data);
						L2.get(r).set(c, data);
					} }
				
				else if (L2.containsKey(r)) {
					System.out.println("Hit in Level2...!"); // read/write
					if (cmd1) {
						if (L2.get(r).get(c) == Integer.MAX_VALUE) {
							System.out.println("EMPTY!");
						} else {
							System.out.println(L2.get(r).get(c));
						}}				
					else {     // if write
						L2.get(r).set(c, data);
					}
					if (L1.size() < cl / 2) { // no replacement needed
						L1.put(r, L2.get(r));
						continue;
					}
					// replacement in L1
					int tempdis = Integer.MIN_VALUE;
					for (Entry<Integer, ArrayList<Integer>> mapElement : L1.entrySet()) {
						int stackdis = 0;
						for (int h = freq1.size() - 1; h >= 0; h--) {
							if (freq1.get(h) == Integer.parseInt((mapElement.getKey())+"")) {
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
					System.out.print("Block "); getbinary(val, b); System.out.println(" gets replaced in L1 cache");
					L1.remove(val);
					freq1 = removal(freq1, val);
					// replacement done
					L1.put(r, L2.get(r));   

				} else {
					System.out.println("Address not found");
					if (L1.size() == cl / 2 && L2.size() == cl) { // replace both
						int tempdis = Integer.MIN_VALUE;
						for (Entry<Integer, ArrayList<Integer>> mapElement : L1.entrySet()) {
							int stackdis = 0;
							for (int h = freq1.size() - 1; h >= 0; h--) {

								if (freq1.get(h) == Integer.parseInt((mapElement.getKey())+"")) {
									break;
								}
								stackdis++;
							}if (tempdis < stackdis) {
								tempdis = stackdis;
							}
						}
						int u = freq1.size() - tempdis - 1;
						int val = freq1.get(u);
						System.out.print("Block "); getbinary(val, b); System.out.println(" gets replaced in L1 cache");
						L1.remove(val);
						freq1 = removal(freq1, val);

						int tempdis2 = Integer.MIN_VALUE;
						for (Entry<Integer, ArrayList<Integer>> mapElement : L2.entrySet()) {
							int stackdis = 0;
							for (int h = freq2.size() - 1; h >= 0; h--) {

								if (freq2.get(h) == Integer.parseInt((mapElement.getKey())+"")) {
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
						L2.remove(val1);
						System.out.print("Block "); getbinary(val1, b); System.out.println(" gets replaced in L2 cache");
						freq2 = removal(freq2, val1);

						ArrayList<Integer> helper = new ArrayList<>();
						for (int j = 0; j < b; j++) {
							helper.add(Integer.MAX_VALUE);
						}
						if (cmd2) {
							helper.set(c, data);
						}
						L1.put(r, helper);
						L2.put(r, helper);
						// replace in both

					} else if (L1.size() < cl / 2 && L2.size() < cl) {    // no replacement
						ArrayList<Integer> helper = new ArrayList<>();
						for (int j = 0; j < b; j++) {
							helper.add(Integer.MAX_VALUE);		}
						if (cmd2) {
							helper.set(c, data);
						}
						L1.put(r, helper);
						L2.put(r, helper);
						// add in both
					} else { // space in L2 but not in L1
						int tempdis1 = Integer.MIN_VALUE;
						for (Entry<Integer, ArrayList<Integer>> mapElement : L1.entrySet()) {
							int stackdis = 0;
							for (int h = freq1.size() - 1; h >= 0; h--) {
								if (freq1.get(h) == Integer.parseInt((mapElement.getKey())+"")) {					
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
						L1.remove(val);
						System.out.print("Block "); getbinary(val, b); System.out.println(" gets replaced in L1 cache");
						freq1 = removal(freq1, val);
						// replacement done
						ArrayList<Integer> helper = new ArrayList<>();
						for (int j = 0; j < b; j++) {
							helper.add(Integer.MAX_VALUE);
						}
						if (cmd2) {
							helper.set(c, data);
						}
						L1.put(r, helper);
						L2.put(r, helper);
					}
				}
			} else {
				System.out.println("INVALID COMMAND");
			}

		}
		System.out.println("--------------------------------------OUTPUT--------------------------------------");
		System.out.println("LEVEL ONE CACHE:");
		if (L1.size() == 0) {System.out.println("EMPTY CACHE");}
		else{printfamap(L1, b);}
		System.out.println();
		System.out.println("LEVEL TWO CACHE:");
		if (L2.size() == 0) {System.out.println("EMPTY CACHE");}
		else {printfamap(L2, b);}

	}

	public static ArrayList<Integer> removal(ArrayList<Integer> freq, int item) {
		ArrayList<Integer> arr = new ArrayList<>();
		for (int i = 0; i < freq.size(); i++) {
			if (freq.get(i) != item) {
				arr.add(freq.get(i));}
		}		return arr;
	}

	public static void printfamap(HashMap<Integer, ArrayList<Integer>> hp, int b) {
		System.out.println("BLOCK NUMBER       BLOCK ADDRESS       BLOCK CONTENT");
		for (Entry<Integer, ArrayList<Integer>> mapElement : hp.entrySet()) {

	System.out.print("     " + mapElement.getKey() + "   \t "); getbinary(mapElement.getKey(),b);
	System.out.print("    \t");
			for (int i = 0; i < mapElement.getValue().size(); i++) {
				if (mapElement.getValue().get(i) == Integer.MAX_VALUE) {
					System.out.print("Empty" + "  ");
				} else {System.out.print(mapElement.getValue().get(i) + "  ");
				}
			}
			System.out.println();
		}
	}

	public static boolean ispowerof2(int n) {
		if (n == 0) {
			return false;
		}else {
			return ((int) (Math.floor(((Math.log(n) / Math.log(2))))))+1 == ((int) (Math.ceil((Math.log(n) / Math.log(2)))))+1;
		}
	}
	
	public static void getbinary(int n, int b) {
		b = getn(b);
		String str = Integer.toBinaryString(n);
		while (str.length()<16-b) {
			str='0' + str;
		}
		System.out.print(str);	
	}

	public static int getn(int n) {
	return (int)(Math.log(n) / Math.log(2));}
	
	public static void get_address(int address, int b) {		
		String str = Integer.toBinaryString(address);
		while (str.length()<16) {
			str='0' + str;
		}		
		int blocksize = getn(b);
		String offset = str.substring(str.length()-blocksize);
		String tag = str.substring(0,str.length()-blocksize);
		System.out.println("Physical Address: "+ str + ", Tag: "+ tag+", Word-Offset: "+ offset +" for both L1 and L2 caches");		
	}
}
