package com.amazonaws.samples;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.amazonaws.services.cloudwatch.model.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JTabbedPane;


public class cloud_GUI extends JFrame {

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
					frame.setVisible(true);
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

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1234, 652);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.control);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setBounds(335, 78, 200, 34);
		textField.setBackground(SystemColor.window);
		contentPane.add(textField);
		textField.setColumns(10);
		JButton btnMonitor = new JButton("Monitor");
		btnMonitor.setFont(new Font("Consolas", Font.PLAIN, 18));
		btnMonitor.setForeground(SystemColor.desktop);
		btnMonitor.setBounds(625, 290, 131, 56);
		JComboBox<String> comboBox_3 = new JComboBox();
		comboBox_3.setBackground(SystemColor.info);
		comboBox_3.setBounds(627, 82, 248, 34);

		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(43, 80, 179, 34);
		comboBox.setFont(new Font("Consolas", Font.PLAIN, 18));
		comboBox.setForeground(SystemColor.desktop);
		comboBox.setBackground(SystemColor.info);
		contentPane.add(comboBox);

		list_regions regions = new list_regions();
		comboBox.setModel(new DefaultComboBoxModel(regions.main(null)));


		JComboBox<String[]> comboBox_1 = new JComboBox<String[]>();
		comboBox_1.setBounds(43, 195, 832, 64);
		comboBox_1.setFont(new Font("Consolas", Font.PLAIN, 18));
		comboBox_1.setBackground(SystemColor.info);
		comboBox_1.setForeground(SystemColor.inactiveCaptionText);
		contentPane.add(comboBox_1);

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

			}
		});



		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setBounds(898, 199, 120, 60);
		comboBox_2.setFont(new Font("Consolas", Font.PLAIN, 18));
		comboBox_2.setBackground(SystemColor.info);
		contentPane.add(comboBox_2);
		comboBox_2.addItem("Start");
		comboBox_2.addItem("Stop");
		comboBox_2.addItem("Reboot");
		comboBox_2.addItem("Terminate");


		JLabel lblDefineRegion = new JLabel("Define Region");
		lblDefineRegion.setBounds(55, 30, 130, 34);
		lblDefineRegion.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(lblDefineRegion);

		JLabel lblAmi = new JLabel("AMI");
		lblAmi.setBounds(418, 30, 30, 34);
		lblAmi.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(lblAmi);

		JLabel lblYourInstances = new JLabel("Your Instances");
		lblYourInstances.setBounds(321, 151, 162, 28);
		lblYourInstances.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(lblYourInstances);


		//************************************
		//Start Stop Terminate Reboot your selected instance
		comboBox_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String z = String.valueOf(comboBox_1.getSelectedItem());
				System.out.println(z);
				String [] parts =z.split("\\ ");
				String inst_id= parts [0];
				String r = String.valueOf(comboBox.getSelectedItem());
				System.out.println(r);
				String action = String.valueOf(comboBox_2.getSelectedItem());
				start_stop_terminate.main(inst_id,action,r);
				//comboBox_1.removeAllItems();
				DescribeInstances instances_list = new DescribeInstances();
				comboBox_1.setModel(new DefaultComboBoxModel(instances_list.main(r)));

			}
		});

		//*********************************************************************
		//Create an instance or list the instances in a particular region

		comboBox_3.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(comboBox_3);
		comboBox_3.addItem("Create");
		comboBox_3.addItem("List Instances");

		comboBox_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				if (comboBox_3.getSelectedItem().equals("Create"))
				{ 	String region = String.valueOf(comboBox.getSelectedItem());
					String ami_id = textField.getText();
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

//********************************************************************************************

		JLabel lblChooseInstanceActions = new JLabel("Choose Instance Actions");
		lblChooseInstanceActions.setBounds(645, 34, 230, 26);
		lblChooseInstanceActions.setFont(new Font("Consolas", Font.PLAIN, 18));
		contentPane.add(lblChooseInstanceActions);

		JLabel lblCloudwatchMetrics = new JLabel("CloudWatch Metrics");
		lblCloudwatchMetrics.setBounds(286, 291, 247, 62);
		lblCloudwatchMetrics.setFont(new Font("Consolas", Font.BOLD, 20));
		contentPane.add(lblCloudwatchMetrics);
		btnMonitor.setBackground(SystemColor.info);
		contentPane.add(btnMonitor);
//***********************************************************************************
// Display Chart with graph of CPU utilization





		btnMonitor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				XYSeries NetworkIn = new XYSeries("Network In (Bytes)");
				XYDataset xyDataset_2 = new XYSeriesCollection(NetworkIn);
				JFreeChart chart_2 = ChartFactory.createXYLineChart("Network In (Bytes) Over Time", "Time", "NetworkIn (bytes)", xyDataset_2, PlotOrientation.VERTICAL, true, true, false);
				ChartPanel CP_2 = new ChartPanel(chart_2);
				CP_2.setBounds(588, 383, 443, 213);
				contentPane.add(CP_2);
				//   CP.setPreferredSize(new Dimension(100, 200));
				CP_2.setMouseWheelEnabled(true);

				String region= String.valueOf(comboBox.getSelectedItem());
				String z = String.valueOf(comboBox_1.getSelectedItem());
				String [] parts =z.split("\\ ");
				String instance_id= parts [0];

				XYSeries CPU = new XYSeries("CPU Utilization");
				XYDataset xyDataset = new XYSeriesCollection(CPU);
				JFreeChart chart = ChartFactory.createXYLineChart("CPU Utilization Over Time", "Time", "CPU (percent) ", xyDataset, PlotOrientation.VERTICAL, true, true, false);
				ChartPanel CP = new ChartPanel(chart);
				CP.setBounds(43, 383, 443, 213);
				contentPane.add(CP);
				//   CP.setPreferredSize(new Dimension(100, 200));
				CP.setMouseWheelEnabled(true);
				cloudwatch_aws cloud = new cloudwatch_aws();
				String [] cpu_metric = cloud.main(instance_id, region);

				Network_In network_watch = new Network_In();
				String [] network_metric = network_watch.main(instance_id, region);


				float network;
				int i = 0;
				int l = cpu_metric.length ;
				float cpu;
				for (i=0; i<l; i++)
				{
					System.out.println(cpu_metric[i]);
					if(cpu_metric[i]!= null)
					{

						String [] m = String.valueOf(cpu_metric[i]).split(" ");

						cpu = Float.parseFloat(m[0]);
						System.out.println(cpu);
						CPU.add(i, cpu);
						System.out.println("REceived"+ network_metric[i]);


						String [] n = String.valueOf(network_metric[i]).split("//.");

						network = Float.parseFloat(n[0]);
						System.out.println("Network"+ network);
						NetworkIn.add(i, network);
					}
					else {
						cpu = 0;
						network = 0;
					}
					i++;
				}

			}
		});

	}
}