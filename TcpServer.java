import java.net.*;
import java.io.*;
import java.util.*;

public class TcpServer
{
	public static final int PORT=8081;
	static ServerSocket serverSocket;
	static HashMap<Socket,String> hashMap=new HashMap<>();
	static Socket socket;
	static String name=null;
	static int count=0;

	public static String ADD_CLIENT="74d228127c5e4554a3f706370978f718";
	public static String REMOVE_CLIENT="8b8b77288d4443ccbe3032f73b8fe3a5";
	public static String LIST_CLIENTS="d34638dc9cd9453db6631e43b4f6c376";
	public static String START_CHAT_DIALOG="78e11adaf50b4cce825d2bfecdd57cec";
	public static String SEND_DIRECT_MESSAGE="5ae3846ad06a415b8810441bba46dbda";
	public static String ACTIVE_DIALOG="d11b5acc90c246a6bb60e7ae4bb06af5";
	public static String DEACTIVE_DIALOG="e078af7ba3934789818b652d938253cb";

	public static void main(String[] args)throws Exception
	{
		serverSocket=new ServerSocket(PORT);
		System.out.println("Started : "+serverSocket);

		Thread server_accepter=new Thread(new Runnable(){
			public void run(){

				while(true){
				
					try{
						socket=serverSocket.accept();
						System.out.println("Connection Accepted: "+socket);

						BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String name=reader.readLine();

						PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);	
						if((name==null)|nameAlreadyPresent(name)){
							// write to that socket informing please select any other name	
					   		out.println("alias already taken... please enter another alias");
					   		System.out.println("alias "+name+" already taken... please enter any another alias");
							continue;
						}

						Socket socket1=socket;
						
						count=count+1;
						
						hashMap.put(socket1,name);	
						System.out.println(hashMap.get(socket1)+" has entered in the room");
						out.println(name+" you are now part of group");
						remotePrint(socket1,hashMap.get(socket1)+" has entered in the room"+ADD_CLIENT);

						//tellRoomMembers(socket1);
						Set<Socket> ss=hashMap.keySet();
						Iterator<Socket> it1=ss.iterator();
						String list_all_active_users=LIST_CLIENTS;

						while(it1.hasNext()){
							String alias=hashMap.get(it1.next());
							list_all_active_users=list_all_active_users+" "+alias;
						}

						out.println(list_all_active_users);
						System.out.println("list send : "+list_all_active_users);

					}catch(Exception exp){
						exp.printStackTrace();
					}

					final Socket socket2=socket;
				
					Thread receiver=new Thread(new Runnable(){
					public void run(){

						try{
							String str=null;
							BufferedReader in=new BufferedReader(new InputStreamReader(socket2.getInputStream()));

							while(true){
								str=in.readLine();

								if((str==null)|(str.equals("null"))|(str.equals("END"))){
									System.out.println(hashMap.get(socket2)+" disconnects");
									remotePrint(socket2,hashMap.get(socket2)+" disconnects"+REMOVE_CLIENT);
									hashMap.remove(socket2);
									break;
								}			

								boolean flag_nextloop=false;

								if((str.contains(START_CHAT_DIALOG+""))||(str.contains(SEND_DIRECT_MESSAGE+""))||(str.contains(ACTIVE_DIALOG+""))||(str.contains(DEACTIVE_DIALOG+""))){
									String[] splits=str.split(" ");		System.out.println("@server : "+str);
									String to_client=splits[3];

									// to find to_client's socket and forward start chat message to it only
									Set<Socket> sockets=hashMap.keySet();
									Iterator<Socket> iterator=sockets.iterator();

									while(iterator.hasNext()){
										Socket sc1=iterator.next();
										String temp=hashMap.get(sc1);
										if(temp.equals(to_client)){
											PrintWriter out1=new PrintWriter(new BufferedWriter(new OutputStreamWriter(sc1.getOutputStream())),true);	
											out1.println(str);
											flag_nextloop=true;
											break;
										}
									}
								}

								if(flag_nextloop==true){
									continue;
								}

	 							System.out.println(""+hashMap.get(socket2)+": "+str);	
	 							remotePrint(socket2,""+hashMap.get(socket2)+": "+str);
							}

							}catch (Exception e) {
								System.out.println(hashMap.get(socket2)+" disconnects");
								remotePrint(socket2,hashMap.get(socket2)+" disconnects"+REMOVE_CLIENT);
								hashMap.remove(socket2);
								//e.printStackTrace();
							}
						}						
					});

					receiver.start();
				}
			}

			public void remotePrint(Socket current,String text) {
				try{
					Set<Socket> sockets=hashMap.keySet();
					Iterator<Socket> iterator=sockets.iterator();

					while(iterator.hasNext()){
						Socket temp=iterator.next();
						if(temp!=current){
						   PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(temp.getOutputStream())),true);	
						   out.println(text);
						}
					}
				}catch(Exception exp){
					//
				}
			}

			public boolean nameAlreadyPresent(String name) {
				boolean present=false;
				Set<Socket> sockets=hashMap.keySet();
				Iterator<Socket> iterator=sockets.iterator();

				while(iterator.hasNext()){
					String alias=hashMap.get(iterator.next());
					if(alias.equals(name)){
						present=true; break;
					}
				}
				
				return present;
			}
					
		});

		server_accepter.start();

		server_accepter.join();

		System.out.println("Closing Client Connections");
		socket.close();
		serverSocket.close();
		
	}
}
