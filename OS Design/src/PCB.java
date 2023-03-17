import java.util.ArrayList;

public class PCB {
	private int ProID;//���̱��
	private int Priority;//����������
	private int InTimes;//���̴���ʱ��
	private int StartTimes;//���̿�ʼʱ��
	private int EndTimes;//���̽���ʱ��
	private int OfferTimes;//������ҵ����ʱ��
	private int PSW;//����״̬
	private int RunTimes;//��������ʱ���б�
	private int TurnTimes;//������תʱ��ͳ��
	private int InstructNum;//���̰�����ָ����Ŀ
	private int PC;//�����������Ϣ
	private int IR;//ָ��Ĵ�����Ϣ
	private int[] ReadyInfo;//����������Ϣ�б�
	private int[] BlockInfo1;//����������Ϣ�б�1
	private int[] BlockInfo2;//����������Ϣ�б�2
	private ArrayList<Instruction> Instructions;//���ָ�������
	private boolean flag;//��̬�����㷨�ĸ�������
	private int Start_Address;//���ڴ��е���ʼ��ַ(����)
	private int End_Address;//���ڴ��еĽ�����ַ(����)
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
	//��������
	public synchronized void CreatProcess(Job job,int Createtime) {
		this.ProID=job.getJobsID();//��ֵID
		this.Priority=job.getPriority();//��ֵ������
		this.InstructNum=job.getInstructNum();//��ֵָ����
		this.OfferTimes=job.getInTimes();//��ֵ��ҵ����ʱ��
		this.InTimes=Createtime;//��ֵ���̴���ʱ��
		this.PSW=0;//�������̬
		this.Instructions=job.getInstructions();//��ֵ��ҵ
	}
	//��������
	public void RemovePro(ArrayList<PCB> Que,int RemoveTime,GUI gui,ArrayList<String> OutPutResult) {
		this.EndTimes=RemoveTime;//���ý���ʱ��
		this.TurnTimes=RemoveTime-this.InTimes;//������תʱ��
		this.RunTimes=RemoveTime-this.StartTimes;//��������ʱ��
		//������־����������
		OutPutResult.add(Clock.Time+":[��������:"+this.ProID+"]\n");
		//����ͼ�λ�����
		gui.getConsole().append(Clock.Time+":[��������:"+this.ProID+"]\n");
		//�Ӿ����������Ƴ��ý���
		Que.remove(this);
	}
	
}
