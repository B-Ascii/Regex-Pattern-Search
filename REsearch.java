import java.util.*;
import java.io.*;

// Bedir Asici
// Casper Tyson

public class REsearch{

	static String fileName;
	public static void main(String[] args){
	
		fileName= args[0];
		REsearch res=new REsearch();
		res.start();
		return;
	}

	//declaring variables
	String currentLine="";
	Item currentItem;
	//where we are in the text
	int mark=0;
	int point=0;

	//statenum -2 represents the state item in the deq
	Item stat= new Item(-2, "-2", 0,0);

	//this helps with formatting the 0th start state
	String replaceWith="0 µ 1 1";
	String template="0 µ ";	

	//flags
	int fullMatch;//if the current item is at this position, wouve found a substring matching the regex.	
	
	public void start(){
	
		try{
			//reads the text
			BufferedReader textReader= new BufferedReader(new FileReader(new File(fileName)));
			//reads in fsm			
			BufferedReader fsmReader=new BufferedReader(new InputStreamReader(System.in));
			
			String fsm;
			while((fsm=fsmReader.readLine())!=null&&fsm.length()!=0){

				if(fsm.charAt(0)=='0'&&fsm.charAt(fsm.length()-1)=='0'){				
					fsm=replaceWith;		
				}
				if(fsm.charAt(0)=='0'&&fsm.charAt(fsm.length()-1)!='0'){		
					
					template+=fsm.charAt(fsm.length()-1)+" "+fsm.charAt(fsm.length()-1);
					fsm=template;
				}

				String[] fsmArray=fsm.split(" ");
				Item i= new Item(Integer.parseInt(fsmArray[0]),fsmArray[1],Integer.parseInt(fsmArray[2]),Integer.parseInt(fsmArray[3]));	
				Items.add(i);//this is jsut an array of fsms, when they are under use, they will be pushed on to the deque
									
				if(i.stateNum!=0 && i.pointer1==0 && i.pointer2==0){
					fsm=null;
					break;
				}	
			}

			Item toAdd;		
			
			//push the state and set up
			Deq.push(stat);
			currentItem=Items.get(Items.get(0).pointer1);
			currentLine=textReader.readLine();

			//know what the exit point is
			fullMatch=Items.size()-1;			

			//start by putting the start state on the stack
			Deq.push(currentItem);
			
			while(currentLine!=null){
				//grab the top item off the Deque
				currentItem=Deq.pop();

				//if its not null and not state
					if(currentItem!=null && currentItem.stateNum!=-2){			
					toAdd=currentItem;
					toAdd.flag=1;
					Items.set(toAdd.stateNum,toAdd);

					//if youve matched the last possible thing in the regex
					if(currentItem.stateNum==fullMatch){					
						
						//remove anything thats leftover gfrom the Deq
						while(Deq.head!=null){
							Deq.pop();
						}
						
						//push the state back
						Deq.push(stat);

						//print the match, read in next line
						System.out.println(currentLine);
						currentLine=textReader.readLine();
						if(currentLine==null){return;}

						//set up for the next regex by going back to the start state
						mark=0;
						point=0;
						resetAll();
						currentItem=Items.get(Items.get(0).pointer1);								
						Deq.push(currentItem);				
						toAdd=currentItem;
						toAdd.flag=1;
						Items.set(toAdd.stateNum,toAdd);
					}
					else if(literal(currentItem)){

						if(point==currentLine.length()){
							//scenario where it is a match but there is no more string to check, and there is still regex remaining
							mark=point;
							resetAll();
						}	
						else if(currentItem.ch.equals(String.valueOf(currentLine.charAt(point)))){
							//if it is a match
							while(Deq.head!=null){
								Deq.pop();
							}							
							//push the state back
							Deq.push(stat);	//clearing stack on match

							Deq.pushBottom(Items.get(currentItem.pointer1));					
							point++;
							resetAll();						
						}
						else if(currentItem.ch.equals("π")){
							//wildcard is a match on anything
							while(Deq.head!=null){ 
								Deq.pop();
							}							
							//push the state back
							Deq.push(stat);//clearing stack on match

							Deq.pushBottom(Items.get(currentItem.pointer1));						
							point++;
							resetAll();	
						}	
						else{							
							if(Deq.head.state.stateNum==-2 && Deq.head.next==null){
								//if state is the only one remaining
							mark++;
							point=mark;
							while(Deq.head!=null){
								Deq.pop();
							}
							resetAll();
							currentItem=Items.get(Items.get(0).pointer1);		
							Deq.push(stat);
							Deq.push(currentItem);
							}
							else{	
									//nothing needs to be done if else
									//System.out.println("not match, trying to match :"+currentItem.ch+" and "+String.valueOf(currentLine.charAt(point)));
							}
						}										
					}
					else{
						//not a literal
						if(Items.get(currentItem.pointer1).flag!=1){
						
							Deq.push(Items.get(currentItem.pointer1));
						}//if both pointers point to the same place skip this next one
						if(Items.get(currentItem.pointer2).flag!=1 && Items.get(currentItem.pointer2).stateNum!=Items.get(currentItem.pointer1).stateNum){

							Deq.push(Items.get(currentItem.pointer2));
						}
					}	
				}

				else if(currentItem.stateNum==-2 && Deq.head!=null){
					//there are no more current states, so the possible states are pushed above the state
					Deq.pushBottom(stat);
				}
				else{
					//this is for when you have no states in current and possiblem, therefore you must increment the pointers
					mark++;
					point=mark;
					resetAll();
					currentItem=Items.get(Items.get(0).pointer1);		
					Deq.push(stat);
					Deq.push(currentItem);
				}

				if(mark==currentLine.length()){		
					currentLine=textReader.readLine();
					if(currentLine==null){
						return;
					}
					//remove anything thats leftover gfrom the Deq
					while(Deq.head!=null){
						Deq.pop();
					}

					//push the state back
					Deq.push(stat);

					currentItem=Items.get(Items.get(0).pointer1);
					Deq.push(currentItem);
					mark=0;
					point=0;
				}
			}				
				
		}
		catch(Exception e){
		
			System.out.println(e);
		
		}
	
	}	

	public boolean literal(Item i){
		if(i.ch.equals("µ")||i.ch.equals("")){		
			return false;
		}
		else{
			return true;
		}
	}

	public void resetAll(){	
		for(Item i:Items){
			i.flag=0;
		}
	}

	ArrayList<Item> Items = new ArrayList<Item>();
	Dequeu Deq=new Dequeu();

	public class  Item{	

		public Item(int a, String b, int c, int d){		
			stateNum=a;
			ch=b;
			pointer1=c;
			pointer2=d;	
		}		
		
		int stateNum;
		String ch;
		int pointer1;
		int pointer2;		
		//1=visited 0 =not visited
		int flag= 0;	
	}

 	public class Dequeu{      
		Node head;
		public void add(Item s){                               
			Node temp = new Node();
			temp.state = s;
			temp.next = head;
			head = temp;
		}
	
		public Item pop(){
			Node last;
			Node current;
			Item toReturn;
			if(head == null){return null;}   

			if(head.next == null){          
				toReturn=head.state;
				head = null;
				return toReturn;
			}
			else{
				current = head.next;
				last = head;
				while(current.next != null){  
					last = current;
					current = current.next;
				}
				toReturn=current.state;
				current=null;
				last.next=null;			
					return toReturn;           
			}

		}

		public void push(Item i){
			if(head==null){
				head =new Node(i);
			}
			else{
				Node last = head;
				Node current =head.next;
				while(current!=null){
					last =current;
					current=current.next;
				}
				Node toAdd=new Node(i);
				last.next=toAdd;
			}
		}

		public void pushBottom(Item i){
			//head will never be null because of state item
			Node bot= new Node(i);
			bot.next=head;
			head=bot;
		}  
		
   
	}

	class Node{                             
		Node next;
		Item state;
		public Node(Item s){
			state = s;
		}
		public Node(){}                    
	}

}


