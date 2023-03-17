
public class Instruction {
	private int Instruc_ID;//指令段编号
	private int Instruc_State;//用户程序指令的类型
	private int L_Address;//用户程序指令访问的逻辑地址
	private boolean flag;//当指令类型为1时判断是否操作过一次的辅助变量
	Instruction(){
		this.flag=false;
	}
	Instruction(int Instruc_ID,int Instruc_State,int L_Address){
		this.Instruc_ID=Instruc_ID;
		this.Instruc_State=Instruc_State;
		this.L_Address=L_Address;
		this.flag=false;
	}
	public int getInstruc_ID() {
		return Instruc_ID;
	}
	public void setInstruc_ID(int instruc_ID) {
		Instruc_ID = instruc_ID;
	}
	public int getInstruc_State() {
		return Instruc_State;
	}
	public void setInstruc_State(int instruc_State) {
		Instruc_State = instruc_State;
	}
	public int getL_Address() {
		return L_Address;
	}
	public void setL_Address(int l_Address) {
		L_Address = l_Address;
	}
	public boolean getFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}
