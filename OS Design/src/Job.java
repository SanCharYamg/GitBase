import java.util.ArrayList;

public class Job {
	private int JobsID;//��ҵ���
	private int Priority;//��ҵ���ȼ�
	private int InTimes;//��ҵ����ʱ��
	private int InstructNum;//��ҵ�����ĳ���ָ����Ŀ
	private ArrayList<Instruction> Instructions;//ָ�
	Job(){
		this.Instructions=new ArrayList<Instruction>();
	}
	Job(int JobsID,int Priority,int InTimes,int InstructNum){
		this.JobsID=JobsID;
		this.Priority=Priority;
		this.InTimes=InTimes;
		this.InstructNum=InstructNum;
		this.Instructions=new ArrayList<Instruction>();
	}
	public int getJobsID() {
		return JobsID;
	}
	public void setJobsID(int jobsID) {
		JobsID = jobsID;
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
	public int getInstructNum() {
		return InstructNum;
	}
	public void setInstructNum(int instructNum) {
		InstructNum = instructNum;
	}
	public ArrayList<Instruction> getInstructions() {
		return Instructions;
	}
	public void setInstructions(ArrayList<Instruction> instructions) {
		Instructions = instructions;
	}
	
	
}
