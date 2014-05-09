/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import java.util.ArrayList;

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
  
  public String insert(String namaTable, String Key, String Value){
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
  
  
}