package appinformes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author raul
 */
public class AppInformes extends Application {

    public static Connection conexion = null;
    TextField tf = new TextField();
    Button btn = new Button();

    @Override
    public void start(Stage primaryStage) {
        //Establece la conexión con la BD
        conectaBD();

        //Crea la escena        
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Informes");
        Menu menu2 = new Menu("Salir");
        tf.setPromptText("Escrbie el ID del cliente");

        MenuItem facturas = new MenuItem("Facturas");
        MenuItem ventasTotales = new MenuItem("Ventas Totales");
        MenuItem facturasPorClientes = new MenuItem("Facturas por Clientes");
        MenuItem subinformeFacturas = new MenuItem("Subinforme Listado Facturas");

        MenuItem salir = new MenuItem("Salir");

        menu.getItems().add(facturas);
        menu.getItems().add(ventasTotales);
        menu.getItems().add(facturasPorClientes);
        menu.getItems().add(subinformeFacturas);

        menu2.getItems().add(salir);

        menuBar.getMenus().add(menu);
        menuBar.getMenus().add(menu2);

        VBox root = new VBox();
        root.getChildren().addAll(menuBar);

        //Eventos menuItem
        facturas.setOnAction(e -> {
            root.getChildren().removeAll(tf, btn);
            try {
                JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("Facturas.jasper"));
                //Map de parámetros
                Map parametros = new HashMap();
                JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
                JasperViewer.viewReport(jp, false);
            } catch (JRException ex) {
                System.out.println("Error al recuperar el jasper");
                JOptionPane.showMessageDialog(null, ex);
            }
        });
        ventasTotales.setOnAction(e -> {
            root.getChildren().removeAll(tf, btn);
            try {
                JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("VentasTotales.jasper"));
                //Map de parámetros
                Map parametros = new HashMap();
                JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
                JasperViewer.viewReport(jp, false);
            } catch (JRException ex) {
                System.out.println("Error al recuperar el jasper");
                JOptionPane.showMessageDialog(null, ex);
            }
        });

        facturasPorClientes.setOnAction(e -> {
            btn.setText("Introducir ID Cliente");
            root.getChildren().addAll(tf, btn);

            btn.setOnAction((ActionEvent a) -> {
                try {
                    JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("FacturasPorClientes.jasper"));
                    //Map de parámetros
                    Map parametros = new HashMap();
                    parametros.put("IDCliente", Integer.parseInt(tf.getText()));
                    JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
                    JasperViewer.viewReport(jp, false);
                } catch (JRException ex) {
                    System.out.println("Error al recuperar el jasper");
                    JOptionPane.showMessageDialog(null, ex);
                }
            });
        });

        subinformeFacturas.setOnAction(e -> {
            root.getChildren().removeAll(tf, btn);
            try {
                JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("ListadoFacturas.jasper"));
                JasperReport jsr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("SubInformeFacturas.jasper"));
                System.out.println("hola");
                
                //Map de parámetros
                Map parametros = new HashMap();
                parametros.put("SubReportParameter", jsr);

                //Mostramos el Informe
                JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion); //TODO: EL FALLO SALTA AQUI
               
                JasperViewer.viewReport(jp, false);
                
            } catch (JRException ex) {
                Logger.getLogger(AppInformes.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        salir.setOnAction(e -> {
            try {
                stop();
                System.exit(0);
            } catch (Exception ex) {
                System.out.println("No se pudo cerrar la conexion a la BD");
            }
        });

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("AppInformes");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        try {
            DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001/xdb1;shutdown=true");
            System.out.println("Conexión cerrada con exito");
        } catch (SQLException ex) {
            System.out.println("No se pudo cerrar la conexion a la BD");
        }
    }

    public void conectaBD() {
        //Establecemos conexión con la BD
        String baseDatos = "jdbc:hsqldb:hsql://localhost:9001/xdb1";
        String usuario = "sa";
        String clave = "";

        try {

            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            conexion = DriverManager.getConnection(baseDatos, usuario, clave);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Fallo al cargar JDBC");
            System.exit(1);
        } catch (SQLException sqle) {
            System.err.println("No se pudo conectar a BD");
            System.exit(1);
        } catch (java.lang.InstantiationException | IllegalAccessException sqlex) {
            System.err.println("Imposible Conectar");
            System.exit(1);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
