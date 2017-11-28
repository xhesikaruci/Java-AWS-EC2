package com.amazonaws.samples;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class cloud_GUI  {

	public JPanel contentPane;
	private JTextField textField;
	private JTable table;
	JTabbedPane tabbedPane = new JTabbedPane();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					cloud_GUI frame = new cloud_GUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public cloud_GUI() {
		
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.control);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(271, 100, 152, 26);
		textField.setBackground(SystemColor.window);
		contentPane.add(textField);
		textField.setColumns(10);
		JButton btnMonitor = new JButton("Monitor");
		btnMonitor.setBounds(625, 290, 131, 56);
		final JComboBox comboBox_3 = new JComboBox();
		comboBox_3.setBounds(544, 102, 248, 28);
		JPanel panel = new JPanel();
		panel.setBounds(56, 357, 512, 184);
		
		final JComboBox comboBox = new JComboBox();
		comboBox.setBounds(43, 96, 162, 24);
		comboBox.setFont(new Font("Consolas", Font.PLAIN, 18));
		comboBox.setForeground(Color.RED);
		comboBox.setBackground(SystemColor.text);
		contentPane.add(comboBox);
		
		list_regions regions = new list_regions();	
		comboBox.setModel(new DefaultComboBoxModel(regions.main(null)));
	
		
		final JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(43, 211, 832, 64);
		comboBox_1.setFont(new Font("Consolas", Font.PLAIN, 18));
		comboBox_1.setBackground(SystemColor.info);
		comboBox_1.setForeground(SystemColor.inactiveCaptionText);
		contentPane.add(comboBox_1);
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
					
			}
		});

		
		
		final JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setBounds(898, 215, 120, 28);
		comboBox_2.setFont(new Font("Consolas", Font.PLAIN, 18));
		comboBox_2.setBackground(SystemColor.textHighlight);
		contentPane.add(comboBox_2);
		comboBox_2.addItem("Start");
		comboBox_2.addItem("Stop");
		comboBox_2.addItem("Reboot");
		comboBox_2.addItem("Terminate");

		
		JLabel lblDefineRegion = new JLabel("Define Region");
		lblDefineRegion.setBounds(43, 50, 130, 22);
		lblDefineRegion.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(lblDefineRegion);
		
		JLabel lblAmi = new JLabel("AMI");
		lblAmi.setBounds(289, 50, 30, 22);
		lblAmi.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(lblAmi);
	
		JLabel lblYourInstances = new JLabel("Your Instances");
		lblYourInstances.setBounds(68, 167, 162, 10);
		lblYourInstances.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(lblYourInstances);
		
		comboBox_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String y = String.valueOf(comboBox.getSelectedItem());
				String z = String.valueOf(comboBox_1.getSelectedItem());
				System.out.println(z);
				String [] parts =z.split("\\ ");
				String inst_id= parts [0];
				String r = String.valueOf(comboBox.getSelectedItem());
				System.out.println(r);
				String action = String.valueOf(comboBox_2.getSelectedItem());
				start_stop_terminate mode = new start_stop_terminate();
				mode.main(inst_id,action,r);					
			}
		});
        panel.validate();
		DefaultTableModel table_1= new DefaultTableModel();
		comboBox_3.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(comboBox_3);
		 comboBox_3.addItem("Create");
		 comboBox_3.addItem("List Instances");
		
		comboBox_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
						
				
				if (comboBox_3.getSelectedItem().equals("Create"))
				{ 	String region = String.valueOf(comboBox.getSelectedItem());
					String ami_id = textField.getText(); 
					Create_instance create= new Create_instance();
					try {
						Create_instance.main(region, ami_id);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				if(comboBox_3.getSelectedItem().equals("List Instances")){
					String x = String.valueOf(comboBox.getSelectedItem());
					System.out.println(x);
					
				DescribeInstances instances_list = new DescribeInstances();
					comboBox_1.setModel(new DefaultComboBoxModel(instances_list.main(x)));	
				}
				
			}
		});		
		
	
		
		JLabel lblChooseInstanceActions = new JLabel("Choose Instance Actions");
		lblChooseInstanceActions.setBounds(474, 50, 230, 22);
		lblChooseInstanceActions.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(lblChooseInstanceActions);
		
		JLabel lblCloudwatchMetrics = new JLabel("CloudWatch Metrics");
		lblCloudwatchMetrics.setBounds(224, 289, 247, 62);
		lblCloudwatchMetrics.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(lblCloudwatchMetrics);
		btnMonitor.setBackground(Color.RED);
		contentPane.add(btnMonitor);
		contentPane.add(panel);
		
        final XYSeries CPU = new XYSeries("CPU Utilization");
      //  Goals.add(1, 1.0);

      //  public setDomainZeroBaselineVisible(boolean visible);
        XYDataset xyDataset = new XYSeriesCollection(CPU);

        JFreeChart chart = ChartFactory.createXYLineChart("CPU Utilization Over Time", "Time", "CPU", xyDataset, PlotOrientation.VERTICAL, true, true, false);
      

        ChartPanel CP = new ChartPanel(chart);
     //   CP.setPreferredSize(new Dimension(100, 200));
        CP.setMouseWheelEnabled(true);

        panel.add(CP, BorderLayout.CENTER);
        

		
		btnMonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
				String region= String.valueOf(comboBox.getSelectedItem());
				String z = String.valueOf(comboBox_1.getSelectedItem());
				String [] parts =z.split("\\ ");
				String instance_id= parts [0];
				cloudwatch_aws cloud = new cloudwatch_aws();
			
				String [] metric = cloud.main(instance_id, region);
				
				int i = 0;
				int l = metric.length -2 ;
				for (i=0; i<24; i++)
				{ 
					System.out.println(metric[i]);
					
				  String [] m = String.valueOf(metric[i]).split(",");
			  
				  float cpu = Float.parseFloat(m[0]);
				  System.out.println(cpu);
				// float timestamp = Float.parseFloat( m[1]);
				//  System.out.println(timestamp);
					CPU.add(i, cpu);
				  i++;
				}
				//textArea.setText(line);
			
			}
		});		
		
		
	}
}
