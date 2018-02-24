package tu.darmstadt.de.swc.kombi.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Responsible for creating controls for choosing algorithms and reductions
 * 
 * @author suhas
 *
 */
@SuppressWarnings("serial")
public class CustomizeStepsWindow extends JFrame {

	// Panel
	JPanel panelCustomize;
	// Reduction button
	JButton buttonReduction;
	// Cluster and Reduce button
	JButton buttonClusterAndReduce;
	// Scroller
	JScrollPane scrollPane;
	// List containing all the steps
	static ArrayList<String> listSteps = new ArrayList<String>();

	public CustomizeStepsWindow(String[] arrCommunityDetectionAlgorithms, Integer sliderMinimum, Integer sliderMaximum,
			Integer sliderInit) {

		panelCustomize = new JPanel(new GridLayout(0, 1));
		panelCustomize.setPreferredSize(new Dimension(400, 400));
		panelCustomize.setBackground(Color.white);

		JPanel panelButtons = new JPanel(new FlowLayout());
		panelButtons.setToolTipText("Add Steps");
		buttonReduction = new JButton("Add Reduce");
		buttonReduction.setPreferredSize(new Dimension(180, 25));
		buttonReduction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JPanel panelReduce = new JPanel(new FlowLayout());
				panelReduce.setToolTipText("Reduce");

				JTextField textReductionLevels = new JTextField();
				textReductionLevels.setPreferredSize(new Dimension(20, 20));
				textReductionLevels.setText("0");
				textReductionLevels.setEditable(false);
				JSlider sliderReductionLevels = new JSlider(JSlider.HORIZONTAL, sliderMinimum, sliderMaximum,
						sliderInit);
				sliderReductionLevels.addChangeListener(new ChangeListener() {

					@Override
					public void stateChanged(ChangeEvent e) {
						// TODO Auto-generated method stub
						textReductionLevels.setText(String.valueOf(((JSlider) e.getSource()).getValue()));
					}

				});

				panelReduce.add(new JLabel("No of Reductions:"));
				panelReduce.add(textReductionLevels);
				panelReduce.add(sliderReductionLevels);
				panelCustomize.add(panelReduce);

				panelCustomize.revalidate();
				panelCustomize.updateUI();

			}

		});

		buttonClusterAndReduce = new JButton("Add C&R ");
		buttonClusterAndReduce.setPreferredSize(new Dimension(180, 25));
		buttonClusterAndReduce.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				JPanel panelClusterAndReduce = new JPanel(new FlowLayout());
				panelClusterAndReduce.setToolTipText("Cluster and Reduce");
				@SuppressWarnings("rawtypes")
				DefaultComboBoxModel communityNames = new DefaultComboBoxModel();
				for (int i = 0; i < arrCommunityDetectionAlgorithms.length; i++) {
					communityNames.addElement(arrCommunityDetectionAlgorithms[i]);

				}

				@SuppressWarnings("rawtypes")
				JComboBox communityCombo = new JComboBox(communityNames);
				communityCombo.setSelectedIndex(0);
				communityCombo.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent e) {
						// TODO Auto-generated method stub
						@SuppressWarnings("unused")
						Object selectedItem = e.getItem();
						// SELECTED_COMMUNITY_NAME = selectedItem.toString();
					}

				});
				JScrollPane scrollPaneCommunityCombo = new JScrollPane(communityCombo);
				panelClusterAndReduce.add(scrollPaneCommunityCombo);

				JTextField textReductionLevels = new JTextField();
				textReductionLevels.setPreferredSize(new Dimension(20, 20));
				textReductionLevels.setText("0");
				textReductionLevels.setEditable(false);
				JSlider sliderReductionLevels = new JSlider(JSlider.HORIZONTAL, sliderMinimum, sliderMaximum,
						sliderInit);
				sliderReductionLevels.addChangeListener(new ChangeListener() {

					@Override
					public void stateChanged(ChangeEvent e) {
						// TODO Auto-generated method stub
						textReductionLevels.setText(String.valueOf(((JSlider) e.getSource()).getValue()));
					}

				});

				JCheckBox checkBoxIncludeNativeCommunity = new JCheckBox();
				@SuppressWarnings("unused")
				String includeValue = "false";
				checkBoxIncludeNativeCommunity.setSelected(true);
				checkBoxIncludeNativeCommunity.setVisible(false);
				checkBoxIncludeNativeCommunity.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(ItemEvent e) {
						// TODO Auto-generated method stub
						if (e.getStateChange() == ItemEvent.SELECTED) {

						} else {

						}
					}

				});

				JLabel labelInCom = new JLabel("Include Native community:");
				labelInCom.setVisible(false);
				panelClusterAndReduce.add(labelInCom);
				panelClusterAndReduce.add(checkBoxIncludeNativeCommunity);
				panelClusterAndReduce.add(new JLabel("No of Reductions:"));
				panelClusterAndReduce.add(textReductionLevels);
				panelClusterAndReduce.add(sliderReductionLevels);
				panelCustomize.add(panelClusterAndReduce);

				panelCustomize.revalidate();
				panelCustomize.updateUI();

			}

		});

		panelButtons.add(buttonReduction);
		panelButtons.add(buttonClusterAndReduce);

		panelCustomize.add(panelButtons);

		scrollPane = new JScrollPane(panelCustomize);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ValidateCustomizeWindow.booleanAllow = true;
				setVisible(false);
				dispose();

			}
		});
		this.add(scrollPane);
		this.pack();

	}

}
