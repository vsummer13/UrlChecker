import qual.*;

public class urlTestType {
    public static void main(String[] args) {
	//Supposed to get 23 errors;
	System.out.println("No more errors!");
    }
	
	public static void testType(){
		@U String url1 = "https://google.com"; //Accept
		@U String url2 = "https://checkerframework.org/manual/#build-source"; //Accept
		
		@U String noturl = "https://#no"; //Error
		//...ECT: Regular Expression: do best I can

		
		@Scheme String scheme1 = "https://";//Accept
		@Scheme String scheme2 = "http://"; //Accept

		@Scheme String notS1 = "https:\\"; //error

		@Host String host1 = "www.google.com"; //Accept
		@Host String host2 = "oxy.edu";  //Accept
		
		@Host String nothost1 = "abc?"; //Error
		@Host String nothost2 = "www.google"; //Error
		@Host String nothost3 = "www.google.com.yahoo.com"; //Error
		@Host String nothost4 = "";  //Error

		@Path String path1 =  "/file/path/item"; //Accept
		@Path String path2 =  ""; //Accept
		
		@Path String notpath1 = " "; //Error
		@Path String notpath2 = "\\path\\no "; //Error
		@Path String notpath3 = "path/no "; //Error
		
		@Query String query1 = "?q=val";
		@Query String query2 = "?q=val&q=val";
		@Query String query3 = "";
		
		@Query String notQ1 = "?q"; //error
		@Query String notQ2 = "?&q=v"; //error
		@Query String notQ3 = "q=v&q=v"; //error
		
		@Fragment String frag1 = "#frag";
		@Fragment String frag2 = "";
		
		@Fragment String notF1 = "?q=v"; //error
		@Fragment String notF2 = "q/q"; //error
		
	}
	
	public static void testSubType(){
		@Scheme String scheme = "https://";
		@Host String host = "www.google.com"; 
		@Path String path =  "/file/path/item"; 
		@Query String query = "?q=val";
		@Fragment String frag = "#frag";
		
		@U String u1 = host; //OK
		
		@U String notU1 = scheme; //error
		@U String notU2 = path;//error
		@U String notU3 = query;//error
		@U String notU4 = frag;//error
		
		@U String url = "https://google.com/search";
		@Scheme String err1 =  url; //error
		@Host String err2 = url; //error
		@Path String err3 = url; //error
		@Query String err4 = url; //error
		@Fragment String err5 = url; //error
	}
	

	
}
