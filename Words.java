package structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Words {

    private static class ValueComparator implements Comparator<Map.Entry<String,Integer>>
    {
        public int compare(Map.Entry<String,Integer> m,Map.Entry<String,Integer> n)
        {
            return n.getValue()-m.getValue();
        }
    }
    String tag;
    public String getname(){
        return tag ;
    }
    public void setname(String s){
        this.tag=s;
    }
    int m_tags;//this tag's all times
    public int getTags(){
        return m_tags;
    }
    public void setTags(int value){
        this.m_tags = value;
    }
    HashMap<String,Integer> wordc;//the map for the word
    public HashMap<String,Integer> getwordc(){
        return wordc;
    }
    public void setwordc(String word,int value){
        wordc.put(word,value);
    }
    HashMap<String,Integer> tagc;//the map for the tag after it
    public HashMap<String,Integer> gettagc(){
        return tagc;
    }
    public void settagc(String word,int value){
        tagc.put(word,value);
    }





    public void sorttagc() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>();
        list.addAll(tagc.entrySet());
        Words.ValueComparator vc = new ValueComparator();
        Collections.sort(list, vc);
        int i = 0;
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            i++;
            System.out.println(it.next());
            if (i == 10)
                break;
        }
    }

    public void sortwordc() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>();
        list.addAll(wordc.entrySet());
        Words.ValueComparator vc = new ValueComparator();
        Collections.sort(list, vc);
        int i = 0;
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            i++;
            System.out.println(it.next());
            if (i == 10)
                break;
        }
    }

    public Words(String k){
        tag=k;
        m_tags=0;
        wordc=new HashMap<String,Integer>();
        tagc=new HashMap<String,Integer>();
    }
}