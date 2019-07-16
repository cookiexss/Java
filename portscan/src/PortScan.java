import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PortScan extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private GridBagLayout gridbag;
    private GridBagConstraints constraints;
    private JPanel mainJPanel,panel1,panel1_1,panel1_2,panel2,panel2_2,panel3,panel4,panel5,panelIP,panelIP1,panelIP2,panelScan;
    private ButtonGroup buttonGroup;  //扫描方式组
    private JRadioButton scanType1,scanType2,scanType3;  //扫描方式1，扫描方式2, 扫描方式3
    private JLabel startJLabel,endJLabel,threadNum,startIPJLabel,endIPJLabel,domainIPJLabel;  //起始端口和结束端口 线程数 起始IP 结束IP
    private JLabel progressJLabel,resultJLabel;  //进度和结果
    private JTextField customPorts,startPort,endPort,customDomain,customThreadNum,startIP,endIP;  //常见端口，起始和结束端口;自定义域名，自定义线程数
    private JButton beginJButton;  //开始扫描
    private JScrollPane progressPane,resultPane;  //进度面板和结果面板
    private JTextArea progressJtJTextArea,resultJTextArea;  //同上

    private JMenuBar jMenuBar;
    private JMenu help;
    private JMenuItem author,contact,version,readme;

    private Font menuFont = new Font("宋体", Font.LAYOUT_NO_LIMIT_CONTEXT, 14);  //菜单字体
    private Font contentFont = new Font("宋体", Font.LAYOUT_NO_LIMIT_CONTEXT, 16);  //正文字体

    public PortScan(){
        super("多线程端口IP扫描工具");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(900, 800));
        int frameWidth = this.getPreferredSize().width;  //界面宽度
        int frameHeight = this.getPreferredSize().height;  //界面高度
        setSize(frameWidth,frameHeight);
        setLocation((screenSize.width - frameWidth) / 2,(screenSize.height - frameHeight) / 2);//窗口位于屏幕中间

        //初始化
        mainJPanel = new JPanel();
        panel1 = new JPanel();
        panel1_1 = new JPanel();
        panel1_2 = new JPanel();
        panel2 = new JPanel();
        panel2_2 = new JPanel();
        panelIP = new JPanel();
        panelIP1 = new JPanel();
        panelIP2 = new JPanel();
        panelScan = new JPanel();
        panel3 = new JPanel();
        panel4 = new JPanel();
        panel5 = new JPanel();
        buttonGroup = new ButtonGroup();
        scanType1 = new JRadioButton("扫描常见端口:");
        scanType2 = new JRadioButton("扫描一个连续段的端口:");
        scanType3 = new JRadioButton("探测存活主机:");
        startJLabel = new JLabel("起始端口:");
        endJLabel = new JLabel("结束端口:");
        startIPJLabel = new JLabel("起始IP:");
        endIPJLabel = new JLabel("结束IP:");
        domainIPJLabel = new JLabel("域名/IP:");
        threadNum = new JLabel("线程:");
        progressJLabel = new JLabel("扫描进度");
        resultJLabel = new JLabel("扫描结果");
        customPorts = new JTextField("21,22,23,25,26,69,80,110," +
                "143,443,465,1080,1158,1433,1521,2100,3306," +
                "3389,7001,8080,8081,8888,9080,9090,43958");
        startPort = new JTextField("20", 10);
        endPort = new JTextField("9000", 10);
        customDomain = new JTextField("cookiexss.github.io", 25);
        startIP = new JTextField("192.168.205.1",25);
        endIP = new JTextField("192.168.205.255",25);
        customThreadNum = new JTextField("5", 5);
        beginJButton = new JButton("开始扫描");
        progressPane = new JScrollPane();
        resultPane = new JScrollPane();
        progressJtJTextArea = new JTextArea(18, 20);
        resultJTextArea = new JTextArea(18, 20);

        //布局
        //添加成一组
        buttonGroup.add(scanType1);
        buttonGroup.add(scanType2);
        buttonGroup.add(scanType3);
        scanType1.setSelected(true);//初始默认选择1

        gridbag = new GridBagLayout();//GridBagLayout设计理念是将父容器看作一个表格，表格中单元格的长宽可根据需求调整
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;//当格子有剩余空间时，填充空间
        mainJPanel.setLayout(gridbag);

        constraints.gridwidth = 0;
        constraints.gridheight = 1;//列占一个单元格
        constraints.weightx = 1;
        constraints.weighty = 0;//当窗口放大时，高度不变
        gridbag.setConstraints(scanType1, constraints);
        scanType1.setFont(contentFont);
        mainJPanel.add(scanType1);

        gridbag.setConstraints(customPorts, constraints);
        customPorts.setFont(contentFont);
        mainJPanel.add(customPorts);

        gridbag.setConstraints(scanType2, constraints);
        scanType2.setFont(contentFont);
        mainJPanel.add(scanType2);

        gridbag.setConstraints(panel1, constraints);
        mainJPanel.add(panel1);

        gridbag.setConstraints(panel2, constraints);
        mainJPanel.add(panel2);

        gridbag.setConstraints(scanType3, constraints);
        scanType3.setFont(contentFont);
        mainJPanel.add(scanType3);

        gridbag.setConstraints(panelIP, constraints);
        mainJPanel.add(panelIP);

        gridbag.setConstraints(panelScan, constraints);
        mainJPanel.add(panelScan);

        constraints.weighty = 1;//当窗口放大时，高度变化
        gridbag.setConstraints(panel3, constraints);
        mainJPanel.add(panel3);

        panel1.setLayout(new FlowLayout(FlowLayout.LEFT,30,5));
        panel1.add(panel1_1);
        panel1.add(panel1_2);
        panel1_1.setLayout(new FlowLayout(FlowLayout.CENTER,0,5));
        startJLabel.setFont(contentFont);
        panel1_1.add(startJLabel);
        startPort.setFont(contentFont);
        panel1_1.add(startPort);
        panel1_2.setLayout(new FlowLayout(FlowLayout.CENTER,0,5));
        endJLabel.setFont(contentFont);
        panel1_2.add(endJLabel);
        endPort.setFont(contentFont);
        panel1_2.add(endPort);

        panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        customDomain.setFont(contentFont);
        domainIPJLabel.setFont(contentFont);
        panel2.add(domainIPJLabel);
        panel2.add(customDomain);
        panel2.add(panel2_2);
        panel2_2.setLayout(new FlowLayout());

        panelIP.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panelIP.add(panelIP1);
        panelIP.add(panelIP2);
        panelIP1.setLayout(new FlowLayout(FlowLayout.CENTER,0,5));
        startIPJLabel.setFont(contentFont);
        panelIP1.add(startIPJLabel);
        startIP.setFont(contentFont);
        panelIP1.add(startIP);
        panelIP2.setLayout(new FlowLayout(FlowLayout.CENTER,0,5));
        endIPJLabel.setFont(contentFont);
        panelIP2.add(endIPJLabel);
        endIP.setFont(contentFont);
        panelIP2.add(endIP);

        panelScan.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
        threadNum.setFont(contentFont);
        panelScan.add(threadNum);
        customThreadNum.setFont(contentFont);
        panelScan.add(customThreadNum);
        beginJButton.setFont(contentFont);
        panelScan.add(beginJButton);

        panel3.setLayout(new GridLayout(1, 2));
        panel3.add(panel4);
        panel3.add(panel5);
        panel4.setLayout(new BorderLayout());
        progressJLabel.setFont(contentFont);
        progressJLabel.setHorizontalAlignment(JLabel.CENTER);
        panel4.add(progressJLabel,BorderLayout.NORTH);
        panel4.add(progressPane,BorderLayout.CENTER);
        progressJtJTextArea.setFont(contentFont);
        progressPane.setViewportView(progressJtJTextArea);
        progressJtJTextArea.setEditable(false);
        progressJtJTextArea.setLineWrap(true);
        progressJtJTextArea.setWrapStyleWord(true);
        panel5.setLayout(new BorderLayout());
        resultJLabel.setFont(contentFont);
        resultJLabel.setHorizontalAlignment(JLabel.CENTER);
        panel5.add(resultJLabel,BorderLayout.NORTH);
        panel5.add(resultPane,BorderLayout.CENTER);
        resultJTextArea.setFont(contentFont);
        resultPane.setViewportView(resultJTextArea);
        resultJTextArea.setEditable(false);
        resultJTextArea.setLineWrap(true);
        resultJTextArea.setWrapStyleWord(true);

        //菜单
        jMenuBar = new JMenuBar();
        help = new JMenu("帮助");
        author = new JMenuItem("作者");
        contact = new JMenuItem("联系方式");
        version = new JMenuItem("版本号");
        readme = new JMenuItem("说明");
        help.setFont(menuFont);
        jMenuBar.add(help);
        author.setFont(menuFont);
        help.add(author);
        contact.setFont(menuFont);
        help.add(contact);
        version.setFont(menuFont);
        help.add(version);
        readme.setFont(menuFont);
        help.add(readme);

        add(mainJPanel);
        setJMenuBar(jMenuBar);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        beginJButton.addActionListener(this);
        author.addActionListener(this);
        contact.addActionListener(this);
        version.addActionListener(this);
        readme.addActionListener(this);
    }

    /**
     * 点击事件，根据选择的不同扫描方式，开启不同的线程开始扫描
     * */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == beginJButton){
            progressJtJTextArea.setText("");
            resultJTextArea.setText("");
            String domain = getDomainString(customDomain.getText().trim());//获取域名
            String startip = getDomainString(startIP.getText().trim());//获取起始IP
            String endip = getDomainString(endIP.getText().trim());//获取结束IP
            int threadNumber = Integer.parseInt(customThreadNum.getText().trim());
            if(domain == null)
                return ;
            if (startip == null)
                return;
            if (endip == null)
                return;
            if(scanType1.isSelected()){
                String[] portsString = customPorts.getText().split(",");
                //端口转化为int型
                int[] ports = new int[portsString.length];
                for(int i=0;i<portsString.length;i++)
                    ports[i] = Integer.parseInt(portsString[i].trim());
                //创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
                ExecutorService threadPool = Executors.newCachedThreadPool();
                for (int i = 0; i < threadNumber; i++) {
                    ScanThread1 scanThread1 = new ScanThread1(domain, ports,
                            threadNumber, i, 800);//新建线程
                    threadPool.execute(scanThread1);//开启线程池中的任务
                }
                //线程池将变成shutdown状态，此时不接收新任务，但会处理完正在运行的 和 在阻塞队列中等待处理的任务
                threadPool.shutdown();
            }
            else if(scanType2.isSelected()){
                int startPortInt = Integer.parseInt(startPort.getText().trim());
                int endPortInt = Integer.parseInt(endPort.getText().trim());

                ExecutorService threadPool = Executors.newCachedThreadPool();
                for (int i = 0; i < threadNumber; i++) {
                    ScanThread2 scanThread2 = new ScanThread2(domain, startPortInt,endPortInt,
                            threadNumber, i, 800);
                    threadPool.execute(scanThread2);
                }
                threadPool.shutdown();
            }
            else if (scanType3.isSelected()){
                String[] startips = startip.split("\\.");//起始IP地址以'.'分割存入字符串数组
                String[] endips = endip.split("\\.");//结束IP地址以'.'分割存入字符串数组
                Queue<String> allIp;//通过起始IP和结束IP构造的IP地址存入队列
                if (startips.length != 4 || endips.length != 4)//判断IP地址是否合法
                    return;
                int[] intStartIP = new int[4];//IP地址四个部分存放在整型数组（起始）
                int[] intEndIP = new int[4];//IP地址四个部分存放在整型数组（结束）
                for (int i=0;i<4;i++){//将字符串数组中的IP地址转换为整型
                    try {
                        intStartIP[i] = Integer.parseInt(startips[i]);
                        intEndIP[i] = Integer.parseInt(endips[i]);
                    }catch (Exception exception){
                        return;
                    }
                }
                allIp = new LinkedList<String>();//新建队列
                //四层循环构造所有IP地址
                for (int i = Math.abs(intEndIP[0]-intStartIP[0]);i>=0;i--)
                    for (int j = Math.abs(intEndIP[1]-intStartIP[1]);j>=0;j--)
                        for (int p = Math.abs(intEndIP[2]-intStartIP[2]);p>=0;p--)
                            for (int q = Math.abs(intEndIP[3]-intStartIP[3]);q>=0;q--){
                                int x = intStartIP[0]+i;
                                int y = intStartIP[1]+j;
                                int z = intStartIP[2]+p;
                                int k = intStartIP[3]+q;
                                allIp.offer(""+x+"."+y+"."+z+"."+k);//将构造的IP地址存入队列
                            }
                ExecutorService threadPool = Executors.newCachedThreadPool();
                for (int i = 0; i < threadNumber; i++) {
                    ScanThread3 scanThread3 = new ScanThread3(allIp);
                    threadPool.execute(scanThread3);
                }
                threadPool.shutdown();
            }
        }
        else if(e.getSource() == author){
            JOptionPane.showMessageDialog(this, "listone","作者：",JOptionPane.INFORMATION_MESSAGE);
        }
        else if(e.getSource() == contact){
            JOptionPane.showMessageDialog(this, "微信公众号：LISTONE\n" +
                    "博客：https://cookiexss.github.io/","联系方式：",JOptionPane.INFORMATION_MESSAGE);
        }
        else if(e.getSource() == version){
            JOptionPane.showMessageDialog(this, "v1.0.0","版本号：",JOptionPane.INFORMATION_MESSAGE);
        }
        else if(e.getSource() == readme){
            JOptionPane.showMessageDialog(this, "多线程端口/IP扫描工具，三个扫描方式任你选择，你值得拥有！！！","说明：",JOptionPane.INFORMATION_MESSAGE);
        }
    }

    class ScanThread1 implements Runnable{
        private String domain;
        private int[] ports; // 待扫描的端口的Set集合
        private int threadNumber, serial, timeout; // 线程数，这是第几个线程，超时时间

        public ScanThread1(String domain,int[] ports, int threadNumber, int serial,
                           int timeout) {
            this.domain = domain;
            this.ports = ports;
            this.threadNumber = threadNumber;
            this.serial = serial;
            this.timeout = timeout;
        }

        public void run() {
            int port = 0;//数组下标
            try {
                InetAddress address = InetAddress.getByName(domain);
                Socket socket;
                SocketAddress socketAddress;
                if (ports.length < 1)
                    return;
                for (port = 0 + serial; port <= ports.length - 1; port += threadNumber) {
                    //UI事件调度线程（EDT） 负责GUI组件的绘制和更新 异步的
                    SwingUtilities.invokeLater(new Progress1Runnable(ports[port]));  //更新界面

                    socket = new Socket();
                    socketAddress = new InetSocketAddress(address, ports[port]);
                    try {
                        socket.connect(socketAddress, timeout);
                        socket.close();
                        SwingUtilities.invokeLater(new Result1Runnable(ports[port]));  //更新界面
                    } catch (IOException e) {

                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }
    class ScanThread2 implements Runnable{
        private String domain;
        private int startPort = 20,endPort = 9000; // 待扫描的端口的Set集合
        private int threadNumber, serial, timeout; // 线程数，这是第几个线程，超时时间

        public ScanThread2(String domain, int startPort, int endPort,
                           int threadNumber, int serial, int timeout) {
            this.domain = domain;
            this.startPort = startPort;
            this.endPort = endPort;
            this.threadNumber = threadNumber;
            this.serial = serial;
            this.timeout = timeout;
        }

        public void run() {
            int port = 0;//端口号
            try {
                InetAddress address = InetAddress.getByName(domain);
                Socket socket;
                SocketAddress socketAddress;
                for (port = startPort + serial; port <= endPort; port += threadNumber) {
                    SwingUtilities.invokeLater(new Progress1Runnable(port));  //更新界面

                    socket = new Socket();
                    socketAddress = new InetSocketAddress(address, port);
                    try {
                        socket.connect(socketAddress, timeout); // 超时时间
                        socket.close();
                        SwingUtilities.invokeLater(new Result1Runnable(port));  //更新界面
                    } catch (IOException e) {

                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        }

    }

    class ScanThread3 implements Runnable{
        private String taskIp = null;
        private Queue<String> allIp;
        public ScanThread3(Queue<String> allIp){
            this.allIp = allIp;
        }

        private String getIp() {
            String ip = null;
            synchronized (allIp) { //一次只有一个线程进入该代码块.此时,线程获得的是成员锁
                ip = allIp.poll();//返回第一个元素，并在队列中删除
            }
            return ip;
        }

        public void run(){
            try {
                while ((taskIp = getIp()) != null) {
                    SwingUtilities.invokeLater(new Progress2Runnable(taskIp));  //更新界面
                    InetAddress addr = InetAddress.getByName(taskIp);
                    if (addr.isReachable(5000)) {
                        SwingUtilities.invokeLater(new Result2Runnable(taskIp));  //更新界面
                    }
                }
            } catch (SocketException e) {

            } catch (Exception e) {

            }
        }
    }

    /**
     * UI事件调度线程（EDT）
     * 由EDT调用 更新界面的线程
     * */
    class Progress1Runnable implements Runnable{
        private int currentPort = 0;

        public Progress1Runnable(int currentPort) {
            this.currentPort = currentPort;
        }

        public void run() {
            progressJtJTextArea.setEditable(true);
            progressJtJTextArea.append("正在扫描端口：" + currentPort + "\n");
            progressJtJTextArea.setEditable(false);
            //设置显示最新内容
            progressJtJTextArea.selectAll();
            progressJtJTextArea.setCaretPosition(progressJtJTextArea.getSelectionEnd());
        }

    }
    /**
     * 同上
     * */
    class Result1Runnable implements Runnable{
        private int currentPort = 0;

        public Result1Runnable(int currentPort) {
            this.currentPort = currentPort;
        }
        public void run() {
            resultJTextArea.setEditable(true);
            resultJTextArea.append("端口：" + currentPort + "    开放\n");
            resultJTextArea.setEditable(false);
            resultJTextArea.selectAll();
            resultJTextArea.setCaretPosition(resultJTextArea.getSelectionEnd());
        }
    }

    class Progress2Runnable implements Runnable{
        private String currentIP = null;

        public Progress2Runnable(String currentPort) {
            this.currentIP = currentPort;
        }

        public void run() {
            progressJtJTextArea.setEditable(true);
            progressJtJTextArea.append("正在扫描IP：" + currentIP + "\n");
            progressJtJTextArea.setEditable(false);
            //设置显示最新内容
            progressJtJTextArea.selectAll();
            progressJtJTextArea.setCaretPosition(progressJtJTextArea.getSelectionEnd());
        }

    }

    class Result2Runnable implements Runnable{
        private String currentIP = null;

        public Result2Runnable(String currentIP) {
            this.currentIP = currentIP;
        }
        public void run() {
            resultJTextArea.setEditable(true);
            resultJTextArea.append("IP：" + currentIP + "    存活\n");
            resultJTextArea.setEditable(false);
            resultJTextArea.selectAll();
            resultJTextArea.setCaretPosition(resultJTextArea.getSelectionEnd());
        }
    }

    /**
     * 根据输入的字符串提取出其中的域名字符串或者IP字符串，如：cookiexss.github.io
     *
     * @param str 输入的包含域名|IP的字符串
     * @return 域名或IP字符串
     * */
    public static String getDomainString(String str){
        String reg = "[a-zA-Z0-9][a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][a-zA-Z0-9]{0,62})+";
        Pattern pattern = Pattern.compile(reg);//创建并初始化模式对象
        Matcher matcher = pattern.matcher(str);//创建并用待匹配字符序列初始化匹配对象
        if(matcher.find()){
            return matcher.group();
        }
        return "";
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {//EDT 保证SwingAPI线程安全
            public void run() {
                new PortScan();
            }
        });
    }
}