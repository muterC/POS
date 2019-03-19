/**
 * 
 */
package analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.porterStemmer;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import structures.LanguageModel;
import structures.Post;
import structures.Token;
import structures.Words;


/**
 * @author hongning
 * Sample codes for demonstrating OpenNLP package usage 
 * NOTE: the code here is only for demonstration purpose, 
 * please revise it accordingly to maximize your implementation's efficiency!
 */
public class DocAnalyzer {
	//N-gram to be created
	int m_N;
	
	//a list of stopwords
	HashSet<String> m_stopwords;
	
	//you can store the loaded reviews in this arraylist for further processing
	ArrayList<Post> m_reviews;
	
	//you might need something like this to store the counting statistics for validating Zipf's and computing IDF
	HashMap<String, Token> m_stats;	
	
	//we have also provided a sample implementation of language model in src.structures.LanguageModel
	Tokenizer m_tokenizer;
	
	//this structure is for language modeling
	LanguageModel m_langModel;

	HashMap<String,Words> cc;

	List<String> TTAAGG = new ArrayList<>();

    int NNS;

    int VBS;

    int JJS;

    int NNPS;

	double parat=0.01;

	double paraw=0.01;

	///double parat;

	//double paraw;

	Integer [] [] M = new Integer [47] [4];

	public void find (){
		/*for (double i = 1;i<10;i++){
			for (double w = 1;w<10;w++){
				parat = i*0.2;
				paraw = w*0.2;
				LoadDirectory3("C:/Users/Admin/Desktop/courses2/text mining/hw2/tagged", ".pos");
				System.out.println("δ"+"="+parat+"  "+"σ"+"="+paraw);
				M22();
			}
		}*/
		parat = 0.001;
		paraw = 0.001;
		LoadDirectory3("C:/Users/Admin/Desktop/courses2/text mining/hw2/tagged", ".pos");
		System.out.println("δ"+"="+parat+"  "+"σ"+"="+paraw);
		M22();
	}

	public void num(){
		for ( int i = 0;i <47;i++){
			for ( int w = 0; w <4; w++){
				M[i][w]=0;
			}
		}
	}






	
	public DocAnalyzer(String tokenModel, int N) throws InvalidFormatException, FileNotFoundException, IOException {
		m_N = N;
		m_reviews = new ArrayList<Post>();
		m_tokenizer = new TokenizerME(new TokenizerModel(new FileInputStream(tokenModel)));
	}
	
	//sample code for loading a list of stopwords from file
	//you can manually modify the stopword file to include your newly selected words
	public void LoadStopwords(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			String line;

			while ((line = reader.readLine()) != null) {
				//it is very important that you perform the same processing operation to the loaded stopwords
				//otherwise it won't be matched in the text content
				line = SnowballStemming(Normalization(line));
				if (!line.isEmpty())
					m_stopwords.add(line);
			}
			reader.close();
			System.out.format("Loading %d stopwords from %s\n", m_stopwords.size(), filename);
		} catch(IOException e){
			System.err.format("[Error]Failed to open file %s!!", filename);
		}
	}
	
	public void analyzeDocument(JSONObject json) {		
		try {
			JSONArray jarray = json.getJSONArray("Reviews");
			for(int i=0; i<jarray.length(); i++) {
				Post review = new Post(jarray.getJSONObject(i));
				
				String[] tokens = Tokenize(review.getContent());
				review.setTokens(tokens);
				
				/**
				 * HINT: perform necessary text processing here based on the tokenization results
				 * e.g., tokens -> normalization -> stemming -> N-gram -> stopword removal -> to vector
				 * The Post class has defined a "HashMap<String, Token> m_vector" field to hold the vector representation 
				 * For efficiency purpose, you can accumulate a term's DF here as well
				 */
				
				
				m_reviews.add(review);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void createLanguageModel() {
		m_langModel = new LanguageModel(m_N, m_stats.size());
		
		for(Post review:m_reviews) {
			String[] tokens = Tokenize(review.getContent());
			/**
			 * HINT: essentially you will perform very similar operations as what you have done in analyzeDocument() 
			 * Now you should properly update the counts in LanguageModel structure such that we can perform maximum likelihood estimation on it
			 */
		}
	}
	
	//sample code for loading a json file
	public JSONObject LoadJson(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			StringBuffer buffer = new StringBuffer(1024);
			String line;
			
			while((line=reader.readLine())!=null) {
				buffer.append(line);
			}
			reader.close();
			
			return new JSONObject(buffer.toString());
		} catch (IOException e) {
			System.err.format("[Error]Failed to open file %s!", filename);
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			System.err.format("[Error]Failed to parse json file %s!", filename);
			e.printStackTrace();
			return null;
		}
	}
	
	// sample code for demonstrating how to recursively load files in a directory 
	public void LoadDirectory(String folder, String suffix) {
		File dir = new File(folder);
		int size = m_reviews.size();
		for (File f : dir.listFiles()) {
			if (f.isFile() && f.getName().endsWith(suffix)) {
				//Wordsequence("NN", f.getAbsolutePath());
				//Tagsequence("NN", f.getAbsolutePath());
				ALL(f.getAbsolutePath());
				//ALL2(f.getAbsolutePath());

			}
			else if (f.isDirectory())
				LoadDirectory(f.getAbsolutePath(), suffix);
		}
		size = m_reviews.size() - size;
		System.out.println("Loading " + size + " review documents from " + folder);
	}

	public void LoadDirectory2(String folder, String suffix) {
		File dir = new File(folder);
		int size = m_reviews.size();
		for (File f : dir.listFiles()) {
			if (f.isFile() && f.getName().endsWith(suffix)) {
				//Wordsequence("NN", f.getAbsolutePath());
				//Tagsequence("NN", f.getAbsolutePath());
				//ALL(f.getAbsolutePath());
				ALL2(f.getAbsolutePath());

			}
			else if (f.isDirectory())
				LoadDirectory(f.getAbsolutePath(), suffix);
		}
		size = m_reviews.size() - size;
		System.out.println("Loading " + size + " review documents from " + folder);
	}

	public void LoadDirectory3(String folder, String suffix) {
		File dir = new File(folder);
		int size = m_reviews.size();
		List<File> train= new ArrayList<File>();
		List<File> TRAIN=new ArrayList<>();

		for ( File m : dir.listFiles()){
			TRAIN.add(m);
		}//this is the constant all the files

		for ( File m : dir.listFiles()){
			train.add(m);
		} // this is for the iterations
		Random r = new Random();

		int index = 0;

		List<File> test1= new ArrayList<File>();
		List<File> test2= new ArrayList<File>();
		List<File> test3= new ArrayList<File>();
		List<File> test4= new ArrayList<File>();
		List<File> test5= new ArrayList<File>();
		List<File> train1= new ArrayList<File>();
		List<File> train2= new ArrayList<File>();
		List<File> train3= new ArrayList<File>();
		List<File> train4= new ArrayList<File>();
		List<File> train5= new ArrayList<File>();

		for (int i = 0; i<40; i++){
			index = r.nextInt(TRAIN.size() - i);
			test1.add(train.get(index));
			train.remove(index);
		}
		train1.addAll(train);

		for (int i = 40; i<79; i++){
			index = r.nextInt(TRAIN.size() - i);
			test2.add(train.get(index));
			train.remove(index);
		}
		train2.addAll(train);
		train2.addAll(test1);

		for (int i = 80; i<119; i++){
			index = r.nextInt(TRAIN.size() - i);
			test3.add(train.get(index));
			train.remove(index);
		}
		train3.addAll(train);
		train3.addAll(test1);
		train3.addAll(test2);


		for (int i = 120; i<159; i++){
			//System.out.println(TRAIN.size()-i);
			index = r.nextInt(TRAIN.size() - i);
			test4.add(train.get(index));
			train.remove(index);
		}
		train4.addAll(train);
		train4.addAll(test1);
		train4.addAll(test2);
		train4.addAll(test3);

		//System.out.println(train.size());
		//System.out.println(train1.size());
		//System.out.println(train2.size());
		//System.out.println(train3.size());

		for (int i = 160; i<198; i++){
			//System.out.println(TRAIN.size()-i);
			index = r.nextInt(TRAIN.size() - i);
			test5.add(train.get(index));
			train.remove(index);
		}
		train5.addAll(test1);
		train5.addAll(test2);
		train5.addAll(test3);
		train5.addAll(test4);


        HashMap<List<File>,List<File>> cross = new HashMap<List<File>,List<File>>();
		cross.put(train1,test1);
		cross.put(train2,test2);
		cross.put(train3,test3);
		cross.put(train4,test4);
		cross.put(train5,test5);

		for ( HashMap.Entry<List<File>, List<File>> entry : cross.entrySet()) {
			for (File f : entry.getKey()) {
				if (f.isFile() && f.getName().endsWith(suffix)) {
					ALL2(f.getAbsolutePath());

				} else if (f.isDirectory())
					LoadDirectory(f.getAbsolutePath(), suffix);
			}

			for (File f : entry.getValue()) {
				if (f.isFile() && f.getName().endsWith(suffix)) {
					CV(f.getAbsolutePath());
				} else if (f.isDirectory())
					LoadDirectory(f.getAbsolutePath(), suffix);
			}
		}
		/*for (File f : train1) {
			if (f.isFile() && f.getName().endsWith(suffix)) {
				ALL2(f.getAbsolutePath());

			} else if (f.isDirectory())
				LoadDirectory(f.getAbsolutePath(), suffix);
		}

		for (File f : test1) {
			if (f.isFile() && f.getName().endsWith(suffix)) {
				CV(f.getAbsolutePath());
			} else if (f.isDirectory())
				LoadDirectory(f.getAbsolutePath(), suffix);
		}*/


	}

	public void M22(){
		int tp = 0;
		int tn = 0;
		int fp = 0;
		int fn = 0;
		for (int i =0;i<47;i++){
			tp = tp + M [i][0];
			tn = tn + M [i][1];
			fp = fp + M [i][2];
			fn = fn + M [i][3];
		}
		//System.out.println(tp);
		//System.out.println(tn);
		//System.out.println(fp);
		//System.out.println(fn);
		System.out.println("accuracy"+(double)(tp+tn)/(double)(tp+tn+fp+fn));
		//System.out.println("precision"+(double)tp/(double)(tp+fp));
		//System.out.println("recall"+(double)tp/(double)(tp+fn));
		System.out.println("accuracy");
		System.out.println("NN"+(double)(M[NNS][0]+M[NNS][1])/(double)(M[NNS][0]+M[NNS][1]+M[NNS][2]+M[NNS][3]));
		System.out.println("VB"+(double)(M[VBS][0]+M[VBS][1])/(double)(M[VBS][0]+M[VBS][1]+M[VBS][2]+M[VBS][3]));
		System.out.println("JJ"+(double)(M[JJS][0]+M[JJS][1])/(double)(M[JJS][0]+M[JJS][1]+M[JJS][2]+M[JJS][3]));
		System.out.println("NNP"+(double)(M[NNPS][0]+M[NNPS][1])/(double)(M[NNPS][0]+M[NNPS][1]+M[NNPS][2]+M[NNPS][3]));
		System.out.println("precision");
		System.out.println("NN"+(double)(M[NNS][0])/(double)(M[NNS][0]+M[NNS][2]));
		System.out.println("VB"+(double)(M[VBS][0])/(double)(M[VBS][0]+M[VBS][2]));
		System.out.println("JJ"+(double)(M[JJS][0])/(double)(M[JJS][0]+M[JJS][2]));
		System.out.println("NNP"+(double)(M[NNPS][0])/(double)(M[NNPS][0]+M[NNPS][2]));
		System.out.println("recall");
		System.out.println("NN"+(double)(M[NNS][0])/(double)(M[NNS][0]+M[NNS][3]));
		System.out.println("VB"+(double)(M[VBS][0])/(double)(M[VBS][0]+M[VBS][3]));
		System.out.println("JJ"+(double)(M[JJS][0])/(double)(M[JJS][0]+M[JJS][3]));
		System.out.println("NNP"+(double)(M[NNPS][0])/(double)(M[NNPS][0]+M[NNPS][3]));
	}

	//sample code for demonstrating how to use Snowball stemmer
	public String SnowballStemming(String token) {
		SnowballStemmer stemmer = new englishStemmer();
		stemmer.setCurrent(token);
		if (stemmer.stem())
			return stemmer.getCurrent();
		else
			return token;
	}
	
	//sample code for demonstrating how to use Porter stemmer
	public String PorterStemming(String token) {
		porterStemmer stemmer = new porterStemmer();
		stemmer.setCurrent(token);
		if (stemmer.stem())
			return stemmer.getCurrent();
		else
			return token;
	}
	
	//sample code for demonstrating how to perform text normalization
	//you should implement your own normalization procedure here
	public String Normalization(String token) {
		// remove all non-word characters
		// please change this to removing all English punctuation
		token = token.replaceAll("\\W+", ""); 
		
		// convert to lower case
		token = token.toLowerCase(); 
		
		// add a line to recognize integers and doubles via regular expression
		// and convert the recognized integers and doubles to a special symbol "NUM"
		
		return token;
	}
	
	String[] Tokenize(String text) {
		return m_tokenizer.tokenize(text);
	}
	
	public void TokenizerDemon(String text) {
		System.out.format("Token\tNormalization\tSnonball Stemmer\tPorter Stemmer\n");
		for(String token:m_tokenizer.tokenize(text)){
			System.out.format("%s\t%s\t%s\t%s\n", token, Normalization(token), SnowballStemming(token), PorterStemming(token));
		}		
	}





	public String[] reads(String f) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			StringBuffer buffer = new StringBuffer(1024);
			String line;

			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				//buffer.append("");
			}
			reader.close();
			String result = buffer.toString();

			result = result.replaceAll("\\=+", "");
			result = result.replaceAll("\\[", "");
			result = result.replaceAll("\\]", "");

			String[] sen = result.split("\\.\\/\\.");
			return sen;
		} catch (IOException e) {
			System.err.format("[Error]Failed to open file %s!", f);
			e.printStackTrace();
			return null;
		}
	}

	public void ALL(String h){
		String[] sentence = reads(h);
		for(int s =0; s<sentence.length; s++) {
			String sent = sentence[s];
			sent = "start/START "+ sent;
			String[] splits = sent.split("\\s+");
			for (int e = 0; e < splits.length - 1; e++) {
				String[] dd = splits[e].split("\\/");
				if (dd.length == 2) {
					if (!TTAAGG.contains(dd[1])){
						TTAAGG.add(dd[1]);
					}
				}
				if (dd.length > 2){
					if (!TTAAGG.contains(dd[dd.length-1])){
						TTAAGG.add(dd[dd.length-1]);
					}
				}
			}

		}
		for ( int i = 0; i<TTAAGG.size();i++){
			cc.put(TTAAGG.get(i),new Words(TTAAGG.get(i)));}
	}

	public void sss(){
		System.out.println(TTAAGG);
		System.out.println(TTAAGG.size());
	}

	public void ALL2(String h){
		for ( int i = 0; i<TTAAGG.size();i++){
			//cc.put(TTAAGG.get(i),new Words(TTAAGG.get(i)));
			Wordsequence(TTAAGG.get(i),h);
			Tagsequence(TTAAGG.get(i),h);
		}
	}


    public void Wordsequence(String k, String h){
		String[] sentence = reads(h);
		Words tag= new Words(k);
		for(int s =0; s<sentence.length; s++) {
			String sent = sentence[s];
			sent = "start/START "+ sent;
			String[] splits = sent.split("\\s+");
			for (int e = 0; e < splits.length; e++) {
				String[] dd = splits[e].split("\\/");
				if (dd.length >= 2) {
					if (dd[1].equals(k)) {
						int tagn=tag.getTags();
						tag.setTags(tagn+1);
						String d = dd[0];
						if (!tag.getwordc().containsKey(d)) {
							tag.setwordc(d,1);
						} else {
							int q = tag.getwordc().get(d);
							tag.setwordc(d,q+1);
						}

					}
				}
			}
		}
		int n = cc.get(k).getTags();
		cc.get(k).setTags(n+tag.getTags());

		for(Map.Entry<String,Integer> m : tag.getwordc().entrySet()){
			if (!cc.get(k).getwordc().containsKey(m.getKey())){
				cc.get(k).setwordc(m.getKey(),m.getValue());
			}
			else{
				int s = cc.get(k).getwordc().get(m.getKey());
				cc.get(k).setwordc(m.getKey(),s+m.getValue());
			}
		}
	}




	public void Tagsequence(String w,String h){
		String[] sentence = reads(h);
		Words tag= new Words(w);
		for(int s =0; s<sentence.length; s++) {
			String sent=sentence[s];
			sent = "start/START "+ sent;
		    String [] splits = sent.split("\\s+");
		    for (int e=0;e<splits.length-1;e++) {
				String[] dd = splits[e].split("\\/");
				String[] ss = splits[e + 1].split("\\/");
				if (dd.length >= 2) {
					if (ss.length >= 2) {
						if (dd[1].equals(w)) {
							String d = ss[1];
							if (!tag.gettagc().containsKey(d)) {
								tag.settagc(d, 1);
							} else {
								int q = tag.gettagc().get(d);
								tag.settagc(d, q + 1);
							}

						}
					}
				}
			}
		}
		for(Map.Entry<String,Integer> m : tag.gettagc().entrySet()){
			if (!cc.get(w).gettagc().containsKey(m.getKey())){
				cc.get(w).settagc(m.getKey(),m.getValue());
			}
			else{
				int s = cc.get(w).gettagc().get(m.getKey());
				cc.get(w).settagc(m.getKey(),s+m.getValue());
			}
		}
	}

	public void ww(String k){
		cc.get(k).sortwordc();
	}

	public void tt(String k){
		cc.get(k).sorttagc();
	}

	public double MLEtt(String a, String b ){
		if (!cc.get(a).gettagc().containsKey(b)) {
			return parat / 47;
		}
		int tt = cc.get(a).gettagc().get(b);
		int sum = cc.get(a).getTags();
		//System.out.println((tt+parat)/(sum+47));
		return(tt+parat)/(sum+47);

	}

	public double MLEwt(String a, String b){
		if (!cc.get(a).getwordc().containsKey(b)){
			return paraw/47;
		}
		int wt = cc.get(a).getwordc().get(b);
		int sum = cc.get(a).getTags();
		//System.out.println((wt+paraw)/(sum+47));
		return (wt+paraw)/(sum+47);

	}

	public  String [] Viterbi(String [] ss){
		List<String> tag = new ArrayList<>(cc.keySet());
		int row = cc.size();
		int column = ss.length;
		double [] []TRAN = new double [row] [column];
		int [] [] NUM = new int [row] [column];

		String [] tt = new String [ss.length];
		for (int s =0; s<ss.length; s++){
			String sent = ss[s];
			if (s == 0) {
				for (int i = 0; i < row; i++) {
					double trans = MLEwt(tag.get(i), ss[0]) * MLEtt(tag.get(i), "START");
					TRAN[i][0] = trans;
					NUM[i][0] = i;
				}
			}
			if (s>0){
				for (int i = 0; i < row; i++){// this is the column now
					double max = 0;
					for (int k = 0; k < row; k++) {//this is the column before
						double trans = TRAN[k][s - 1] * MLEtt(tag.get(i),tag.get(k));
						if (max < trans){
							max = trans;
							TRAN[i][s]= max*MLEwt(tag.get(i),ss[s]);
							NUM[i][s]=k;
						}
					}
				}
			}
			}
		double maxx = 0;
		int last = 0;
		for ( int i = 0; i<row;i++){
			if (maxx<TRAN[i][column-1]){
				maxx=TRAN[i][column-1];
				last = i;
			}
		}

		String [] TTT = new String [column];
		TTT[column-1]= tag.get(last);
		/*int dd = NUM[last][column-1];
		for (int i = column-2; i>=0;i--){
			if (i== column - 2){
				TTT[i] = tag.get(NUM[last][i + 1]);
			}
			else {
				TTT[i] = tag.get(NUM[NUM[dd][i+2]][i + 1]);
				dd = NUM[NUM[dd][i+2]][i + 1];
			}
		}*/
		for(int i = column-1; i>=0; i--) {
			TTT[i] = tag.get(last);
			last = NUM[last][i];
		}
		/*for (int i=0;i<column;i++){
			System.out.print(TTT[i]+" ");
		}*/
        return TTT;
	}

	public void CV (String h){
		List<String> tag = new ArrayList<>(cc.keySet());
		String[] sentence = reads(h);
		for (int i = 0;i<tag.size();i++){
			if(tag.get(i).equals("NN")){
				NNS = i;
			}
			if(tag.get(i).equals("VB")){
				VBS = i;
			}
			if(tag.get(i).equals("JJ")){
				JJS = i;
			}
			if(tag.get(i).equals("NNP")){
				NNPS = i;
			}

		}
		for(int s =0; s<sentence.length; s++) {
			String sent = sentence[s];
			sent = "start/START "+ sent;
			String[] splits = sent.split("\\s+");
			//String [] TTT = new String [splits.length];
			String [] WWW = new String [splits.length];
			for (int e = 0; e < splits.length; e++) {
				String[] dd = splits[e].split("\\/");
				if (dd.length >= 2) {
					WWW[e] = dd[0];
				}
			}
			String [] TTT = Viterbi(WWW);
			/*for (int i = 0;i<splits.length;i++){
				System.out.println(TTT[i]);
			}*/

			for (int e = 0; e < splits.length; e++) {
				String[] dd = splits[e].split("\\/");

				if (dd.length >= 2) {
					for (int cc = 0; cc<tag.size();cc++){
						/*if (dd[dd.length-1].equals(tag.get(cc))){
							System.out.print("success");
						}*/

					  if (dd[dd.length-1].equals(tag.get(cc)) && TTT[e].equals(tag.get(cc))){
					  	int t1 = M[cc] [0];
					  	M[cc] [0] = t1+1;
					  }
					  if (dd[dd.length-1]!=tag.get(cc) && TTT[e]!=tag.get(cc)){
					  	int t2 = M [cc] [1];
					  	M[cc][1]=t2 +1;
					  }
					  if (dd[dd.length-1].equals(tag.get(cc)) && TTT[e]!=tag.get(cc)){
					  	int t3 = M [cc] [2];
					  	M[cc][2]=t3 +1;
					  }
					  if (dd[dd.length-1]!=tag.get(cc) && TTT[e].equals(tag.get(cc))){
					  	int t4 = M [cc] [3];
					  	M[cc][3]=t4 +1 ;
					  }
					}
				}
			}
		}
	}







	public String samplingtt(String h) {
		double prob = 0.5 * Math.random();
		for(String token:cc.get(h).gettagc().keySet()) {
			prob -= MLEtt(h,token);
			if (prob<=0)
				return token;
		}
		return "no"; //How to deal with this special case?
	}

	public String samplingwt(String h) {
		double prob = 0.5 * Math.random();
		for(String token:cc.get(h).getwordc().keySet()) {
			prob -= MLEwt(h,token);
			if (prob<=0)
				return token;
		}
		return "no"; //How to deal with this special case?
	}



	public void sample(){
		List<String> tagg = new ArrayList<>(cc.keySet());
		for (int i = 0;i<100;i++){
			double sum = 0;
			String [] word = new String [10];
			String [] tag = new String [10];
 			for (int k = 0; k<10;k++) {
				if (k == 0) {
					tag[0] = samplingtt("START");
					word[0] = samplingwt(tag[0]);
					sum = sum + Math.log(MLEtt("START", tag[0]));
					sum = sum + Math.log(MLEwt(tag[0], word[0]));
				}
				if (k > 0) {
					tag[k] = samplingtt(tag[k - 1]);
					word[k] = samplingwt(tag[k]);
					sum = sum + Math.log(MLEtt(tag[k - 1], tag[k]));
					sum = sum + Math.log(MLEwt(tag[k], word[k]));
				}
			}
			System.out.print("Tag"+":"+" ");
            for (int s = 0; s<10;s++){
            	System .out.print(tag[s]+" ");
			}
            System.out.println();
            System.out.print("Word"+":"+" ");
            for (int w = 0; w<10;w++){
            	System .out.print(word[w]+" ");
            }
            System.out.println();
            //System.out.print("log-likelihood"+":");
            //System.out.print(Math.exp(sum));
            //System.out.println();
			int ttpp = 0;
			int ttnn = 0;
			String [] TTT = Viterbi(word);
			for ( int l = 0; l <10; l++){

				for (int cc = 0; cc<tagg.size();cc++) {
					if (tag[l].equals(tagg.get(cc)) && TTT[l].equals(tagg.get(cc))) {
						int t1 = M[cc][0];
						M[cc][0] = t1 + 1;
						ttpp = ttpp+1;
					}
					if (tag[l] != tagg.get(cc) && TTT[l] != tagg.get(cc)) {
						int t2 = M[cc][1];
						M[cc][1] = t2 + 1;
						ttnn=ttnn+1;
					}
					if (tag[l].equals(tagg.get(cc)) && TTT[l] != tagg.get(cc)) {
						int t3 = M[cc][2];
						M[cc][2] = t3 + 1;
					}
					if (tag[l]!= tagg.get(cc) && TTT[l].equals(tagg.get(cc))) {
						int t4 = M[cc][3];
						M[cc][3] = t4 + 1;
					}
				}
			}
			System.out.print("accuracy for the centence:"+" "+(double)(ttnn+ttpp)/470.0);
			System.out.println();
			}

	}





		public static void main(String[] args) throws InvalidFormatException, FileNotFoundException, IOException {
		DocAnalyzer analyzer = new DocAnalyzer("./data/Model/en-token.bin", 2);
		
		//code for demonstrating tokenization and stemming
		//analyzer.TokenizerDemon("I've practiced for 30 years in pediatrics, and I've never seen anything quite like this.");
		analyzer.cc= new HashMap<String, Words>();
		analyzer.num();
		//analyzer.cc.put("NN",new Words("NN"));
		//entry point to deal with a collection of documents
		analyzer.LoadDirectory("C:/Users/Admin/Desktop/courses2/text mining/hw2/tagged", ".pos");
		analyzer.LoadDirectory2("C:/Users/Admin/Desktop/courses2/text mining/hw2/tagged", ".pos");
		analyzer.sample();
		//analyzer.tt("NN");
		//analyzer.ww("MD");
		//analyzer.MLEtt("NN","IN");
		//analyzer.MLEwt("NN","can");
		//analyzer.sss();
		//String  [] z = {"Vinken","I","love","director"};
		//analyzer.Viterbi(z);
		//analyzer.Wordsequence("NN",);
		//analyzer.Tagsequence("NN");
		//analyzer.Tagsequence("VB")
		//analyzer.M22();
		//analyzer.find();

	}

}
