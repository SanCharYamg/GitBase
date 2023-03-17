import java.util.ArrayList;

public class CPU {
	private volatile int PC;//程序计数器
	private volatile int IR;//指令寄存器
	private volatile int PSW;//状态寄存器
	private MMU mmu;//计算物理地址的MMU
	public int getPC() {
		return PC;
	}
	public void setPC(int pC) {
		PC = pC;
	}
	public int getIR() {
		return IR;
	}
	public void setIR(int iR) {
		IR = iR;
	}
	public int getPSW() {
		return PSW;
	}
	public void setPSW(int pSW) {
		PSW = pSW;
	}
	public MMU getMmu() {
		return mmu;
	}
	public void setMmu(MMU mmu) {
		this.mmu = mmu;
	}
	CPU(){
		this.PC=0;
		this.IR=0;
		this.PSW=0;
		this.mmu=new MMU();
	}
	public CPU(int PC,int IR,int PSW){
		this.PC=PC;
		this.IR=IR;
		this.PSW=PSW;
		this.mmu=new MMU();
	}
	//现场保护
	public synchronized void Protect(PCB RunninPCB) {
		//将当前运行的进程的PC、IR、PSW改为当前CPU的相应数值
		RunninPCB.setPC(this.PC);
		RunninPCB.setIR(this.IR);
		RunninPCB.setPSW(this.PSW);
	}
	//现场恢复
	public synchronized void Recover(PCB RunninPCB,GUI gui,ArrayList<String> OutPutResult) {
		//将CPU的PC、IR、PSW改为当前运行的进程的相应数值
		this.setPC(RunninPCB.getPC());
		this.setIR(RunninPCB.getIR());
		this.setPSW(RunninPCB.getPSW());
		//运行日志加入结果队列
		OutPutResult.add(Clock.Time+":[恢复进程:"+RunninPCB.getProID()+"]\n");
		//更新图形化界面
		gui.getConsole().append(Clock.Time+":[恢复进程:"+RunninPCB.getProID()+"]\n");	
	}
	//处理进程
	public synchronized int HandleProcess(PCB RunningPCB,GUI gui,ArrayList<String> OutPutResult) {
		this.PSW=0;//转为内核态
		this.IR=this.PC;//更新IR
		RunningPCB.setRunTimes(Clock.Time-RunningPCB.getStartTimes());//设置运行时间
		RunningPCB.setTurnTimes(Clock.Time-RunningPCB.getInTimes());//设置周转时间
		Instruction I=RunningPCB.getInstructions().get(this.IR-1);//暂存当前运行指令的对象,this.IR必须-1，因为执行第1条指令在数组中的索引为0
		int REG=-1;//暂存指令类型的变量
		REG=I.getInstruc_State();
		if(REG==0) {//指令类型为0
			//运行日志加入结果队列
			OutPutResult.add(Clock.Time+":[运行进程"+RunningPCB.getProID()+","+this.IR+",0,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//更新图形化界面
			gui.getConsole().append(Clock.Time+":[运行进程"+RunningPCB.getProID()+","+this.IR+",0,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//直接更新PC即可
			this.PC++;
			return 0;
		}
		else if(REG==1) {//指令类型为1
			//运行日志加入结果队列
			OutPutResult.add(Clock.Time+":[运行进程"+RunningPCB.getProID()+","+this.IR+",1,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//更新图形化界面
			gui.getConsole().append(Clock.Time+":[运行进程"+RunningPCB.getProID()+","+this.IR+",1,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//在主程序中决定是否要更新PC
			return 1;
		}
		else if(REG==2) {//指令类型为2
			//直接更新PC
			++this.PC;
			//运行日志加入结果队列
			OutPutResult.add(Clock.Time+":[运行进程"+RunningPCB.getProID()+","+this.IR+",2,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//更新图形化界面
			gui.getConsole().append(Clock.Time+":[运行进程"+RunningPCB.getProID()+","+this.IR+",2,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			return 2;
		}
		else if(REG==3) {//指令类型为3
			//运行日志加入结果队列
			OutPutResult.add(Clock.Time+":[运行进程"+RunningPCB.getProID()+","+this.IR+",3,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//更新图形化界面
			gui.getConsole().append(Clock.Time+":[运行进程"+RunningPCB.getProID()+","+this.IR+",3,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//直接更新PC
			this.PC++;
			return 3;
		}
		else//错误情况
			return -1;
		}
}
