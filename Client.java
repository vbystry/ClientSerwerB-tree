import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame implements ActionListener{
    private ArrayList<treeCell> drawList;
    protected JDialog addDialog, deleteDialog, treeDialog;
    private JTextField addField, delField, treeField;
    private MenuBar menuBar;
    private JPanel cardPanel;
    private JTabbedPane cardPane;
    private treePanel treePane;
    private int activeTreeNum, treeNum;

    Socket socket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    Client(){
        super("");
        setBounds(200, 200, 500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        treeNum = 0;
        //Tworzenie menuBara do dodawania drzew
        menuBar = new MenuBar();
        Menu addTree = new Menu("Add Tree");
        addTree.add(new MenuItem("Integer"));
        addTree.add(new MenuItem("Double"));
        addTree.add(new MenuItem("String"));
        addTree.addActionListener(this);
        menuBar.add(addTree);
        setMenuBar(menuBar); 

        //Tworzenie panelu do obsługi drzew
        cardPane = new JTabbedPane();
        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());
        this.setLayout(new BorderLayout());
        this.add(cardPanel, BorderLayout.CENTER);
        cardPanel.add(cardPane);

        //Tworzenie okna dialogowego do wyświetlania drzewa
        treeDialog = new JDialog(this, "Tree", true);
        treeDialog.setSize(700,550);
        treeDialog.setLayout(new BorderLayout());
        treeDialog.setLocationRelativeTo(null);
        treeDialog.setResizable(false);
        this.treePane = new treePanel();
        treeDialog.add(this.treePane, BorderLayout.CENTER);
        treeDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);

        //Tworzenie okna dialogowego do dodawania elementu
        addDialog = new JDialog(this, "New element", true);
        addDialog.setSize(370, 90);
        addDialog.setLayout(new BorderLayout());
        addDialog.setLocationRelativeTo(null);
        addDialog.setResizable(false);

        JLabel label = new JLabel("Podaj element do dodania");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        addDialog.add(label, BorderLayout.PAGE_START);

        addField = new JTextField();
        addField.setSize(350, 50);
        addDialog.add(addField, BorderLayout.CENTER);

        JButton b = new JButton("Dodaj");
        b.addActionListener(this);

        addDialog.add(b, BorderLayout.PAGE_END);
        addDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);

        //Tworzenie okna dialogowego do usuwania elementu
        deleteDialog = new JDialog(this, "Delete element", true);
        deleteDialog.setSize(370, 90);
        deleteDialog.setLayout(new BorderLayout());
        deleteDialog.setLocationRelativeTo(null);
        deleteDialog.setResizable(false);

        JLabel label2 = new JLabel("Podaj element do usuniecia");
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        deleteDialog.add(label2, BorderLayout.PAGE_START);

        delField = new JTextField();
        delField.setSize(350, 50);
        deleteDialog.add(delField, BorderLayout.CENTER);

        JButton b2 = new JButton("Usun");
        b2.addActionListener(this);

        deleteDialog.add(b2, BorderLayout.PAGE_END);
        deleteDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);

        //Lista stosowana do wyświetlania drzewa
        this.drawList = new ArrayList<treeCell>();

    }

    public void listenSocket(){
        try {
          socket = new Socket("localhost", 4444);
          //Połączenie z socketem
          //Wysyłanie do serwera
          out = new PrintWriter(socket.getOutputStream(), true);
          //Odbieranie z serwera
          in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (UnknownHostException e) {
           System.out.println("Unknown host: localhost"); System.exit(1);
         }
         catch  (IOException e) {
           System.out.println("No I/O"); System.exit(1);
         }
      }

    // Klasa przycisków znajdujących się w panelu do obsługi drzew
    private class myButton extends JButton{
        private int t;
        
        myButton(int t, String str){
            super(str);
            this.t = t;
            myButton.this.addActionListener(new myButtonListener());
        }

        private class myButtonListener implements ActionListener{

            public void actionPerformed(ActionEvent event) 
            {
                
                Client.this.activeTreeNum = myButton.this.t;
                switch(event.getActionCommand()){
                    //Obsługa akcji dodania elementu
                    case "New element":
                        Client.this.addDialog.show();
                        break;
                    //Obsługa akcji usunięcia elementu
                    case "Delete element":
                        Client.this.deleteDialog.show();
                        break;
                    //Obsługa akcji wyświetlenia drzewa
                    case "Show Tree":
                        try {
                            out.println("GET TREE");
                            out.println(Client.this.activeTreeNum);
                            drawList.clear();
                            String line = "";
                            while(true)
                            {
                                line = in.readLine();
                                if(line.equals("END")) {break;}
                                String element = line;
                                line = in.readLine();
                                if(line.equals("END")) {break;}
                                int x= Integer.parseInt(line);
                                line = in.readLine();
                                if(line.equals("END")) {break;}
                                int y = Integer.parseInt(line);
                                drawList.add(new treeCell(element, x, y));
                            }
                            Client.this.treeDialog.show();
                            
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        break;
                }
                
            }
        }
    }

    //Metoda tworząca obsługę nowego drzewa
    private void newTree(String type){
        out.println(type);
        
        this.treeNum++;
        JPanel card = new JPanel();
        myButton addButton = new myButton(this.treeNum,"New element");
        myButton delButton = new myButton(this.treeNum,"Delete element");
        myButton showButton = new myButton(this.treeNum,"Show Tree");

        card.setLayout(new BorderLayout());
        card.add(addButton,BorderLayout.LINE_START);
        card.add(delButton,BorderLayout.LINE_END);
        card.add(showButton,BorderLayout.CENTER);
        
        this.cardPane.add(card, "Tree " + (this.treeNum + "") + "(" + type + ")");

    }

    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "Integer":
                out.println("NEW TREE");
                out.println(e.getActionCommand());
                this.newTree(e.getActionCommand());
                break;
            case "Double":
                out.println("NEW TREE");
                out.println(e.getActionCommand());
                this.newTree(e.getActionCommand());
                break;
            case "String":
                out.println("NEW TREE");
                out.println(e.getActionCommand());
                this.newTree(e.getActionCommand());
                break;
            case "Dodaj":
                out.println("NEW ELEMENT");
                out.println(activeTreeNum);
                out.println(addField.getText());
                addField.setText("");
                addDialog.hide();
                break;
            case "Usun":
                out.println("DELETE ELEMENT");
                out.println(activeTreeNum);
                out.println(delField.getText());
                
                delField.setText("");
                deleteDialog.hide();
                break;
            

        }
    }

    //Klasa panelu do wyświetlania drzewa
    private class treePanel extends JPanel{
        treePanel(){
            super();

        }

        private void doDrawing(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            
            //Rysowanie po kolei kadego elementu
            for(int i=0; i<drawList.size(); i++)
            {
                g2d.setColor(Color.BLACK);
                g2d.drawString(drawList.get(i).element, drawList.get(i).X,drawList.get(i).Y+5);
            }
              
        }

        @Override
        public void paintComponent(Graphics g) {       
            super.paintComponent(g);
            doDrawing(g);  
        }
        
    }

    //Klasa komórek zawierających elementy drzewa
    //do narysowania
    private class treeCell extends Rectangle2D.Double{
        String element;
        Font font;
        public int X,Y;

        treeCell(String element, int x, int y){
            super(x,y, 20, 10);
            this.X=x;
            this.Y=y;
            this.element = element;
            int S = 12;
            this.font = new Font(Font.SANS_SERIF,Font.PLAIN,S);
            JLabel test = new JLabel(element);
            test.setFont(this.font);
            while(test.getWidth()>20)
            {
                S--;
                this.font = new Font(Font.SANS_SERIF,Font.PLAIN,S);
            }
        }
    }
    public static void main(String[] args){
        Client frame = new Client();

        frame.setVisible(true);
        frame.listenSocket();
    }
}