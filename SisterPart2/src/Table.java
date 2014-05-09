
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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
    private Map<String,List<Map.Entry<Integer,String>>> table;
    private String Nama;
    //contructor
    public Table(String Nama){
        
    this.Nama = Nama;    
    table = new HashMap<String,List<Map.Entry<Integer,String>>>();    
    System.out.println("Table Created !\n Table Name : "+this.Nama);
    
    
    
    }

    //getter setter
    public String getNama() {
        return Nama;
    }

    public Map<String, List<Map.Entry<Integer, String>>> getTable() {
        return table;
    }

    public void setTable(Map<String, List<Map.Entry<Integer, String>>> table) {
        this.table = table;
    }

    public void setNama(String Nama) {
        this.Nama = Nama;
    }
    
    //fungsi
    public String create(String Key,String Value){
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
    
    public void read(String Key){
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
    
    public String update(String Key,String Value){
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
        
    public void delete(String Key){
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

        System.out.println("Silahkan Masukan Pilihan Anda ! \n");
        int pilihan =  scan.nextInt();
        if(pilihan == 1){
            System.out.println("Masukan Key dari data yang akan disimpan :");
            String Key =   scan.next();
            System.out.println("Masukan Value dari data yang akan disimpan :");
            String Value =  scan.next();
            DB.create(Key, Value);
        }else if(pilihan == 2){
           System.out.println("Masukan Key dari data yang akan disimpan :");
            String Key =   scan.next();
            DB.read(Key);
        }else if(pilihan == 3){
            System.out.println("Masukan Key dari data yang akan Dirubah :");
            String Key =   scan.next();
            System.out.println("Masukan Value dari data yang akan Dirubah :");
            String Value =  scan.next();
            DB.update(Key, Value);
        
        }else if(pilihan == 4){
            System.out.println("Masukan Key dari data yang akan di Delete :");
            String Key =   scan.next();
            DB.delete(Key);
        }else if(pilihan == 5){
            System.out.println("Isi dari Database :");
            DB.List();
        }

    }
    
    
    }
    */
}
