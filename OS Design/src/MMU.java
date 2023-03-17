
public class MMU {
	public int CalPhyAddress(int StartAddress,int L_Address) {
		//传入起始地址和逻辑地址，直接相加返回即可
		return StartAddress+L_Address;
	}
}
