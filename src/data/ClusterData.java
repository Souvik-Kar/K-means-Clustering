package data;

public class ClusterData {
	public int rtt;
	public float lat, lon;
	
	public void display()
	{
		System.out.println("RTT = "+rtt+"Latitude = "+lat+"Longitude= "+lon);
	}
	public void initialize(TableData t)
	{
		this.rtt=t.rtt;
		this.lat=t.lat;
		this.lon=t.lon;
	}
}
