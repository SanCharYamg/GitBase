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
	private static final int MaxConcurrent=10;//最大并发数为10
	private static final int WaitTime=1000;//每1秒时钟计数
	private volatile CPU cpu;//处理器CPU
	private volatile PCB RunningPCB;//存放正在处理的进程的PCB
	private volatile ArrayList<Job> REG_Arr;//存放jobs-input文件的数组
	private volatile ArrayList<Job> ReserveQue;//后备队列
	private volatile ArrayList<PCB> ReadyQue;//就绪队列
	private volatile ArrayList<PCB> BlockQue1;//输入阻塞队列
	private volatile ArrayList<PCB> BlockQue2;//输出阻塞队列
	private volatile ArrayList<PCB> ResultQue;//最后输出结果的队列
	private int BlockTime1;//阻塞队列1新增进程时间
	private int BlockTime2;//阻塞队列2新增进程时间
	private Instruction[] RAM;//内存，假设每条指令100B，即占用一个基本存储单元，一共16KB，因此在下面初始化为长度为160的定长数组
	private JobRequest_thread JobReq;//作业请求线程
	private LZ_Method LZ;//时间片轮转线程
	private InputBlock_thread InBT;//输入阻塞线程
	private OutputBlock_thread OutBT;//输出阻塞线程
	private GUI gui;//图形界面
	private ArrayList<String> OutPutResult;//输出结果寄存队列
	private ArrayList<int[]> InputBlockInfo;//输入阻塞信息队列
	private ArrayList<int[]> OutputBlockInfo;//输出阻塞信息队列
	Simulator(){//初始化对象
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
	//即时新建作业(一开始不能直接加入后备队列)
	public Job Creat_Job() throws IOException {
		Job NewJob=new Job();//存放新作业的对象
		File JobInput=new File("input1/jobs-input.txt");//打开jobs-input文件
		BufferedReader JobReader=new BufferedReader(new FileReader(JobInput));//读取文件内容对象
		int i=1;//确定JobID的辅助变量
		Random ran=new Random();//生成随机数的对象
		//先确定目前文件中有多少个作业，以此确定新建作业的JobID
		while(JobReader.readLine()!=null)
			i++;
		NewJob.setJobsID(i);//设置JobID
		NewJob.setPriority(ran.nextInt(4));//设置优先级
		NewJob.setInTimes(ran.nextInt(10)+Clock.Time);//设置申请时间
		NewJob.setInstructNum((ran.nextInt(20))+1);//设置指令数
		//将生成的作业信息写入jobs-input文件中
		BufferedWriter JobWriter=new BufferedWriter(new FileWriter(JobInput,true));
		JobWriter.write("\n"+NewJob.getJobsID()+","+NewJob.getPriority()+","+NewJob.getInTimes()+","+NewJob.getInstructNum());
		//Writer对象必须关闭，否则写入出错
		JobReader.close();
		JobWriter.close();
		//创建指令文件
		File Instructs=new File("input1/"+NewJob.getJobsID()+".txt");
		if(!Instructs.exists())//若文件不存在
			Instructs.createNewFile();//创建文件
		BufferedWriter InsWriter=new BufferedWriter(new FileWriter(Instructs));//写入指令文件的对象
		Instruction InsREG=new Instruction();//存放新的随机指令的对象
		for(int j=1;j<NewJob.getInstructNum();j++) {
			InsREG.setInstruc_ID(j);//指令ID
			InsREG.setInstruc_State(ran.nextInt(4));//指令类型
			InsREG.setL_Address(ran.nextInt(NewJob.getInstructNum()/2));//用户程序指令访问的逻辑地址(L_Address)
			NewJob.getInstructions().add(InsREG);//给新作业的指令集添加新产生的指令
			InsWriter.write(InsREG.getInstruc_ID()+","+InsREG.getInstruc_State()+","+InsREG.getL_Address()+"\n");//写入文件
			InsREG=new Instruction();//更新寄存指令对象，不更新最后指令集都一样
		}
		//最后一条指令写入时不需要换行，因此单独写出
		InsREG.setInstruc_ID(NewJob.getInstructNum());//指令ID
		InsREG.setInstruc_State(ran.nextInt(4));//指令类型
		InsREG.setL_Address(ran.nextInt(NewJob.getInstructNum()/2));//用户程序指令访问的逻辑地址(L_Address)
		NewJob.getInstructions().add(InsREG);//给新作业的指令集添加新产生的指令
		InsWriter.write(InsREG.getInstruc_ID()+","+InsREG.getInstruc_State()+","+InsREG.getL_Address());//写入文件(不换行)
		InsWriter.close();//关闭写入对象
		//将运行结果增加进输出结果寄存队列
		this.OutPutResult.add(Clock.Time+":[新建作业:"+NewJob.getJobsID()+","+NewJob.getInTimes()+","+NewJob.getInstructNum()+"]\n");
		//图形化界面更新
		gui.getConsole().append(Clock.Time+":[新建作业:"+NewJob.getJobsID()+","+NewJob.getInTimes()+","+NewJob.getInstructNum()+"]\n");
		//在开始执行之前就点击新建作业的话，只需要新建文件并写入即可。在开始执行之后读作业方法会自动将所有作业读入到临时数组中
		return NewJob;
	}
	//运行过程中新建作业直接加入后备队列
	public void AddJob() {
		//开始执行之后创建的作业应进入临时数组
		try {
			this.REG_Arr.add(Creat_Job());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//读job-input文件到数组
	public synchronized void Read_Job_To_Array() throws IOException {
		File Job_Input=new File("input1/jobs-input.txt");//打开jobs_input文件
		File Instructions;//打开各指令文件的文件对象
		BufferedReader JobReader=new BufferedReader(new FileReader(Job_Input));//读取jobs_input内容
		BufferedReader InstructReader;//读取指令文件内容
		String str;//读取时的暂存字符串
		String[] REGstr = null;//按逗号分割的寄存字符串数组
		while((str=JobReader.readLine())!=null){//当读取的字符串不为空时即可执行，读到最后一行结束
			REGstr=str.split(",");//按逗号分割
			this.REG_Arr.add(new Job(Integer.parseInt(REGstr[0]), Integer.parseInt(REGstr[1]), Integer.parseInt(REGstr[2]), Integer.parseInt(REGstr[3])));//在数组中增加读取的作业
		}
		for(int i=0;i<REG_Arr.size();i++) {//读取指令文件，用数组长度限制读取文件个数
			Instructions=new File("input1/"+this.REG_Arr.get(i).getJobsID()+".txt");//打开指定指令文件
			InstructReader=new BufferedReader(new FileReader(Instructions));//读取文件
			while((str=InstructReader.readLine())!=null) {
				REGstr=str.split(",");//分割指令参数
				this.REG_Arr.get(i).getInstructions().add(new Instruction(Integer.parseInt(REGstr[0]), Integer.parseInt(REGstr[1]), Integer.parseInt(REGstr[2])));//增加指令
			}
		}
		//关闭读取对象
		JobReader.close();
	}
	//判断内存是否够用，采用最坏适配算法(worst fit)
	public ArrayList<Integer> Worst_Fit(Job job) {
		int Start_Pos;//记录起始位置
		int Size=0;//记录有多少个空内存块
		ArrayList<Integer> Result=new ArrayList<Integer>();//返回的结果数组，包含是否满足的标志(1表示满足，0表示不满足)和起始位置
		ArrayList<Integer> REG=new ArrayList<Integer>();//存放空内存块的索引的数组
		ArrayList<ArrayList<Integer>> SpareREG=new ArrayList<ArrayList<Integer>>();//存放每段连续的空内存块的起始位置和大小
		for(int i=0;i<this.RAM.length;i++) {//遍历一遍，将所有空内存的索引存入数组
			if(this.RAM[i]==null)
				REG.add(i);
		}
		//只有一个可用空闲块
		if(REG.size()==1) {
			ArrayList<Integer> Temp=new ArrayList<Integer>();//临时存放连续空内存块的对象
			Temp.add(1);//长度只有1
			Temp.add(REG.get(0));//设置起始位置
			SpareREG.add(Temp);//新增对象
		}
		//没有可用的空闲块
		if(REG.size()==0)
			Result.add(0);
		else {//有两个以上可用空闲块
			Start_Pos=REG.get(0);//第一个开始位置
			while(REG.size()>1) {//当数组有2个及以上元素时可执行
				if((REG.get(1)-REG.get(0))==1) {//内存块相邻
					++Size;//增加空内存块数量
					REG.remove(0);//移除刚遍历过的内存块
				}
				else {//下一个空内存块不连续，则重置空闲内存大小与起始地址，并将结果存入数组，后续再进行对比
					ArrayList<Integer> Temp=new ArrayList<Integer>();//临时存放连续空内存块的对象
					Temp.add(Size+1);//此时已经遍历到连续内存的最后一块，但Size还没有+1，因此将Size+1存入临时对象(当然也可以先++Size再存入)
					Temp.add(Start_Pos);//设置起始位置
					SpareREG.add(Temp);//新增对象
					Start_Pos=REG.get(1);//重置起始位置
					Size=0;//重置连续内存大小
					REG.remove(0);//移除刚遍历过的内存块
				}
				if(REG.size()==2) {//当数组只有两个元素时
					if((REG.get(1)-REG.get(0))==1) {//仍然相邻，和上面的操作一样，但是Size要+2
						ArrayList<Integer> Temp=new ArrayList<Integer>();
						Temp.add(Size+2);//上面+1是因为遍历到REG[0]却没有让Size+1，但此时REG[1]也是空内存块，因此要+2
						Temp.add(Start_Pos);
						SpareREG.add(Temp);
					}
					else {//不相邻
						//设置两个临时对象
						ArrayList<Integer> Temp1=new ArrayList<Integer>();
						ArrayList<Integer> Temp2=new ArrayList<Integer>();
						//正常执行和上面内存块不连续时一样的操作
						Temp1.add(Size+1);
						Temp1.add(Start_Pos);
						SpareREG.add(Temp1);
						//为最后一个空闲块新增一个队列
						Temp2.add(1);
						Temp2.add(REG.get(1));
						SpareREG.add(Temp2);
					}
					//遍历结束，全部移除
					REG.removeAll(REG);//将REG的内容全部移除
				}
			}
			//开始从SpareREG中寻找长度最大的空闲内存
			while(SpareREG.size()>1) {//当该数组长度大于等于2时
				if(SpareREG.get(0).get(0)>=SpareREG.get(1).get(0))//第一个内存块长度大
					SpareREG.remove(1);//移除第二个
				else
					SpareREG.remove(0);//否则移除第一个
			}
			//最后剩下的那个就是长度最大的内存块
			//比较内存块是否够用
			if(job.getInstructNum()<=SpareREG.get(0).get(0)) {//够用
				Result.add(1);//将内存是否够用标志设为1，表示够用
				Result.add(SpareREG.get(0).get(1));//返回连续内存的其实位置
			}
			else {//不够用
				Result.add(0);//将内存是否够用标志设为0，表示不够用
				//既然内存都不够用了那也就没必要返回内存地址了，因此在后续判断中也可以用数组长度是1还是2来确定内存是否够用
			}
		}
		//返回结果
		return Result;
	}
	
	//将作业从数组中转移到后备队列,若就绪队列中的进程数小于最大并发数且内存足够，则直接加入就绪队列
	public synchronized void REG_To_Reserve() {
		//加入后备队列
		for(int i=0;i<this.REG_Arr.size();i++) {//遍历所有存放在临时数组里的对象
			if(this.REG_Arr.get(i).getInTimes()<=Clock.Time) {//作业的请求时间小于等于当前的时间，可以转至后备队列
				Job REG=new Job();//新建临时作业对象
				REG=this.REG_Arr.get(i);//设置作业
				this.ReserveQue.add(REG);//添加至后备队列
				//将运行的状态信息加入到结果队列
				this.OutPutResult.add(Clock.Time+":[新增作业:"+REG.getJobsID()+","+REG.getInTimes()+","+REG.getInstructNum()+"]\n");
				//更新图形化界面
				gui.getConsole().append(Clock.Time+":[新增作业:"+REG.getJobsID()+","+REG.getInTimes()+","+REG.getInstructNum()+"]\n");
				//将已经进入后备队列的对象移出临时数组
				this.REG_Arr.remove(i);
				//将遍历变量-1
				--i;
			}
		}
		//判断是否能直接加入就绪队列
		for(int i=0;i<this.ReserveQue.size();i++) {
			//先判断内存是否够用
			ArrayList<Integer> REG=this.Worst_Fit(this.ReserveQue.get(i));
			if(this.ReadyQue.size()<MaxConcurrent&&REG.get(0)==1) {//进程数量小于最大并发数且内存够用
				this.Reserve_To_Ready(this.ReserveQue.get(i),REG.get(1));//后备转就绪
				this.ReserveQue.remove(i);//移除进入就绪队列的作业
				--i;//减小遍历变量
			}
		}
	}
	//后备队列内容加入就绪队列
	public synchronized void Reserve_To_Ready(Job REG,int StartPos) {
		PCB pro=new PCB();//新建进程对象，将要加入就绪队列的作业变为进程
		pro.CreatProcess(REG, Clock.Time);//之间调用PCB的进程创建方法来创建进程
		pro.setStart_Address(StartPos);//设置起始地址
		pro.setEnd_Address((StartPos+pro.getInstructNum()));//设置结束地址
		//设置就绪队列信息列表
		pro.getReadyInfo()[0]=this.ReadyQue.size()+1;//在就绪队列中的位置，1为开始
		pro.getReadyInfo()[1]=Clock.Time;//设置进入就绪队列的时间
		this.ReadyQue.add(pro);//添加进就绪队列
		for(int i=pro.getStart_Address(),j=0;i<pro.getEnd_Address();i++,j++) {//填充内存
			RAM[i]=pro.getInstructions().get(j);//将进入就绪队列的进程的每个指令都存入内存
			gui.getRAM()[i].setText("Pro"+pro.getProID());//更新图形化界面
		}
		//将运行日志信息加入结束队列
		this.OutPutResult.add(Clock.Time+":[创建进程:"+pro.getProID()+","+pro.getStart_Address()*100+",连续内存分配]\n");
		//更新图形化界面
		gui.getConsole().append(Clock.Time+":[创建进程:"+pro.getProID()+","+pro.getStart_Address()*100+",连续内存分配]\n");
		//将运行日志信息加入结束队列
		this.OutPutResult.add(Clock.Time+":[进入就绪队列:"+pro.getProID()+","+(pro.getInstructNum()-pro.getIR())+"]\n");
		//更新图形化界面
		gui.getConsole().append(Clock.Time+":[进入就绪队列:"+pro.getProID()+","+(pro.getInstructNum()-pro.getIR())+"]\n");
	}
	//就绪态进入运行态
	public synchronized void Ready_To_Run(PCB Pro) {
		this.RunningPCB=Pro;//设置当前运行的进程
		this.RunningPCB.setFlag(true);//将静态优先调度标志设为True，表示正常调度一次
		if(this.RunningPCB.getIR()==0) {//第一次运行
			this.RunningPCB.setStartTimes(Clock.Time);//设置开始运行的时间
			this.cpu.setPC(1);//设置PC为1
			}
		else //不是第一次执行
			this.cpu.Recover(this.RunningPCB,gui,this.OutPutResult);//调用现场恢复方法
		//将其移出就绪队列
		this.ReadyQue.remove(Pro);
	}
	//静态优先算法
	//原本的静态优先算法容易抓着一个进程不放导致严重的饥饿现象，这样做并发度太小，因此我设置的算法为按每一轮算优先级，若运行过则改变标志，以确保在其他进程还没有调度时不会继续调用它
	public synchronized void JTYX() {
		int REG=-1;//比较优先级大小的变量
		int pos=-1;//储存选出的进程的位置
		//判断是否执行完一轮调度
		boolean flag=true;
		for(int i=0;i<this.ReadyQue.size();i++) {
			if(flag)
				flag=flag&&this.ReadyQue.get(i).getFlag();//将就绪队列中所有进程的运行标志相与，全为true时则表示全部运行过一次，一轮结束该重新开始
			else
				break;
		}
		if(flag) {//一轮结束
			for(int i=0;i<this.ReadyQue.size();i++)
				this.ReadyQue.get(i).setFlag(false);//将所有进程的运行标志都设置为false
		}
		//无论是否完成一轮调度，调用静态优先算法都必须选出进程进入运行态，因此直接写调度方法即可
		for(int i=0;i<this.ReadyQue.size();i++) {//遍历所有在就绪队列中的进程
			if(!this.ReadyQue.get(i).getFlag()) {//运行标志为false，即在这一轮中还没有调度
				if(pos==-1) {//这一轮第一次遍历，也就没有优先级可供比较
					REG=this.ReadyQue.get(i).getPriority();//设置优先级
					pos=i;//设置进程位置
				}
				else {
					if(this.ReadyQue.get(i).getPriority()<REG) {//该进程有更高的优先级
						REG=this.ReadyQue.get(i).getPriority();//更新优先级
						pos=i;//更新位置
					}
				}
			}
		}
		//循环后的pos即为这一轮中优先级最高的的进程的位置
		this.Ready_To_Run(this.ReadyQue.get(pos));//调用就绪→运行方法使选出的进程进入运行态
	}
	//运行态返回就绪态
	public synchronized void Run_To_Ready() {
		this.cpu.Protect(this.RunningPCB);//保护现场
		this.RunningPCB.getReadyInfo()[0]=this.ReadyQue.size()+1;//更新在就绪队列中的位置，因为是直接加入队尾且尚未进入就绪队列，所以位置是大小+1
		this.RunningPCB.getReadyInfo()[1]=Clock.Time;//更新进入就绪队列时间
		this.ReadyQue.add(this.RunningPCB);//返回就绪队列
		//运行日志加入结果队列
		this.OutPutResult.add(Clock.Time+":[重新进入就绪队列:"+RunningPCB.getProID()+","+(RunningPCB.getInstructNum()-RunningPCB.getIR())+"]\n");
		//更新图形化界面
		gui.getConsole().append(Clock.Time+":[重新进入就绪队列:"+RunningPCB.getProID()+","+(RunningPCB.getInstructNum()-RunningPCB.getIR())+"]\n");
		//移除当前运行的进程
		this.RunningPCB=null;
	}
	//运行态进入阻塞态1
	public synchronized void Run_To_Block1() {
		this.cpu.Protect(this.RunningPCB);//保护现场
		if(this.BlockQue1.size()==0) {//阻塞队列增加第一个进程
			this.BlockTime1=Clock.Time;//设置开始时间
		}
		this.RunningPCB.getBlockInfo1()[0]=this.BlockQue1.size()+1;//在阻塞队列中的位置，1为开始
		this.RunningPCB.getBlockInfo1()[1]=Clock.Time;//阻塞时间
		this.BlockQue1.add(this.RunningPCB);//加入输入阻塞队列
		int[] REG=new int[2];//存放阻塞队列信息的临时数组
		REG[0]=RunningPCB.getProID();REG[1]=Clock.Time;//设置进程ID和进入时间
		this.InputBlockInfo.add(REG);//添加入阻塞队列
		REG=null;//清空临时数组
		//运行日志添加到结果队列
		this.OutPutResult.add(Clock.Time+":[阻塞进程:InputBlock_thread,"+RunningPCB.getProID()+"]\n");
		//更新图形化界面
		gui.getConsole().append(Clock.Time+":[阻塞进程:InputBlock_thread,"+RunningPCB.getProID()+"]\n");
		//清空运行进程
		this.RunningPCB=null;
	}
	//运行态进入阻塞态2
	public synchronized void Run_To_Block2() {
		this.cpu.Protect(this.RunningPCB);//保护现场
		if(this.BlockQue2.size()==0) {//阻塞队列增加第一个进程
			this.BlockTime2=Clock.Time;//设置开始时间
		}
		this.RunningPCB.getBlockInfo2()[0]=this.BlockQue2.size()+1;//在阻塞队列中的位置，1为开始
		this.RunningPCB.getBlockInfo2()[1]=Clock.Time;//阻塞时间
		this.BlockQue2.add(this.RunningPCB);//加入输入阻塞队列
		int[] REG=new int[2];//存放阻塞队列信息的临时数组
		REG[0]=RunningPCB.getProID();REG[1]=Clock.Time;//设置进程ID和进入时间
		this.OutputBlockInfo.add(REG);//添加入阻塞队列
		REG=null;//清空临时数组
		//运行日志添加到结果队列
		this.OutPutResult.add(Clock.Time+":[阻塞进程:OutputBlock_thread,"+RunningPCB.getProID()+"]\n");
		//更新图形化界面
		gui.getConsole().append(Clock.Time+":[阻塞进程:OutputBlock_thread,"+RunningPCB.getProID()+"]\n");
		//清空运行进程
		this.RunningPCB=null;
	}
	//阻塞态返回就绪态1
	public synchronized void Block_To_Ready1() {
		if(BlockQue1.get(0).getIR()<BlockQue1.get(0).getInstructNum()) {//进程没做完
			BlockQue1.get(0).getReadyInfo()[0]=this.ReadyQue.size()+1;//设置在就绪队列中的位置
			BlockQue1.get(0).getReadyInfo()[1]=Clock.Time;//设置进入时间
			ReadyQue.add(BlockQue1.get(0));//添加到就绪队列
			//运行日志添加到结果队列
			this.OutPutResult.add(Clock.Time+":[阻塞态重新进入就绪队列:"+BlockQue1.get(0).getProID()+","+(BlockQue1.get(0).getInstructNum()-BlockQue1.get(0).getIR())+"]\n");
			//更新图形化界面
			gui.getConsole().append(Clock.Time+":[阻塞态重新进入就绪队列:"+BlockQue1.get(0).getProID()+","+(BlockQue1.get(0).getInstructNum()-BlockQue1.get(0).getIR())+"]\n");
			//移出阻塞队列
			BlockQue1.remove(0);
		}
		else {//进程已经做完了
			//新建一个进程对象存放要结束的进程
			PCB REG=BlockQue1.get(0);
			REG.RemovePro(BlockQue1, Clock.Time,gui,OutPutResult);//移除进程
			for(int i=REG.getStart_Address();i<REG.getEnd_Address();i++) {//将进程从内存中移除
				RAM[i]=null;//清空占用的内存
				gui.getRAM()[i].setText(null);//更新图形化界面
			}
			//添加入结束队列
			ResultQue.add(REG);
			//运行日志添加到结果队列
			this.OutPutResult.add(Clock.Time+":[结束进程:"+REG.getProID()+"]\n");
			//更新图形化界面
			gui.getConsole().append(Clock.Time+":[结束进程:"+REG.getProID()+"]\n");
		}
	}
	//阻塞态返回就绪态2
	public synchronized void Block_To_Ready2() {
		if(BlockQue2.get(0).getIR()<BlockQue2.get(0).getInstructNum()) {//进程没做完
			BlockQue2.get(0).getReadyInfo()[0]=this.ReadyQue.size()+1;//设置在就绪队列中的位置
			BlockQue2.get(0).getReadyInfo()[1]=Clock.Time;//设置进入时间
			ReadyQue.add(BlockQue2.get(0));//添加到就绪队列
			//运行日志添加到结果队列
			this.OutPutResult.add(Clock.Time+":[阻塞态重新进入就绪队列:"+BlockQue2.get(0).getProID()+","+(BlockQue2.get(0).getInstructNum()-BlockQue2.get(0).getIR())+"]\n");
			//更新图形化界面
			gui.getConsole().append(Clock.Time+":[阻塞态重新进入就绪队列:"+BlockQue2.get(0).getProID()+","+(BlockQue2.get(0).getInstructNum()-BlockQue2.get(0).getIR())+"]\n");
			//移出阻塞队列
			BlockQue2.remove(0);
		}
		else {//进程已经做完了
			//新建一个进程对象存放要结束的进程
			PCB REG=BlockQue2.get(0);
			REG.RemovePro(BlockQue2, Clock.Time,gui,OutPutResult);//移除进程
			for(int i=REG.getStart_Address();i<REG.getEnd_Address();i++) {//将进程从内存中移除
				RAM[i]=null;//清空占用的内存
				gui.getRAM()[i].setText(null);//更新图形化界面
			}
			//添加入结束队列
			ResultQue.add(REG);
			//运行日志添加到结果队列
			this.OutPutResult.add(Clock.Time+":[结束进程:"+REG.getProID()+"]\n");
			//更新图形化界面
			gui.getConsole().append(Clock.Time+":[结束进程:"+REG.getProID()+"]\n");
		}
	}
	//进程调度线程(时钟中断)
	public class ProcessScheduling_thread extends TimerTask{
		int REG=-1;//存放指令类型的对象
		public void run() {
			ShowAll();//每秒钟更新图形化界面中的内容
			if(RunningPCB!=null) {//有正在执行的进程
				//将处理方法设置返回值，如果是0则正常运行，如果是1则判断是否需要延长时间片，如果是2或3则加入阻塞队列，然后在阻塞线程里释放即可
				REG=cpu.HandleProcess(RunningPCB,gui,OutPutResult);
				if(REG==0) {//指令类型为0正常运行，也可以不写
					
				}
				else if(REG==1) {//指令类型为1，需要判断时间片是否够用
					if(!RunningPCB.getInstructions().get(cpu.getIR()-1).getFlag()) {//刚开始执行这条指令
						if(Clock.TimeSlice==1) {//时间片不够用
							Clock.TimeSlice--;//延长1s
						}
						RunningPCB.getInstructions().get(cpu.getIR()-1).setFlag(true);//运行标志设为true，表示已经运行了1s
						cpu.setIR(cpu.getIR()-1);//IR倒退一步，使得下一秒cpu仍然执行这条指令
					}
					else {//已经执行了1s，此时时间片必定为1，所以不用管，将cpu的pc+1即可
						cpu.setPC(cpu.getPC()+1);
					}
				}
				else if(REG==2) {//指令类型为2，表示输入阻塞，调用运行→阻塞的方法1即可，同时将RunningPCB设为null，时间片设为0，使下一个进程能够正常运行
					Run_To_Block1();//加入阻塞队列
					Clock.TimeSlice=0;//将时间片置零，保证下一个进程正常运行
				}
				else if(REG==3) {//指令类型为3，表示输出阻塞，调用运行→阻塞的方法2即可，同时将RunningPCB设为null，时间片设为0，使下一个进程能够正常运行
					Run_To_Block2();//加入阻塞队列
					Clock.TimeSlice=0;//将时间片置零，保证下一个进程正常运行
				}
				//正常判断进程是否可以结束
				if(RunningPCB!=null) {//有正在执行的进程
					if(cpu.getPC()>RunningPCB.getInstructNum()) {//指令做完
						RunningPCB.RemovePro(ReadyQue, Clock.Time,gui,OutPutResult);//移除进程
						for(int i=RunningPCB.getStart_Address();i<RunningPCB.getEnd_Address();i++) {//将进程从内存中移除
							RAM[i]=null;//清空占用内存
							gui.getRAM()[i].setText(null);//更新图形化界面
						}
						//添加入结束队列
						ResultQue.add(RunningPCB);
						//运行日志添加到结果队列
						OutPutResult.add(Clock.Time+":[结束进程:"+RunningPCB.getProID()+"]\n");
						//更新图形化界面
						gui.getConsole().append(Clock.Time+":[结束进程:"+RunningPCB.getProID()+"]\n");
						//清空运行的进程
						RunningPCB=null;
						//重置时间片
						Clock.TimeSlice=0;
					}
					else//指令没做完，进程未结束
						++Clock.TimeSlice;//更新时间片
				}
			}
			else {//没有执行的进程
				if(ReadyQue.size()==0) {//没有需要处理的进程
					//运行日志添加到结果队列
					OutPutResult.add(Clock.Time+":[CPU空闲]\n");
					//更新图形化界面
					gui.getConsole().append(Clock.Time+":[CPU空闲]\n");
				}
				else//就绪队列中还有等待运行的进程
					JTYX();//调用静态优先算法
			}
			++Clock.Time;//更新时间
		}
	}
	//作业请求线程,10秒查询一次REGArr队列,若符合要求则加入后备队列
	public class JobRequest_thread extends Thread{ 
		public synchronized void run() {
			while(true) {
				if(Clock.Time>0&&Clock.Time%10==0) {//每10s运行一次
					REG_To_Reserve();//调用临时数组→后备队列方法
				}
			}
		}
	}
	//时间片轮转算法
	public class LZ_Method extends Thread{
		public synchronized void run() {
			while(true) {
				if(Clock.TimeSlice==2) {//时间片到
					if(ReadyQue.size()!=0&&RunningPCB!=null) {//就绪队列中还有等待运行的进程而且此时有进程正在运行，才调换进程运行
						Run_To_Ready();//将当前执行的进程送回就绪队列
						JTYX();//调用静态优先算法
					}
					Clock.TimeSlice=0;//重置时间片
				}
			}
		}
	}
	//输入阻塞线程
	public class InputBlock_thread extends Thread{
		public synchronized void run() {
			while(true) {//无限循环一直监测阻塞队列是否为空
				if(BlockQue1.size()>0) {//阻塞队列非空
					if((Clock.Time-BlockTime1)>0&&(Clock.Time-BlockTime1)%2==0) {//运行2s，移出阻塞队列，返回就绪队列
						Block_To_Ready1();//将第一个阻塞进程转移到就绪队列
					}
				}
			}
		}
	}
	//输出阻塞线程
	public class OutputBlock_thread extends Thread{
		public synchronized void run() {
			while(true) {//无限循环一直监测阻塞队列是否为空
				if(BlockQue2.size()>0) {//阻塞队列非空
					if((Clock.Time-BlockTime2)>0&&(Clock.Time-BlockTime2)%3==0) {//运行3s，移出阻塞队列，返回就绪队列
						Block_To_Ready2();//将第一个阻塞进程转移到就绪队列
					}
				}
			}
		}
	}
	//显示所有队列
	public void ShowAll() {
		int i=0;//循环变量
		//后备队列
		while(gui.getReserveQue().getRowCount()>0)//清空当前的显示队列，要实现每秒实时更新队列信息，因为下面需要调用addRow方法，所有必须先清空
			gui.getReserveQue().removeRow(0);
		for(i=0;i<this.ReserveQue.size();i++) {
			gui.getReserveQue().addRow(new String[] {
					Integer.toString(this.ReserveQue.get(i).getJobsID()),
					Integer.toString(this.ReserveQue.get(i).getPriority()),
					Integer.toString(this.ReserveQue.get(i).getInTimes()),
					Integer.toString(this.ReserveQue.get(i).getInstructNum())
			});
		}
		//就绪队列
		while(gui.getReadyQue().getRowCount()>0)//清空当前显示的队列，要实现每秒实时更新队列信息，因为下面需要调用addRow方法，所有必须先清空
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
		//运行队列(即CPU当前执行的进程)
		while(gui.getRunningQue().getRowCount()>0)//清空当前显示的队列，要实现每秒实时更新队列信息，因为下面需要调用addRow方法，所有必须先清空
			gui.getRunningQue().removeRow(0);
		if(RunningPCB!=null) {
			gui.getRunningQue().addRow(new String[] {
					Integer.toString(RunningPCB.getProID()),
					Integer.toString(RunningPCB.getInstructNum()),
					Integer.toString(cpu.getIR()),
					Integer.toString(cpu.getPC()),
			});
		}
		//阻塞队列1
		while(gui.getBlockQue1().getRowCount()>0)//清空当前显示的队列，要实现每秒实时更新队列信息，因为下面需要调用addRow方法，所有必须先清空
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
		//阻塞队列2
		while(gui.getBlockQue2().getRowCount()>0)//清空当前显示的队列，要实现每秒实时更新队列信息，因为下面需要调用addRow方法，所有必须先清空
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
		//结束队列
		while(gui.getResultQue().getRowCount()>0)//清空当前显示的队列，要实现每秒实时更新队列信息，因为下面需要调用addRow方法，所有必须先清空
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
	//将运行日志写入文件
	public int Write_Log(int time) throws IOException {
		File f=new File("output1/ProcessResults-"+time+"-JTYX.txt");//新建作业文件，因为运行日志和状态信息要写入一个文件，所以采用传递参数来确定时间
		if(!f.exists()) {//文件不存在
			f.createNewFile();//创建文件
		}
		BufferedWriter W=new BufferedWriter(new FileWriter(f,true));//写入文件对象
		W.write("进程调度事件:\n");
		for(int i=0;i<this.OutPutResult.size();i++) {//遍历结果数组
			W.write(this.OutPutResult.get(i));//写入内容
		}
		W.close();//关闭写入对象
		return time;//返回参数传递给状态信息写入方法
	}
	//将状态信息写入文件
	public void WriteResult(int time) throws IOException {
		File f=new File("output1/ProcessResults-"+time+"-JTYX.txt");//打开上面创建的文件
		if(!f.exists()) {//文件一定存在，但为了代码的健壮性，增加不存在新建文件的代码
			f.createNewFile();
		}
		BufferedWriter W=new BufferedWriter(new FileWriter(f,true));//写入对象
		W.write("----------------------------------------------------\n状态信息统计:\n");//分割线
		//写入进程信息
		for(int i=0;i<ResultQue.size();i++) {
			W.write(ResultQue.get(i).getEndTimes()+":["+ResultQue.get(i).getProID()+":"+ResultQue.get(i).getOfferTimes()+"+"+ResultQue.get(i).getInTimes()+"+"+ResultQue.get(i).getRunTimes()+"]\n");
		}
		//写入阻塞队列信息
		W.write("BBBB:[InputBlock-thread:");
		for(int i=0;i<this.InputBlockInfo.size();i++) {
			W.write(InputBlockInfo.get(i)[0]+","+InputBlockInfo.get(i)[1]+";");
		}W.write("]\n");
		//写入阻塞队列信息2
		W.write("BBBB:[OutputBlock-thread:");
		for(int i=0;i<this.OutputBlockInfo.size();i++) {
			W.write(OutputBlockInfo.get(i)[0]+","+OutputBlockInfo.get(i)[1]+";");
		}W.write("]\n");
		//关闭写入对象
		W.close();
	}
	//开始执行的代码(绑定在开始按钮上)
	public void Start1() {
		//创建作业请求、时钟中断、时间片轮转、输入输出阻塞线程
		this.JobReq=new JobRequest_thread();
		ProcessScheduling_thread ProSc=new ProcessScheduling_thread();
		this.LZ=new LZ_Method();
		this.InBT=new InputBlock_thread();
		this.OutBT=new OutputBlock_thread();
		try {
			this.Read_Job_To_Array();//将作业读入临时数组
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Clock.clock.schedule(ProSc,0,WaitTime);//开始时钟中断
		InBT.start();//开始输入阻塞线程
		OutBT.start();//开始输出阻塞线程
		LZ.start();//开始时间片轮转线程
		JobReq.start();//开始作业请求线程
	}
	//整合所有方法，调用即可执行
	public void Start() {
		//绑在开始按钮上的监听器
		ActionListener start=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Start1();//开始执行
			}
		};
		//添加监听器
		gui.getRunButton().addActionListener(start);
		//绑在创建作业按钮上的监听器
		ActionListener creat=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(Clock.Time==0&&REG_Arr.size()==0) {//没有点击开始执行按钮时创建作业
					try {
						Creat_Job();//调用只写入文件的新建作业方法
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}//已经开始
				else {
					//调用直接添加到后备队列的方法
					AddJob();
				}
			}
		};
		//添加监听器
		gui.getCreateJobButton().addActionListener(creat);
		//绑在停止按钮上的监听器
		ActionListener Stop=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					//将运行结果写入文件。因为在所有作业做完之后操作者也还有可能新建作业，所以无法用所以作业做完的时间来当作结束时间，因此采用点击结束运行按钮才确定运行时间的方法写入文件
					WriteResult(Write_Log(Clock.Time));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//关闭时钟，中断所有线程
				Clock.clock.cancel();
				Clock.clock=new Timer();
				Clock.Time=0;
				JobReq.interrupt();
				LZ.interrupt();
				InBT.interrupt();
				OutBT.interrupt();
			}
		};
		//添加监听器
		gui.getStopButton().addActionListener(Stop);
		
	}
	
}
