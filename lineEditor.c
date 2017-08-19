// Libraries included
#include<stdio.h>
#include<unistd.h>
#include<string.h>
#include<stdlib.h>
#include<conio.h>

/**
* This is a program of line editor
* based on the data structure linked list,
* with each node as a line in an array.
* With the help of line numbers, 
* edition operations are performed, 
* modifying the list and generating required output
*/ 


//node of the linked list
struct doublystruct
{
char s[40];//array of max length of 40 characters (defines a line in editor)
int loc;//defines a line number
struct doublystruct *prev, *next; //doubly linked list
};

//all the functions prototypes
 void commandToEdit(void);    
 void lineToAdd(struct doublystruct *temp);
 void input(void );
 void printlist(void );
 void closes(void );
 void edit(void );
 void nodeToAdd(char t[],struct doublystruct *q);
 void nodeToDelete(struct doublystruct *p);
 void clearlist(void );
 void nodeToEdit(struct doublystruct *p);
 void save(void );

struct doublystruct *head;                        //header node declaration
char file[30];							//for filename

FILE *fp=NULL;                            //file pointer declaration

main()
{
char c;

head = (struct doublystruct*)malloc(sizeof(struct doublystruct));            //header node memory allocation
head->next = head->prev = NULL;                //initialization
head->loc = 0;

while(1)
{
system("cls");

printf("\nThis Editor provides the following features \n");

printf("o :open a file and read through buffer\n   if file don't exist, it creates one\n");
printf("e :edit the current file\n");
printf("x :close the current file and/or open another file\n");
printf("q :quit and dicard all changes\n");

printf("\n-----------------------------------------------------------------------------\n");

c=getch();
//kep capture for operation to be performed
switch(c)
{
case 'o' :
case 'O' :
input();
break;


case 'e' :
case 'E' :
edit();
break;


case 'x' :
case 'X' :
closes();
break;


case 'q' :
case 'Q' :
system("cls");
exit(1);
break;
}
}
}


/*
adds a node to the linkedlist 
and make necessary links
*/
void nodeToAdd(char t[],struct doublystruct *q)
{

struct doublystruct*p=(struct doublystruct*)malloc(sizeof(struct doublystruct));//new node address given to the pointer
struct doublystruct *temp=q->next;	//the list
strcpy(p->s,t); 	//string entered copied to the array inside the node
//necessary linkages
p->prev=q;
p->next=q->next;

if((q->next)!=NULL)
{
((q->next)->prev)=p;
while(temp!=NULL)
{
(temp->loc)++;

temp=temp->next;		
}
}
q->next=p;
p->loc = q->loc + 1;
}


/* to delete a line in the file*/

void nodeToDelete(struct doublystruct *p)
{
struct doublystruct *temp=p->next;
//breking and bonding necessary links
((p->prev->next))=p->next;
if(p->next!=NULL)
{
((p->next)->prev)=p->prev;
while(temp!=NULL)
{
temp->loc=temp->loc-1;

temp=temp->next;
}
}
//delocating/freeing the memory
free(p);
}



void clearlist(void)
{
	//deletes all node
while(head->next!=NULL)
nodeToDelete(head->next);
}



void nodeToEdit(struct doublystruct *p)
{
//to edit a line
printf("\nThe line original is :\n %s ",p->s);
printf("\nEnter the new line : \n ");
//get the string and stored it in the same array 
gets(p->s);
printf("\nLine has been edited\n");
}


void printlist(void)
{
	
struct doublystruct *temp=head;
system("cls");//clearing the system
//and printing the whole list(lines of the file
while(temp->next!=NULL)
{
temp=temp->next;
printf("%d %s\n",temp->loc,temp->s);
}
}



void closes(void)//closes the file
{
if(fp==NULL)
return;
fclose(fp);
fp=NULL;
clearlist();
}



void input(void)
{
struct doublystruct *buffer=head; //allocating buffer first node initially
char ch;
char buf[40];//buffer for a line read from the file and adds it to the linked list while opening

if(fp!=NULL)
{
printf("\nsome file open, which will be closed\ndo you want to go on?( Y / N ):");
ch=getch();
if(ch=='n'||ch=='N')
return;
else
closes();//function closes the file
}

fflush(stdin);
//asks for the filename
printf("\nEnter the file name you want to open :");
scanf("%s",file);
strcat(file,".txt");
getchar();
fflush(stdin);
clearlist();

fp=fopen(file,"r");
if(fp==NULL)
{
printf("\nThe file doesnot exist do you want to create one?(Y/N) :");//if the file doesnt exists
ch=getchar();
//getchar();
if(ch=='N'||ch=='n')
return;
else
{
fp=fopen(file,"w");
edit();
return;
}
}

if(feof(fp))
return;
/*
	reading from the file(memory) all the lines with 
	the '\n' character which is implicit in the function
	and copying it in to the list for editing
*/
while((fgets(buf,41,fp))!=NULL)
{


//	removing the ASCII value of '\n'(10) and giving it '\0'
// 	so the next time we add, newline doesnt get added again 
buf[strlen(buf)-1]='\0';
nodeToAdd(buf,buffer);
buffer=buffer->next;
}
edit();//now it edits the list/lines/file
}



void edit(void)
{
struct doublystruct *temp=head->next;
char c,d;

system("cls");		//clears the system

if(fp==NULL)
{
printf("\nNo files  open\n");
return;
}

printf("\ncontents of the file will be displayed along with the line number\npress any key\n");
getch();
system("cls");
printlist();                            //buffered list printing
if(temp!=NULL)
printf("You are at the line no. %d",temp->loc);    //printing the line number of control
else
temp=head;

commandToEdit();

while(1)
{
c=getch();

switch(c)
{
case 'c' :
case 'C' :						//edit current line

nodeToEdit(temp);
break;

case 'p' :
case 'P' :                    //transit to prev line
if(temp==head)
{
printf("\nFile is empty");
break;
}
if(temp->prev!=head)								//for showing the line number being navigated
{
temp=temp->prev;
printf("\nYou are at line no. %d ",temp->loc);
}
else

printf("\nyou are at first line already");				//if at first line, no more transit
break;

case 'n' :
case 'N' :
if(temp->next!=NULL)
{
temp=temp->next;
printf("\nYou are at line no. %d",temp->loc);		//for showing the line number being navigated
}
else if(temp==head)
printf("\nFile is empty");
else
printf("\nyou are at last line already");		//last line, no more transit
break;

case 'a' :
case 'A' :
lineToAdd(temp);                //lineToAdd function takes input and creates a new node via nodeToAdd function
temp=temp->next;
printlist();
printf("\nYou are at line no. %d",temp->loc);
break;

case 'h' :
case 'H' :
system("cls");
commandToEdit();
system("cls");

case 'v' :
case 'V' :

printlist();

printf("\nYou are at line number %d",temp->loc);
break;

case 'D' :
case 'd' :
if(temp==head)
{
printf("\nFile empty\n");
break;
}
//all the messages to navigate
temp=temp->prev;
nodeToDelete(temp->next);            //delete node
printf("\nLine has been deleted\n");
printlist();                //prints list
if(temp->loc)
printf("\nYou are currently at line no. %d",temp->loc);//shows the line number you are currently at everytime
if((temp->prev==NULL) && (temp->next)!=NULL)
temp=temp->next;
else if((temp==head)&&((temp->next)==NULL))
printf("\nFile empty");        //empty list warning
break;

case 'X' :
case 'x' :                    //exit to main menu

printf("\nDo you want to save?(y/n) :");

d=getch();
if(d=='y'||d=='Y')
save();	//saves the function
closes();
return;
break;

case 's' :
case 'S' :                    //save and exit
save();
closes();
return;
break;

}

}

}


void lineToAdd(struct doublystruct *temp)
{
	//string gets to the buffer and nodetoadd function called

char buffer[40]; printf("\nenter new line :\n");
gets(buffer);
//int ind=strlen(buffer);
//buffer[ind]='\n';
nodeToAdd(buffer,temp);
}


void save(void)
{
struct doublystruct *temp=head->next;
fclose(fp);
fp=fopen(file,"w");

while(temp!=NULL)
{
fprintf(fp,"%s \n",temp->s);//'\n' explicitly given so that while reading it doesnt take it all in on line (line feed)
//fprintf(fp,"\n");
temp=temp->next;//saving the whole list
}

}


void commandToEdit(void)
{
	//all the editing features available
printf("\nall the Editor commands\n");
printf(" The edit menu provides the following features \n");
printf("C : edit the current line\n");
printf("P : move a line up\n");
printf("N :move a line down\n");
printf("D :delete the  current line\n");
printf("V :display cntents in buffer\n");
printf("A :add a line after the navigating line\n");
printf("S :save changes and exit to main menu\n");
printf("X :exit and discard changes \n");
printf("H :show list of all commands\n");
getch();
}

