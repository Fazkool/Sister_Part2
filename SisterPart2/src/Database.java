/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author hp
 */
public class Database {
  private List<Table> database = new ArrayList<Table>();
  
  public boolean isContainTable(String namaTable){
     boolean result = false;
      if(database.isEmpty()){//kondisi apabila database kosong
          result = false;
      }else{//kondisi database tidak kosong
          for(int i =0;i<database.size();i++){
              //cek table satupersatu apakah kita punya table dengan nama tersebut
              if(database.get(i).getNama().equals(namaTable)){ //ada table dengan nama tersebut
                  result = true;
                  break;
              }
          }
      }
  return result;
  }
  
  public Table getTable(String namaTable){
  Table T=null;
    for(int i=0;i<database.size();i++){
        if (database.get(i).getNama().equals(namaTable)){
            T = database.get(i);
            break;
        }
    }
  return T;
  }

    public void setDatabase(List<Table> database) {
        this.database = database;
    }

    public List<Table> getDatabase() {
        return database;
    }
  
  public String createTable(String namaTable){
      //check apakah table dengan nama tersebut sudah ada
      String result=null;
      if(isContainTable(namaTable)){ //sudah ada table dengan nama ini
          result = "Sudah ada table dengan nama tersebut di database !";
          
      }else{
          Table newTable = new Table(namaTable);
          database.add(newTable);
          result ="Table Created !/n nama table : "+namaTable;
      }
      return result;
    }
  
  
  public String display(String namaTable){
    //check apakah table dengan nama tersebut sudah ada
      String result=null;
      if(isContainTable(namaTable)){ //sudah ada table dengan nama ini
          //System.out.println("Sudah ada table dengan nama tersebut di database !");
          
          result =getTable(namaTable).List();
      
      }else{
          
          System.out.println("Tidak ada Table dengan nama : "+namaTable);
      
      }
      return result;
  }
  
  
  public void deleteTable(String namaTable){
      if(isContainTable(namaTable)){ //sudah ada table dengan nama ini
      //System.out.println("Sudah ada table dengan nama tersebut di database !");
          
//          database.
      
      }else{
          
          System.out.println("Tidak ada Table dengan nama : "+namaTable);
      
      }
  
  }
  
  public String insert(String namaTable, int Key, String Value){
      //check apakah table dengan nama tersebut sudah ada
      String result=null;
      if(isContainTable(namaTable)){ //sudah ada table dengan nama ini
          //System.out.println("Sudah ada table dengan nama tersebut di database !");
          result =getTable(namaTable).create(Key, Value);
      }else{          
          result="Tidak ada Table dengan nama : "+namaTable;
      }
       return result;
  
  }
public Database splitDatabase(int tokenMin, int tokenMax){
    Database newDB = new Database();
    //int length = database.size()/2;
    for(int i=0;i<database.size();i++){  
        newDB.getDatabase().add(database.get(i).splitTable(tokenMin,tokenMax));
    }
    
    System.out.println("DataBase splited !");
    
    
    return newDB;
}

    public int getSize(){
        int size =0;
        for(int i=0;i<this.database.size();i++)
            size += database.get(i).getSize();
    return size;
    }

    public String JsonIt(){
        String jsontext="";
        //struktur database
        // listof table
        //pake Json aRRAY :v
        JSONArray ja = new JSONArray();
        for(int i=0;i<database.size();i++)
            ja.add(database.get(i).JSonIt());
        jsontext = ja.toJSONString();
        System.out.println("isi Database : "+jsontext);
    return jsontext;
    }
    
    public void setDataFromJson(String Json){
        //baca string json nya
         Object obj=JSONValue.parse(Json);
         JSONArray data = (JSONArray) obj;
         this.database.clear();
         List<Table> newDB = new ArrayList <Table>();
         //iterasi array nya buat masukin data ke table
         for(int i =0;i<data.size();i++){
             Table T = new Table("dummy");
             T.setDataFromJson((String)data.get(i));
             //table sudah di update , masukan ke database
             boolean add = newDB.add(T);
         }
    
    }
    
}
