public class SimpleQRCode {
    public static void main(String[] args) {
        int size = 21;
        int[][] qr = new int[size][size];

        drawFinder(qr, 0,0);
        drawFinder(qr, size-7,0);
        drawFinder(qr, 0,size-7);

        // place demo data bits in diagonal
        int bit = 0;
        for (int i=0;i<size;i++)
            qr[i][i] = (bit++ % 2);

        print(qr);
    }

    static void drawFinder(int[][] a, int x, int y){
        for (int i=0;i<7;i++) for (int j=0;j<7;j++) {
            boolean v = (i==0||i==6||j==0||j==6 || (i>=2&&i<=4&&j>=2&&j<=4));
            a[y+i][x+j] = v?1:0;
        }
    }

    static void print(int[][] a){
        for (int[] r:a){
            for(int c:r) System.out.print(c==1?"██":"  ");
            System.out.println();
        }
    }
}
