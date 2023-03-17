import java.util.ArrayList;

public class CPU {
	private volatile int PC;//���������
	private volatile int IR;//ָ��Ĵ���
	private volatile int PSW;//״̬�Ĵ���
	private MMU mmu;//���������ַ��MMU
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
	//�ֳ�����
	public synchronized void Protect(PCB RunninPCB) {
		//����ǰ���еĽ��̵�PC��IR��PSW��Ϊ��ǰCPU����Ӧ��ֵ
		RunninPCB.setPC(this.PC);
		RunninPCB.setIR(this.IR);
		RunninPCB.setPSW(this.PSW);
	}
	//�ֳ��ָ�
	public synchronized void Recover(PCB RunninPCB,GUI gui,ArrayList<String> OutPutResult) {
		//��CPU��PC��IR��PSW��Ϊ��ǰ���еĽ��̵���Ӧ��ֵ
		this.setPC(RunninPCB.getPC());
		this.setIR(RunninPCB.getIR());
		this.setPSW(RunninPCB.getPSW());
		//������־����������
		OutPutResult.add(Clock.Time+":[�ָ�����:"+RunninPCB.getProID()+"]\n");
		//����ͼ�λ�����
		gui.getConsole().append(Clock.Time+":[�ָ�����:"+RunninPCB.getProID()+"]\n");	
	}
	//�������
	public synchronized int HandleProcess(PCB RunningPCB,GUI gui,ArrayList<String> OutPutResult) {
		this.PSW=0;//תΪ�ں�̬
		this.IR=this.PC;//����IR
		RunningPCB.setRunTimes(Clock.Time-RunningPCB.getStartTimes());//��������ʱ��
		RunningPCB.setTurnTimes(Clock.Time-RunningPCB.getInTimes());//������תʱ��
		Instruction I=RunningPCB.getInstructions().get(this.IR-1);//�ݴ浱ǰ����ָ��Ķ���,this.IR����-1����Ϊִ�е�1��ָ���������е�����Ϊ0
		int REG=-1;//�ݴ�ָ�����͵ı���
		REG=I.getInstruc_State();
		if(REG==0) {//ָ������Ϊ0
			//������־����������
			OutPutResult.add(Clock.Time+":[���н���"+RunningPCB.getProID()+","+this.IR+",0,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//����ͼ�λ�����
			gui.getConsole().append(Clock.Time+":[���н���"+RunningPCB.getProID()+","+this.IR+",0,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//ֱ�Ӹ���PC����
			this.PC++;
			return 0;
		}
		else if(REG==1) {//ָ������Ϊ1
			//������־����������
			OutPutResult.add(Clock.Time+":[���н���"+RunningPCB.getProID()+","+this.IR+",1,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//����ͼ�λ�����
			gui.getConsole().append(Clock.Time+":[���н���"+RunningPCB.getProID()+","+this.IR+",1,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//���������о����Ƿ�Ҫ����PC
			return 1;
		}
		else if(REG==2) {//ָ������Ϊ2
			//ֱ�Ӹ���PC
			++this.PC;
			//������־����������
			OutPutResult.add(Clock.Time+":[���н���"+RunningPCB.getProID()+","+this.IR+",2,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//����ͼ�λ�����
			gui.getConsole().append(Clock.Time+":[���н���"+RunningPCB.getProID()+","+this.IR+",2,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			return 2;
		}
		else if(REG==3) {//ָ������Ϊ3
			//������־����������
			OutPutResult.add(Clock.Time+":[���н���"+RunningPCB.getProID()+","+this.IR+",3,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//����ͼ�λ�����
			gui.getConsole().append(Clock.Time+":[���н���"+RunningPCB.getProID()+","+this.IR+",3,"+I.getL_Address()+","+mmu.CalPhyAddress(RunningPCB.getStart_Address()*100, I.getL_Address())+"]\n");
			//ֱ�Ӹ���PC
			this.PC++;
			return 3;
		}
		else//�������
			return -1;
		}
}
