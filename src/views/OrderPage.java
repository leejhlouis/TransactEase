package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import models.order.Order;
import models.order.OrderDetail;
import models.products.Product;
import utils.AuthService;
import utils.ProductService;
import views.component.ButtonColumn;

public class OrderPage extends JFrame {
	private JPanel allPanel, leftPanel, rightPanel, cartPanel, listPanel, totalPanel;
	private JLabel lblTitle, lblCart, lblSubtotal, lblTax, lblTotal, lblSubtotalVal, lblTaxVal, lblTotalVal;
	private JButton btnOrder;
	private JTable table;
	private DefaultTableModel data;
	private JScrollPane scroll;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem1, menuItem2;
	
	private Vector<Product> products = new Vector<>();
	private Order current = new Order();

	public OrderPage() {
		initialize();
		setSize(new Dimension(800, 500));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
				
		addListener();
		loadData();
	}

	private void initialize() {
		allPanel = new JPanel(new GridLayout(1, 2, 20, 20));
		allPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		leftPanel = new JPanel(new BorderLayout());
		rightPanel = new JPanel(new BorderLayout());
		cartPanel = new JPanel(new BorderLayout());
		listPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        listPanel.add(new JPanel(), gbc);
        totalPanel = new JPanel(new GridLayout(3,2));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
		
		lblTitle = new JLabel("All Products");
		lblTitle.setFont(new Font("Lucida Grande", Font.BOLD, 20));
		lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		lblCart = new JLabel("Your shopping cart: ");
		lblSubtotal = new JLabel("Subtotal");
		lblTax = new JLabel("Tax");
		lblTotal = new JLabel("Total");
		lblSubtotalVal = new JLabel("0");
		lblTaxVal = new JLabel("0");
		lblTotalVal = new JLabel("0");
		
		btnOrder = new JButton("Order");
		
		Vector<Object> header = new Vector<>();
		header.add("ID");
		header.add("Name");
		header.add("Price");
		header.add("Stock");
		header.add("Action");

		data = new DefaultTableModel(header, 0);
		table = new JTable(data) {
			public boolean isCellEditable(int row, int column) 		{
				return true;
			};
		};

		scroll = new JScrollPane(table);
		
		menuBar = new JMenuBar();
		menu = new JMenu("Hello user!");
		menuItem1 = new JMenuItem("View Order History");
		menuItem2 = new JMenuItem("Logout");
		
		menu.add(menuItem1);
		menu.add(menuItem2);
		
		menuBar.add(menu);
		
		setJMenuBar(menuBar);
		
		totalPanel.add(lblSubtotal);
		totalPanel.add(lblSubtotalVal);
		totalPanel.add(lblTax);
		totalPanel.add(lblTaxVal);
		totalPanel.add(lblTotal);
		totalPanel.add(lblTotalVal);
		
		cartPanel.add(listPanel, BorderLayout.CENTER);
		cartPanel.add(totalPanel, BorderLayout.SOUTH);
		
		leftPanel.add(lblTitle, BorderLayout.NORTH);
		leftPanel.add(scroll, BorderLayout.CENTER);
		rightPanel.add(lblCart, BorderLayout.NORTH);
		rightPanel.add(cartPanel, BorderLayout.CENTER);
		rightPanel.add(btnOrder, BorderLayout.SOUTH);
		
		allPanel.add(leftPanel);
		allPanel.add(rightPanel);
		
		add(allPanel);
	}

	private void addListener() {
		menuItem1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new HistoryPage();
				dispose();
			}
		});
		
		menuItem2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AuthService.getInstance().logout();
				new LoginPage();
				dispose();
			}
		});
		
		Action order = new AbstractAction() {
		    public void actionPerformed(ActionEvent e)
		    {
		        JTable table = (JTable)e.getSource();
		        int modelRow = Integer.valueOf(e.getActionCommand());
		        System.out.println(modelRow);
		        
		        boolean flag = false;
		        for (OrderDetail orderDetail : current.getOrders()) {
					if(orderDetail.getProduct().getId().equals(data.getValueAt(modelRow, 0))) {
						orderDetail.addQuantity();
						System.out.println("qty:" + orderDetail.getQuantity());
						flag = true;
					}
				}
		        if(!flag) {
		        	current.addOrder(new OrderDetail(products.get(modelRow)));
		        }
		        loadCart();
		    }
		};
		ButtonColumn buttonColumn = new ButtonColumn(table, order, 4);
		buttonColumn.setMnemonic(KeyEvent.VK_D);
		
		btnOrder.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(current.getOrders().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Your shopping cart is empty!");
				} else {
					new PaymentPage(current);
					dispose();
				}				
			}
		});
	}
	
	public void loadCart() {
		listPanel.removeAll();
		
		JPanel header = new JPanel(new GridLayout(1, 3));
		header.add(new JLabel("Name"));
		header.add(new JLabel("Price"));
		header.add(new JLabel("Qty"));
		header.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        listPanel.add(header, gbc, 0);
        
        
		for (OrderDetail orderDetail : current.getOrders()) {
			
			JPanel panel = new JPanel(new GridLayout(1, 3));
            panel.add(new JLabel(orderDetail.getProduct().getName()));
            panel.add(new JLabel("Rp. " + Integer.toString(orderDetail.getProduct().getPrice())));
            panel.add(new JLabel(Integer.toString(orderDetail.getQuantity())));
            panel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridwidth = GridBagConstraints.REMAINDER;
            gbc2.weightx = 1;
            gbc2.fill = GridBagConstraints.HORIZONTAL;
            listPanel.add(panel, gbc);

		}
		
		lblSubtotalVal.setText(String.format("Rp. %.2f", current.calculateSubotal()));
		lblTaxVal.setText(String.format("Rp. %.2f", current.calculateTax()));
		lblTotalVal.setText(String.format("Rp. %.2f", current.calculateTotal()));

        revalidate();
        repaint();
	}

	public void setData() {
		data.setRowCount(0);
		
		for (Product p : products) {
			Vector<Object> rowData = new Vector<>();
			rowData.add(p.getId());
			rowData.add(p.getName());
			rowData.add(p.getPrice());
			rowData.add(p.getStock());
			rowData.add("Order");
			
			data.addRow(rowData);
		}
	}

	public void loadData() {
		products = ProductService.getInstance().getAllProducts();
				
		setData();
	}

}
