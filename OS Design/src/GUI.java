import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

public class GUI {
	private JFrame Frame;//���
    private JPanel Container;//������
    private JTextArea Console;//�ı���
    private JButton RunButton;//��ʼ��ť
    private JButton StopButton;//��ֹ��ť
    private JButton CreateJobButton;//������ҵ��ť
    private DefaultTableModel ReserveQue;//�󱸶���չʾ���
    private DefaultTableModel RunningQue;//��ǰ�������еĽ��̵ı��
    private DefaultTableModel ReadyQue;//��������չʾ���
    private DefaultTableModel BlockQue1;//��������1�ı��
    private DefaultTableModel BlockQue2;//��������2�ı��
    private DefaultTableModel ResultQue;//������Ľ��̵�չʾ���
    private JPanel RAMContainer;//�ڴ�ģ������
    private JTextField[] RAM;//�ڴ���ʾ�ı���
    
    GUI(){
    	this.Frame = new JFrame("����ϵͳ�γ����");//�������
        this.Frame.setBounds(10, 10, 1500, 800);//���ÿ��λ�úʹ�С
        this.Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�����������
        this.Container = new JPanel();//��������
        this.Container.setLayout(null);//���Բ���
        
        //�����ı����ȡ���������̨���ַ���
        JLabel ConsoleTitle = new JLabel("����̨�����Ϣ");//�ı���ı���
        ConsoleTitle.setBounds(10, 10, 200, 20);//���þ��Դ�С
        this.Container.add(ConsoleTitle);
        this.Console = new JTextArea();
        //Ϊ�ı������ù�����
        JScrollPane ScrollOfConsole = new JScrollPane(Console);
        ScrollOfConsole.setBounds(10, 40, 300, 700);
        //���ֹ������ڵ׶�
        DefaultCaret caret = (DefaultCaret)Console.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.Container.add(ScrollOfConsole);
        
        //�ڴ���ӻ�
        this.RAMContainer=new JPanel();
        this.RAMContainer.setBounds(330, 40, 750, 450);//�����������Դ�С
        this.RAMContainer.setLayout(new GridLayout(16,10));//���ò��֣��������񲼾�
        //��ʼ���ı�������
        this.RAM=new JTextField[160];
        for(int i=0;i<160;i++) {
        	this.RAM[i]=new JTextField();//��ʼ��
        	this.RAM[i].setFont(new Font("����", Font.BOLD, 15));
        	this.RAM[i].setHorizontalAlignment(JTextField.CENTER);//�ı�����
        	this.RAMContainer.add(this.RAM[i]);//��������
        }
        this.Container.add(this.RAMContainer);
        
        
        //չʾ�󱸶���
        JLabel ReserveTitle = new JLabel("�󱸶���");
        ReserveTitle.setBounds(1100, 10, 300, 30);
        this.Container.add(ReserveTitle);
        String[] reserveHeader = new String[]{"��ҵID","���ȼ�", "����ʱ��", "ָ����"};
        this.ReserveQue = new DefaultTableModel(new String[][]{}, reserveHeader);
        JTable ReserveTable = new JTable(this.ReserveQue);
        //��ӹ�����(��ҵ����,������ò���)
        JScrollPane ReserveScroll = new JScrollPane(ReserveTable);
        ReserveScroll.setBounds(1100, 40, 350, 150);
        this.Container.add(ReserveScroll);

        //չʾ��������
        JLabel ReadyTitle = new JLabel("��������");
        ReadyTitle.setBounds(1100, 200, 300, 10);
        this.Container.add(ReadyTitle);
        String[] ReadyHeader = new String[]{"����ID", "����ʱ��", "ָ����", "IR", "PC"};
        this.ReadyQue = new DefaultTableModel(new String[][]{}, ReadyHeader);
        JTable ReadyTable = new JTable(this.ReadyQue);
        //��ӹ�����(��ҵ����,������ò���)
        JScrollPane ReadyScroll = new JScrollPane(ReadyTable);
        ReadyScroll.setBounds(1100, 220, 350, 130);
        this.Container.add(ReadyScroll);
        
        //չʾ���ж���
        JLabel RunningTitle = new JLabel("��ǰ���н���");
        RunningTitle.setBounds(1100, 360, 300, 10);
        this.Container.add(RunningTitle);
        String[] runningHeader = new String[]{"����ID", "ָ����", "IR", "PC"};
        this.RunningQue = new DefaultTableModel(new String[][]{}, runningHeader);
        JTable runningTable = new JTable(this.RunningQue);
        //��ӹ�����(��ҵ����,������ò���)
        JScrollPane RunningScroll = new JScrollPane(runningTable);
        RunningScroll.setBounds(1100, 380, 350, 50);
        this.Container.add(RunningScroll);
        
        //չʾ��������1
        JLabel BlockTitle1 = new JLabel("��������1");
        BlockTitle1.setBounds(1100, 440, 300, 10);
        this.Container.add(BlockTitle1);
        String[] BlockHeader1 = new String[]{"����ID", "����ʱ��","ָ����", "IR", "PC"};
        this.BlockQue1 = new DefaultTableModel(new String[][]{}, BlockHeader1);
        JTable BlockTable1 = new JTable(this.BlockQue1);
        //��ӹ�����(��ҵ����,������ò���)
        JScrollPane BlockScroll1 = new JScrollPane(BlockTable1);
        BlockScroll1.setBounds(1100, 460, 350, 115);
        this.Container.add(BlockScroll1);
        
      //չʾ��������2
        JLabel BlockTitle2 = new JLabel("��������2");
        BlockTitle2.setBounds(1100, 585, 300, 15);
        this.Container.add(BlockTitle2);
        String[] BlockHeader2 = new String[]{"����ID", "����ʱ��","ָ����", "IR", "PC"};
        this.BlockQue2 = new DefaultTableModel(new String[][]{}, BlockHeader2);
        JTable BlockTable2 = new JTable(this.BlockQue2);
        //��ӹ�����(��ҵ����,������ò���)
        JScrollPane BlockScroll2 = new JScrollPane(BlockTable2);
        BlockScroll2.setBounds(1100, 610, 350, 120);
        this.Container.add(BlockScroll2);
        
        
        //չʾ�Ѿ�ִ����Ľ���
        JLabel EndTitle = new JLabel("�ѽ�������");
        EndTitle.setBounds(620, 500, 400, 20);
        this.Container.add(EndTitle);
        String[] EndHeader = new String[]{"����ID","��ҵ����ʱ��","����ʱ��","��ʼʱ��", "����ʱ��","������ʱ��"};
        this.ResultQue = new DefaultTableModel(new String[][]{}, EndHeader);
        JTable EndTable = new JTable(this.ResultQue);
        //��ӹ�����(��ҵ����,������ò���)
        JScrollPane EndScroll = new JScrollPane(EndTable);
        EndScroll.setBounds(620,530, 450, 200);
        this.Container.add(EndScroll);
        
        //��ӿ�ʼ��ť
        this.RunButton = new JButton("��ʼִ��");
        this.RunButton.setBounds(370, 500, 150, 70);
        this.Container.add(this.RunButton);
        //����½���ҵ��ť
        this.CreateJobButton = new JButton("�½���ҵ");
        this.CreateJobButton.setBounds(370, 580, 150, 70);
        this.Container.add(this.CreateJobButton);
        //��ӽ�����ť
        this.StopButton = new JButton("����ִ��");
        this.StopButton.setBounds(370, 660, 150, 70);
        this.Container.add(this.StopButton);
        //����ܵ�������ΪContainer
        this.Frame.setContentPane(this.Container);
        //�ɼ�
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
