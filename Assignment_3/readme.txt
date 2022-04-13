
1)Open the terminal from the Server folder. For each client, open a new terminal from the Client folder. 
The specific commands are detailed below. 

1.1)
Running the Reactor server:
mvn clean
mvn compile
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="<port> <Num of threads>" 
            
(for example: mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 5")

Running the Thread per client server:
mvn clean
mvn compile
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="<port>"

(for example: mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777")

Running the client:
make 
bin/BGSclient <host> <port> 
(for example: bin/BGSclient 127.0.0.1 7777)

1.2) At the end of each command initiation, please don't add any space char.
	1. REGISTER Dana 123 04-10-1996
	(the birthday needs to be in the format: DD-MM-YYYY)
	2. LOGIN Dana 123 1
	(must write captcha)
	3. LOGOUT
	4.FOLLOW 0 Dana
	5.POST Hi @Dana how are you? 
	(after each "@username" a space must appear ,unless this is the end of the sentence)
	6.PM Dana Hi Dana how are you? 
	7.LOGSTAT
	8. STAT Dana Ben Gal
	9.BLOCK Dana
	

2) We stored the filtered array of words in a private field named "Filter" in DATA class.
(bgu.spl.net.api.bidi.DATA)
