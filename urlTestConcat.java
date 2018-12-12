import qual.*;

public class urlTestConcat {
    public static void main(String[] args) {
	//suppose to get 16 errors
    }
	
	public static void testConcat(){
		@U String concatU1 = "google.com" + "/search"; //OK
		@U String concatU2 = "https://"+"google.com";  //OK

		
		@U String concatNotU1 = "google"+"/search"; //error
		@U String concatNotU2 = "#gre"+"/search"; //error
		@U String concatNotU3 = "google"+"http://"; //error
		@U String concatNotU4 = "/path"+"?q=v"; //error

		
		
		@Host String concatH1 = "google"+".com";
		@Host String concatH2 = "google."+"com";
		@Host String concatH3 = "images."+"google.com";
		@Host String concatH4 = "images"+".google.com";
		
		
		@Host String concatNotH1 = "images." + "google"; //error
		@Host String concatNotH2 = "google.com" + "yahoo.com"; //error
		@Host String concatNotH3 = "google.com" + "google"; //error
		@Host String concatNotH4 = "images" + ".google"; //error
		
	}
	
	public static void testConcat2(){
		String scheme = "https://";
		String host = "www.google.com"; 
		String path =  "/file/path/item"; 
		String query = "?q=val";
		String frag = "#frag";
		
		String temp1 = query + frag;
		String temp2 = path + query;
		@U String concatU3 = scheme + host;//OK
		@U String concatU4 = host + path;//OK
		
		@U String concatU5 = host + temp1;//OK
		@U String concatU6 = host + temp2;//OK
		@U String concatU7 = host + path + query;//OK
		
		
		String temp3 = frag + query;
		String temp4 = query+ path;
		
		@U String concatNotU5 = scheme + path; //error
		@U String concatNotU6 = path + host; //error
		@U String concatNotU7 = path + query; //error
		@U String concatNotU8 = query + frag; //error
		@U String concatNotU9 = frag + path; //error
		@U String concatNotU10 = frag + query; //error
		@U String concatNotU11 = host + temp3; //error
		@U String concatNotU12 = host + temp4; //error
		
	}
	
}
