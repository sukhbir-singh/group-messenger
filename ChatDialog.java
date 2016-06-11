import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class ChatDialog extends JDialog implements ActionListener{

	JFrame parentFrame;
	JEditorPane editor;
	JButton chat_sendButton;
	JTextField inputField;

	public ChatDialog(JFrame frame,String title){
		super(frame,title,Dialog.ModalityType.APPLICATION_MODAL);
		parentFrame=frame;

		setSize(350,350);
		setLocationRelativeTo(null);

		int bottomHeight=35;
		int bottomWidth=50;
		Dimension d=new Dimension(bottomWidth,bottomHeight);

		JPanel p=new JPanel(new BorderLayout());
		editor=new JEditorPane();
		editor.setEditable(false);
		editor.setFont(new Font("Arial",Font.PLAIN,14));
		editor.setText("Connected..");
		editor.setBackground(new Color(226,226,226));
		editor.setForeground(new Color(43,43,43));

		JPanel bottomPane=new JPanel(new GridBagLayout());
		chat_sendButton=new JButton("Send");
		chat_sendButton.addActionListener(this);
		chat_sendButton.setPreferredSize(d);

		inputField=new JTextField(100);
		inputField.setPreferredSize(d);

		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridx=0;	gbc.gridy=0;	gbc.gridheight=1;	gbc.gridwidth=1;	gbc.anchor=GridBagConstraints.CENTER;
		gbc.weightx=1.0;	gbc.weighty=1.0; 	
		gbc.fill=GridBagConstraints.BOTH;
		bottomPane.add(inputField,gbc);
		
		gbc.weightx=0;	gbc.weighty=0; 	
		gbc.gridx=1;	
		gbc.fill=GridBagConstraints.VERTICAL;
		bottomPane.add(chat_sendButton,gbc);

		p.add(editor,"Center");
		p.add(bottomPane,"South");
		add(p);

		setVisible(true);
	}

	public void title(String in){
		setTitle(in);
	}

	public void actionPerformed(ActionEvent event){

	}

}