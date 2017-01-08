package komunikatorrsa;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Random;
import java.util.Scanner;


public class KomunikatorRSAKlient {
    
    public static final int PORT=50007;
    public static final String HOST = "127.0.0.1";
    
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
        
        for(;;){ try{
            
        Socket sock;                                                                     
        sock=new Socket(HOST,PORT);                                                      
        System.out.println("Nawiazalem polaczenie: "+sock);
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
    
    
    System.out.println("Mój klucz publiczny (e,n) = (" + e +"," + n +")");
    System.out.println("Mój klucz prywatny  (d,n) = (" + d +"," + n +")");
    
    
    
        
      
      //tworzenie strumienia danych pobieranych z gniazda sieciowego
      BufferedReader inp;                                                    
      inp=new BufferedReader(new InputStreamReader(sock.getInputStream()));  
                                                                             
      //komunikacja - czytanie danych ze strumienia                          
      String e1, n1;                                                             
      e1 =inp.readLine();
      n1 =inp.readLine();
      
      //klucz publiczny serwera
      int e2 = Integer.parseInt(e1);
      int n2 = Integer.parseInt(n1);
      
      BigInteger nn2 = BigInteger.valueOf(n2);
      
     
      
      System.out.println("<Od Serwer:> Serwer klucz publiczny (e,n) = (" + e2 +"," + n2 +")");
      
      BufferedReader klaw;                                                             
      klaw=new BufferedReader(new InputStreamReader(System.in));                       
      PrintWriter outp;                                                                
      outp=new PrintWriter(sock.getOutputStream());                                    
                                                                                       
      //komunikacja - czytanie danych z klawiatury i przekazywanie ich do strumienia   
      System.out.print("<Do Serwer:> Klient klucz publiczny (e,n) = (" + e +"," + n +")");
     
      
      
                                                          
      outp.println(e);
      outp.println(n);
      outp.flush();
      
      for(;;){
      
      inp=new BufferedReader(new InputStreamReader(sock.getInputStream()));
      String dlugosc1, rsa1;
      dlugosc1 = inp.readLine();
      int dlugosc = Integer.parseInt(dlugosc1);
      char[] tablica = new char[dlugosc];
      
        System.out.println("\n<Od Serwer:>Zaszyfrowana wiadomość: ");
      for(int i = 0; i <= dlugosc-1; i++)
      {
         rsa1 = inp.readLine();
         
         int rsa = Integer.parseInt(rsa1);
         BigInteger wiadomosc = BigInteger.valueOf(rsa);
         wiadomosc = wiadomosc.pow(d);
         wiadomosc = wiadomosc.mod(n);
         rsa = wiadomosc.intValue();
         char b = (char) rsa;
         tablica[i] = b;
         System.out.println(rsa1);
        
         
         
         
      }
      System.out.println("<Od Serwer:>Odszyfrowana wiadomosc: ");
      System.out.println(tablica);
      
      outp.flush();
      
      //2
      outp=new PrintWriter(sock.getOutputStream());
      
      Scanner sc = new Scanner(System.in);
      System.out.print ("\nTreść wiadomości: ");
      String wiad = sc.nextLine();
      
      
      
      char[] tab;
       
      tab = wiad.toCharArray();
      int dlug = wiad.length();
     
           
      outp.println(dlug);
      for(int i = 0; i <= dlug-1; i++)
      {
           int rs = (int) tab[i];
          
           BigInteger rs1 = BigInteger.valueOf(rs);
           rs1 = rs1.pow(e2);
           rs1 = rs1.mod(nn2);
           
           
           rs = rs1.intValue();
           
           System.out.println("(Szyfruje kluczem publicznym Serweru: ) " + rs);
           outp.println(rs);
           

           
           
       }
      outp.flush();
      System.out.println("Wysłano!!");
      
      
    }
      
        
    }catch(Exception e){
    System.out.println("Problem łączności z serwerem\n"
            + "Próbujemy połączyć się ponownie");
   }
   }
    
}
}