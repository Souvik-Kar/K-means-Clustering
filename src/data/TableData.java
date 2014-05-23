package data;

public class TableData {
	public int rtt;
	public String ip;
	public float lat, lon;
	
	public void display()
	{
		System.out.println("IP = "+ip+"\tRTT = "+rtt+"\tLatitude = "+lat+"\tLongitude= "+lon);
	}
}
