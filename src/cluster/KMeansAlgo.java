package cluster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

import data.ClusterData;
import data.TableData;



public class KMeansAlgo {
	private int k = 10;
	ArrayList<TableData> input = new ArrayList<TableData>();
	ArrayList<ArrayList<TableData>>   output = new ArrayList<ArrayList<TableData>>();
	ClusterData cluster[] = new ClusterData[k];
	
	public void initialize()
	{
		int i;
		for(i=0; i<k; i++)
		{
			output.add(new ArrayList<TableData>());
		}
	}
	
	public void readFile(String path) throws FileNotFoundException
	{
		File f = new File(path);
		Scanner sc = new Scanner(f);
		while(sc.hasNext())
		{
			String line = sc.nextLine();
			StringTokenizer tokens = new StringTokenizer(line, ",");
			TableData temp = new TableData();
			String token = tokens.nextToken();
			if(token.equals("null"))
				continue;
			temp.ip = token;
			token = tokens.nextToken();
			if(token.equals("null"))
				continue;
			temp.lat = Float.parseFloat(token);
			token = tokens.nextToken();
			if(token.equals("null"))
				continue;
			temp.lon = Float.parseFloat(token);
			token = tokens.nextToken();
			if(token.equals("null"))
				continue;
			temp.rtt = Integer.parseInt(token);
			
			/*temp.display();
			temp.ip = sc.nextInt();
			temp.lat = sc.nextFloat();
			temp.lon = sc.nextFloat();
			temp.rtt = sc.nextInt();*/
			input.add(temp);
			//input.get(input.size()-1).display();
		}
		sc.close();
	}
	
	
	public void clustering(String path)
	{
		initialize();
		try {
			readFile(path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int i, min, dist, min_clust;
		boolean changed = true;
		//initCluster();
		initCluster_kmeansplusplus();
		while(changed)
		{
			changed=false;
			for(i=0; i<k; i++)
			{
				output.get(i).clear();
			}
			Iterator<TableData> it = input.iterator();
			while(it.hasNext())
			{
				TableData val = it.next();
				min_clust = 0;
				min = distance(0, val);
				for(i=1; i<k; i++)
				{
					dist = distance(i, val);
					if(dist<min)
					{
						min = dist;
						min_clust = i;
					}
					
				}
				output.get(min_clust).add(val);
			}
			for(i=0; i<k; i++)
			{
				ArrayList<TableData> tempa=output.get(i);
				Iterator<TableData> it3 = tempa.iterator();
				
				float sumlat=0.0f, sumlon=0.0f, mlat, mlon;
				int sumrtt=0, mrtt;
				int size=0;
				while(it3.hasNext())
				{
					TableData tempTable = it3.next();
					sumlat+=tempTable.lat;
					sumlon+=tempTable.lon;
					sumrtt+=tempTable.rtt;
					size++;
				}
				if(size==0)
					continue;
				mlat = sumlat/size;
				mlon = sumlon/size;
				mrtt = sumrtt/size;
				
				if((cluster[i].lat - mlat) > 0.5)
				{
					changed=true;
					cluster[i].lat = mlat;
				}
				
				if((cluster[i].lon - mlon) > 0.5)
				{
					changed=true;
					cluster[i].lon = mlon;
				}
				
				if(cluster[i].rtt != mrtt)
				{
					changed=true;
					cluster[i].rtt = mrtt;
				}
				
			}
		}
		//displayOutput();
		try {
			generateOutput();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int distance(int i, TableData val) {
		int dist;
		dist = (int) ((val.lat - cluster[i].lat)*(val.lat - cluster[i].lat)
				+ (val.lon - cluster[i].lon)*(val.lon - cluster[i].lon)
				+ (val.rtt - cluster[i].rtt)*(val.rtt - cluster[i].rtt));
		return dist;
	}
	
	private void initCluster_kmeansplusplus()
	{
		int i, in_size=input.size(), clusterCount=1, max_point, max_val, tableCount;
		Random rnd = new Random(System.currentTimeMillis());
		TableData temp = input.get(Math.abs(rnd.nextInt()%in_size));
		cluster[0]=new ClusterData();
		cluster[0].initialize(temp);
		while(clusterCount<k)
		{
			Iterator<TableData> it= input.iterator();
			max_point=-1;
			max_val=-1;
			in_size= input.size();
			for(tableCount=0; tableCount<in_size; tableCount++)
			{
				temp = it.next();
				//System.out.println(tableCount);
				int min_dist = distance(0, temp);
				for(i=1; i<clusterCount;i++)
				{
					int dist = distance(i, temp);
					if(dist<min_dist)
					{
						min_dist=dist;
					}
				}
				if(max_point==-1 || min_dist > max_val)
				{
					max_point = tableCount;
					max_val = min_dist;
				}
				
			}
			cluster[clusterCount]=new ClusterData();
			//System.out.println(max_point);
			TableData t = input.get(max_point);
			cluster[clusterCount].initialize(t);
			clusterCount++;
		}
		
	}

	private void initCluster() {
		int i, in_size=input.size();
		Random rnd = new Random(System.currentTimeMillis());
		for(i=0; i<k; i++)
		{
			int temp_ind = Math.abs(rnd.nextInt()%in_size);
			TableData temp = input.get(temp_ind);
			//System.out.println(temp_ind + " " + temp.rtt);
			cluster[i]=new ClusterData();
			cluster[i].initialize(temp);
			Collections.swap(input, temp_ind, in_size-1);
			in_size--;
			//cluster[i].display();
		}//initial clusters defined*/
	}
	
	private void displayOutput()
	{
		int i;
		for(i=0; i<k; i++)
		{
			System.out.println("Cluster " + i+1);
			System.out.print("Cluter Info : ");
			cluster[i].display();
			System.out.println("Contents As follows : ");
			ArrayList<TableData> contents = output.get(i);
			Iterator<TableData> it = contents.iterator();
			while(it.hasNext())
			{
				it.next().display();
			}
		}
	}
	
	private void generateOutput() throws FileNotFoundException
	{
		File [] clusters = new File[k];
		File dir = new File("clusters");
		dir.mkdir();
		File cluster_info = new File("clusters/cluster_info.txt");
		PrintWriter info = new PrintWriter(cluster_info);
		int i;
		for(i=0; i<k; i++)
		{
			System.out.println("Cluster " + i+1);
			System.out.print("Cluter Info : ");
			cluster[i].display();
			info.println("Cluster " + i+1 +"\n");
			info.println("RTT = "+cluster[i].rtt+"Latitude = "+cluster[i].lat+"Longitude= "+cluster[i].lon);;
			clusters[i] = new File("clusters/cluster_"+i);
			//clusters[i].mkdir();
			PrintWriter p = new PrintWriter(clusters[i]);
			ArrayList<TableData> contents = output.get(i);
			Iterator<TableData> it = contents.iterator();
			while(it.hasNext())
			{
				TableData temp = it.next();
				p.println(temp.ip+"\t"+temp.lat+"\t"+temp.lon+"\t"+temp.rtt);
				temp.display();
			}
			p.close();
		}
		info.close();
	}
	
	public void generateInputDataset() throws FileNotFoundException
	{
		Random rnd = new Random(System.currentTimeMillis());
		int ip = 123456, i, rtt;
		float lat, lon;
		File f = new File("gen_input.txt");
		PrintWriter p = new PrintWriter(f);
		for(i=0; i<200; i++)
		{
			lat = (Math.abs(rnd.nextFloat()*180) - 90);
			lon = (Math.abs(rnd.nextFloat()*360) - 180);
			rtt = (Math.abs(rnd.nextInt())%200);
			p.println(ip+","+lat+","+lon+","+rtt);
		}
		p.close();
	}
	
	public static void main(String args[])
	{
		KMeansAlgo algo = new KMeansAlgo();
		try {
			algo.generateInputDataset();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		algo.clustering("gen_input.txt");
		
	}
}
