import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class TcpClient extends JFrame implements ActionListener, ListSelectionListener, MouseListener{

	int screen_width=900,screen_height=600;
	int side_panel_width=150;
	int bottom_panel_height=45;
	int server_port=8081;
	String server_ip="localhost";
	String clientName="client";
	JDialog oneTimeDialog;
	JDialog chatDialog;

	Thread receiver,sender;
	HashMap<String,Integer> channels;	// 1 refers "on", 0 refers "off"

	JButton sendButton,connectButton;
	JTextField inputField;
	JEditorPane editor;
	JTextField name_field;
	JLabel label_name;

	JList<String> list;
	DefaultListModel<String> model;
	boolean clickedAlready=false;
	PrintWriter writer;

	static boolean threads_running_flag=false;
	public static String ADD_CLIENT="74d228127c5e4554a3f706370978f718";
	public static String REMOVE_CLIENT="8b8b77288d4443ccbe3032f73b8fe3a5";
	public static String LIST_CLIENTS="d34638dc9cd9453db6631e43b4f6c376";

	TcpClient(){ 
		setBackground(Color.white);
		setSize(screen_width,screen_height);
		setTitle("Group Messenger");
		channels=new HashMap<>();
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		model=new DefaultListModel<>();
		list=new JList<>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setFixedCellWidth(side_panel_width);
		list.setFixedCellHeight(30);
		list.addListSelectionListener(this);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.addMouseListener(this);

		sendButton=new JButton("Send");
		sendButton.addActionListener(this);
		sendButton.setPreferredSize(new Dimension(side_panel_width,bottom_panel_height));

		createOneTimeDialog();
		//showChatDialog();

		createUsersPanel();
		createMainPanel();

		inputField.requestFocus();
		setVisible(true);		
	}

	public void createOneTimeDialog(){
		oneTimeDialog=new JDialog(this,"Required !!",Dialog.ModalityType.APPLICATION_MODAL);
		oneTimeDialog.setLocationRelativeTo(null);
		oneTimeDialog.setSize(360,270);
		oneTimeDialog.setLocation(screen_width/2-300/2,screen_height/2-300/2);
		oneTimeDialog.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				System.out.println("close window");
				dispose();
				System.exit(1);
			}
		});

		JPanel p=new JPanel(new GridLayout(3,1));
		label_name=new JLabel("Enter your name");
		name_field=new JTextField(25);
		name_field.addActionListener(this);
		connectButton=new JButton("Connect");
		connectButton.addActionListener(this);

		JPanel p1=new JPanel(new GridBagLayout());
		JPanel p2=new JPanel(new GridBagLayout());
		JPanel p3=new JPanel(new GridBagLayout());

		p1.add(label_name);
		p2.add(name_field);
		p3.add(connectButton);

		p.add(p1);
		p.add(p2);
		p.add(p3);
		oneTimeDialog.add(p);
		oneTimeDialog.setVisible(true);
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
		inputField.addActionListener(this);

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

	public void showChatDialog(){
		chatDialog=new JDialog(this,"Required !!",Dialog.ModalityType.APPLICATION_MODAL);
		chatDialog.setSize(350,350);
		chatDialog.setLocationRelativeTo(null);
		chatDialog.setLocation(screen_width/2-300/2,screen_height/2-300/2);
		chatDialog.setTitle("Client1 to Client2");

		int bottomHeight=35;
		int bottomWidth=50;
		Dimension d=new Dimension(bottomWidth,bottomHeight);

		JPanel p=new JPanel(new BorderLayout());
		JEditorPane ed1=new JEditorPane();
		ed1.setEditable(false);
		ed1.setFont(new Font("Arial",Font.PLAIN,14));
		ed1.setText("Connected..");
		ed1.setBackground(new Color(226,226,226));
		ed1.setForeground(new Color(43,43,43));

		JPanel bottomPane=new JPanel(new GridBagLayout());
		JButton chat_sendButton=new JButton("Send");
		chat_sendButton.addActionListener(this);
		chat_sendButton.setPreferredSize(d);

		JTextField chat_messageText=new JTextField(100);
		chat_messageText.setPreferredSize(d);

		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridx=0;	gbc.gridy=0;	gbc.gridheight=1;	gbc.gridwidth=1;	gbc.anchor=GridBagConstraints.CENTER;
		gbc.weightx=1.0;	gbc.weighty=1.0; 	
		gbc.fill=GridBagConstraints.BOTH;
		bottomPane.add(chat_messageText,gbc);
		
		gbc.weightx=0;	gbc.weighty=0; 	
		gbc.gridx=1;	
		gbc.fill=GridBagConstraints.VERTICAL;
		bottomPane.add(chat_sendButton,gbc);

		p.add(ed1,"Center");
		p.add(bottomPane,"South");
		chatDialog.add(p);

		chatDialog.setVisible(true);
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
		editor.setCaretPosition(editor.getText().length());
	}

	public void test(Object in){
		System.out.println(in.toString());
	}

	public static void main(String args[]){
		TcpClient client=new TcpClient();
	}

	public void actionPerformed(ActionEvent action){

		// for one time dialog
		if((action.getSource()==connectButton||action.getSource()==name_field)&&(!clickedAlready)){
			test("Enter a name: "+ name_field.getText());	

			if((name_field.getText()!=null)&(name_field.getText().length()!=0)){

				try{
					Socket socket=null;

					if(server_ip.equals("localhost")){
						InetAddress addr = InetAddress.getByName("localhost");
						socket=new Socket(addr,server_port);
					}else{
						socket=new Socket(server_ip,server_port);
					}
					
					test("Socket= "+socket);

					clientName=name_field.getText();
					test("Enter a name: "+clientName);		

					writer = new PrintWriter(socket.getOutputStream(),true);
					writer.println(clientName);

					BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String fromServer=reader.readLine();

					if(fromServer.equals("alias already taken... please enter another alias")){
						label_name.setText("Alias already taken.. Please enter another alias");
						name_field.setText("");
						return;

					}else{
						setTitle("Group Messenger (alias: "+clientName+")");

						String list_all_actives=reader.readLine();
						if(list_all_actives.contains(LIST_CLIENTS)){
							test("list recieved "+list_all_actives);

							String[] aliases=list_all_actives.split(" ");
							for(int i=1;i<aliases.length;i++){
								addAliasToList(aliases[i]);
							}
						}

						final Socket socket2=socket;

						// sender and reciever threads
						sender=new Thread(new Runnable(){
							public void run(){
								//
							}
						});

						receiver=new Thread(new Runnable(){
							public void run(){
								
								try{
									boolean flag=true;
									String str=null;
									int index_add_client=-1;
									int index_remove_client=-1;
									BufferedReader in = new BufferedReader(new InputStreamReader(socket2.getInputStream()));

									while(flag){	
										str=in.readLine(); test("log: "+str);

										index_add_client=str.indexOf(ADD_CLIENT+"");
										boolean old_client=false;

										if(index_add_client!=-1){
											int index=str.indexOf(" has entered in the room");
											if(index==-1){	 

												index=index_add_client;	
												old_client=true;   
												addAliasToList(str.substring(0,index));	
												str=str.substring(0,index);

											}else{
												addAliasToList(str.substring(0,index));	
												str=str.substring(0,index_add_client);
											}	
											
										}	

										if((str==null)||(old_client==true)){
											continue;
										}

										index_remove_client=str.indexOf(REMOVE_CLIENT+"");
										if(index_remove_client!=-1){
											int index=str.indexOf(" disconnects");
											if(index==-1){   
												index=index_remove_client;    
												old_client=true;    

												removeAliasFromList(str.substring(0,index));
												str=str.substring(0,index);	
											}else{
												removeAliasFromList(str.substring(0,index));
												str=str.substring(0,index_remove_client);	
											}
										}	

										if((str.trim().length()==0)||(old_client==true)){
											continue;
										}

										System.out.println("$ "+str);
										write2Editor(str);

										if(str.equals("END")){
											flag=false;
										}
									}

								}catch(Exception exp){
									exp.printStackTrace();
								}
							}
						});

						threads_running_flag=true;
						//sender.start();
						receiver.start();

						addAliasToList(clientName);

						oneTimeDialog.setVisible(false);
					}

					clickedAlready=true;

				}catch(Exception exp){
					test(exp.getMessage());
				}

			}else{
				label_name.setText("Enter valid Name");
			}

		}else if(action.getSource()==sendButton||action.getSource()==inputField){
			String text=inputField.getText();
			if(text.trim().length()==0){
				return;
			}

			writer.println(text);
			write2Editor("Me: "+inputField.getText());
			inputField.setText("");
		}
	}

	public void addAliasToList(String alias){
		//test("request received for adding "+alias);

		if(!model.contains(alias)&&(!alias.trim().equals(clientName))){
			model.addElement(alias);	
			channels.put(alias,0);
		}
	}

	public void removeAliasFromList(String alias){
		//test("request received for removing "+alias);

		model.removeElement(alias);
		channels.remove(alias);
	}

	public void valueChanged(ListSelectionEvent event){
		if(!event.getValueIsAdjusting()){
			JList source=(JList)event.getSource();
			String item=source.getSelectedValue().toString().trim();
			//test("item selected "+item);
		}
	}

	public void mouseClicked(MouseEvent e){
		if(e.getClickCount()==2){
		 int index = list.locationToIndex(e.getPoint());
         System.out.println("Double clicked on Item " + index+" "+clientName+" to "+list.getSelectedValue());
         //showChatDialog();
         ChatDialog dialog=new ChatDialog(this,clientName+" to "+list.getSelectedValue());
		}
	}

	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
}