
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.IOException;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author user
 */

public class Table {

    /**
     * @param args the command line arguments
     */
    
    
    public static int timestamp=0;
    
    //ini database nya
    private Map<Integer,List<Map.Entry<Integer,String>>> table;
    private String Nama;
  
    //contructor
    public Table(String Nama){
        
    this.Nama = Nama;    
    table = new HashMap<Integer,List<Map.Entry<Integer,String>>>();    
    System.out.println("Table Created !\n Table Name : "+this.Nama);
    
    
    
    }
    
 
    //getter setter
    public String getNama() {
        return Nama;
    }

    public Map<Integer, List<Map.Entry<Integer, String>>> getTable() {
        return table;
    }

    public void setTable(Map<Integer, List<Map.Entry<Integer, String>>> table) {
        this.table = table;
    }

    public void setNama(String Nama) {
        this.Nama = Nama;
    }
    
    //fungsi
    public String create(Integer Key,String Value){
        //cek apakah data sudah ada dengan key berikut
       String result="";
        if(table.isEmpty()||!table.containsKey(Key)){
            //buat pair baru untuk jenis data ini
            Map.Entry<Integer,String> newData = new AbstractMap.SimpleEntry<>(timestamp,Value);
            timestamp++;
            //masukin data ke list perubahan data di table Timesatmp
            List<Map.Entry<Integer,String>> TimeStampTable = new ArrayList<>();
            boolean add = TimeStampTable.add(newData);
            //masukin Timestamp table kedalam Database
            if(add){
                table.put(Key, TimeStampTable);
                result="Data Sudah di Entry";
            }else{
                result="Gagal Melakukan Entry Data";
                timestamp--;
            }
        }else{ //data ada di table , table harus diupdate
            result = update(Key, Value);
        }
    return result;
    }
    
    public void read(Integer Key){
        //cek apakah data sudah ada di database
        if(table.containsKey(Key)){
            //daya ada di database
            
            //ambil data yang ada di timestamptable dengan timestamp paling tinggi saat itu
             Map.Entry<Integer,String> dataToRead = table.get(Key).get(table.get(Key).size()-1);
             System.out.println("Data : "+dataToRead.getValue()+"\n Timestamp : "+dataToRead.getKey());
        }else{
            System.out.println("Data dengan Key ini tidak terdapat di database");
        }
    
    
    }
    
    public String update(Integer Key,String Value){
        String result="";
        //cek apakah data ada di database
        if(table.containsKey(Key)){
            //daya ada di database
            
            //ambil data yang ada di timestamptable dengan timestamp paling tinggi saat itu
             Map.Entry<Integer,String> newData = new AbstractMap.SimpleEntry<>(timestamp,Value);
             timestamp++;
            //masukin data ke database
            boolean add = table.get(Key).add(newData);
            if(add){
                result="Update berhasil ! \n Data : "+Value+"\n Timestamp : "+newData.getKey();
            }else{
                 result="Gagal Melakukan Update Data";
                timestamp--; 
            }
        }else{
            result="Data dengan Key ini tidak terdapat di database";
        }
    return result;
    }
   public Table splitTable(int tokenMin , int tokenMax){
       Table T = new Table(Nama);
       int length = this.table.size()/2;
      // Iterator it = table.entrySet().iterator();
       int i = 0;
      // for(int j=0;j<length;j++)
      //      it.next();
       Object[] Listkey =  table.keySet().toArray();
      for(i=0;i<table.size();i++){
           //pindahin datanya
          System.out.println("test "+i);
           // Map.Entry pairs = (Map.Entry)it.next();
           // List<Map.Entry<Integer,String>> newL=(List<Map.Entry<Integer,String>>) pairs.getValue();
            if((Integer) Listkey[i] > tokenMin && (Integer) Listkey[i] <= tokenMax){
                T.table.put( (Integer) Listkey[i], table.get(Listkey[i]));
                delete((Integer)Listkey[i]);
            }
            //it.remove();
           
       }
       T.List();
   return T;
   }     
    public void delete(int Key){
     //cek apakah data ada di database
        if(table.containsKey(Key)){
            List<Map.Entry<Integer, String>> removedData = table.remove(Key);
            if(removedData != null){ //data berhasil dibuang
            
            System.out.println("Data : "+removedData.get(removedData.size()-1).getValue()+"\n Timestamp : "+removedData.get(removedData.size()-1).getKey()+"\n telah berhasil di hapus !");
            }else{
            
            System.out.println("Gagal menghapus data");
            
            }
            
        }else{
             System.out.println("Data dengan Key ini tidak terdapat di database");
        }
    
    
    
    }
    
    public String List(){ //menampilkan seluruh isi table database
        String result="";
        Object[] Listkey =  table.keySet().toArray();
         Map.Entry<Integer,String> dataToShow;
        for(int i =0;i< table.size();i++){
            dataToShow = table.get(Listkey[i]).get(table.get(Listkey[i]).size()-1);
           result+="Key : "+Listkey[i]+"\n Value : "+dataToShow.getValue()+"\n Timestamp : "+dataToShow.getKey()+"\n";
        }
    
    
    
    return result;
    }
    
   public int getSize(){
       return this.table.size();
   
   
   }
    /*
    public static void main(String[] args) {
        // TODO code application logic here
    Table DB = new Table("Test");
    Scanner scan = new Scanner(System.in);
    for(;;){
        System.out.println("Welcome ! \n");

        System.out.println("1.Tambah Data");
        System.out.println("2.Read Data");
        System.out.println("3.Update Data");
        System.out.println("4.Delete Data");
        System.out.println("5.List Data");
         System.out.println("6.Split Data");

        System.out.println("Silahkan Masukan Pilihan Anda ! \n");
        int pilihan =  scan.nextInt();
        if(pilihan == 1){
            System.out.println("Masukan Key dari data yang akan disimpan :");
            int Key =   scan.nextInt();
            System.out.println("Masukan Value dari data yang akan disimpan :");
            String Value =  scan.next();
            DB.create(Key, Value);
        }else if(pilihan == 2){
           System.out.println("Masukan Key dari data yang akan disimpan :");
            int Key =   scan.nextInt();
            DB.read(Key);
        }else if(pilihan == 3){
            System.out.println("Masukan Key dari data yang akan Dirubah :");
            int Key =   scan.nextInt();
            System.out.println("Masukan Value dari data yang akan Dirubah :");
            String Value =  scan.next();
            DB.update(Key, Value);
        
        }else if(pilihan == 4){
            System.out.println("Masukan Key dari data yang akan di Delete :");
            int Key =   scan.nextInt();
            DB.delete(Key);
        }else if(pilihan == 5){
            System.out.println("Isi dari Database :");
            System.out.println(DB.List());
            
            DB.setDataFromJson(DB.JSonIt());
        }else if(pilihan ==6 ){
            int tokenMin,tokenMax;
            System.out.println("split Table !");
            System.out.println("Masukan token min !");
            tokenMin=scan.nextInt();
            System.out.println("Masukan token max !");
            tokenMax=scan.nextInt();
            System.out.println("panjang data awal : "+DB.table.size());
            System.out.println("panjang data di Table Baru : "+DB.splitTable(tokenMin,tokenMax).table.size());
            System.out.println("panjang data akhir : "+DB.table.size());
        }

    }
    
    
    }
    
    */
    public String JSonIt(){
        String jsontext="";
        JSONObject obj = new JSONObject();
        //simpen nama table nya
        obj.put("nama", this.Nama);
        //simpen panjang table nya
        //obj.put("tablesize", table.size());
        //jadiin list nya JsonArray
       // JSONArray list = new JSONArray();
       // list.add(table.)
        Object[] Listkey =  table.keySet().toArray();
        
        JSONObject data = new JSONObject(); //buat nyimpen key,value nya
        //value = list<pair(int,string)> :v
        for(int i=0;i<table.size();i++){//iterasi tiap key
            JSONObject entry = new JSONObject();//buat nyimpen list of pair
            for(int j=0;j<table.get(Listkey[i]).size();j++){
                
                //entry.add(j); //masukin pair
                entry.put(table.get(Listkey[i]).get(j).getKey(), table.get(Listkey[i]).get(j).getValue());
            }
            data.put(Listkey[i], entry.toJSONString());
            //list.add(data.toJSONString());
        }
        obj.put("table", data.toJSONString());
        jsontext =obj.toJSONString();
        //jsontext = JSONValue.toJSONString(this.table);
      //  System.out.print(jsontext);
        System.out.println("hasil json "+jsontext);
     return jsontext;
    }
    
    public void setDataFromJson(String Json){
        
        Object obj=JSONValue.parse(Json);
        JSONObject data = (JSONObject) obj;
        String nama = (String) data.get("nama");
        this.setNama(nama);
        this.table.clear();
        Object keyMap = JSONValue.parse((String)data.get("table"));
        JSONObject table = (JSONObject) keyMap;
        Object[] tableKey = table.keySet().toArray();//key dari data di table
       
        //Kontainer baru table
        Map<Integer,List<Map.Entry<Integer,String>>> newTable =  new HashMap<Integer,List<Map.Entry<Integer,String>>>();
        
        for(int i=0;i<table.size();i++){//iterasi table
            //sekarang buka value tiapkey
            //value= List<pair(timestamp,value)>
            //implementasi nya -> JSONObject(timestamp,value);
            Object timestampMap =JSONValue.parse((String)table.get(tableKey[i]));
            JSONObject timestamp = (JSONObject) timestampMap;
            Object[] timestampKey = timestamp.keySet().toArray();//timestamp dari data di table
            System.out.println("key : "+tableKey[i]+" value : "+timestamp.toJSONString());
            
            //buat kontainetList yang nyimpen Map,Entry
            List<Map.Entry<Integer,String>> nList = new ArrayList<>();
            for(int j=0;j<timestamp.size();j++){//iterasi data di tiap time stamp
                //bacapasangan key,value
                System.out.println("timestamp : "+timestampKey[j]+" value : "+timestamp.get(timestampKey[j]));
                //buat pair Timestamp , value
                String times = (String) timestampKey[j];
                Map.Entry<Integer,String> newData = new AbstractMap.SimpleEntry<>(Integer.parseInt(times),(String)timestamp.get(timestampKey[j]));
                nList.add(newData);
            }
            //masukin List  ke dalam Map key,value :v
            newTable.put(Integer.parseInt((String)tableKey[i]), nList);
        }
        
        //set table dengan hasil baca tadi
        this.setTable(newTable);
        
       
    }
}
