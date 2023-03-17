import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

public class GUI {
	private JFrame Frame;//框架
    private JPanel Container;//总容器
    private JTextArea Console;//文本域
    private JButton RunButton;//开始按钮
    private JButton StopButton;//终止按钮
    private JButton CreateJobButton;//创建作业按钮
    private DefaultTableModel ReserveQue;//后备队列展示表格
    private DefaultTableModel RunningQue;//当前正在运行的进程的表格
    private DefaultTableModel ReadyQue;//就绪队列展示表格
    private DefaultTableModel BlockQue1;//阻塞队列1的表格
    private DefaultTableModel BlockQue2;//阻塞队列2的表格
    private DefaultTableModel ResultQue;//运行完的进程的展示表格
    private JPanel RAMContainer;//内存模块容器
    private JTextField[] RAM;//内存显示文本域
    
    GUI(){
    	this.Frame = new JFrame("操作系统课程设计");//创建框架
        this.Frame.setBounds(10, 10, 1500, 800);//设置框架位置和大小
        this.Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//点×结束运行
        this.Container = new JPanel();//创建容器
        this.Container.setLayout(null);//绝对布局
        
        //建立文本域获取输出至控制台的字符串
        JLabel ConsoleTitle = new JLabel("控制台输出信息");//文本框的标题
        ConsoleTitle.setBounds(10, 10, 200, 20);//设置绝对大小
        this.Container.add(ConsoleTitle);
        this.Console = new JTextArea();
        //为文本域设置滚动条
        JScrollPane ScrollOfConsole = new JScrollPane(Console);
        ScrollOfConsole.setBounds(10, 40, 300, 700);
        //保持滚动条在底端
        DefaultCaret caret = (DefaultCaret)Console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.Container.add(ScrollOfConsole);
        
        //内存可视化
        this.RAMContainer=new JPanel();
        this.RAMContainer.setBounds(330, 40, 750, 450);//设置容器绝对大小
        this.RAMContainer.setLayout(new GridLayout(16,10));//设置布局，采用网格布局
        //初始化文本域数组
        this.RAM=new JTextField[160];
        for(int i=0;i<160;i++) {
        	this.RAM[i]=new JTextField();//初始化
        	this.RAM[i].setFont(new Font("宋体", Font.BOLD, 15));
        	this.RAM[i].setHorizontalAlignment(JTextField.CENTER);//文本居中
        	this.RAMContainer.add(this.RAM[i]);//加入容器
        }
        this.Container.add(this.RAMContainer);
        
        
        //展示后备队列
        JLabel ReserveTitle = new JLabel("后备队列");
        ReserveTitle.setBounds(1100, 10, 300, 30);
        this.Container.add(ReserveTitle);
        String[] reserveHeader = new String[]{"作业ID","优先级", "请求时间", "指令数"};
        this.ReserveQue = new DefaultTableModel(new String[][]{}, reserveHeader);
        JTable ReserveTable = new JTable(this.ReserveQue);
        //添加滚动条(作业较少,大概率用不上)
        JScrollPane ReserveScroll = new JScrollPane(ReserveTable);
        ReserveScroll.setBounds(1100, 40, 350, 150);
        this.Container.add(ReserveScroll);

        //展示就绪队列
        JLabel ReadyTitle = new JLabel("就绪队列");
        ReadyTitle.setBounds(1100, 200, 300, 10);
        this.Container.add(ReadyTitle);
        String[] ReadyHeader = new String[]{"进程ID", "创建时间", "指令数", "IR", "PC"};
        this.ReadyQue = new DefaultTableModel(new String[][]{}, ReadyHeader);
        JTable ReadyTable = new JTable(this.ReadyQue);
        //添加滚动条(作业较少,大概率用不上)
        JScrollPane ReadyScroll = new JScrollPane(ReadyTable);
        ReadyScroll.setBounds(1100, 220, 350, 130);
        this.Container.add(ReadyScroll);
        
        //展示运行队列
        JLabel RunningTitle = new JLabel("当前运行进程");
        RunningTitle.setBounds(1100, 360, 300, 10);
        this.Container.add(RunningTitle);
        String[] runningHeader = new String[]{"进程ID", "指令数", "IR", "PC"};
        this.RunningQue = new DefaultTableModel(new String[][]{}, runningHeader);
        JTable runningTable = new JTable(this.RunningQue);
        //添加滚动条(作业较少,大概率用不上)
        JScrollPane RunningScroll = new JScrollPane(runningTable);
        RunningScroll.setBounds(1100, 380, 350, 50);
        this.Container.add(RunningScroll);
        
        //展示阻塞队列1
        JLabel BlockTitle1 = new JLabel("阻塞进程1");
        BlockTitle1.setBounds(1100, 440, 300, 10);
        this.Container.add(BlockTitle1);
        String[] BlockHeader1 = new String[]{"进程ID", "阻塞时间","指令数", "IR", "PC"};
        this.BlockQue1 = new DefaultTableModel(new String[][]{}, BlockHeader1);
        JTable BlockTable1 = new JTable(this.BlockQue1);
        //添加滚动条(作业较少,大概率用不上)
        JScrollPane BlockScroll1 = new JScrollPane(BlockTable1);
        BlockScroll1.setBounds(1100, 460, 350, 115);
        this.Container.add(BlockScroll1);
        
      //展示阻塞队列2
        JLabel BlockTitle2 = new JLabel("阻塞进程2");
        BlockTitle2.setBounds(1100, 585, 300, 15);
        this.Container.add(BlockTitle2);
        String[] BlockHeader2 = new String[]{"进程ID", "阻塞时间","指令数", "IR", "PC"};
        this.BlockQue2 = new DefaultTableModel(new String[][]{}, BlockHeader2);
        JTable BlockTable2 = new JTable(this.BlockQue2);
        //添加滚动条(作业较少,大概率用不上)
        JScrollPane BlockScroll2 = new JScrollPane(BlockTable2);
        BlockScroll2.setBounds(1100, 610, 350, 120);
        this.Container.add(BlockScroll2);
        
        
        //展示已经执行完的进程
        JLabel EndTitle = new JLabel("已结束进程");
        EndTitle.setBounds(620, 500, 400, 20);
        this.Container.add(EndTitle);
        String[] EndHeader = new String[]{"进程ID","作业请求时间","创建时间","开始时间", "结束时间","总运行时间"};
        this.ResultQue = new DefaultTableModel(new String[][]{}, EndHeader);
        JTable EndTable = new JTable(this.ResultQue);
        //添加滚动条(作业较少,大概率用不上)
        JScrollPane EndScroll = new JScrollPane(EndTable);
        EndScroll.setBounds(620,530, 450, 200);
        this.Container.add(EndScroll);
        
        //添加开始按钮
        this.RunButton = new JButton("开始执行");
        this.RunButton.setBounds(370, 500, 150, 70);
        this.Container.add(this.RunButton);
        //添加新建作业按钮
        this.CreateJobButton = new JButton("新建作业");
        this.CreateJobButton.setBounds(370, 580, 150, 70);
        this.Container.add(this.CreateJobButton);
        //添加结束按钮
        this.StopButton = new JButton("结束执行");
        this.StopButton.setBounds(370, 660, 150, 70);
        this.Container.add(this.StopButton);
        //将框架的容器设为Container
        this.Frame.setContentPane(this.Container);
        //可见
        this.Frame.setVisible(true);
    	
    	
    	
    	
    	
    	
    	
    	
    	
    }
	public JFrame getFrame() {
		return Frame;
	}
	public void setFrame(JFrame frame) {
		Frame = frame;
	}
	public JPanel getContainer() {
		return Container;
	}
	public void setContainer(JPanel container) {
		Container = container;
	}
	public JTextArea getConsole() {
		return Console;
	}
	public void setConsole(JTextArea console) {
		Console = console;
	}
	public JButton getRunButton() {
		return RunButton;
	}
	public void setRunButton(JButton runButton) {
		RunButton = runButton;
	}
	public JButton getStopButton() {
		return StopButton;
	}
	public void setStopButton(JButton stopButton) {
		StopButton = stopButton;
	}
	public JButton getCreateJobButton() {
		return CreateJobButton;
	}
	public void setCreateJobButton(JButton createJobButton) {
		CreateJobButton = createJobButton;
	}
	public DefaultTableModel getReserveQue() {
		return ReserveQue;
	}
	public void setReserveQue(DefaultTableModel reserveQue) {
		ReserveQue = reserveQue;
	}
	public DefaultTableModel getRunningQue() {
		return RunningQue;
	}
	public void setRunningQue(DefaultTableModel runningQue) {
		RunningQue = runningQue;
	}
	public DefaultTableModel getReadyQue() {
		return ReadyQue;
	}
	public void setReadyQue(DefaultTableModel readyQue) {
		ReadyQue = readyQue;
	}
	public DefaultTableModel getResultQue() {
		return ResultQue;
	}
	public void setResultQue(DefaultTableModel resultQue) {
		ResultQue = resultQue;
	}
    public DefaultTableModel getBlockQue1() {
		return BlockQue1;
	}
	public void setBlockQue1(DefaultTableModel blockQue1) {
		BlockQue1 = blockQue1;
	}
	public DefaultTableModel getBlockQue2() {
		return BlockQue2;
	}
	public void setBlockQue2(DefaultTableModel blockQue2) {
		BlockQue2 = blockQue2;
	}
	public JPanel getRAMContainer() {
		return RAMContainer;
	}
	public void setRAMContainer(JPanel rAMContainer) {
		RAMContainer = rAMContainer;
	}
	public JTextField[] getRAM() {
		return RAM;
	}
	public void setRAM(JTextField[] rAM) {
		RAM = rAM;
	}
}
