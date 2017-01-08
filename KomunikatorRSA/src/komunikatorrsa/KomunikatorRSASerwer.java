
package komunikatorrsa;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Random;
import java.util.Scanner;


public class KomunikatorRSASerwer {
    
    public static final int PORT=50007;
    
    // Funkcja obliczająca NWD dla dwóch liczb
    private static int nwd(int a, int b) {
        int t;
        while(b != 0)
             {
                 t = b;
                 b = a % b;
                 a = t;
             }
             return a;
    }
    //funkcja obliczająca odwmod dla dwóch liczb
    private static int odwr_mod(int a, int n){
        int a0,n0,p0,p1,q,r,t;
        p0 = 0; p1 = 1; a0 = a; n0 = n;
        q  = n0 / a0;
        r  = n0 % a0;
        while(r > 0) {
            t = p0 - q * p1;
            if(t >= 0)
                t = t % n;
            else
                t = n - ((-t) % n);
            p0 = p1; p1 = t;
            n0 = a0; a0 = r;
            q  = n0 / a0;
            r  = n0 % a0;
        }
        return p1;
    }
    
    
    public static void main(String[] args) throws IOException {
        
        //tworzenie gniazda serwerowego                                        
      ServerSocket serv;                                                     
      serv=new ServerSocket(PORT);                                           
      for(;;){
          try{
              
          
                                                                             
      //oczekiwanie na polaczenie i tworzenie gniazda sieciowego             
      System.out.println("Czekam na klienta: "+serv);                               
      Socket sock;                                                           
      sock=serv.accept();                                                    
      System.out.println("Jest polaczenie: "+sock);
      
      //losowanie dwóch liczb
        Random generator = new Random();
         int p = 0;
         int q = 0;
         
         p = (generator.nextInt(1000));
         q = (generator.nextInt(1000));
         
         //wyznaczenie liczby pierwszej p
    for(;;)
    {
    
        boolean pierwsza = true;
        for(int i=2;i*i<=p;i++)
        if(p%i==0)
         pierwsza = false;

        if(pierwsza){         
         break;
        }
        else{
        p++;
        }

    }
    //wyznaczenie liczby pierwszej q
     for(;;)
    {
    
        boolean pierwsza = true;
        for(int i=2;i*i<=q;i++)
        if(q%i==0)
         pierwsza = false;

        if(pierwsza){
         break;
        }
        else{
        q++;
        }

    }
    
    //tymczasowe
   // p = 13;
   // q = 11;
    
    System.out.println("p = " + p);
    System.out.println("q = " + q);
    
    BigInteger p1 = BigInteger.valueOf(p);
    BigInteger q1 = BigInteger.valueOf(q);
    
    BigInteger n = (p1.multiply(q1));
    
    BigInteger p_1 = BigInteger.valueOf(p-1);
    BigInteger q_1 = BigInteger.valueOf(q-1);
    
    BigInteger phi1 = (p_1.multiply(q_1));
    
    int phi = phi1.intValue();
    
    //wyznaczanie wykładnika publicznego e. Ma on być wzglednie pierwszy z phi
    int e = 0;
        for(e = 3; nwd(e,phi) != 1; e += 2);
    //wyznaczenie wykladnika prywatnego d, ktory ma byc odwrotnoscia modulo phi    
    int d = odwr_mod(e,phi);
    
    
    System.out.println("Serwer klucz publiczny (e,n) = (" + e +"," + n +")");
    System.out.println("Serwer klucz prywatny  (d,n) = (" + d +"," + n +")");
    
    //tworzenie strumieni danych pobieranych z klawiatury i dostarczanych do socketu 
      BufferedReader klaw;                                                             
      klaw=new BufferedReader(new InputStreamReader(System.in));                       
      PrintWriter outp;                                                                
      outp=new PrintWriter(sock.getOutputStream());                                    
                                                                                       
      //komunikacja - czytanie danych z klawiatury i przekazywanie ich do strumienia   
      System.out.print("<Do Klient:> Serwer klucz publiczny (e,n) = (" + e +"," + n +")"+"\n");
      //int n1 = n.intValue();
      
      
                                                          
      outp.println(e);
      outp.println(n);
      outp.flush();
      
      //tworzenie strumienia danych pobieranych z gniazda sieciowego
      BufferedReader inp;                                                    
      inp=new BufferedReader(new InputStreamReader(sock.getInputStream()));  
                                                                             
      //komunikacja - czytanie danych ze strumienia                          
      String e2, n2;                                                             
      e2 =inp.readLine();
      n2 =inp.readLine();
      
      int e3 = Integer.parseInt(e2);
      int n3 = Integer.parseInt(n2);
      BigInteger n4 = BigInteger.valueOf(n3);
      
      System.out.println("<Od Klient:> Klient klucz publiczny (e,n) = (" + e2 +"," + n2 +")");
      
      for(;;){
      outp=new PrintWriter(sock.getOutputStream());
      
      Scanner sc = new Scanner(System.in);
      System.out.print ("\nTreść wiadomości: ");
      String wiadomosc = sc.nextLine();
      
      
      
      
      char[] tablica;
       
      tablica = wiadomosc.toCharArray();
      int dlugosc = wiadomosc.length();
     
           
      outp.println(dlugosc);
      for(int i = 0; i <= dlugosc-1; i++)
      {
           int rsa = (int) tablica[i];
          
           BigInteger rsa1 = BigInteger.valueOf(rsa);
           rsa1 = rsa1.pow(e3);
           rsa1 = rsa1.mod(n4);
           
           
           rsa = rsa1.intValue();
           
           System.out.println("(Szyfruje kluczem publicznym Klienta: ) " + rsa);
           outp.println(rsa);
           

           
           
       }
          
      outp.flush();
      System.out.println("Wysłano!!");
      
      //2 
      inp=new BufferedReader(new InputStreamReader(sock.getInputStream()));
      String dlug1, rs1;
      dlug1 = inp.readLine();
      int dlug = Integer.parseInt(dlug1);
      char[] tab = new char[dlug];
      
      System.out.println("\n<Od Klient:>Zaszyfrowana wiadomość: ");
      
      for(int i = 0; i <= dlug-1; i++)
      {
         rs1 = inp.readLine();
         
         int rs = Integer.parseInt(rs1);
         BigInteger wiad = BigInteger.valueOf(rs);
         wiad = wiad.pow(d);
         wiad = wiad.mod(n);
         rs = wiad.intValue();
         char b = (char) rs;
         tab[i] = b;
         System.out.println(rs1);
        
         
         
         
      }
      System.out.println("<Od Klient:>Odszyfrowana wiadomosc: ");
      System.out.println(tab);
      
      outp.flush();
        
      
      
      
      
      
      }
          }catch(Exception e){
        System.out.println("Klient się rozłączył");
    }}  
    }
    
}
