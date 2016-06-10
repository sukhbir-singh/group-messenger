import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TcpClient extends JFrame implements ActionListener, ListSelectionListener{

	int screen_width=900,screen_height=600;
	int side_panel_width=150;
	int bottom_panel_height=45;
	int server_port=8081;
	String server_ip="localhost";
	Vector<String> online_users;

	JButton sendButton;
	JTextField inputField;
	JEditorPane editor;

	JList<String> list;
	DefaultListModel<String> model;

	TcpClient(){
		setBackground(Color.white);
		setSize(screen_width,screen_height);
		setTitle("Group Messenger");
		online_users=new Vector<>();
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		model=new DefaultListModel<>();
		list=new JList<>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFixedCellWidth(side_panel_width);
		list.setFixedCellHeight(30);
		list.addListSelectionListener(this);
		model.addElement("Client1");
		model.addElement("Client2");
		model.addElement("Client3");
		model.addElement("Client4");
		model.addElement("Client5");
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

		sendButton=new JButton("Send");
		sendButton.setPreferredSize(new Dimension(side_panel_width,bottom_panel_height));

		createUsersPanel();
		createMainPanel();

		inputField.requestFocus();
		setVisible(true);
	}

	public void createUsersPanel(){
		JPanel panel=new JPanel(new BorderLayout());
		JPanel topPanel=new JPanel(new GridBagLayout());
		JPanel listPanel=new JPanel(new GridLayout());
		JLabel label_online=new JLabel("Online");
		JScrollPane listScroll=new JScrollPane(listPanel);

		topPanel.setPreferredSize(new Dimension(side_panel_width,50));
		topPanel.add(label_online);
		listPanel.add(list);

		panel.add(topPanel,"North");
		panel.add(listScroll,"Center");
		panel.add(sendButton,"South");

		add(panel,"East");
	}

	public void createMainPanel(){
		JPanel main_panel=new JPanel(new BorderLayout());
		inputField=new JTextField("");
		inputField.setPreferredSize(new Dimension(100,bottom_panel_height));
		inputField.setFont(new Font("Arial",Font.PLAIN,20));

		editor=new JEditorPane();
		editor.setEditable(false);
		editor.setFont(new Font("Arial",Font.PLAIN,15));
		editor.setText("Welcome...");
		editor.setBackground(new Color(226,226,226));
		editor.setForeground(new Color(43,43,43));

		JScrollPane sp=new JScrollPane(editor);

		main_panel.add(inputField,"South");
		main_panel.add(sp,"Center");
		add(main_panel,"Center");
	}

	public void write2Editor(String in){
		write2Editor(in,false);
	}

	public void write2Editor(String in,boolean nextLine){
		if(nextLine){
			editor.setText(editor.getText()+"\n"+in+"\n");
		}else{
			editor.setText(editor.getText()+"\n"+in);
		}
		inputField.requestFocus();
	}

	public void test(Object in){
		System.out.println(in.toString());
	}

	public static void main(String args[]){
		TcpClient client=new TcpClient();
	}

	public void actionPerformed(ActionEvent action){

	}

	public void valueChanged(ListSelectionEvent event){
		if(!event.getValueIsAdjusting()){
			JList source=(JList)event.getSource();
			String item=source.getSelectedValue().toString().trim();
			test("item selected "+item);
		}
	}
}