import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.JLabel;

public class Serwer {
    ServerSocket server = null;
    Socket client = null;
    BufferedReader in = null;
    PrintWriter out = null;
    String line = "";
    private ArrayList<Tree> treeList;
    private ArrayList<String> typeList;

    Serwer() { 
        try {
            server = new ServerSocket(4444); 
        } 
        catch (IOException e) {
            System.out.println("Could not listen on port 4444"); System.exit(-1);
        }
        treeList = new ArrayList<Tree>();
        typeList = new ArrayList<String>();
    }

    public void listenSocket() {
        try {
            client = server.accept();
        } 
        catch (IOException e) {
            System.out.println("Accept failed: 4444"); System.exit(-1);
            }
            try {
                // Odbieranie od socketa
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // Wysylanie do socketa
            out = new PrintWriter(client.getOutputStream(), true);
            } 
            catch (IOException e) {
            System.out.println("Accept failed: 4444"); System.exit(-1);
            }
            while(line != null) {
            try {
                // Odbieranie od socketa
                line = in.readLine();
                int treeNum;
                //Obsługa komend wychodzących od klienta      
                switch (line) {
                    //Obsługa dodania nowego elementu do aktywnego w kliencie drzewa
                    case "NEW ELEMENT":
                        try {
                            treeNum = Integer.parseInt(in.readLine())-1;
                            if(this.typeList.get(treeNum).equals("Integer"))  
                            {
                                try {
                                    treeList.get(treeNum).insert(Integer.parseInt(in.readLine()));   //dodac kapsulkowanie
                                } catch (Exception ex) {
                                    //TODO: handle exception
                                }
                            }
                            else if(this.typeList.get(treeNum).equals("Double")) 
                            {
                                try {
                                    treeList.get(treeNum).insert(Double.parseDouble(in.readLine()));
                                } catch (Exception ex) {
                                    //TODO: handle exception
                                }
                            }
                            else
                            {
                                try {
                                    treeList.get(treeNum).insert(in.readLine());
                                } catch (Exception ex) {
                                    //TODO: handle exception
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        break;
                    //Obsługa usuniecia elementu z aktywnego w kliencie drzewa
                    case "DELETE ELEMENT":
                        try {
                            treeNum = Integer.parseInt(in.readLine())-1;
                            Tree activeTree = treeList.get(treeNum);
                            if(this.typeList.get(treeNum).equals("Integer"))  
                            {
                                try {
                                    treeList.get(treeNum).Delete(Integer.parseInt(in.readLine()));   
                                } catch (Exception ex) {
                                    //TODO: handle exception
                                }
                            }
                            else if(this.typeList.get(treeNum).equals("Double")) 
                            {
                                try {
                                    treeList.get(treeNum).Delete(Double.parseDouble(in.readLine()));
                                } catch (Exception ex) {
                                    //TODO: handle exception
                                }
                            }
                            else
                            {
                                try {
                                    treeList.get(treeNum).Delete(in.readLine());
                                } catch (Exception ex) {
                                    //TODO: handle exception
                                }
                            }
                        } catch (Exception e) {
                            //TODO: handle exception
                        }
                        break;
                    //Obsługa wyświetlenia aktywnego w kliencie drzewa
                    case "GET TREE":
                        try { 
                            treeNum = Integer.parseInt(in.readLine())-1;
                            int xJump = 350;
                            int yJump = (int)(500/(treeList.get(treeNum).H));
                            sendWholeTree(treeNum, xJump, yJump);
                            out.println("END");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        break;
                    //Obsługa dodania nowego drzewa
                    case "NEW TREE":
                        String type = in.readLine();
                        switch (type) {
                            case "Integer":
                                this.treeList.add(new Tree<Integer>());
                                this.typeList.add("Integer");
                                break;
                            case "Double":
                                this.treeList.add(new Tree<Double>());
                                this.typeList.add("Double");
                                break;
                            case "String":
                                this.treeList.add(new Tree<String>());
                                this.typeList.add("String");
                                break;
                            default:
                                break;
                        }
                    default:
                        break;
                }
            } 
            catch (IOException e) {
                System.out.println("Read failed"); System.exit(-1);
            } 
        }
    }

    //Algorytm przesyłający do klienta drzewo w formie gotowej do narysowania
    //Wywołuje się rekurencyjnie przesyłając podany jako argument korzeń i dwa poddrzewa
    private void sendTree(TreeNode node, int x, int y, int jumpX, int jumpY, boolean right){
        String element = node.elemToString();
        String Y = Integer.toString(y+jumpY);
        String X = "";
        if(right)   {X = Integer.toString(x+jumpX);}
        if(!right)        {X = Integer.toString(x-jumpX);}

        out.println(element);
        out.println(X);
        out.println(Y);

        jumpX = (int)jumpX/2;
        if(node.right != null)  {sendTree(node.right, Integer.parseInt(X), y+jumpY, jumpX, jumpY, true);}
        if(node.left != null)   {sendTree(node.left, Integer.parseInt(X), y+jumpY, jumpX, jumpY, false);}
        
    }

    //Motoda przesyłająca całe drzewo w formie gotowej do narysowania
    //poprzed wywłowanie w/w algorytmu dla korzenia całego drzewa
    private void sendWholeTree(int treeNum, int jumpX, int jumpY){
        out.println(treeList.get(treeNum).getRoot());
        out.println(350);
        out.println(10);
        jumpX = 350/2;
        if(treeList.get(treeNum).getFullRoot().right != null)  {sendTree(treeList.get(treeNum).getFullRoot().right, 350, 10, jumpX, jumpY, true);}
        if(treeList.get(treeNum).getFullRoot().left != null)   {sendTree(treeList.get(treeNum).getFullRoot().left, 350, 10, jumpX, jumpY, false);}
    }

    protected void finalize() {
        try {
        in.close();
        out.close();
        client.close();
        server.close();
        } 
        catch (IOException e) {
        System.out.println("Could not close."); System.exit(-1);
        }
    }

    public static void main(String[] args) {
        Serwer server = new Serwer();
        server.listenSocket();
    }
}


//Implementacja klas i algorytmów do obsługi drzew binarnych 
class TreeNode<T extends Comparable<T>> {
    public T elem;
    TreeNode<T> left;
    TreeNode<T> right;
    TreeNode<T> father;

    //Konstruktor dla "pędów"
    TreeNode(T elem, TreeNode<T> father){
        this.elem = elem;
        this.father = father;
        this.left = null;
        this.right = null;
    }

    //Konstruktor dla pierwszego korzenia
    TreeNode(){
        this.elem = null;
        this.left = null;
        this.right = null;
    }

    public String elemToString(){
        return elem.toString();
    }

    public String toString() {return elem.toString();}
}

class Tree<T extends Comparable<T>>{
    private TreeNode<T> root;
    public int H;
    private int maxSize;
    
    
    Tree() 
    {
        this.root = null;
        this.H=1;
    }

    private String toS(TreeNode<T> w){
        if(w!=null) {return "(" + w.elem + toS(w.left)+ ":" + toS(w.right)+")";}
        return "()";
    }

    public String toString() {return toS(this.root);}

    //Metoda przeszukiwania elementu s
    public TreeNode<T> Search(TreeNode<T> n, T s){
        if(n == null || n.elem.equals(s)) {return n;}
        else if(s.compareTo(n.elem) < 0) {return Search(n.left, s);}
        else {return Search(n.right, s);}

    }

    //Metoda wyznaczjąca sukcesor węzła
    private TreeNode<T> Succesor(TreeNode<T> F){
        //jeśli węzeł ma prawe poddrzewo
        //zwróć najmniejszy (najbardziej na lewo)
        //element prawego poddrzewa
        if(F.right != null) 
        {
            TreeNode<T> son = F.right;

            while(son.left != null) {son = son.left;}
            return son;
        }
        //Jeśli nie zwróć ...
        TreeNode<T> s = F.father;
        while(s != null && F.equals(s.right))
        {
            F = s;
            s = s.father;
        }
        return s;

    }

    //Metoda dodająca element do drzewa
    public void insert(T elem){
        TreeNode y = null;
        TreeNode x = this.root;
        int i=0;

        while(x != null)
        {
            i++;
            y = x;
            if(x.elem.compareTo(elem)>0) {x=x.left;}
            else if(x.elem.compareTo(elem) == 0) {return;}
            else {x=x.right; }
        }

        TreeNode<T> z = new TreeNode<T>(elem, y);

        if(y==null) {this.root = z;}
        else if(y.elem.compareTo(elem)>0) {y.left = z;}
        else    {y.right=z;}
        if(i>this.H) {this.H = i;}
    }


    public void Delete(T elem){
        Delete(Search(this.root, elem));
    }

    public void Delete(TreeNode<T> N){
        TreeNode<T> x,y;
        if(N.left == null || N.right == null)   {y=N;}
        else {y=Succesor(N);}
        if(y.left != null)                      {x=y.left;}
        else                                    {x=y.right;}

        if(x != null)                           {x.father = y.father;}

        if(y.father == null)                    {this.root = x;}
        else if(y.equals(y.father.left))        {y.father.left = x;}
        else                                    {y.father.right = x;}

        if(!y.equals(N))                        {N.elem = y.elem;}

    }

    public T getRoot() {return this.root.elem;}
    public TreeNode<T> getFullRoot() {return this.root;}
}