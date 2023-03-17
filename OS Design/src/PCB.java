import java.util.ArrayList;

public class PCB {
	private int ProID;//进程编号
	private int Priority;//进程优先数
	private int InTimes;//进程创建时间
	private int StartTimes;//进程开始时间
	private int EndTimes;//进程结束时间
	private int OfferTimes;//储存作业请求时间
	private int PSW;//进程状态
	private int RunTimes;//进程运行时间列表
	private int TurnTimes;//进程周转时间统计
	private int InstructNum;//进程包含的指令数目
	private int PC;//程序计数器信息
	private int IR;//指令寄存器信息
	private int[] ReadyInfo;//就绪队列信息列表
	private int[] BlockInfo1;//阻塞队列信息列表1
	private int[] BlockInfo2;//阻塞队列信息列表2
	private ArrayList<Instruction> Instructions;//存放指令的数组
	private boolean flag;//静态优先算法的辅助变量
	private int Start_Address;//在内存中的起始地址(索引)
	private int End_Address;//在内存中的结束地址(索引)
	PCB(){
		this.ProID=0;
		this.Priority=0;
		this.InTimes=0;
		this.InstructNum=0;
		this.StartTimes=0;
		this.EndTimes=0;
		this.PSW=0;
		this.RunTimes=0;
		this.TurnTimes=0;
		this.OfferTimes=0;
		this.PC=1;
		this.IR=0;
		this.ReadyInfo=new int[2];
		this.ReadyInfo[0]=-1;
		this.ReadyInfo[1]=-1;
		this.BlockInfo1=new int[2];
		this.BlockInfo1[0]=-1;
		this.BlockInfo1[1]=-1;
		this.BlockInfo2=new int[2];
		this.BlockInfo2[0]=-1;
		this.BlockInfo2[1]=-1;
		this.Instructions=new ArrayList<Instruction>();
		this.flag=false;
		this.Start_Address=0;
		this.End_Address=0;
	}
	PCB(Job job,int i){
		this.ProID=job.getJobsID();
		this.Priority=job.getPriority();
		this.OfferTimes=job.getInTimes();
		this.InstructNum=job.getInstructNum();
		this.InTimes=0;
		this.StartTimes=0;
		this.EndTimes=0;
		this.PSW=0;
		this.RunTimes=0;
		this.TurnTimes=0;
		this.PC=1;
		this.IR=0;
		this.ReadyInfo=new int[2];
		this.ReadyInfo[0]=i;
		this.ReadyInfo[1]=Clock.Time;
		this.BlockInfo1=new int[2];
		this.BlockInfo1[0]=-1;
		this.BlockInfo1[1]=-1;
		this.BlockInfo2=new int[2];
		this.BlockInfo2[0]=-1;
		this.BlockInfo2[1]=-1;
		this.Instructions=job.getInstructions();
		this.flag=false;
		this.Start_Address=0;
		this.End_Address=0;
	}
	public int getProID() {
		return ProID;
	}
	public void setProID(int proID) {
		ProID = proID;
	}
	public int getPriority() {
		return Priority;
	}
	public void setPriority(int priority) {
		Priority = priority;
	}
	public int getInTimes() {
		return InTimes;
	}
	public void setInTimes(int inTimes) {
		InTimes = inTimes;
	}
	public int getEndTimes() {
		return EndTimes;
	}
	public void setEndTimes(int endTimes) {
		EndTimes = endTimes;
	}
	public int getPSW() {
		return PSW;
	}
	public void setPSW(int pSW) {
		PSW = pSW;
	}
	public int getRunTimes() {
		return RunTimes;
	}
	public void setRunTimes(int runTimes) {
		RunTimes = runTimes;
	}
	public int getTurnTimes() {
		return TurnTimes;
	}
	public void setTurnTimes(int turnTimes) {
		TurnTimes = turnTimes;
	}
	public int getInstructNum() {
		return InstructNum;
	}
	public void setInstructNum(int instructNum) {
		InstructNum = instructNum;
	}
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
	public int[] getReadyInfo() {
		return ReadyInfo;
	}
	public void setReadyInfo(int[] readyInfo) {
		ReadyInfo = readyInfo;
	}
	public int[] getBlockInfo1() {
		return BlockInfo1;
	}
	public void setBlockInfo1(int[] blockInfo1) {
		BlockInfo1 = blockInfo1;
	}
	public int[] getBlockInfo2() {
		return BlockInfo2;
	}
	public void setBlockInfo2(int[] blockInfo2) {
		BlockInfo2 = blockInfo2;
	}
	public int getStartTimes() {
		return StartTimes;
	}
	public void setStartTimes(int startTimes) {
		StartTimes = startTimes;
	}
	public ArrayList<Instruction> getInstructions() {
		return Instructions;
	}
	public void setInstructions(ArrayList<Instruction> instructions) {
		Instructions = instructions;
	}
	public boolean getFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public int getStart_Address() {
		return Start_Address;
	}
	public void setStart_Address(int start_Address) {
		Start_Address = start_Address;
	}
	public int getEnd_Address() {
		return End_Address;
	}
	public void setEnd_Address(int end_Address) {
		End_Address = end_Address;
	}
	public int getOfferTimes() {
		return OfferTimes;
	}
	public void setOfferTimes(int offerTimes) {
		OfferTimes = offerTimes;
	}
	//创建进程
	public synchronized void CreatProcess(Job job,int Createtime) {
		this.ProID=job.getJobsID();//赋值ID
		this.Priority=job.getPriority();//赋值优先数
		this.InstructNum=job.getInstructNum();//赋值指令数
		this.OfferTimes=job.getInTimes();//赋值作业请求时间
		this.InTimes=Createtime;//赋值进程创建时间
		this.PSW=0;//进入就绪态
		this.Instructions=job.getInstructions();//赋值作业
	}
	//撤销进程
	public void RemovePro(ArrayList<PCB> Que,int RemoveTime,GUI gui,ArrayList<String> OutPutResult) {
		this.EndTimes=RemoveTime;//设置结束时间
		this.TurnTimes=RemoveTime-this.InTimes;//设置周转时间
		this.RunTimes=RemoveTime-this.StartTimes;//设置运行时间
		//运行日志加入结果队列
		OutPutResult.add(Clock.Time+":[撤销进程:"+this.ProID+"]\n");
		//更新图形化界面
		gui.getConsole().append(Clock.Time+":[撤销进程:"+this.ProID+"]\n");
		//从就绪队列中移除该进程
		Que.remove(this);
	}
	
}
