import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Simulator {
	private static final int MaxConcurrent=10;//��󲢷���Ϊ10
	private static final int WaitTime=1000;//ÿ1��ʱ�Ӽ���
	private volatile CPU cpu;//������CPU
	private volatile PCB RunningPCB;//������ڴ���Ľ��̵�PCB
	private volatile ArrayList<Job> REG_Arr;//���jobs-input�ļ�������
	private volatile ArrayList<Job> ReserveQue;//�󱸶���
	private volatile ArrayList<PCB> ReadyQue;//��������
	private volatile ArrayList<PCB> BlockQue1;//������������
	private volatile ArrayList<PCB> BlockQue2;//�����������
	private volatile ArrayList<PCB> ResultQue;//����������Ķ���
	private int BlockTime1;//��������1��������ʱ��
	private int BlockTime2;//��������2��������ʱ��
	private Instruction[] RAM;//�ڴ棬����ÿ��ָ��100B����ռ��һ�������洢��Ԫ��һ��16KB������������ʼ��Ϊ����Ϊ160�Ķ�������
	private JobRequest_thread JobReq;//��ҵ�����߳�
	private LZ_Method LZ;//ʱ��Ƭ��ת�߳�
	private InputBlock_thread InBT;//���������߳�
	private OutputBlock_thread OutBT;//��������߳�
	private GUI gui;//ͼ�ν���
	private ArrayList<String> OutPutResult;//�������Ĵ����
	private ArrayList<int[]> InputBlockInfo;//����������Ϣ����
	private ArrayList<int[]> OutputBlockInfo;//���������Ϣ����
	Simulator(){//��ʼ������
		this.cpu=new CPU();
		this.RunningPCB=null;
		this.REG_Arr=new ArrayList<Job>();
		this.ReserveQue=new ArrayList<Job>();
		this.ReadyQue=new ArrayList<PCB>();
		this.BlockQue1=new ArrayList<PCB>();
		this.BlockQue2=new ArrayList<PCB>();
		this.ResultQue=new ArrayList<PCB>();
		this.BlockTime1=0;
		this.BlockTime2=0;
		this.RAM=new Instruction[160];
		this.JobReq=new JobRequest_thread();
		this.LZ=new LZ_Method();
		this.InBT=new InputBlock_thread();
		this.OutBT=new OutputBlock_thread();
		this.gui=new GUI();
		this.OutPutResult=new ArrayList<String>();
		this.InputBlockInfo=new ArrayList<int[]>();
		this.OutputBlockInfo=new ArrayList<int[]>();
	}
	public ArrayList<PCB> getResultQue() {
		return ResultQue;
	}
	public void setResultQue(ArrayList<PCB> resultQue) {
		ResultQue = resultQue;
	}
	public ArrayList<Job> getREG_Arr() {
		return REG_Arr;
	}
	public void setREG_Arr(ArrayList<Job> rEG_Arr) {
		REG_Arr = rEG_Arr;
	}
	public CPU getCpu() {
		return cpu;
	}
	public void setCpu(CPU cpu) {
		this.cpu = cpu;
	}
	public PCB getRunningPCB() {
		return RunningPCB;
	}
	public void setRunningPCB(PCB runningPCB) {
		RunningPCB = runningPCB;
	}
	public ArrayList<Job> getReserveQue() {
		return ReserveQue;
	}
	public void setReserveQue(ArrayList<Job> reserveQue) {
		ReserveQue = reserveQue;
	}
	public ArrayList<PCB> getReadyQue() {
		return ReadyQue;
	}
	public void setReadyQue(ArrayList<PCB> readyQue) {
		ReadyQue = readyQue;
	}
	public ArrayList<PCB> getBlockQue1() {
		return BlockQue1;
	}
	public void setBlockQue1(ArrayList<PCB> blockQue1) {
		BlockQue1 = blockQue1;
	}
	public ArrayList<PCB> getBlockQue2() {
		return BlockQue2;
	}
	public void setBlockQue2(ArrayList<PCB> blockQue2) {
		BlockQue2 = blockQue2;
	}
	public static int getMaxconcurrent() {
		return MaxConcurrent;
	}
	public static int getWaittime() {
		return WaitTime;
	}
	public int getBlockTime1() {
		return BlockTime1;
	}
	public void setBlockTime1(int blockTime1) {
		BlockTime1 = blockTime1;
	}
	public int getBlockTime2() {
		return BlockTime2;
	}
	public void setBlockTime2(int blockTime2) {
		BlockTime2 = blockTime2;
	}
	public Instruction[] getRAM() {
		return RAM;
	}
	public void setRAM(Instruction[] rAM) {
		RAM = rAM;
	}
	public GUI getGui() {
		return gui;
	}
	public void setGui(GUI gui) {
		this.gui = gui;
	}
	public JobRequest_thread getJobReq() {
		return JobReq;
	}
	public void setJobReq(JobRequest_thread jobReq) {
		JobReq = jobReq;
	}
	public LZ_Method getLZ() {
		return LZ;
	}
	public void setLZ(LZ_Method lZ) {
		LZ = lZ;
	}
	public InputBlock_thread getInBT() {
		return InBT;
	}
	public void setInBT(InputBlock_thread inBT) {
		InBT = inBT;
	}
	public OutputBlock_thread getOutBT() {
		return OutBT;
	}
	public void setOutBT(OutputBlock_thread outBT) {
		OutBT = outBT;
	}
	public ArrayList<String> getOutPutResult() {
		return OutPutResult;
	}
	public void setOutPutResult(ArrayList<String> outPutResult) {
		OutPutResult = outPutResult;
	}
	public ArrayList<int[]> getInputBlockInfo() {
		return InputBlockInfo;
	}
	public void setInputBlockInfo(ArrayList<int[]> inputBlockInfo) {
		InputBlockInfo = inputBlockInfo;
	}
	public ArrayList<int[]> getOutputBlockInfo() {
		return OutputBlockInfo;
	}
	public void setOutputBlockInfo(ArrayList<int[]> outputBlockInfo) {
		OutputBlockInfo = outputBlockInfo;
	}
	//��ʱ�½���ҵ(һ��ʼ����ֱ�Ӽ���󱸶���)
	public Job Creat_Job() throws IOException {
		Job NewJob=new Job();//�������ҵ�Ķ���
		File JobInput=new File("input1/jobs-input.txt");//��jobs-input�ļ�
		BufferedReader JobReader=new BufferedReader(new FileReader(JobInput));//��ȡ�ļ����ݶ���
		int i=1;//ȷ��JobID�ĸ�������
		Random ran=new Random();//����������Ķ���
		//��ȷ��Ŀǰ�ļ����ж��ٸ���ҵ���Դ�ȷ���½���ҵ��JobID
		while(JobReader.readLine()!=null)
			i++;
		NewJob.setJobsID(i);//����JobID
		NewJob.setPriority(ran.nextInt(4));//�������ȼ�
		NewJob.setInTimes(ran.nextInt(10)+Clock.Time);//��������ʱ��
		NewJob.setInstructNum((ran.nextInt(20))+1);//����ָ����
		//�����ɵ���ҵ��Ϣд��jobs-input�ļ���
		BufferedWriter JobWriter=new BufferedWriter(new FileWriter(JobInput,true));
		JobWriter.write("\n"+NewJob.getJobsID()+","+NewJob.getPriority()+","+NewJob.getInTimes()+","+NewJob.getInstructNum());
		//Writer�������رգ�����д�����
		JobReader.close();
		JobWriter.close();
		//����ָ���ļ�
		File Instructs=new File("input1/"+NewJob.getJobsID()+".txt");
		if(!Instructs.exists())//���ļ�������
			Instructs.createNewFile();//�����ļ�
		BufferedWriter InsWriter=new BufferedWriter(new FileWriter(Instructs));//д��ָ���ļ��Ķ���
		Instruction InsREG=new Instruction();//����µ����ָ��Ķ���
		for(int j=1;j<NewJob.getInstructNum();j++) {
			InsREG.setInstruc_ID(j);//ָ��ID
			InsREG.setInstruc_State(ran.nextInt(4));//ָ������
			InsREG.setL_Address(ran.nextInt(NewJob.getInstructNum()/2));//�û�����ָ����ʵ��߼���ַ(L_Address)
			NewJob.getInstructions().add(InsREG);//������ҵ��ָ�����²�����ָ��
			InsWriter.write(InsREG.getInstruc_ID()+","+InsREG.getInstruc_State()+","+InsREG.getL_Address()+"\n");//д���ļ�
			InsREG=new Instruction();//���¼Ĵ�ָ����󣬲��������ָ���һ��
		}
		//���һ��ָ��д��ʱ����Ҫ���У���˵���д��
		InsREG.setInstruc_ID(NewJob.getInstructNum());//ָ��ID
		InsREG.setInstruc_State(ran.nextInt(4));//ָ������
		InsREG.setL_Address(ran.nextInt(NewJob.getInstructNum()/2));//�û�����ָ����ʵ��߼���ַ(L_Address)
		NewJob.getInstructions().add(InsREG);//������ҵ��ָ�����²�����ָ��
		InsWriter.write(InsREG.getInstruc_ID()+","+InsREG.getInstruc_State()+","+InsREG.getL_Address());//д���ļ�(������)
		InsWriter.close();//�ر�д�����
		//�����н�����ӽ��������Ĵ����
		this.OutPutResult.add(Clock.Time+":[�½���ҵ:"+NewJob.getJobsID()+","+NewJob.getInTimes()+","+NewJob.getInstructNum()+"]\n");
		//ͼ�λ��������
		gui.getConsole().append(Clock.Time+":[�½���ҵ:"+NewJob.getJobsID()+","+NewJob.getInTimes()+","+NewJob.getInstructNum()+"]\n");
		//�ڿ�ʼִ��֮ǰ�͵���½���ҵ�Ļ���ֻ��Ҫ�½��ļ���д�뼴�ɡ��ڿ�ʼִ��֮�����ҵ�������Զ���������ҵ���뵽��ʱ������
		return NewJob;
	}
	//���й������½���ҵֱ�Ӽ���󱸶���
	public void AddJob() {
		//��ʼִ��֮�󴴽�����ҵӦ������ʱ����
		try {
			this.REG_Arr.add(Creat_Job());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//��job-input�ļ�������
	public synchronized void Read_Job_To_Array() throws IOException {
		File Job_Input=new File("input1/jobs-input.txt");//��jobs_input�ļ�
		File Instructions;//�򿪸�ָ���ļ����ļ�����
		BufferedReader JobReader=new BufferedReader(new FileReader(Job_Input));//��ȡjobs_input����
		BufferedReader InstructReader;//��ȡָ���ļ�����
		String str;//��ȡʱ���ݴ��ַ���
		String[] REGstr = null;//�����ŷָ�ļĴ��ַ�������
		while((str=JobReader.readLine())!=null){//����ȡ���ַ�����Ϊ��ʱ����ִ�У��������һ�н���
			REGstr=str.split(",");//�����ŷָ�
			this.REG_Arr.add(new Job(Integer.parseInt(REGstr[0]), Integer.parseInt(REGstr[1]), Integer.parseInt(REGstr[2]), Integer.parseInt(REGstr[3])));//�����������Ӷ�ȡ����ҵ
		}
		for(int i=0;i<REG_Arr.size();i++) {//��ȡָ���ļ��������鳤�����ƶ�ȡ�ļ�����
			Instructions=new File("input1/"+this.REG_Arr.get(i).getJobsID()+".txt");//��ָ��ָ���ļ�
			InstructReader=new BufferedReader(new FileReader(Instructions));//��ȡ�ļ�
			while((str=InstructReader.readLine())!=null) {
				REGstr=str.split(",");//�ָ�ָ�����
				this.REG_Arr.get(i).getInstructions().add(new Instruction(Integer.parseInt(REGstr[0]), Integer.parseInt(REGstr[1]), Integer.parseInt(REGstr[2])));//����ָ��
			}
		}
		//�رն�ȡ����
		JobReader.close();
	}
	//�ж��ڴ��Ƿ��ã�����������㷨(worst fit)
	public ArrayList<Integer> Worst_Fit(Job job) {
		int Start_Pos;//��¼��ʼλ��
		int Size=0;//��¼�ж��ٸ����ڴ��
		ArrayList<Integer> Result=new ArrayList<Integer>();//���صĽ�����飬�����Ƿ�����ı�־(1��ʾ���㣬0��ʾ������)����ʼλ��
		ArrayList<Integer> REG=new ArrayList<Integer>();//��ſ��ڴ�������������
		ArrayList<ArrayList<Integer>> SpareREG=new ArrayList<ArrayList<Integer>>();//���ÿ�������Ŀ��ڴ�����ʼλ�úʹ�С
		for(int i=0;i<this.RAM.length;i++) {//����һ�飬�����п��ڴ��������������
			if(this.RAM[i]==null)
				REG.add(i);
		}
		//ֻ��һ�����ÿ��п�
		if(REG.size()==1) {
			ArrayList<Integer> Temp=new ArrayList<Integer>();//��ʱ����������ڴ��Ķ���
			Temp.add(1);//����ֻ��1
			Temp.add(REG.get(0));//������ʼλ��
			SpareREG.add(Temp);//��������
		}
		//û�п��õĿ��п�
		if(REG.size()==0)
			Result.add(0);
		else {//���������Ͽ��ÿ��п�
			Start_Pos=REG.get(0);//��һ����ʼλ��
			while(REG.size()>1) {//��������2��������Ԫ��ʱ��ִ��
				if((REG.get(1)-REG.get(0))==1) {//�ڴ������
					++Size;//���ӿ��ڴ������
					REG.remove(0);//�Ƴ��ձ��������ڴ��
				}
				else {//��һ�����ڴ�鲻�����������ÿ����ڴ��С����ʼ��ַ����������������飬�����ٽ��жԱ�
					ArrayList<Integer> Temp=new ArrayList<Integer>();//��ʱ����������ڴ��Ķ���
					Temp.add(Size+1);//��ʱ�Ѿ������������ڴ�����һ�飬��Size��û��+1����˽�Size+1������ʱ����(��ȻҲ������++Size�ٴ���)
					Temp.add(Start_Pos);//������ʼλ��
					SpareREG.add(Temp);//��������
					Start_Pos=REG.get(1);//������ʼλ��
					Size=0;//���������ڴ��С
					REG.remove(0);//�Ƴ��ձ��������ڴ��
				}
				if(REG.size()==2) {//������ֻ������Ԫ��ʱ
					if((REG.get(1)-REG.get(0))==1) {//��Ȼ���ڣ�������Ĳ���һ��������SizeҪ+2
						ArrayList<Integer> Temp=new ArrayList<Integer>();
						Temp.add(Size+2);//����+1����Ϊ������REG[0]ȴû����Size+1������ʱREG[1]Ҳ�ǿ��ڴ�飬���Ҫ+2
						Temp.add(Start_Pos);
						SpareREG.add(Temp);
					}
					else {//������
						//����������ʱ����
						ArrayList<Integer> Temp1=new ArrayList<Integer>();
						ArrayList<Integer> Temp2=new ArrayList<Integer>();
						//����ִ�к������ڴ�鲻����ʱһ���Ĳ���
						Temp1.add(Size+1);
						Temp1.add(Start_Pos);
						SpareREG.add(Temp1);
						//Ϊ���һ�����п�����һ������
						Temp2.add(1);
						Temp2.add(REG.get(1));
						SpareREG.add(Temp2);
					}
					//����������ȫ���Ƴ�
					REG.removeAll(REG);//��REG������ȫ���Ƴ�
				}
			}
			//��ʼ��SpareREG��Ѱ�ҳ������Ŀ����ڴ�
			while(SpareREG.size()>1) {//�������鳤�ȴ��ڵ���2ʱ
				if(SpareREG.get(0).get(0)>=SpareREG.get(1).get(0))//��һ���ڴ�鳤�ȴ�
					SpareREG.remove(1);//�Ƴ��ڶ���
				else
					SpareREG.remove(0);//�����Ƴ���һ��
			}
			//���ʣ�µ��Ǹ����ǳ��������ڴ��
			//�Ƚ��ڴ���Ƿ���
			if(job.getInstructNum()<=SpareREG.get(0).get(0)) {//����
				Result.add(1);//���ڴ��Ƿ��ñ�־��Ϊ1����ʾ����
				Result.add(SpareREG.get(0).get(1));//���������ڴ����ʵλ��
			}
			else {//������
				Result.add(0);//���ڴ��Ƿ��ñ�־��Ϊ0����ʾ������
				//��Ȼ�ڴ涼����������Ҳ��û��Ҫ�����ڴ��ַ�ˣ�����ں����ж���Ҳ���������鳤����1����2��ȷ���ڴ��Ƿ���
			}
		}
		//���ؽ��
		return Result;
	}
	
	//����ҵ��������ת�Ƶ��󱸶���,�����������еĽ�����С����󲢷������ڴ��㹻����ֱ�Ӽ����������
	public synchronized void REG_To_Reserve() {
		//����󱸶���
		for(int i=0;i<this.REG_Arr.size();i++) {//�������д������ʱ������Ķ���
			if(this.REG_Arr.get(i).getInTimes()<=Clock.Time) {//��ҵ������ʱ��С�ڵ��ڵ�ǰ��ʱ�䣬����ת���󱸶���
				Job REG=new Job();//�½���ʱ��ҵ����
				REG=this.REG_Arr.get(i);//������ҵ
				this.ReserveQue.add(REG);//������󱸶���
				//�����е�״̬��Ϣ���뵽�������
				this.OutPutResult.add(Clock.Time+":[������ҵ:"+REG.getJobsID()+","+REG.getInTimes()+","+REG.getInstructNum()+"]\n");
				//����ͼ�λ�����
				gui.getConsole().append(Clock.Time+":[������ҵ:"+REG.getJobsID()+","+REG.getInTimes()+","+REG.getInstructNum()+"]\n");
				//���Ѿ�����󱸶��еĶ����Ƴ���ʱ����
				this.REG_Arr.remove(i);
				//����������-1
				--i;
			}
		}
		//�ж��Ƿ���ֱ�Ӽ����������
		for(int i=0;i<this.ReserveQue.size();i++) {
			//���ж��ڴ��Ƿ���
			ArrayList<Integer> REG=this.Worst_Fit(this.ReserveQue.get(i));
			if(this.ReadyQue.size()<MaxConcurrent&&REG.get(0)==1) {//��������С����󲢷������ڴ湻��
				this.Reserve_To_Ready(this.ReserveQue.get(i),REG.get(1));//��ת����
				this.ReserveQue.remove(i);//�Ƴ�����������е���ҵ
				--i;//��С��������
			}
		}
	}
	//�󱸶������ݼ����������
	public synchronized void Reserve_To_Ready(Job REG,int StartPos) {
		PCB pro=new PCB();//�½����̶��󣬽�Ҫ����������е���ҵ��Ϊ����
		pro.CreatProcess(REG, Clock.Time);//֮�����PCB�Ľ��̴�����������������
		pro.setStart_Address(StartPos);//������ʼ��ַ
		pro.setEnd_Address((StartPos+pro.getInstructNum()));//���ý�����ַ
		//���þ���������Ϣ�б�
		pro.getReadyInfo()[0]=this.ReadyQue.size()+1;//�ھ��������е�λ�ã�1Ϊ��ʼ
		pro.getReadyInfo()[1]=Clock.Time;//���ý���������е�ʱ��
		this.ReadyQue.add(pro);//��ӽ���������
		for(int i=pro.getStart_Address(),j=0;i<pro.getEnd_Address();i++,j++) {//����ڴ�
			RAM[i]=pro.getInstructions().get(j);//������������еĽ��̵�ÿ��ָ������ڴ�
			gui.getRAM()[i].setText("Pro"+pro.getProID());//����ͼ�λ�����
		}
		//��������־��Ϣ�����������
		this.OutPutResult.add(Clock.Time+":[��������:"+pro.getProID()+","+pro.getStart_Address()*100+",�����ڴ����]\n");
		//����ͼ�λ�����
		gui.getConsole().append(Clock.Time+":[��������:"+pro.getProID()+","+pro.getStart_Address()*100+",�����ڴ����]\n");
		//��������־��Ϣ�����������
		this.OutPutResult.add(Clock.Time+":[�����������:"+pro.getProID()+","+(pro.getInstructNum()-pro.getIR())+"]\n");
		//����ͼ�λ�����
		gui.getConsole().append(Clock.Time+":[�����������:"+pro.getProID()+","+(pro.getInstructNum()-pro.getIR())+"]\n");
	}
	//����̬��������̬
	public synchronized void Ready_To_Run(PCB Pro) {
		this.RunningPCB=Pro;//���õ�ǰ���еĽ���
		this.RunningPCB.setFlag(true);//����̬���ȵ��ȱ�־��ΪTrue����ʾ��������һ��
		if(this.RunningPCB.getIR()==0) {//��һ������
			this.RunningPCB.setStartTimes(Clock.Time);//���ÿ�ʼ���е�ʱ��
			this.cpu.setPC(1);//����PCΪ1
			}
		else //���ǵ�һ��ִ��
			this.cpu.Recover(this.RunningPCB,gui,this.OutPutResult);//�����ֳ��ָ�����
		//�����Ƴ���������
		this.ReadyQue.remove(Pro);
	}
	//��̬�����㷨
	//ԭ���ľ�̬�����㷨����ץ��һ�����̲��ŵ������صļ�������������������̫С����������õ��㷨Ϊ��ÿһ�������ȼ��������й���ı��־����ȷ�����������̻�û�е���ʱ�������������
	public synchronized void JTYX() {
		int REG=-1;//�Ƚ����ȼ���С�ı���
		int pos=-1;//����ѡ���Ľ��̵�λ��
		//�ж��Ƿ�ִ����һ�ֵ���
		boolean flag=true;
		for(int i=0;i<this.ReadyQue.size();i++) {
			if(flag)
				flag=flag&&this.ReadyQue.get(i).getFlag();//���������������н��̵����б�־���룬ȫΪtrueʱ���ʾȫ�����й�һ�Σ�һ�ֽ��������¿�ʼ
			else
				break;
		}
		if(flag) {//һ�ֽ���
			for(int i=0;i<this.ReadyQue.size();i++)
				this.ReadyQue.get(i).setFlag(false);//�����н��̵����б�־������Ϊfalse
		}
		//�����Ƿ����һ�ֵ��ȣ����þ�̬�����㷨������ѡ�����̽�������̬�����ֱ��д���ȷ�������
		for(int i=0;i<this.ReadyQue.size();i++) {//���������ھ��������еĽ���
			if(!this.ReadyQue.get(i).getFlag()) {//���б�־Ϊfalse��������һ���л�û�е���
				if(pos==-1) {//��һ�ֵ�һ�α�����Ҳ��û�����ȼ��ɹ��Ƚ�
					REG=this.ReadyQue.get(i).getPriority();//�������ȼ�
					pos=i;//���ý���λ��
				}
				else {
					if(this.ReadyQue.get(i).getPriority()<REG) {//�ý����и��ߵ����ȼ�
						REG=this.ReadyQue.get(i).getPriority();//�������ȼ�
						pos=i;//����λ��
					}
				}
			}
		}
		//ѭ�����pos��Ϊ��һ�������ȼ���ߵĵĽ��̵�λ��
		this.Ready_To_Run(this.ReadyQue.get(pos));//���þ��������з���ʹѡ���Ľ��̽�������̬
	}
	//����̬���ؾ���̬
	public synchronized void Run_To_Ready() {
		this.cpu.Protect(this.RunningPCB);//�����ֳ�
		this.RunningPCB.getReadyInfo()[0]=this.ReadyQue.size()+1;//�����ھ��������е�λ�ã���Ϊ��ֱ�Ӽ����β����δ����������У�����λ���Ǵ�С+1
		this.RunningPCB.getReadyInfo()[1]=Clock.Time;//���½����������ʱ��
		this.ReadyQue.add(this.RunningPCB);//���ؾ�������
		//������־����������
		this.OutPutResult.add(Clock.Time+":[���½����������:"+RunningPCB.getProID()+","+(RunningPCB.getInstructNum()-RunningPCB.getIR())+"]\n");
		//����ͼ�λ�����
		gui.getConsole().append(Clock.Time+":[���½����������:"+RunningPCB.getProID()+","+(RunningPCB.getInstructNum()-RunningPCB.getIR())+"]\n");
		//�Ƴ���ǰ���еĽ���
		this.RunningPCB=null;
	}
	//����̬��������̬1
	public synchronized void Run_To_Block1() {
		this.cpu.Protect(this.RunningPCB);//�����ֳ�
		if(this.BlockQue1.size()==0) {//�����������ӵ�һ������
			this.BlockTime1=Clock.Time;//���ÿ�ʼʱ��
		}
		this.RunningPCB.getBlockInfo1()[0]=this.BlockQue1.size()+1;//�����������е�λ�ã�1Ϊ��ʼ
		this.RunningPCB.getBlockInfo1()[1]=Clock.Time;//����ʱ��
		this.BlockQue1.add(this.RunningPCB);//����������������
		int[] REG=new int[2];//�������������Ϣ����ʱ����
		REG[0]=RunningPCB.getProID();REG[1]=Clock.Time;//���ý���ID�ͽ���ʱ��
		this.InputBlockInfo.add(REG);//�������������
		REG=null;//�����ʱ����
		//������־��ӵ��������
		this.OutPutResult.add(Clock.Time+":[��������:InputBlock_thread,"+RunningPCB.getProID()+"]\n");
		//����ͼ�λ�����
		gui.getConsole().append(Clock.Time+":[��������:InputBlock_thread,"+RunningPCB.getProID()+"]\n");
		//������н���
		this.RunningPCB=null;
	}
	//����̬��������̬2
	public synchronized void Run_To_Block2() {
		this.cpu.Protect(this.RunningPCB);//�����ֳ�
		if(this.BlockQue2.size()==0) {//�����������ӵ�һ������
			this.BlockTime2=Clock.Time;//���ÿ�ʼʱ��
		}
		this.RunningPCB.getBlockInfo2()[0]=this.BlockQue2.size()+1;//�����������е�λ�ã�1Ϊ��ʼ
		this.RunningPCB.getBlockInfo2()[1]=Clock.Time;//����ʱ��
		this.BlockQue2.add(this.RunningPCB);//����������������
		int[] REG=new int[2];//�������������Ϣ����ʱ����
		REG[0]=RunningPCB.getProID();REG[1]=Clock.Time;//���ý���ID�ͽ���ʱ��
		this.OutputBlockInfo.add(REG);//�������������
		REG=null;//�����ʱ����
		//������־��ӵ��������
		this.OutPutResult.add(Clock.Time+":[��������:OutputBlock_thread,"+RunningPCB.getProID()+"]\n");
		//����ͼ�λ�����
		gui.getConsole().append(Clock.Time+":[��������:OutputBlock_thread,"+RunningPCB.getProID()+"]\n");
		//������н���
		this.RunningPCB=null;
	}
	//����̬���ؾ���̬1
	public synchronized void Block_To_Ready1() {
		if(BlockQue1.get(0).getIR()<BlockQue1.get(0).getInstructNum()) {//����û����
			BlockQue1.get(0).getReadyInfo()[0]=this.ReadyQue.size()+1;//�����ھ��������е�λ��
			BlockQue1.get(0).getReadyInfo()[1]=Clock.Time;//���ý���ʱ��
			ReadyQue.add(BlockQue1.get(0));//��ӵ���������
			//������־��ӵ��������
			this.OutPutResult.add(Clock.Time+":[����̬���½����������:"+BlockQue1.get(0).getProID()+","+(BlockQue1.get(0).getInstructNum()-BlockQue1.get(0).getIR())+"]\n");
			//����ͼ�λ�����
			gui.getConsole().append(Clock.Time+":[����̬���½����������:"+BlockQue1.get(0).getProID()+","+(BlockQue1.get(0).getInstructNum()-BlockQue1.get(0).getIR())+"]\n");
			//�Ƴ���������
			BlockQue1.remove(0);
		}
		else {//�����Ѿ�������
			//�½�һ�����̶�����Ҫ�����Ľ���
			PCB REG=BlockQue1.get(0);
			REG.RemovePro(BlockQue1, Clock.Time,gui,OutPutResult);//�Ƴ�����
			for(int i=REG.getStart_Address();i<REG.getEnd_Address();i++) {//�����̴��ڴ����Ƴ�
				RAM[i]=null;//���ռ�õ��ڴ�
				gui.getRAM()[i].setText(null);//����ͼ�λ�����
			}
			//������������
			ResultQue.add(REG);
			//������־��ӵ��������
			this.OutPutResult.add(Clock.Time+":[��������:"+REG.getProID()+"]\n");
			//����ͼ�λ�����
			gui.getConsole().append(Clock.Time+":[��������:"+REG.getProID()+"]\n");
		}
	}
	//����̬���ؾ���̬2
	public synchronized void Block_To_Ready2() {
		if(BlockQue2.get(0).getIR()<BlockQue2.get(0).getInstructNum()) {//����û����
			BlockQue2.get(0).getReadyInfo()[0]=this.ReadyQue.size()+1;//�����ھ��������е�λ��
			BlockQue2.get(0).getReadyInfo()[1]=Clock.Time;//���ý���ʱ��
			ReadyQue.add(BlockQue2.get(0));//��ӵ���������
			//������־��ӵ��������
			this.OutPutResult.add(Clock.Time+":[����̬���½����������:"+BlockQue2.get(0).getProID()+","+(BlockQue2.get(0).getInstructNum()-BlockQue2.get(0).getIR())+"]\n");
			//����ͼ�λ�����
			gui.getConsole().append(Clock.Time+":[����̬���½����������:"+BlockQue2.get(0).getProID()+","+(BlockQue2.get(0).getInstructNum()-BlockQue2.get(0).getIR())+"]\n");
			//�Ƴ���������
			BlockQue2.remove(0);
		}
		else {//�����Ѿ�������
			//�½�һ�����̶�����Ҫ�����Ľ���
			PCB REG=BlockQue2.get(0);
			REG.RemovePro(BlockQue2, Clock.Time,gui,OutPutResult);//�Ƴ�����
			for(int i=REG.getStart_Address();i<REG.getEnd_Address();i++) {//�����̴��ڴ����Ƴ�
				RAM[i]=null;//���ռ�õ��ڴ�
				gui.getRAM()[i].setText(null);//����ͼ�λ�����
			}
			//������������
			ResultQue.add(REG);
			//������־��ӵ��������
			this.OutPutResult.add(Clock.Time+":[��������:"+REG.getProID()+"]\n");
			//����ͼ�λ�����
			gui.getConsole().append(Clock.Time+":[��������:"+REG.getProID()+"]\n");
		}
	}
	//���̵����߳�(ʱ���ж�)
	public class ProcessScheduling_thread extends TimerTask{
		int REG=-1;//���ָ�����͵Ķ���
		public void run() {
			ShowAll();//ÿ���Ӹ���ͼ�λ������е�����
			if(RunningPCB!=null) {//������ִ�еĽ���
				//�����������÷���ֵ�������0���������У������1���ж��Ƿ���Ҫ�ӳ�ʱ��Ƭ�������2��3������������У�Ȼ���������߳����ͷż���
				REG=cpu.HandleProcess(RunningPCB,gui,OutPutResult);
				if(REG==0) {//ָ������Ϊ0�������У�Ҳ���Բ�д
					
				}
				else if(REG==1) {//ָ������Ϊ1����Ҫ�ж�ʱ��Ƭ�Ƿ���
					if(!RunningPCB.getInstructions().get(cpu.getIR()-1).getFlag()) {//�տ�ʼִ������ָ��
						if(Clock.TimeSlice==1) {//ʱ��Ƭ������
							Clock.TimeSlice--;//�ӳ�1s
						}
						RunningPCB.getInstructions().get(cpu.getIR()-1).setFlag(true);//���б�־��Ϊtrue����ʾ�Ѿ�������1s
						cpu.setIR(cpu.getIR()-1);//IR����һ����ʹ����һ��cpu��Ȼִ������ָ��
					}
					else {//�Ѿ�ִ����1s����ʱʱ��Ƭ�ض�Ϊ1�����Բ��ùܣ���cpu��pc+1����
						cpu.setPC(cpu.getPC()+1);
					}
				}
				else if(REG==2) {//ָ������Ϊ2����ʾ�����������������С������ķ���1���ɣ�ͬʱ��RunningPCB��Ϊnull��ʱ��Ƭ��Ϊ0��ʹ��һ�������ܹ���������
					Run_To_Block1();//������������
					Clock.TimeSlice=0;//��ʱ��Ƭ���㣬��֤��һ��������������
				}
				else if(REG==3) {//ָ������Ϊ3����ʾ����������������С������ķ���2���ɣ�ͬʱ��RunningPCB��Ϊnull��ʱ��Ƭ��Ϊ0��ʹ��һ�������ܹ���������
					Run_To_Block2();//������������
					Clock.TimeSlice=0;//��ʱ��Ƭ���㣬��֤��һ��������������
				}
				//�����жϽ����Ƿ���Խ���
				if(RunningPCB!=null) {//������ִ�еĽ���
					if(cpu.getPC()>RunningPCB.getInstructNum()) {//ָ������
						RunningPCB.RemovePro(ReadyQue, Clock.Time,gui,OutPutResult);//�Ƴ�����
						for(int i=RunningPCB.getStart_Address();i<RunningPCB.getEnd_Address();i++) {//�����̴��ڴ����Ƴ�
							RAM[i]=null;//���ռ���ڴ�
							gui.getRAM()[i].setText(null);//����ͼ�λ�����
						}
						//������������
						ResultQue.add(RunningPCB);
						//������־��ӵ��������
						OutPutResult.add(Clock.Time+":[��������:"+RunningPCB.getProID()+"]\n");
						//����ͼ�λ�����
						gui.getConsole().append(Clock.Time+":[��������:"+RunningPCB.getProID()+"]\n");
						//������еĽ���
						RunningPCB=null;
						//����ʱ��Ƭ
						Clock.TimeSlice=0;
					}
					else//ָ��û���꣬����δ����
						++Clock.TimeSlice;//����ʱ��Ƭ
				}
			}
			else {//û��ִ�еĽ���
				if(ReadyQue.size()==0) {//û����Ҫ����Ľ���
					//������־��ӵ��������
					OutPutResult.add(Clock.Time+":[CPU����]\n");
					//����ͼ�λ�����
					gui.getConsole().append(Clock.Time+":[CPU����]\n");
				}
				else//���������л��еȴ����еĽ���
					JTYX();//���þ�̬�����㷨
			}
			++Clock.Time;//����ʱ��
		}
	}
	//��ҵ�����߳�,10���ѯһ��REGArr����,������Ҫ�������󱸶���
	public class JobRequest_thread extends Thread{ 
		public synchronized void run() {
			while(true) {
				if(Clock.Time>0&&Clock.Time%10==0) {//ÿ10s����һ��
					REG_To_Reserve();//������ʱ������󱸶��з���
				}
			}
		}
	}
	//ʱ��Ƭ��ת�㷨
	public class LZ_Method extends Thread{
		public synchronized void run() {
			while(true) {
				if(Clock.TimeSlice==2) {//ʱ��Ƭ��
					if(ReadyQue.size()!=0&&RunningPCB!=null) {//���������л��еȴ����еĽ��̶��Ҵ�ʱ�н����������У��ŵ�����������
						Run_To_Ready();//����ǰִ�еĽ����ͻؾ�������
						JTYX();//���þ�̬�����㷨
					}
					Clock.TimeSlice=0;//����ʱ��Ƭ
				}
			}
		}
	}
	//���������߳�
	public class InputBlock_thread extends Thread{
		public synchronized void run() {
			while(true) {//����ѭ��һֱ������������Ƿ�Ϊ��
				if(BlockQue1.size()>0) {//�������зǿ�
					if((Clock.Time-BlockTime1)>0&&(Clock.Time-BlockTime1)%2==0) {//����2s���Ƴ��������У����ؾ�������
						Block_To_Ready1();//����һ����������ת�Ƶ���������
					}
				}
			}
		}
	}
	//��������߳�
	public class OutputBlock_thread extends Thread{
		public synchronized void run() {
			while(true) {//����ѭ��һֱ������������Ƿ�Ϊ��
				if(BlockQue2.size()>0) {//�������зǿ�
					if((Clock.Time-BlockTime2)>0&&(Clock.Time-BlockTime2)%3==0) {//����3s���Ƴ��������У����ؾ�������
						Block_To_Ready2();//����һ����������ת�Ƶ���������
					}
				}
			}
		}
	}
	//��ʾ���ж���
	public void ShowAll() {
		int i=0;//ѭ������
		//�󱸶���
		while(gui.getReserveQue().getRowCount()>0)//��յ�ǰ����ʾ���У�Ҫʵ��ÿ��ʵʱ���¶�����Ϣ����Ϊ������Ҫ����addRow���������б��������
			gui.getReserveQue().removeRow(0);
		for(i=0;i<this.ReserveQue.size();i++) {
			gui.getReserveQue().addRow(new String[] {
					Integer.toString(this.ReserveQue.get(i).getJobsID()),
					Integer.toString(this.ReserveQue.get(i).getPriority()),
					Integer.toString(this.ReserveQue.get(i).getInTimes()),
					Integer.toString(this.ReserveQue.get(i).getInstructNum())
			});
		}
		//��������
		while(gui.getReadyQue().getRowCount()>0)//��յ�ǰ��ʾ�Ķ��У�Ҫʵ��ÿ��ʵʱ���¶�����Ϣ����Ϊ������Ҫ����addRow���������б��������
			gui.getReadyQue().removeRow(0);
		for(i=0;i<this.ReadyQue.size();i++) {
			gui.getReadyQue().addRow(new String[] {
					Integer.toString(this.ReadyQue.get(i).getProID()),
					Integer.toString(this.ReadyQue.get(i).getInTimes()),
					Integer.toString(this.ReadyQue.get(i).getInstructNum()),
					Integer.toString(this.ReadyQue.get(i).getIR()),
					Integer.toString(this.ReadyQue.get(i).getPC())
			});
		}
		//���ж���(��CPU��ǰִ�еĽ���)
		while(gui.getRunningQue().getRowCount()>0)//��յ�ǰ��ʾ�Ķ��У�Ҫʵ��ÿ��ʵʱ���¶�����Ϣ����Ϊ������Ҫ����addRow���������б��������
			gui.getRunningQue().removeRow(0);
		if(RunningPCB!=null) {
			gui.getRunningQue().addRow(new String[] {
					Integer.toString(RunningPCB.getProID()),
					Integer.toString(RunningPCB.getInstructNum()),
					Integer.toString(cpu.getIR()),
					Integer.toString(cpu.getPC()),
			});
		}
		//��������1
		while(gui.getBlockQue1().getRowCount()>0)//��յ�ǰ��ʾ�Ķ��У�Ҫʵ��ÿ��ʵʱ���¶�����Ϣ����Ϊ������Ҫ����addRow���������б��������
			gui.getBlockQue1().removeRow(0);
		for(i=0;i<this.BlockQue1.size();i++) {
			gui.getBlockQue1().addRow(new String[] {
					Integer.toString(this.BlockQue1.get(i).getProID()),
					Integer.toString(this.BlockQue1.get(i).getBlockInfo1()[1]),
					Integer.toString(this.BlockQue1.get(i).getInstructNum()),
					Integer.toString(this.BlockQue1.get(i).getIR()),
					Integer.toString(this.BlockQue1.get(i).getPC())
			});
		}
		//��������2
		while(gui.getBlockQue2().getRowCount()>0)//��յ�ǰ��ʾ�Ķ��У�Ҫʵ��ÿ��ʵʱ���¶�����Ϣ����Ϊ������Ҫ����addRow���������б��������
			gui.getBlockQue2().removeRow(0);
		for(i=0;i<this.BlockQue2.size();i++) {
			gui.getBlockQue2().addRow(new String[] {
					Integer.toString(this.BlockQue2.get(i).getProID()),
					Integer.toString(this.BlockQue2.get(i).getBlockInfo2()[1]),
					Integer.toString(this.BlockQue2.get(i).getInstructNum()),
					Integer.toString(this.BlockQue2.get(i).getIR()),
					Integer.toString(this.BlockQue2.get(i).getPC())
			});
		}
		//��������
		while(gui.getResultQue().getRowCount()>0)//��յ�ǰ��ʾ�Ķ��У�Ҫʵ��ÿ��ʵʱ���¶�����Ϣ����Ϊ������Ҫ����addRow���������б��������
			gui.getResultQue().removeRow(0);
		for(i=0;i<this.ResultQue.size();i++) {
			gui.getResultQue().addRow(new String[] {
					Integer.toString(this.ResultQue.get(i).getProID()),
					Integer.toString(this.ResultQue.get(i).getOfferTimes()),
					Integer.toString(this.ResultQue.get(i).getInTimes()),
					Integer.toString(this.ResultQue.get(i).getStartTimes()),
					Integer.toString(this.ResultQue.get(i).getEndTimes()),
					Integer.toString(this.ResultQue.get(i).getRunTimes())
			});
		}
		
	}
	//��������־д���ļ�
	public int Write_Log(int time) throws IOException {
		File f=new File("output1/ProcessResults-"+time+"-JTYX.txt");//�½���ҵ�ļ�����Ϊ������־��״̬��ϢҪд��һ���ļ������Բ��ô��ݲ�����ȷ��ʱ��
		if(!f.exists()) {//�ļ�������
			f.createNewFile();//�����ļ�
		}
		BufferedWriter W=new BufferedWriter(new FileWriter(f,true));//д���ļ�����
		W.write("���̵����¼�:\n");
		for(int i=0;i<this.OutPutResult.size();i++) {//�����������
			W.write(this.OutPutResult.get(i));//д������
		}
		W.close();//�ر�д�����
		return time;//���ز������ݸ�״̬��Ϣд�뷽��
	}
	//��״̬��Ϣд���ļ�
	public void WriteResult(int time) throws IOException {
		File f=new File("output1/ProcessResults-"+time+"-JTYX.txt");//�����洴�����ļ�
		if(!f.exists()) {//�ļ�һ�����ڣ���Ϊ�˴���Ľ�׳�ԣ����Ӳ������½��ļ��Ĵ���
			f.createNewFile();
		}
		BufferedWriter W=new BufferedWriter(new FileWriter(f,true));//д�����
		W.write("----------------------------------------------------\n״̬��Ϣͳ��:\n");//�ָ���
		//д�������Ϣ
		for(int i=0;i<ResultQue.size();i++) {
			W.write(ResultQue.get(i).getEndTimes()+":["+ResultQue.get(i).getProID()+":"+ResultQue.get(i).getOfferTimes()+"+"+ResultQue.get(i).getInTimes()+"+"+ResultQue.get(i).getRunTimes()+"]\n");
		}
		//д������������Ϣ
		W.write("BBBB:[InputBlock-thread:");
		for(int i=0;i<this.InputBlockInfo.size();i++) {
			W.write(InputBlockInfo.get(i)[0]+","+InputBlockInfo.get(i)[1]+";");
		}W.write("]\n");
		//д������������Ϣ2
		W.write("BBBB:[OutputBlock-thread:");
		for(int i=0;i<this.OutputBlockInfo.size();i++) {
			W.write(OutputBlockInfo.get(i)[0]+","+OutputBlockInfo.get(i)[1]+";");
		}W.write("]\n");
		//�ر�д�����
		W.close();
	}
	//��ʼִ�еĴ���(���ڿ�ʼ��ť��)
	public void Start1() {
		//������ҵ����ʱ���жϡ�ʱ��Ƭ��ת��������������߳�
		this.JobReq=new JobRequest_thread();
		ProcessScheduling_thread ProSc=new ProcessScheduling_thread();
		this.LZ=new LZ_Method();
		this.InBT=new InputBlock_thread();
		this.OutBT=new OutputBlock_thread();
		try {
			this.Read_Job_To_Array();//����ҵ������ʱ����
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Clock.clock.schedule(ProSc,0,WaitTime);//��ʼʱ���ж�
		InBT.start();//��ʼ���������߳�
		OutBT.start();//��ʼ��������߳�
		LZ.start();//��ʼʱ��Ƭ��ת�߳�
		JobReq.start();//��ʼ��ҵ�����߳�
	}
	//�������з��������ü���ִ��
	public void Start() {
		//���ڿ�ʼ��ť�ϵļ�����
		ActionListener start=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Start1();//��ʼִ��
			}
		};
		//��Ӽ�����
		gui.getRunButton().addActionListener(start);
		//���ڴ�����ҵ��ť�ϵļ�����
		ActionListener creat=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(Clock.Time==0&&REG_Arr.size()==0) {//û�е����ʼִ�а�ťʱ������ҵ
					try {
						Creat_Job();//����ֻд���ļ����½���ҵ����
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}//�Ѿ���ʼ
				else {
					//����ֱ����ӵ��󱸶��еķ���
					AddJob();
				}
			}
		};
		//��Ӽ�����
		gui.getCreateJobButton().addActionListener(creat);
		//����ֹͣ��ť�ϵļ�����
		ActionListener Stop=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					//�����н��д���ļ�����Ϊ��������ҵ����֮�������Ҳ���п����½���ҵ�������޷���������ҵ�����ʱ������������ʱ�䣬��˲��õ���������а�ť��ȷ������ʱ��ķ���д���ļ�
					WriteResult(Write_Log(Clock.Time));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//�ر�ʱ�ӣ��ж������߳�
				Clock.clock.cancel();
				Clock.clock=new Timer();
				Clock.Time=0;
				JobReq.interrupt();
				LZ.interrupt();
				InBT.interrupt();
				OutBT.interrupt();
			}
		};
		//��Ӽ�����
		gui.getStopButton().addActionListener(Stop);
		
	}
	
}
