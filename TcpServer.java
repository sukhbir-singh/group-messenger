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
						remotePrint(socket1,hashMap.get(socket1)+" has entered in the room");

					}catch(IOException exp){
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
									remotePrint(socket2,hashMap.get(socket2)+" disconnects");
									break;
								}			

	 							System.out.println("From "+hashMap.get(socket2)+": "+str);	
	 							remotePrint(socket2,"From "+hashMap.get(socket2)+": "+str);
							}

							}catch (IOException e) {
								System.out.println(hashMap.get(socket2)+" disconnects");
								remotePrint(socket2,hashMap.get(socket2)+" disconnects");
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
				}catch(IOException exp){
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
