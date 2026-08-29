public class MiniPrimeCheck { public static void main(String[] a) { int n=37; boolean p=n>1; for(int d=2;d*d<=n;d++) if(n%d==0)p=false; System.out.println(p); } }
